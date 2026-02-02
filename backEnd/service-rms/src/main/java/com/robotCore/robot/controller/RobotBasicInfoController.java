package com.robotCore.robot.controller;

import com.alibaba.fastjson.JSON;
import com.beans.redis.RedisUtil;
import com.entity.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.config.ControllerLog;
import com.robotCore.common.config.OpsLogType;
import com.robotCore.common.constant.Constant;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.dto.RobotAttributeDTO;
import com.robotCore.robot.dto.RobotMapNameListDTO;
import com.robotCore.robot.dto.RobotStatusMapResDTO;
import com.robotCore.robot.entity.RobotAttribute;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.entityVo.*;
import com.robotCore.robot.service.RobotAttributeService;
import com.robotCore.robot.service.RobotBasicInfoService;
import com.robotCore.scheduing.common.enums.RobotMapPointAttrEnum;
import com.robotCore.scheduing.dto.GroupAndRobotDTO;
import com.robotCore.task.tcpCilent.TcpClientThread;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Des: 机器人基本信息管理控制类
 * @author: zxl
 * @date: 2023/4/17
 **/
@Slf4j
@RestController
@Api(value = "RobotBasicInfoController", description = "机器人基本信息管理")
@RequestMapping(value = "robotBasic")
public class RobotBasicInfoController extends BaseController {

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RobotAttributeService robotAttributeService;

    //    @ControllerLog(value = "根据分组名查询机器人列表")
    @ApiOperation(value = "根据分组名查询机器人列表", notes = "根据分组名查询机器人列表")
    @PostMapping(value = "/list")
    public Object list(String groupName) {
        List<RobotBasicInfo> list = robotBasicInfoService.findListByGroupName(groupName);
        return R.ok(list);
    }

    @ApiOperation(value = "校验是否绑定停放点和充电点", notes = "校验是否绑定停放点和充电点")
    @PostMapping(value = "/checkBindPoints")
    public Object checkParkAndChargePoint() {
        List<RobotBasicInfo> list = robotBasicInfoService.findListByGroupName(null);
        if (list.size() > 0) {
            for (RobotBasicInfo robotBasicInfo : list) {
                List<RobotAttribute> robotBindAttr1 = robotAttributeService.getRobotBindAttr(robotBasicInfo.getVehicleId(), RobotMapPointAttrEnum.PARK_POINT.getCode());
                if (robotBindAttr1.size() < 1) {
                    return R.error(robotBasicInfo.getVehicleId() + "没有绑定待命点！");
                }
                List<RobotAttribute> robotBindAttr2 = robotAttributeService.getRobotBindAttr(robotBasicInfo.getVehicleId(), RobotMapPointAttrEnum.CHARGE_POINT.getCode());
                if (robotBindAttr2.size() < 1) {
                    return R.error(robotBasicInfo.getVehicleId() + "没有绑定充电点！");
                }
            }
        }
        return R.ok();
    }

    @ApiOperation(value = "根据地图名称查询机器人列表", notes = "查询机器人列表")
    @PostMapping(value = "/robotList")
    public Object findListByMapName(@RequestBody RobotMapNameListDTO dto) {
        List<RobotBasicInfo> list = robotBasicInfoService.findListByMapNames(dto.getMapNames());
        return R.ok(list);
    }

    //    @ControllerLog(value = "获取全部机器人分组和分组下的机器人列表")
    @ApiOperation(value = "获取全部机器人分组和分组下的机器人列表", notes = "获取全部机器人分组和分组下的机器人列表")
    @PostMapping(value = "/getGroupAndRobot")
    public Object getGroupAndRobot() {
        List<GroupAndRobotDTO> groupAndRobots = robotBasicInfoService.getGroupAndRobot();
        return R.ok(groupAndRobots);
    }

    @ApiOperation(value = "获取机器人基本信息", notes = "获取机器人基本信息")
    @PostMapping(value = "/info")
    public Object getRobotBasicInfo(String robotIp) {
        if (ObjectUtil.isNotEmpty(robotIp)) {
            Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
            //如果已经连接机器人，则获取机器人服务端返回的实时信息
            if (map.containsKey(robotIp + PortConstant.ROBOT_STATUS_PORT)) {
                robotBasicInfoService.getRealTimeBaseInfo(robotIp);
                RobotBasicInfoVO robotBasicInfoVO = robotBasicInfoService.realTimeDataInit();
                return R.ok(robotBasicInfoVO);
            } else {
                //否则，则从数据库获取机器人对应的信息
                RobotBasicInfoVO offLineBaseInfo = robotBasicInfoService.getOffLineBaseInfo(robotIp);
                return R.ok(offLineBaseInfo);
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(value = "添加虚拟机器人", type = OpsLogType.ADDORUPDATE)
    @ApiOperation(value = "添加虚拟机器人", notes = "添加虚拟机器人")
    @PostMapping(value = "/saveVirtual")
    public Object saveVirtualRobot(String data) {
        //data={"robotIp":"192.168.13.117","robotName":"robotTest","groupName":"T1"}
        if (ObjectUtil.isNotEmpty(data)) {
            //重新把data由string解析为类对象
            RobotBasicInfo model = JSON.parseObject(data, RobotBasicInfo.class);
            model.setState(Constant.STATE_VALID);
            boolean b = robotBasicInfoService.saveOrUpdate(model);
            return b ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    //    @ControllerLog(value = "获取需要添加的真实机器人信息")
    @ApiOperation(value = "获取需要添加的真实机器人列表", notes = "当点击添加机器人按钮的加号")
    @PostMapping(value = "/getAddRobotInfo")
    public Object getAddRobotInfo() {
        List<RobotByAddVO> addList = robotBasicInfoService.getAddRobotInfo();
        return R.ok(addList);
    }

    //    @ControllerLog(value = "添加真实机器人")
    @ApiOperation(value = "添加真实机器人", notes = "添加真实机器人")
    @PostMapping(value = "/saveReal")
    public Object addRealRobots(@RequestBody String data) throws Exception {
        if (ObjectUtil.isNotEmpty(data)) {
            RobotsAddVO robotsAddVO = JSON.parseObject(data, RobotsAddVO.class);
            //同一分组下，如果地图名称不一致（MapMd5）,则不可以添加（同一分组下的机器人地图必须一致）
            String groupName = robotsAddVO.getGroupName();
            List<RobotBasicInfo> listByGroupName = robotBasicInfoService.findListByGroupName(groupName);
            boolean flag = true;
            for (int i = 0; i < robotsAddVO.getData().size(); i++) {
                RobotByAddVO robotByAddVO = robotsAddVO.getData().get(i);
                RobotBasicInfo model = robotBasicInfoService.setRobotValue(robotByAddVO, robotsAddVO);
                if (listByGroupName.size() > 0) {
                    RobotBasicInfo robotBasicInfo = listByGroupName.get(0);
                    if (!robotBasicInfo.getCurrentMap().equals(model.getCurrentMap())) {
                        return R.error("当前机器人地图版本与该分组下的机器人不一致！");
                    }
                }
                UserVO curUser = getCurUser();//当前创建人
                boolean b = robotBasicInfoService.save(model, curUser.getId(), curUser.getUserName());
                //只有每次往分组中添加机器人时，才保存机器人所有路径和站点信息
                robotBasicInfoService.getPathList(model.getVehicleId());
                flag = flag && b;
                if (flag) {
                    List<String> robots = (List<String>) redisUtil.stringWithGet("robots");
                    //如果不存在当前机器人，則添加
                    if (!robots.contains(robotByAddVO.getVehicleId())) {
                        robots.add(robotByAddVO.getVehicleId());
                        redisUtil.stringWithSet("robots", robots);
                    }
                }
            }
            return flag ? R.ok() : R.error("获取添加的真实机器人信息失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    //    @ControllerLog(type = OpsLogType.DELETE, value ="删除机器人")
    @ApiOperation(value = "根据Id删除机器人对象")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotBasicInfo model = robotBasicInfoService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean b = robotBasicInfoService.save(model, curUser.getId(), curUser.getUserName());
            if (b) {
                List<String> robots = (List<String>) redisUtil.stringWithGet("robots");
                for (int i = 0; i < robots.size(); i++) {
                    if (model.getVehicleId().equals(robots.get(i))) {
                        robots.remove(i);
                    }
                }
                redisUtil.stringWithSet("robots", robots);
            }
            return b ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取机器人对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotBasicInfo model = robotBasicInfoService.getById(id);
            return R.ok(model);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取机器人载入的地图以及储存的地图", notes = "获取机器人载入的地图以及储存的地图")
    @PostMapping(value = "/getMap")
    public Object getRobotMap(String robotIp) {
        if (ObjectUtil.isNotEmpty(robotIp)) {
            Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
            RobotMapInfoVO vo = new RobotMapInfoVO();
            //如果已经连接机器人，则获取机器人服务端返回的机器人中全部地图名称
            if (map.containsKey(robotIp + PortConstant.ROBOT_STATUS_PORT)) {
                RobotStatusMapResDTO dto = robotBasicInfoService.getRobotMapInfo(robotIp);
                if (dto.getRetCode() != 0) {
                    return R.error("查询机器人载入的地图以及储存的地图失败,retCode：" + dto.getRetCode() + ",errMsg:" + dto.getErrMsg());
                } else {
                    vo.setCurrentMap(dto.getCurrentMap());
                    vo.setMaps(dto.getMaps());
                    return R.ok(vo);
                }
            } else {
                //如果机器人不在线,返回空值
                return R.ok(vo);
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

//    @ApiOperation(value = "测试下载机器人地图接口", notes = "测试下载机器人地图接口")
//    @PostMapping(value = "/testDownloadRobotMap")
//    public Object testDownloadRobotMap(String robotIp) throws JsonProcessingException {
//        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
//        // 连接机器人配置端口
//        if (!map.containsKey(robotIp + PortConstant.ROBOT_CONFIGURATION_PORT)) {
//
//            int retryCount = 0;
//            do {
//                // 成功时或已达最大重试次数时退出循环
//                if (robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT) || retryCount >= 3) {
//                    break;
//                }
//                // 等待0.5秒后重试
//                CountDownLatch latch = new CountDownLatch(1);
//                try {
//                    if (!latch.await(500, TimeUnit.MILLISECONDS)) {
//                        log.info("连接机器人配置端口{}：{}任务状态超时，重试次数：{}", robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, retryCount + 1);
//                    }
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    // 处理中断逻辑
//                }
//                retryCount++;
//            } while (!Thread.currentThread().isInterrupted());
//            if (retryCount >= 3) return false;
//        }
//
//        // 构造下载地图请求指令
//        String mapName = "T1-13_test";
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("map_name", mapName);
//        String json = JSON.toJSONString(jsonObject);
//        String data = DataConvertUtil.convertStringToHex(json);
//        String dataLength = Integer.toHexString(data.length() / 2);
//        String instruction = "5A010000000000" + dataLength + "0FAB000000000000" + data;
//        // 发送请求指令
//        int retryCount = 0;
//        do {
//            EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, instruction);
//            // 成功时或已达最大重试次数时退出循环
//            if (result.isSuccess() || retryCount >= 5) {
//                log.info("地图{}下载请求指令发送成功", mapName);
//                break;
//            }
//            // 等待0.5秒后重试
//            CountDownLatch latch = new CountDownLatch(1);
//            try {
//                if (!latch.await(500, TimeUnit.MILLISECONDS)) {
//                    log.info("地图下载请求指令发送失败{}：{}，重试次数：{}", robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, retryCount + 1);
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                // 处理中断逻辑
//            }
//            retryCount++;
//        } while (!Thread.currentThread().isInterrupted());
//        return retryCount < 5;
//    }

    @ApiOperation(value = "从机器人下载地图", notes = "从机器人下载地图")
    @PostMapping(value = "/downloadMap")
    public Object downloadRobotMap() throws JsonProcessingException {
        //因为此处下载实时更新的图片太慢，此处关闭每次获取图片时立即刷新
//        boolean flag = robotBasicInfoService.downloadAllRobotMap();
        //从redis数据库加载数据
        List<String> mapList = robotBasicInfoService.getAllMapsData();
        return !mapList.isEmpty() ? R.ok(mapList) : R.error();
    }

    @ApiOperation(value = "修改机器人属性值", notes = "修改机器人属性值")
    @PostMapping(value = "/modifyAtt")
    public Object modifyAttribute(String vehicleId, Integer chargeOnly, Integer chargeNeed, Integer chargeOk, Integer chargeFull) {
        if (ObjectUtil.isNotEmpty(vehicleId) && ObjectUtil.isNotEmpty(chargeOnly) && ObjectUtil.isNotEmpty(chargeNeed)
                && ObjectUtil.isNotEmpty(chargeOk) && ObjectUtil.isNotEmpty(chargeFull)) {
            RobotBasicInfo robotBasicInfo = robotBasicInfoService.findByVehicleId(vehicleId);
            if (robotBasicInfo != null) {
                robotBasicInfo.setChargeOnly(chargeOnly);
                robotBasicInfo.setChargeNeed(chargeNeed);
                robotBasicInfo.setChargeOk(chargeOk);
                robotBasicInfo.setChargeFull(chargeFull);
                robotBasicInfoService.saveOrUpdate(robotBasicInfo);
                return R.ok();
            } else {
                return R.error("获取机器人信息失败");
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取机器人属性列表", notes = "获取机器人属性列表")
    @PostMapping(value = "/getAttributeList")
    public Object getAttributeList() {
        List<RobotAttributeDTO> list = new ArrayList<>();
        for (int i = 3; i < RobotMapPointAttrEnum.values().length + 3; i++) {
            RobotAttributeDTO attributeDTO = new RobotAttributeDTO();
            attributeDTO.setCode(Objects.requireNonNull(RobotMapPointAttrEnum.getByCode(i)).getCode());
            attributeDTO.setName(Objects.requireNonNull(RobotMapPointAttrEnum.getByCode(i)).getName());
            list.add(attributeDTO);
        }
        return R.ok(list);
    }

    @ApiOperation(value = "获取机器人已经绑定的属性列表", notes = "获取机器人已经绑定的属性列表")
    @PostMapping(value = "/attributedList")
    public Object getAttributedList(String vehicleId) {
        if (ObjectUtil.isNotEmpty(vehicleId)) {
            List<RobotAttribute> list = robotAttributeService.getRobotBindAttr(vehicleId);
            List<RobotAttributeVO> robotAttributeVOS = new ArrayList<>();
            for (RobotAttribute robotAttribute : list) {
                RobotAttributeVO vo = new RobotAttributeVO();
                vo.setId(robotAttribute.getId());
                vo.setAttributeCode(robotAttribute.getAttributeCode());
                vo.setPoint(robotAttribute.getPoint());
                vo.setAttributeName(Objects.requireNonNull(RobotMapPointAttrEnum.getByCode(robotAttribute.getAttributeCode())).getName());
                robotAttributeVOS.add(vo);
            }
            return R.ok(robotAttributeVOS);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取全部已经绑定的属性列表", notes = "获取全部已经绑定的属性列表")
    @PostMapping(value = "/allAttributedList")
    public Object getAllAttributedList() {
        List<RobotAttribute> list = robotAttributeService.getRobotBindAttr();
        List<RobotAttributeVO> robotAttributeVOS = new ArrayList<>();
        for (RobotAttribute robotAttribute : list) {
            RobotAttributeVO vo = new RobotAttributeVO();
            vo.setId(robotAttribute.getId());
            vo.setAttributeCode(robotAttribute.getAttributeCode());
            vo.setPoint(robotAttribute.getPoint());
            vo.setAttributeName(Objects.requireNonNull(RobotMapPointAttrEnum.getByCode(robotAttribute.getAttributeCode())).getName());
            robotAttributeVOS.add(vo);
        }
        return R.ok(robotAttributeVOS);
    }
}