package com.robotCore.robot.controller;

import com.alibaba.fastjson.JSON;
import com.beans.redis.RedisUtil;
import com.entity.EntityResult;
import com.entity.R;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.constant.Constant;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.constant.ProtocolConstant;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.entity.RobotConnect;
import com.robotCore.robot.entity.RobotType;
import com.robotCore.robot.entityVo.RobotTypeVO;
import com.robotCore.robot.service.RobotBasicInfoService;
import com.robotCore.robot.service.RobotConnectService;
import com.robotCore.robot.service.RobotTypeService;
import com.robotCore.scheduing.common.config.CustomScheduledTaskRegistrar;
import com.robotCore.scheduing.vo.RobotConnectVO;
import com.robotCore.task.core.ProtocolConvert;
import com.robotCore.task.tcpCilent.TcpClientThread;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Des: 连接机器人的控制器类
 * @author: zxl
 * @date: 2023/4/24
 **/
@Slf4j
@RestController
@Api(value = "RobotConnectController", description = "机器人连接控制器")
@RequestMapping(value = "robotConnect")
public class RobotConnectController extends BaseController {

    @Autowired
    private RobotConnectService robotConnectService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private RobotTypeService robotTypeService;

    @ApiOperation(value = "获取机器人设备列表")
    @RequestMapping(value = "/list")
    public Object list(){
        List<RobotConnect> list = robotConnectService.findList();
        List<RobotConnectVO> voList = robotConnectService.dataInit(list);
        return R.ok(voList);
    }

    @ApiOperation(value = "根据Ip获取机器人设备列表")
    @RequestMapping(value = "/listByIp")
    public Object listByIp(String robotIp){
        List<RobotConnect> list = robotConnectService.listByIp(robotIp);
        List<RobotConnectVO> voList = robotConnectService.dataInit(list);
        return R.ok(voList);
    }

    //    @ControllerLog(type = OpsLogType.ADDORUPDATE, value ="添加设备")
    @ApiOperation(value = "添加机器人设备", notes = "添加机器人设备")
    @PostMapping(value = "/addEquipment")
    public Object addEquipment(String robotIp, String typeName) throws InterruptedException {
         if (ObjectUtil.isNotEmpty(robotIp)) {
            EntityResult result = ProtocolConvert.sendHexMsg(typeName, robotIp, PortConstant.ROBOT_STATUS_PORT, ProtocolConstant.ROBOT_BASIC_INFO);
            //延迟读取redis中的数据
            Thread.sleep(600);
            String statusInfoRes = (String)redisUtil.stringWithGet("robot_status_info_res");
            RobotConnect model = JSON.parseObject(statusInfoRes, RobotConnect.class);
            model.setState(Constant.STATE_VALID);
            model.setRobotType(typeName);
            UserVO curUser = getCurUser();//当前创建人
            if (result != null && result.isSuccess()) {
                boolean save = robotConnectService.save(model, curUser.getId(), curUser.getUserName());
                //每次添加机器人时，同步拉取机器人地图
                robotBasicInfoService.downloadCurrentRobotMap(robotIp);
                return save ? R.ok() : R.error("添加机器人设备失败");
            } else {
                return R.error("连接机器人失败:" + robotIp);
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取机器人类型列表", notes = "获取机器人类型列表")
    @PostMapping(value = "/typeList")
    public Object robotTypeList() {
        List<RobotType> list = robotTypeService.findList();
        List<RobotTypeVO> typeList = new ArrayList<>();
        for (RobotType robotType : list) {
            RobotTypeVO typeVO = new RobotTypeVO();
            typeVO.setId(robotType.getId());
            String typeName = robotType.getTypeName();
            typeVO.setTypeName(typeName);
            typeList.add(typeVO);
        }
        return R.ok(typeList);
    }

    //    @ControllerLog(type = OpsLogType.DELETE, value ="删除添加的机器人设备")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotConnect model = robotConnectService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean flag = robotConnectService.save(model,curUser.getId(),curUser.getUserName());
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    //    @ControllerLog(value = "开始连接")
    @ApiOperation(value = "开始连接", notes = "开始连接")
    @PostMapping(value = "/connect")
    public Object connect(String robotIp) throws Exception {
        if (ObjectUtil.isNotEmpty(robotIp)) {
            String typeName = robotConnectService.getRobotType(robotIp);
            EntityResult result = ProtocolConvert.sendHexMsg(typeName, robotIp, PortConstant.ROBOT_STATUS_PORT, ProtocolConstant.ROBOT_BASIC_INFO);
            RobotConnect robotConnect = robotConnectService.findByIp(robotIp);
            String statusInfoRes = (String)redisUtil.stringWithGet("robot_status_info_res");
            RobotConnect model = JSON.parseObject(statusInfoRes, RobotConnect.class);
            //如果mapMd5值发生变更，则更新地图
            if (!robotConnect.getCurrentMapMd5().equals(model.getCurrentMapMd5())) {
                robotBasicInfoService.downloadCurrentRobotMap(robotIp);
                //修改机器人连接信息为修改地图后的数据
                RobotConnect oldRobotConnect = robotConnectService.findByIp(robotIp);
                oldRobotConnect.setCurrentMap(model.getCurrentMap());
                oldRobotConnect.setCurrentMapMd5(model.getCurrentMapMd5());
                robotConnectService.saveOrUpdate(oldRobotConnect);
            }
            boolean downloadCurrentRobotMapFlag = robotBasicInfoService.downloadCurrentRobotMap(robotIp);
            try {
                InetAddress geek = InetAddress.getByName(robotIp);
                if(geek.isReachable(1000) && Objects.requireNonNull(result).isSuccess() && downloadCurrentRobotMapFlag){
                    return R.ok("机器人连接成功:" + robotIp);
                } else {
                    return  R.error("机器人连接失败:" + robotIp);
                }
            } catch (Exception e) {
                return  R.error("机器人连接失败:" + robotIp);
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

    //    @ControllerLog(value = "根据IP断开机器人连接")
    @ApiOperation(value = "根据IP断开机器人连接", notes = "根据IP断开机器人连接")
    @PostMapping(value = "/disConnect")
    public Object disConnect(String robotIp) {
        if (ObjectUtil.isNotEmpty(robotIp)) {
            String typeName = robotConnectService.getRobotType(robotIp);
            boolean flag = ProtocolConvert.disConnect(typeName, robotIp);
            return flag ? R.ok("机器人断开连接成功:" + robotIp) : R.error("机器人断开连接错误:" + robotIp);
        } else {
            return R.error("前端传递参数为空");
        }
    }
}
