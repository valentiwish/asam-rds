package com.robotCore.robot.controller;

import com.beans.redis.RedisUtil;
import com.entity.R;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.entity.RobotGroup;
import com.robotCore.robot.entityVo.RobotBasicInfoVO;
import com.robotCore.robot.service.RobotBasicInfoService;
import com.robotCore.robot.service.RobotGroupService;
import com.robotCore.robot.service.RobotRealTimeInfoService;
import com.robotCore.scheduing.vo.RobotMonitorVO;
import com.robotCore.task.tcpCilent.TcpClientThread;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Des: 机器人实时状态信息控制器
 * @author: zxl
 * @date: 2023/4/26
 **/
@Slf4j
@RestController
@Api(value = "RobotStatusInfoController", description = "机器人状态信息管理")
@RequestMapping(value = "robotRealTime")
public class RobotRealTimeInfoController extends BaseController {

    @Autowired
    private RobotRealTimeInfoService robotRealTimeInfoService;

    @Autowired
    private RobotGroupService robotGroupService;

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "获取机器人实时状态信息", notes = "获取机器人实时状态信息")
    @PostMapping(value = "/stateInfo")
    public Object getRobotStateInfo(String robotIp) {
        if (ObjectUtil.isNotEmpty(robotIp)) {
            //在推送机器人实时信息之前先配置推送端口
            boolean flag = robotRealTimeInfoService.pushMsgConfig(robotIp);
            //配置推送端口之后，开始推送
            if (flag) {
                robotRealTimeInfoService.getRealTimeStatusInfo(robotIp);
                return R.ok();
            }
            return R.error("配置机器人推送端口失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取机器人基本信息", notes = "获取机器人基本信息")
    @PostMapping(value = "/baseInfo")
    public Object getRobotInfoByName(String vehicleId) {
        if (ObjectUtil.isNotEmpty(vehicleId)) {
            List<RobotBasicInfo> robotBasicInfos = robotBasicInfoService.findByName(vehicleId);
            if (robotBasicInfos.size() != 0) {
                RobotBasicInfo robotBasicInfo = robotBasicInfos.get(0);
                RobotBasicInfoVO vo = new RobotBasicInfoVO();
                vo.setVehicleId(robotBasicInfo.getVehicleId());
                vo.setCurrentIp(robotBasicInfo.getCurrentIp());
                vo.setModel(robotBasicInfo.getModel());
                vo.setVersion(robotBasicInfo.getVersion());
                return R.ok(vo);
            }
            return R.error("获取机器人数据失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取地图中全部机器人的实时运行信息", notes = "获取地图中全部机器人的实时运行信息")
    @PostMapping(value = "/runInfo")
    public Object getRobotRunInfo() throws InterruptedException {
        List<RobotMonitorVO> robotMonitorVOList = new ArrayList<>();
        Map<String, RobotMonitorVO> monitorVOMap = new HashMap<>();
        List<RobotGroup> list = robotGroupService.findList();
        for (RobotGroup robotGroup : list) {
            List<RobotBasicInfo> robotBasicInfoList = robotBasicInfoService.findListByGroupName(robotGroup.getGroupName());
            Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
            for (RobotBasicInfo robotBasicInfo : robotBasicInfoList) {
                String currentIp = robotBasicInfo.getCurrentIp();
                String vehicleId = robotBasicInfo.getVehicleId();
                //如果机器人在线
                if (map.containsKey(currentIp + PortConstant.ROBOT_STATUS_PORT)) {
                    //在推送机器人实时信息之前先配置推送端口，后续需要注意是否判断是否配置成功
                    robotRealTimeInfoService.pushMsgConfig(currentIp);
                    //配置推送端口之后，开始推送
                    robotRealTimeInfoService.getRealTimeStatusInfo(currentIp);
                    Thread.sleep(100L);
                }
                Map<String, RobotMonitorVO> robotInfoVOMap = (Map<String, RobotMonitorVO>)redisUtil.stringWithGet("robot_push_less_res");
                //获取redis存储的当前在分组中存在的机器人名字
                List<String> robots = (List<String>) redisUtil.stringWithGet("robots");
                for (String robot : robots) {
                    Set<String> robotNames = robotInfoVOMap.keySet();
                    if (robotNames.contains(robot)) {
                        RobotMonitorVO monitorVO = robotInfoVOMap.get(robot);
                        monitorVOMap.put(robot, monitorVO);
                    }
                }
                RobotMonitorVO robotMonitorVO = monitorVOMap.get(vehicleId);
                robotMonitorVO.setGroupName(robotBasicInfo.getGroupName());
                robotMonitorVO.setCurrentIp(currentIp);
                robotMonitorVO.setVersion(robotBasicInfo.getVersion());
                robotMonitorVO.setModel(robotBasicInfo.getModel());
                robotMonitorVO.setDspVersion(robotBasicInfo.getDspVersion());
                robotMonitorVO.setZaiXian(map.containsKey(currentIp + PortConstant.ROBOT_STATUS_PORT));
                robotMonitorVOList.add(robotMonitorVO);
            }
        }
        return R.ok(robotMonitorVOList);
    }

    @ApiOperation(value = "断开实时获取机器人状态信息", notes = "断开实时获取机器人状态信息")
    @PostMapping(value = "/disConnect")
    public Object disConnect(String robotIp) {
        if (ObjectUtil.isNotEmpty(robotIp)) {
            robotRealTimeInfoService.disRealTimeStatusInfo(robotIp);
            return R.ok();
        } else {
            return R.error("前端传递参数为空");
        }
    }
}
