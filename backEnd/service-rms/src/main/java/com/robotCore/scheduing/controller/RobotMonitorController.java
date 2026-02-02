package com.robotCore.scheduing.controller;

import com.alibaba.fastjson.JSON;
import com.beans.redis.RedisUtil;
import com.entity.R;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.constant.Constant;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.service.RobotBasicInfoService;
import com.robotCore.scheduing.common.enums.ControlStatusEnum;
import com.robotCore.scheduing.common.enums.RobotStatusEnum;
import com.robotCore.scheduing.dto.RobotLockPathAndPoint;
import com.robotCore.scheduing.dto.RobotResDTO;
import com.robotCore.scheduing.dto.RobotRunDTO;
import com.robotCore.scheduing.entity.RobotNavigation;
import com.robotCore.scheduing.service.RobotMonitorService;
import com.robotCore.scheduing.service.RobotNavigationService;
import com.robotCore.scheduing.service.RobotWaybillService;
import com.robotCore.scheduing.vo.RobotCountVO;
import com.robotCore.scheduing.vo.RobotInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Des: 机器人运行监控的控制器类
 * @author: zxl
 * @date: 2023/5/25
 **/
@Slf4j
@RestController
@Api(value = "RunMonitorController", description = "机器人运行监控的控制器类")
@RequestMapping(value = "monitor")
public class RobotMonitorController extends BaseController {

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private RobotMonitorService robotMonitorService;

    @Autowired
    private RobotNavigationService robotNavigationService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RobotWaybillService robotWaybillService;

    @ApiOperation(value = "获取全部机器人在线信息", notes = "获取全部机器人在线信息(在线2/3)")
    @PostMapping(value = "/onlineInfo")
    public Object getRobotOnlineInfo() {
        RobotCountVO robotCountVO = robotMonitorService.getRobotOnlineInfo();
        return R.ok(robotCountVO);
    }

    //    @ControllerLog(value = "查询机器人状态列表")
    @ApiOperation(value = "机器人状态列表", notes = "机器人状态列表")
    @PostMapping(value = "/statusList")
    public Object getStatusList() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Map<Integer, String> robotStatusList = robotMonitorService.getRobotStatusList();
        return R.ok(robotStatusList);
    }

    //    @ControllerLog(value = "设置机器人的状态为可接单状态")
    @ApiOperation(value = "设置机器人的状态为可接单状态", notes = "设置机器人的状态为可接单状态")
    @PostMapping(value = "/accepted")
    public Object requestAccepted(String robotName, float batteryLevel) {
        //机器人如果属于可接单状态，是机器人可以参与下发运单的分配的必要条件，除此之外，服务器需要有机器人的控制权，机器人的定位已经被确认，机器人的电量正常，机器人正在使用的地图已添加到场景中
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
        //登录用户名称
        UserVO curUser = this.getCurUser();
        if (robots.size() > 0) {
            //获取当前机器人
            RobotBasicInfo robot = robots.get(0);
            //1.获取机器人控制权
            boolean flag1 = robotMonitorService.acquireControl(robots,curUser);
            //2.机器人定位已确认
            boolean flag2 = robotMonitorService.confirmLocation(robots);
            //3.机器人电量正常
            boolean flag3 = batteryLevel * 100 >= robot.getChargeOnly() || robot.getChargeOnly() == -1;
            boolean b = false;
            if (flag1 && flag2 && flag3) {
                //设置机器人的状态为可接单
                robot.setOrderState(RobotStatusEnum.ORDER_AVAILABLE.getCode());
                b = robotBasicInfoService.saveOrUpdate(robot);
            }
            return b ? R.ok() : R.error("设置机器人为可接单状态失败：" + robotName);
        }
        return R.error(robotName + "不存在");
    }

    //    @ControllerLog(value = "设置机器人的状态为不接单且占用资源")
    @ApiOperation(value = "设置机器人的状态为不接单且占用资源", notes = "设置机器人的状态为不接单且占用资源")
    @PostMapping(value = "/notOrderButRes")
    public Object notOrderButResource(String robotName) {
        //机器人如果属于可接单状态，是机器人可以参与下发运单的分配的必要条件，除此之外，服务器需要有机器人的控制权，机器人的定位已经被确认，机器人的电量正常，机器人正在使用的地图已添加到场景中
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
        if (robots.size() > 0) {
            //获取当前机器人
            RobotBasicInfo robot = robots.get(0);
            //设置当前的机器人为不接单且占用资源
            robot.setOrderState(RobotStatusEnum.NOT_ORDER_BUT_RESOURCE.getCode());
            boolean b = robotBasicInfoService.saveOrUpdate(robot);
            return b ? R.ok() : R.error("设置机器人为不接单且占用资源状态失败：" + robotName);
        }
        return R.error(robotName + "不存在");
    }

    //    @ControllerLog(value = "设置机器人的状态为不接单不接单不占用资源")
    @ApiOperation(value = "设置机器人的状态为不接单不占用资源", notes = "设置机器人的状态为不接单不占用资源")
    @PostMapping(value = "/notOrderNotRes")
    public Object notOrderNotResource(String robotName) {
        //机器人如果属于可接单状态，是机器人可以参与下发运单的分配的必要条件，除此之外，服务器需要有机器人的控制权，机器人的定位已经被确认，机器人的电量正常，机器人正在使用的地图已添加到场景中
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
        if (robots.size() > 0) {
            //获取当前机器人
            RobotBasicInfo robot = robots.get(0);
            //设置当前的机器人为不接单且占用资源
            robot.setOrderState(RobotStatusEnum.NOT_ORDER_NOT_RESOURCE.getCode());
            boolean b = robotBasicInfoService.saveOrUpdate(robot);
            return b ? R.ok() : R.error("设置机器人为不接单不接单不占用资源状态失败：" + robotName);
        }
        return R.error(robotName + "不存在");
    }

    //    @ControllerLog(value = "抢占机器人控制权")
    @ApiOperation(value = "抢占机器人控制权", notes = "抢占机器人控制权")
    @PostMapping(value = "/getControl")
    public Object getControl(String robotName) {
        //登录用户名称
        UserVO user = this.getCurUser();
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
        if (robots.size() > 0) {
            boolean flag = robotMonitorService.acquireControl(robots,user);
            String data = (String)redisUtil.stringWithGet("robot_config_lock_res");
            RobotResDTO resp = JSON.parseObject(data, RobotResDTO.class);
            if (resp.getRetCode() == 0) {
                boolean flag1 = robotMonitorService.confirmLocation(robots);
                String data1 = (String)redisUtil.stringWithGet("robot_control_comfirmloc_res");
                RobotResDTO resp1 = JSON.parseObject(data1, RobotResDTO.class);
                if (resp1.getRetCode() == 0) {
                    return flag && flag1 ? R.ok() : R.error("抢占控制权失败：" + robotName);
                } else {
                    return R.error("确认定位失败,retCode:" + resp.getRetCode() + ",errMsg:" + resp.getErrMsg());
                }
            } else {
                return R.error("抢占控制权失败,retCode:" + resp.getRetCode() + ",errMsg:" + resp.getErrMsg());
            }
        }
        return R.error(robotName + "不存在");
    }

    //    @ControllerLog(value = "释放机器人控制权")
    @ApiOperation(value = "释放机器人控制权", notes = "释放机器人控制权")
    @PostMapping(value = "/releaseControl")
    public Object releaseControl(String robotName) {
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
        if (robots.size() > 0) {
            boolean flag = robotMonitorService.releaseControl(robots);
            String data = (String)redisUtil.stringWithGet("robot_config_unlock_res");
            RobotResDTO resp = JSON.parseObject(data, RobotResDTO.class);
            if (resp.getRetCode() == 0) {
                return flag ? R.ok() : R.error("释放控制权失败：" + robotName);
            } else {
                return R.error("释放控制权失败,retCode:" + resp.getRetCode() + ",errMsg:" + resp.getErrMsg());
            }
        }
        return R.error(robotName + "不存在");
    }

    //    @ControllerLog(value = "确认定位")
    @ApiOperation(value = "确认定位", notes = "确认定位")
    @PostMapping(value = "/confirmLoc")
    public Object confirmLocation(String robotName) {
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
        if (robots.size() > 0) {
            boolean flag = robotMonitorService.confirmLocation(robots);
            String data = (String)redisUtil.stringWithGet("robot_control_comfirmloc_res");
            RobotResDTO resp = JSON.parseObject(data, RobotResDTO.class);
            if (resp.getRetCode() == 0) {
                return flag ? R.ok() : R.error("确认定位失败：" + robotName);
            } else {
                return R.error("确认定位失败,retCode:" + resp.getRetCode() + ",errMsg:" + resp.getErrMsg());
            }
        }
        return R.error(robotName + "不存在");
    }

    //    @ControllerLog(value = "继续当前导航")
    @ApiOperation(value = "继续当前导航", notes = "继续当前导航")
    @PostMapping(value = "/continueNav")
    public Object continueNavigation(String robotName) {
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
        if (robots.size() > 0) {
            boolean flag = robotMonitorService.continueNavigation(robots);
            String data = (String)redisUtil.stringWithGet("robot_task_resume_res");
            RobotResDTO resp = JSON.parseObject(data, RobotResDTO.class);
            if (resp.getRetCode() == 0) {
                return flag ? R.ok() : R.error("继续当前导航失败：" + robotName);
            } else {
                return R.error("继续当前导航失败,retCode:" + resp.getRetCode() + ",errMsg:" + resp.getErrMsg());
            }
        }
        return R.error(robotName + "不存在");
    }

    //    @ControllerLog(value = "暂停当前导航")
    @ApiOperation(value = "暂停当前导航", notes = "暂停当前导航")
    @PostMapping(value = "/pauseNav")
    public Object pauseNavigation(String robotName) {
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
        if (robots.size() > 0) {
            boolean flag = robotMonitorService.pauseNavigation(robots);
            String data = (String)redisUtil.stringWithGet("robot_task_pause_res");
            RobotResDTO resp = JSON.parseObject(data, RobotResDTO.class);
            if (resp.getRetCode() == 0) {
                return flag ? R.ok() : R.error("暂停当前导航失败：" + robotName);
            } else {
                return R.error("暂停当前导航失败,retCode:" + resp.getRetCode() + ",errMsg:" + resp.getErrMsg());
            }
        }
        return R.error(robotName + "不存在");
    }

    @ApiOperation(value = "导航到某个站点")
    @RequestMapping(value = "/runTask")
    public Object navigation(String robotName, String beginPort, String endPort) throws Exception {
        if (ObjectUtil.isNotEmpty(robotName) && ObjectUtil.isNotEmpty(endPort)) {
            if (Objects.equals(beginPort, "")) {
                List<RobotNavigation> robotNavigations = robotNavigationService.findByRobotName(robotName);
                if (robotNavigations.size() > 0) {
                    RobotNavigation robotNavigation = robotNavigations.get(0);
                    beginPort = robotNavigation.getBeginPort();
                } else {
                    beginPort = endPort;
                }
            }
            //封装机器人任务列表
            List<RobotRunDTO> subTaskList = new ArrayList<>();
            RobotRunDTO firstRunDTO = new RobotRunDTO();
            firstRunDTO.setVehicle(robotName);
            firstRunDTO.setStation(beginPort);
            firstRunDTO.setInstruction("机器人路径导航");
            subTaskList.add(firstRunDTO);

            RobotRunDTO secondRunDTO = new RobotRunDTO();
            secondRunDTO.setVehicle(robotName);
            secondRunDTO.setStation(endPort);
            secondRunDTO.setInstruction("机器人路径导航");
            subTaskList.add(secondRunDTO);
            //运行机器人导航任务
            boolean flag = robotWaybillService.runNavigationTask(null, robotName, subTaskList);
            if (flag) {
                List<RobotBasicInfo> list = robotBasicInfoService.findByName(robotName);
                if (list.size() > 0) {
                    //获取机器人实时状态信息
                    RobotInfoVO robotInfoVO = robotWaybillService.getRobotStatus(list.get(0));
                    if (robotInfoVO != null && robotInfoVO.isOnline()) {
                        //如果机器人出去可接单状态并且已经抢占控制权（可以运行WIFI任务）
                        if (!(Objects.equals(robotInfoVO.getOrderState(), Constant.ORDER_AVAILABLE_NUMBER)
                                && ControlStatusEnum.SEIZED_CONTROL.getCode().equals(robotInfoVO.getControlState()))) {
                            return R.error("没有WIFI运行控制权，请抢占WIFI控制权！");
                        }
                        return R.ok();
                    } else {
                        return R.error("当前机器人不在线！");
                    }
                } else {
                    return R.error("没有获取到有效机器人！");
                }
            } else {
                return R.error("请求路径规划算法失败！");
            }
        } else {
            return R.error("请选择导航终点！");
        }
    }

    @ApiOperation(value = "设置机器人的状态为空闲", notes = "设置机器人的状态为空闲")
    @PostMapping(value = "/setLeisure")
    public Object setLeisure(String robotName) {
        if (ObjectUtil.isNotEmpty(robotName)) {
            List<RobotBasicInfo> basicInfos = robotBasicInfoService.findByName(robotName);
            RobotBasicInfo robotBasicInfo = basicInfos.get(0);
            robotBasicInfo.setLeisure(1);
            boolean b = robotBasicInfoService.saveOrUpdate(robotBasicInfo);
            return b ? R.ok() : R.error("设置机器人的状态为空闲失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "异常机器人复位后解除该机器人的路径和点位占用", notes = "异常机器人复位后解除该机器人的路径和点位占用")
    @PostMapping(value = "/liftRobotLockPathAndPoint")
    public Object liftRobotLockPathAndPoint(String robotName) {
        if (ObjectUtil.isNotEmpty(robotName)) {
            robotWaybillService.liftRobotLockPathAndPoint(robotName);
            return R.ok();
        } else {
            return R.error("前端传递参数为空");
        }
    }

}
