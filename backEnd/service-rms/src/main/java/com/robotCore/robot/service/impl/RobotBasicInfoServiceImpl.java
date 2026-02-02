package com.robotCore.robot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beans.redis.RedisUtil;
import com.entity.EntityResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robotCore.common.constant.Constant;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.constant.ProtocolConstant;
import com.robotCore.common.utils.DataConvertUtil;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.dto.RobotBasicInfoResDTO;
import com.robotCore.robot.dto.RobotBatteryResDTO;
import com.robotCore.robot.dto.RobotLocResDTO;
import com.robotCore.robot.dto.RobotStatusMapResDTO;
import com.robotCore.robot.entity.*;
import com.robotCore.robot.entityVo.RobotBasicInfoVO;
import com.robotCore.robot.entityVo.RobotByAddVO;
import com.robotCore.robot.entityVo.RobotsAddVO;
import com.robotCore.robot.mapper.RobotBasicInfoDao;
import com.robotCore.robot.service.*;
import com.robotCore.scheduing.common.enums.RobotMapPointAttrEnum;
import com.robotCore.scheduing.common.enums.RobotStatusEnum;
import com.robotCore.scheduing.common.utils.BezierCurveLengthUtil;
import com.robotCore.scheduing.common.utils.HttpClientUtil;
import com.robotCore.scheduing.dto.*;
import com.robotCore.scheduing.service.RobotWaybillService;
import com.robotCore.scheduing.vo.RobotAbnormalVO;
import com.robotCore.scheduing.vo.RobotMonitorVO;
import com.robotCore.scheduing.vo.RobotPathVO;
import com.robotCore.task.core.ProtocolConvert;
import com.robotCore.task.tcpCilent.TcpClientThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.robotCore.common.constant.Constant.STATE_VALID;

/**
 * @Description: 描述
 * @Author: Created by zxl on 2023/4/17.
 */
@Slf4j
@Service
public class RobotBasicInfoServiceImpl extends ServiceImpl<RobotBasicInfoDao, RobotBasicInfo> implements RobotBasicInfoService {

    @Autowired
    private RobotConnectService robotConnectService;

    @Autowired
    private RobotGroupService robotGroupService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RobotAttributeService robotAttributeService;

    @Autowired
    private RobotDmsService robotDmsService;

    @Value("${dms.port}")
    private Integer dmsPort;

    private final RobotWaybillService robotWaybillService;

    @Value("${data.serverIP}")
    private String currentServerIp;

    public RobotBasicInfoServiceImpl(@Lazy RobotWaybillService robotWaybillService) {
        this.robotWaybillService = robotWaybillService;
    }

    /**
     * 根据分组名，获取当前分组列表下的所有机器人
     *
     * @param groupName
     * @return
     */
    @Override
    public List<RobotBasicInfo> findListByGroupName(String groupName) {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(groupName), RobotBasicInfo::getGroupName, groupName);
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByAsc(RobotBasicInfo::getId);
        //返回list类型对象列表
        return list(wrapper);
    }

    @Override
    public List<RobotBasicInfo> findListAll() {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByDesc(RobotBasicInfo::getId);
        //返回list类型对象列表
        return list(wrapper);
    }

    @Override
    public List<RobotBasicInfo> findListByMapName(String robotName, String mapName) {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RobotBasicInfo::getCurrentMap, mapName);
        wrapper.lambda().ne(RobotBasicInfo::getVehicleId, robotName);
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByDesc(RobotBasicInfo::getId);
        //返回list类型对象列表
        return list(wrapper);
    }

    @Override
    public List<RobotBasicInfo> findListByMapNames(List<String> mapNames) {
        List<RobotBasicInfo> robotBasicInfoList = new ArrayList<>();
        Set<String> set = new HashSet<>(mapNames);
        for (String mapName : set) {
            QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID);
            wrapper.lambda().eq(RobotBasicInfo::getCurrentMap, mapName);
            List<RobotBasicInfo> list = list(wrapper);
            robotBasicInfoList.addAll(list);
        }
        return robotBasicInfoList;
    }

    /**
     * 获取全部机器人分组和分组下的机器人列表
     *
     * @return
     */
    @Override
    public List<GroupAndRobotDTO> getGroupAndRobot() {
        List<GroupAndRobotDTO> groupAndRobotDTOS = new ArrayList<>();
        List<RobotGroup> robotGroups = robotGroupService.findList();
        for (int i = 0; i < robotGroups.size(); i++) {
            List<RobotBasicInfoVO> robotVOS = new ArrayList<>();
            GroupAndRobotDTO dto = new GroupAndRobotDTO();
            Set<String> mapNames = new HashSet<>();
            List<RobotBasicInfo> robotBasicInfos = findListByGroupName(robotGroups.get(i).getGroupName());
            for (int j = 0; j < robotBasicInfos.size(); j++) {
                Map<String, RobotMonitorVO> robotInfoVOMap = (Map<String, RobotMonitorVO>) redisUtil.stringWithGet("robot_push_less_res");
                RobotMonitorVO robotMonitorVO = robotInfoVOMap.get(robotBasicInfos.get(j).getVehicleId());
                RobotBasicInfoVO robotBasicInfoVO = new RobotBasicInfoVO();
                robotBasicInfoVO.setExistAbnormal(isExistAbnormal(robotMonitorVO, robotBasicInfos.get(j)));
                robotBasicInfoVO.setId(robotBasicInfos.get(j).getId());
                robotBasicInfoVO.setCurrentMapMd5(robotBasicInfos.get(j).getCurrentMapMd5());
                robotBasicInfoVO.setBatteryLevel(robotMonitorVO.getBatteryLevel());
                robotBasicInfoVO.setCurrentIp(robotBasicInfos.get(j).getCurrentIp());
                robotBasicInfoVO.setVehicleId(robotMonitorVO.getVehicleId());
                robotBasicInfoVO.setRobotNote(robotMonitorVO.getRobotNote());
                robotBasicInfoVO.setModel(robotBasicInfos.get(j).getModel());
                robotBasicInfoVO.setVersion(robotBasicInfos.get(j).getVersion());
                robotBasicInfoVO.setDspVersion(robotBasicInfos.get(j).getDspVersion());
                robotBasicInfoVO.setCurrentMap(robotMonitorVO.getCurrentMap());
                robotBasicInfoVO.setBatteryLevel((int) (robotMonitorVO.getBatteryLevel()));
                robotBasicInfoVO.setCharging(robotMonitorVO.isCharging());
                robotBasicInfoVO.setConfidence(robotMonitorVO.getConfidence());
                Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
                if (map.containsKey(robotBasicInfos.get(j).getCurrentIp() + PortConstant.ROBOT_STATUS_PORT)) {
                    robotBasicInfoVO.setCharging(robotMonitorVO.isCharging());
                    robotBasicInfoVO.setOnline(true);
                } else {
                    robotBasicInfoVO.setCharging(false);
                    robotBasicInfoVO.setOnline(false);
                }
                robotBasicInfoVO.setChargeOnly(robotBasicInfos.get(j).getChargeOnly());
                robotBasicInfoVO.setChargeNeed(robotBasicInfos.get(j).getChargeNeed());
                robotBasicInfoVO.setChargeOk(robotBasicInfos.get(j).getChargeOk());
                robotBasicInfoVO.setChargeFull(robotBasicInfos.get(j).getChargeFull());
                //把一个分组下的机器人的地图名称存放到一个set集合中
                mapNames.add(robotBasicInfos.get(j).getCurrentMap());
                robotVOS.add(robotBasicInfoVO);
            }
            //分组中所有机器人地图是否一致
            if (robotBasicInfos.size() == 0) {
                dto.setMapAbnormal(true);
            } else {
                dto.setMapAbnormal(robotBasicInfos.size() != mapNames.size());
            }
            dto.setGroupId(robotGroups.get(i).getId());
            dto.setGroupName(robotGroups.get(i).getGroupName());
            dto.setGroupDes(robotGroups.get(i).getGroupDes());
            dto.setPooling(robotGroups.get(i).getPooling());
            dto.setRobots(robotVOS);
            groupAndRobotDTOS.add(dto);
        }
        return groupAndRobotDTOS;
    }

    /**
     * 判断机器人是否存在异常
     *
     * @param robotMonitorVO
     * @return
     */
    @Override
    public boolean isExistAbnormal(RobotMonitorVO robotMonitorVO, RobotBasicInfo robotBasicInfo) {
        RobotAbnormalVO vo = new RobotAbnormalVO();
        //机器人电量异常判断
        RobotAbnormalDTO batteryLevelDto = new RobotAbnormalDTO();
        if (robotMonitorVO.getBatteryLevel() < robotBasicInfo.getChargeOnly()) {
            batteryLevelDto.setAbnormal(true);
            batteryLevelDto.setDescription("机器人电量过低");
        } else {
            batteryLevelDto.setAbnormal(false);
            batteryLevelDto.setDescription("机器人电量正常");
        }
        //机器人急停按钮判断
        RobotAbnormalDTO scramDto = new RobotAbnormalDTO();
        if (!robotMonitorVO.isEmergency()) {
            scramDto.setAbnormal(true);
            scramDto.setDescription("机器人处于急停状态");
        } else {
            scramDto.setAbnormal(false);
            scramDto.setDescription("急停按钮正常");
        }
        return false;
    }

    @Override
    public List<RobotBasicInfo> getEffectiveRobots(String vehicleId, String groupName) {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(vehicleId), RobotBasicInfo::getVehicleId, vehicleId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(groupName), RobotBasicInfo::getGroupName, groupName);
        //获取机器人的运行状态为空闲
        wrapper.lambda().eq(RobotBasicInfo::getLeisure, STATE_VALID);
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByDesc(RobotBasicInfo::getId);

        //返回list类型对象列表
        return list(wrapper);
    }

    @Override
    public List<RobotBasicInfo> findByName(String vehicleId) {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(vehicleId), RobotBasicInfo::getVehicleId, vehicleId);
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByDesc(RobotBasicInfo::getId);
        //返回list类型对象列表
        return list(wrapper);
    }

    @Override
    public RobotBasicInfo findByVehicleId(String vehicleId) {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(vehicleId), RobotBasicInfo::getVehicleId, vehicleId);
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByDesc(RobotBasicInfo::getId);
        //返回list类型对象列表
        List<RobotBasicInfo> list = list(wrapper);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public RobotBasicInfo findByIp(String robotIp) {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotIp), RobotBasicInfo::getCurrentIp, robotIp);
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByDesc(RobotBasicInfo::getId);
        return getOne(wrapper);
    }

    /**
     * 获取需要添加的机器人基本信息
     *
     * @return
     */
    @Override
    public List<RobotByAddVO> getAddRobotInfo() {
        List<RobotConnect> robotConnects = robotConnectService.findList();
        List<RobotBasicInfo> robotBasicInfos = findListAll();
        List<String> robotIpList = new ArrayList<>();
        for (int j = 0; j < robotBasicInfos.size(); j++) {
            RobotBasicInfo robotBasicInfo = robotBasicInfos.get(j);
            robotIpList.add(robotBasicInfo.getCurrentIp());
        }
        //需要添加的机器人列表
        List<RobotByAddVO> addList = new ArrayList<>();
        for (RobotConnect robotConnect : robotConnects) {
            //连接机器人的IP
            String connectIp = robotConnect.getCurrentIp();
            //如果机器人列表中不存在已经添加的设备，则获取该机器人设备信息
            if (!robotIpList.contains(connectIp)) {
                RobotByAddVO vo = new RobotByAddVO();
                vo.setCurrentIp(robotConnect.getCurrentIp());
                vo.setId(robotConnect.getId());
                vo.setVehicleId(robotConnect.getVehicleId());
                vo.setRobotNote(robotConnect.getRobotNote());
                vo.setVersion(robotConnect.getVersion());
                vo.setDspVersion(robotConnect.getDspVersion());
                vo.setModel(robotConnect.getModel());
                vo.setCurrentMap(robotConnect.getCurrentMap());
                vo.setCurrentMapMd5(robotConnect.getCurrentMapMd5());
                vo.setRobotType(robotConnect.getRobotType());
                addList.add(vo);
            }
        }
        return addList;
    }

    /**
     * 根据ip获取机器人在线实时基本信息
     *
     * @param robotIp
     * @return
     */
    @Override
    public void getRealTimeBaseInfo(String robotIp) {
        //查询机器人信息
        TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_STATUS_PORT, ProtocolConstant.ROBOT_BASIC_INFO);
        //查询机器人电量信息
        TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_STATUS_PORT, ProtocolConstant.ROBOT_STATUS_BATTERY);
        //查询机器人的置信度
        TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_STATUS_PORT, ProtocolConstant.ROBOT_STATUS_LOC);
    }

    /**
     * 从redis中获取实时数据，并赋值给机器人
     */
    @Override
    public RobotBasicInfoVO realTimeDataInit() {
        //获取机器人ip、id、名称等基本信息
        String baseInfoRes = (String) redisUtil.stringWithGet("robot_status_info_res");
        RobotBasicInfoResDTO robotBasicInfo = JSON.parseObject(baseInfoRes, RobotBasicInfoResDTO.class);
        //获取机器人电量信息以及是否在充电
        String batteryInfoRes = (String) redisUtil.stringWithGet("robot_status_battery_res");
        RobotBatteryResDTO robotBattery = JSON.parseObject(batteryInfoRes, RobotBatteryResDTO.class);
        //获取机器人置信度信息
        String locInfoRes = (String) redisUtil.stringWithGet("robot_status_loc_res");
        RobotLocResDTO robotLoc = JSON.parseObject(locInfoRes, RobotLocResDTO.class);
        RobotBasicInfoVO robotBasicInfoVO = new RobotBasicInfoVO();
        robotBasicInfoVO.setCurrentIp(robotBasicInfo.getCurrentIp());
        robotBasicInfoVO.setId(robotBasicInfo.getId());
        robotBasicInfoVO.setVehicleId(robotBasicInfo.getVehicleId());
        robotBasicInfoVO.setRobotNote(robotBasicInfo.getRobotNote());
        robotBasicInfoVO.setModel(robotBasicInfo.getModel());
        robotBasicInfoVO.setVersion(robotBasicInfo.getVersion());
        robotBasicInfoVO.setDspVersion(robotBasicInfo.getDspVersion());
        robotBasicInfoVO.setCurrentMap(robotBasicInfo.getCurrentMap());
        robotBasicInfoVO.setCurrentMapMd5(robotBasicInfo.getCurrentMapMd5());
        robotBasicInfoVO.setBatteryLevel((int) (robotBattery.getBatteryLevel()));
        robotBasicInfoVO.setCharging(robotBattery.isCharging());
        robotBasicInfoVO.setConfidence(robotLoc.getConfidence());
        robotBasicInfoVO.setCharging(robotBattery.isCharging());
        return robotBasicInfoVO;
    }

    /**
     * 根据ip获取机器人离线基本信息
     *
     * @param robotIp
     * @return
     */
    @Override
    public RobotBasicInfoVO getOffLineBaseInfo(String robotIp) {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotIp), RobotBasicInfo::getCurrentIp, robotIp);
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByDesc(RobotBasicInfo::getId);
        List<RobotBasicInfo> list = list(wrapper);
        RobotBasicInfoVO robotBaseInfoVO = new RobotBasicInfoVO();
        if (list.size() > 0) {
            RobotBasicInfo robotBasicInfo = list.get(0);
            robotBaseInfoVO.setCurrentIp(robotIp);
            robotBaseInfoVO.setId(robotBasicInfo.getId());
            robotBaseInfoVO.setVehicleId(robotBasicInfo.getVehicleId());
            robotBaseInfoVO.setRobotNote(robotBasicInfo.getRobotNote());
            robotBaseInfoVO.setModel(robotBasicInfo.getModel());
            robotBaseInfoVO.setVersion(robotBasicInfo.getVersion());
            robotBaseInfoVO.setDspVersion(robotBasicInfo.getDspVersion());
            robotBaseInfoVO.setCurrentMap(robotBasicInfo.getCurrentMap());
            robotBaseInfoVO.setCurrentMapMd5(robotBasicInfo.getCurrentMapMd5());
            robotBaseInfoVO.setBatteryLevel(robotBasicInfo.getBatteryLevel());
            robotBaseInfoVO.setConfidence(robotBasicInfo.getConfidence());
            //离线状态，设置为不充电
            robotBaseInfoVO.setCharging(false);
            robotBaseInfoVO.setGroupName(robotBasicInfo.getGroupName());
        }
        return robotBaseInfoVO;
    }

    /**
     * 设置机器人的属性值
     *
     * @param robotByAddVO
     * @param robotsAddVO
     * @return
     */
    @Override
    public RobotBasicInfo setRobotValue(RobotByAddVO robotByAddVO, RobotsAddVO robotsAddVO) {
        RobotBasicInfo model = new RobotBasicInfo();
        model.setVehicleId(robotByAddVO.getVehicleId());
        model.setCurrentIp(robotByAddVO.getCurrentIp());
        model.setId(robotByAddVO.getId());
        model.setRobotNote(robotByAddVO.getRobotNote());
        model.setModel(robotByAddVO.getModel());
        model.setVersion(robotByAddVO.getVersion());
        model.setDspVersion(robotByAddVO.getDspVersion());
        model.setCurrentMap(robotByAddVO.getCurrentMap());
        model.setCurrentMapMd5(robotByAddVO.getCurrentMapMd5());
        model.setRobotType(robotByAddVO.getRobotType());
        model.setState(Constant.STATE_VALID);
        //设置机器人的组别名称
        model.setGroupName(robotsAddVO.getGroupName());
        //设置机器人的默认阈值
        model.setChargeOnly(-1);
        model.setChargeNeed(-1);
        model.setChargeFull(90);
        model.setChargeOk(50);
        //由于把机器人可接单的功能按钮去掉，因此这里默认设置机器人为可接单状态
        model.setOrderState(RobotStatusEnum.ORDER_AVAILABLE.getCode());
        //设置机器人的状态为空闲
        model.setLeisure(Constant.STATE_VALID);
        //设置机器人不充电
        model.setCharge(Constant.STATE_INVALID);
        return model;
    }

    /**
     * 根据ip获取机器人载入的地图以及存储的地图
     *
     * @param robotIp
     */
    @Override
    public RobotStatusMapResDTO getRobotMapInfo(String robotIp) {
        //查询机器人载入的地图以及存储的地图
        TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_STATUS_PORT, ProtocolConstant.ROBOT_STATUS_MAP);
        String mapsRes = (String) redisUtil.stringWithGet("robot_status_map_res");
        return JSON.parseObject(mapsRes, RobotStatusMapResDTO.class);
    }

    /**
     * <根据机器人ip从机器人控制器下载地图> leo update
     *
     * @param robotIp 机器人ip
     */
    @Override
    public boolean downloadRobotMap(String robotIp) {
        /*
         * 请求更新该机器人全部地图
         */

        // 根据ip获取连接的机器人信息
        RobotConnect equipment = robotConnectService.findByIp(robotIp);
        // 连接端口检查
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        if (!map.containsKey(robotIp + PortConstant.ROBOT_CONFIGURATION_PORT)) {
            //连接机器人配置端口
            int retryCount = 0;
            do {
                // 成功时或已达最大重试次数时退出循环
                if (connectTcp(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT) || retryCount >= 5) {
                    break;
                }
                // 等待0.5秒后重试
                CountDownLatch latch = new CountDownLatch(1);
                try {
                    if (!latch.await(500, TimeUnit.MILLISECONDS)) {
                        log.info("连接机器人{}配置端口{}：{}任务状态超时，重试次数：{}", equipment.getVehicleId(), robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, retryCount + 1);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    // 处理中断逻辑
                }
                retryCount++;
            } while (!Thread.currentThread().isInterrupted());
            if (retryCount >= 5) {
                log.error("机器人{}无法连接配置端口{}：{}", equipment.getVehicleId(), robotIp, PortConstant.ROBOT_CONFIGURATION_PORT);
                return false;
            }
        }
        // 获取机器人存储的全部地图名称
        String[] mapNames = {};
        try {
            RobotStatusMapResDTO robotStatusMapResDTO = getRobotMapInfo(robotIp);
            mapNames = robotStatusMapResDTO.getMaps();
            if (mapNames.length == 0) {
                log.error("机器人{}没有可用的地图，请进行检查", equipment.getVehicleId());
                return false;
            }
        } catch (Exception e) {
            log.error("获取机器人{}地图信息失败，原因：{}", equipment.getVehicleId(), e.getMessage());
            return false;
        }
        boolean isAllMapUpdate = true;
        for (String mapName : mapNames) {
            // 构造下载地图请求指令
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("map_name", mapName);
            String jsonString = JSON.toJSONString(jsonObject);
            String data = DataConvertUtil.convertStringToHex(jsonString);
            String dataLength = String.format("%04x", data.length() / 2);
            String instruction = "5A0100000000" + dataLength + "0FAB000000000000" + data;
            // 发送请求指令
            int retryCount = 0;
            do {
                EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, instruction);
                // 成功时或已达最大重试次数时退出循环
                if (result.isSuccess() || retryCount >= 5) {
                    log.info("地图{}下载请求指令发送成功", mapName);
                    break;
                }
                // 等待0.5秒后重试
                CountDownLatch latch = new CountDownLatch(1);
                try {
                    if (!latch.await(500, TimeUnit.MILLISECONDS)) {
                        log.info("地图下载请求指令发送失败{}：{}，重试次数：{}", robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, retryCount + 1);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    // 处理中断逻辑
                }
                retryCount++;
            } while (!Thread.currentThread().isInterrupted());
            if (retryCount >= 5) {
                isAllMapUpdate = false;
            }
        }
        return isAllMapUpdate;

    }

    /**
     * 下载所有机器人地图
     *
     * @return
     */
    @Override
    public boolean downloadAllRobotMap() {
        List<RobotGroup> list = robotGroupService.findList();
        boolean flag = true;
        for (RobotGroup robotGroup : list) {
            List<RobotBasicInfo> basicInfoServiceList = findListByGroupName(robotGroup.getGroupName());
            if (basicInfoServiceList.size() > 0) {
                String currentIp = basicInfoServiceList.get(0).getCurrentIp();
                Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
                //如果当前ip的机器人已经连接，则在地图上加入该机器人的地图信息
                if (map.containsKey(currentIp + PortConstant.ROBOT_STATUS_PORT) && robotConnectService.isOnline(currentIp)) {
                    boolean b = downloadRobotMap(currentIp);
                    //如果存在一次下载地图失败，则flag一直为false
                    flag = flag && b;
                }
            }
        }
        return flag;
    }

    /**
     * 下载当前机器人地图
     *
     * @return
     */
    @Override
    public boolean downloadCurrentRobotMap(String currentIp) {
        boolean flag = false;
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip的机器人已经连接，则在地图上加入该机器人的地图信息
        if (map.containsKey(currentIp + PortConstant.ROBOT_STATUS_PORT) && robotConnectService.isOnline(currentIp)) {
            flag = downloadRobotMap(currentIp);
        }
        return flag;
    }

    /**
     * 获取分组中机器人所有地图数据
     *
     * @return
     */
    @Override
    public List<String> getAllMapsData() throws JsonProcessingException {
        //获取redis中存储的机器人地图信息
        Map<String, String> downloadMapRes = (Map<String, String>) redisUtil.stringWithGet("robot_config_downloadmap_res");
        //获取redis存储的当前在分组中存在的机器人名字
        List<String> robots = (List<String>) redisUtil.stringWithGet("robots");
        Set<String> mapNameList = new HashSet<>();
        //获取当前在分组中所有机器人的地图名字的集合
        for (String robot : robots) {
            List<RobotBasicInfo> robotBasicInfoList = findByName(robot);
            if (robotBasicInfoList.size() > 0) {
                mapNameList.add(robotBasicInfoList.get(0).getCurrentMap());
            }
        }
        //获取当前在分组中所有机器人的地图数据的集合
        List<String> mapList = new ArrayList<>();
        for (String s : mapNameList) {
            Set<String> mapNames = downloadMapRes.keySet();
            if (mapNames.contains(s)) {
                String mapText = downloadMapRes.get(s);
                RobotMapDTO dto = JSON.parseObject(mapText, RobotMapDTO.class);
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(dto);
                mapList.add(jsonString);
            }
        }
        return mapList;
    }

    /**
     * 根据ip和端口号连接机器人
     *
     * @param robotIp
     * @param port
     * @return
     */
    @Override
    public boolean connectTcp(String robotIp, int port) {
        EntityResult result = null;
        try {
            result = TcpClientThread.start(robotIp, port);
            //为了防止在给机器人发送指令的时候，机器人端口还未建立连接，因此给改线程加了延迟
            Thread.sleep(300L);
//            log.info("TCP客户端启动结果：{}", JSON.toJSON(result));
        } catch (Exception e) {
            log.error("nettyStart:{}", e.getMessage());
        }
        if (result != null) {
            return result.isSuccess();
        } else {
            return false;
        }
    }

    @Override
    public boolean save(RobotBasicInfo model, Long userId, String userName) {
        if (model.getId() == null) {
            model.setCreateId(userId);
            model.setCreateUser(userName);
            model.setCreateTime(new Timestamp(new Date().getTime()));
        } else {
            model.setUpdateId(userId);
            model.setUpdateUser(userName);
            model.setUpdateTime(new Timestamp(new Date().getTime()));
        }
        return saveOrUpdate(model);
    }

    /**
     * 让机器人执行从A点到B点的导航任务
     *
     * @param robotName
     * @param beginPort
     * @param endPort
     * @return
     */
    @Override
    public boolean runRobotTask(String robotName, String beginPort, String endPort) {
        RobotBasicInfo robot = findByName(robotName).get(0);
        String robotIp = robot.getCurrentIp();
        String convertString = "{\"source_id\":\"" + beginPort + "\",\"id\":\"" + endPort + "\"}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        String instruction = "5A010001000000" + dataLength + "0BEB000000000000" + data;
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        if (!map.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人其它端口
            connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        return result.isSuccess();
    }

    /**
     * 机器人根据电量自动充电
     *
     * @param vehicleId
     */
    @Override
    public void autoCharge(String vehicleId) throws Exception {
        //获取数据库中存储的机器人信息
        RobotBasicInfo robotBasicInfo = findByName(vehicleId).get(0);
        //获取存在redis中的机器人实时信息
        Map<String, RobotMonitorVO> robotInfoVOMap = (Map<String, RobotMonitorVO>) redisUtil.stringWithGet("robot_push_less_res");
        //获取当前机器人绑定的充电点
        List<RobotAttribute> chargePoints = robotAttributeService.getRobotBindAttr(vehicleId, RobotMapPointAttrEnum.CHARGE_POINT.getCode());
        //获取当前机器人绑定的停放点
        List<RobotAttribute> parkPoints = robotAttributeService.getRobotBindAttr(vehicleId, RobotMapPointAttrEnum.PARK_POINT.getCode());
        RobotMonitorVO robotMonitorVO = robotInfoVOMap.get(vehicleId);
        if (chargePoints.size() > 0 && !robotMonitorVO.isCharging()) {
            if (robotBasicInfo.getChargeOnly() <= robotBasicInfo.getChargeNeed()) {
                //机器人在电量下降到此值时，需要前往充电桩进行充电，但是在充电过程中可以接单
                if (robotBasicInfo.getChargeNeed() > robotMonitorVO.getBatteryLevel() && robotBasicInfo.getChargeNeed() >= 0) {
                    robotWaybillService.runChargeTask(vehicleId, robotMonitorVO.getCurrentStation(), chargePoints.get(0).getPoint());
//                    runRobotTask(vehicleId,robotMonitorVO.getCurrentStation(),chargePoints.get(0).getPoint());
                } else if (robotBasicInfo.getChargeNeed() == -1 && robotMonitorVO.getBatteryLevel() == 0) {
                    robotWaybillService.runChargeTask(vehicleId, robotMonitorVO.getCurrentStation(), chargePoints.get(0).getPoint());
//                    runRobotTask(vehicleId,robotMonitorVO.getCurrentStation(),chargePoints.get(0).getPoint());
                }
            } else {
                //机器人在电量下降到此值时，需要前往充电桩进行充电，并且在充电过程不可以接单
                if (robotBasicInfo.getChargeOnly() > robotMonitorVO.getBatteryLevel() && robotBasicInfo.getChargeOnly() >= 0) {
                    robotWaybillService.runChargeTask(vehicleId, robotMonitorVO.getCurrentStation(), chargePoints.get(0).getPoint());
//                    runRobotTask(vehicleId,robotMonitorVO.getCurrentStation(),chargePoints.get(0).getPoint());
                } else if (robotBasicInfo.getChargeOnly() == -1 && robotMonitorVO.getBatteryLevel() == 0) {
                    robotWaybillService.runChargeTask(vehicleId, robotMonitorVO.getCurrentStation(), chargePoints.get(0).getPoint());
//                    runRobotTask(vehicleId,robotMonitorVO.getCurrentStation(),chargePoints.get(0).getPoint());
                }
            }
        }
        if (chargePoints.size() > 0 && parkPoints.size() > 0 && robotMonitorVO.isCharging()) {
            if (robotBasicInfo.getChargeOk() <= robotBasicInfo.getChargeFull()) {
                //机器人在充电时，电量达到此值后可以离开充电点
                if (robotBasicInfo.getChargeFull() < robotMonitorVO.getBatteryLevel()) {
                    robotWaybillService.runChargeTask(vehicleId, chargePoints.get(0).getPoint(), parkPoints.get(0).getPoint());
//                    runRobotTask(vehicleId,chargePoints.get(0).getPoint(),parkPoints.get(0).getPoint());
                }
            } else {
                //机器人在充电时，电量达到此值后可以离开充电点
                if (robotBasicInfo.getChargeOk() < robotMonitorVO.getBatteryLevel()) {
                    robotWaybillService.runChargeTask(vehicleId, chargePoints.get(0).getPoint(), parkPoints.get(0).getPoint());
//                    runRobotTask(vehicleId,chargePoints.get(0).getPoint(),parkPoints.get(0).getPoint());
                }
            }
        }
    }

    /**
     * 光通信自动充电
     * @param vehicleId
     * @param dmsParkPoint
     */
    @Override
    public void autoDmsCharge(String vehicleId, String dmsParkPoint) throws InterruptedException, IOException {
        String str = "{\"task_list_name\":\"" + vehicleId + "_" + dmsParkPoint + "\",\"with_robot_status\":true}";
        String data = DataConvertUtil.convertStringToHex(str);
        String dataLength = Integer.toHexString(data.length() / 2);
        String instruction = "5A010001000000" + dataLength + "0C1D000000000000" + data;
        List<RobotBasicInfo> robots = findByName(vehicleId);
        RobotDms robotDms;
        //获取机器人光通信站充电点属性
        List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(vehicleId, RobotMapPointAttrEnum.CHARGE_POINT.getCode());
        String chargePoint = robotBindAttr.get(0).getPoint();
        //如果机器人没有充电
        if (robots.get(0).getCharge() == 0) {
            //获取当前站点的光通信站信息
            robotDms = robotDmsService.getDmsByDmsPoint(dmsParkPoint);
        } else {
            //获取当前站点的光通信站信息
            robotDms = robotDmsService.getDmsByDmsPoint(chargePoint);
        }
        String dmsIp = robotDms.getDmsIp();
        //连接获取机器人导航状态的端口
        if (connectTcp(dmsIp, dmsPort)) {
            Thread.sleep(2000);
            //获取机器人任务链信息
            EntityResult result = TcpClientThread.sendHexMsg(dmsIp, dmsPort, instruction);
            //获取任务链信息成功
            if (result.isSuccess()) {
                //获取机器人信息
                RobotBasicInfo robotBasicInfo = findByName(vehicleId).get(0);
                //获取当前机器人绑定的充电点
                List<RobotAttribute> chargePoints = robotAttributeService.getRobotBindAttr(vehicleId, RobotMapPointAttrEnum.CHARGE_POINT.getCode());
                //获取机器人任务链信息,为了防止任务链信息还没有在redis中写入，这里加入延迟
                Thread.sleep(3000);
                Map<String, String> taskListMapStatus = (Map<String, String>) redisUtil.stringWithGet("robot_tasklist_status_res");
                String taskListStatus = taskListMapStatus.get(vehicleId);
                if (taskListStatus != null) {
                    RobotTaskListResDTO model = JSON.parseObject(taskListStatus, RobotTaskListResDTO.class);
                    //获取电池电量
                    float batteryLevel = model.getRobotStatus().getBatteryLevel() * 100;
                    //当前电池电量低于需要充电最低值 && 机器人没有充电
                    if (chargePoints.size() > 0 && robotBasicInfo.getChargeNeed() > batteryLevel && robots.get(0).getCharge() == 0) {
                        //调用机器人充电
                        RobotTaskRequestDTO reqDto = new RobotTaskRequestDTO();
                        reqDto.setTaskNumber("task00008");
                        List<RobotTaskRequestDTO.SubParameter> taskParameter = new ArrayList<>();

                        RobotTaskRequestDTO.SubParameter robotNameParameter = new RobotTaskRequestDTO.SubParameter();
                        robotNameParameter.setName("robotName");
                        robotNameParameter.setValue(vehicleId);
                        robotNameParameter.setType("字符串");

                        RobotTaskRequestDTO.SubParameter startPointParameter = new RobotTaskRequestDTO.SubParameter();
                        startPointParameter.setName("startPoint");
                        startPointParameter.setValue(dmsParkPoint);
                        startPointParameter.setType("字符串");

                        RobotTaskRequestDTO.SubParameter endPointParameter = new RobotTaskRequestDTO.SubParameter();
                        endPointParameter.setName("endPoint");
                        endPointParameter.setValue(chargePoint);
                        endPointParameter.setType("字符串");

                        taskParameter.add(robotNameParameter);
                        taskParameter.add(startPointParameter);
                        taskParameter.add(endPointParameter);
                        reqDto.setTaskParameter(taskParameter);

                        String jsonData = JSON.toJSONString(reqDto, SerializerFeature.DisableCircularReferenceDetect);
                        HttpClientUtil.clientPost("http://" + currentServerIp + ":8091/task/runDmsTask", HttpMethod.POST, jsonData);
                    }
                    //当前电池电量大于充满电最大值 && 机器人在充电
                    if (chargePoints.size() > 0 && robotBasicInfo.getChargeOk() < batteryLevel && robots.get(0).getCharge() == 1) {
                        //调用机器人充电完成
                        RobotTaskRequestDTO reqDto = new RobotTaskRequestDTO();
                        reqDto.setTaskNumber("task00009");
                        List<RobotTaskRequestDTO.SubParameter> taskParameter =new ArrayList<>();

                        RobotTaskRequestDTO.SubParameter robotNameParameter = new RobotTaskRequestDTO.SubParameter();
                        robotNameParameter.setName("robotName");
                        robotNameParameter.setValue(vehicleId);
                        robotNameParameter.setType("字符串");

                        RobotTaskRequestDTO.SubParameter startPointParameter = new RobotTaskRequestDTO.SubParameter();
                        startPointParameter.setName("startPoint");
                        startPointParameter.setValue(chargePoint);
                        startPointParameter.setType("字符串");

                        RobotTaskRequestDTO.SubParameter endPointParameter = new RobotTaskRequestDTO.SubParameter();
                        endPointParameter.setName("endPoint");
                        endPointParameter.setValue(dmsParkPoint);
                        endPointParameter.setType("字符串");

                        taskParameter.add(robotNameParameter);
                        taskParameter.add(startPointParameter);
                        taskParameter.add(endPointParameter);
                        reqDto.setTaskParameter(taskParameter);

                        String jsonData = JSON.toJSONString(reqDto, SerializerFeature.DisableCircularReferenceDetect);
                        HttpClientUtil.clientPost("http://" + currentServerIp + ":8091/task/runDmsTask", HttpMethod.POST, jsonData);
                    }
                }
            }
        }
    }

    /**
     * <获取机器人地图的路径信息列表> leo update
     *
     * @param vehicle 机器人名称
     */
    @Override
    public boolean getPathList(String vehicle) throws Exception {
        Map<String, String> downloadMapRes = (Map<String, String>) redisUtil.stringWithGet("robot_config_downloadmap_res");
        List<RobotBasicInfo> robots = findByName(vehicle);
        if (robots.size() < 1) {
            throw new Exception("输入的机器人不存在！");
        } else {
            RobotBasicInfo robot = robots.get(0);
            /*
             * 1. 获取机器人的全部地图名称数组
             */
            String robotIp = robot.getCurrentIp();
            Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
            String[] mapNames = {};
            // 连接端口检查
            if (!map.containsKey(robotIp + PortConstant.ROBOT_STATUS_PORT)) {
                // 连接机器人状态端口
                int retryCount = 0;
                do {
                    // 成功时或已达最大重试次数时退出循环
                    if (connectTcp(robotIp, PortConstant.ROBOT_STATUS_PORT) || retryCount >= 5) {
                        break;
                    }
                    // 等待0.5秒后重试
                    CountDownLatch latch = new CountDownLatch(1);
                    try {
                        if (!latch.await(500, TimeUnit.MILLISECONDS)) {
                            log.info("连接机器人{}状态端口{}：{}任务状态超时，重试次数：{}", vehicle, robotIp, PortConstant.ROBOT_STATUS_PORT, retryCount + 1);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        // 处理中断逻辑
                    }
                    retryCount++;
                } while (!Thread.currentThread().isInterrupted());
                if (retryCount >= 5) {
                    log.error("机器人{}无法连接状态端口{}：{}", vehicle, robotIp, PortConstant.ROBOT_STATUS_PORT);
                    return false;
                }
            }
            RobotStatusMapResDTO robotStatusMapResDTO = getRobotMapInfo(robotIp);
            if (robotStatusMapResDTO.getRetCode() == 0) {
                mapNames = robotStatusMapResDTO.getMaps();
            }
            /*
             * 2. 遍历地图名称数组，获取:
             *   ① 该机器人<地图名,站点列表>映射
             *   ② 该机器人全部地图的合并路径信息
             *   ③ 该机器人全部地图的合并站点信息（去重）
             */
            Map<String, List<String>> mapNameToPointsMap = new HashMap<>();
            // 定义该机器人全部地图的合并路径列表
            List<RobotPathVO> pathVOList = new ArrayList<>();
            // 定义该机器人全部地图的合并站点集合（去重）
            Set<String> allPointsByRobot = new HashSet<>();
            for (String mapName : mapNames) {
                // 获取该机器人对应的地图Json数据
                String mapJson = downloadMapRes.get(mapName);
                RobotMapDTO robotMapDTO = JSON.parseObject(mapJson, RobotMapDTO.class);
                /*
                 * 构造该机器人站点列表Map
                 */
                List<RobotMapAdvancePointDTO> advancedPointList = robotMapDTO.getAdvancedPointList();
                // 定义该机器人当前地图的站点名称列表并初始化长度
                List<String> pointsByMap = new ArrayList<>(advancedPointList.size());
                // 将该机器人当前地图的高级站点列表重构为站点名称列表
                for (RobotMapAdvancePointDTO pointDTO : advancedPointList) {
                    pointsByMap.add(pointDTO.getInstanceName());
                }
                mapNameToPointsMap.put(mapName, pointsByMap);
                // 将该机器人当前地图的站点名称加入全部站点名称集合
                allPointsByRobot.addAll(pointsByMap);

                /*
                 * 构造步进路径列表
                 */
                List<MapAdvancedCurveDTO> advancedCurveList = robotMapDTO.getAdvancedCurveList();
                // 初始化路径长度
                float bezierCurveLength = 0;
                for (MapAdvancedCurveDTO mapAdvancedCurveDTO : advancedCurveList) {
                    /*
                     * 根据步进路径类型计算步进路径长度
                     */
                    // 获取贝塞尔曲线四个点的坐标
                    MapAdvancedCurveDTO.PointPos startPos = mapAdvancedCurveDTO.getStartPos();
                    MapAdvancedCurveDTO.PointPos endPos = mapAdvancedCurveDTO.getEndPos();
                    MapAdvancedCurveDTO.Pos controlPos1 = mapAdvancedCurveDTO.getControlPos1();
                    MapAdvancedCurveDTO.Pos controlPos2 = mapAdvancedCurveDTO.getControlPos2();
                    if ("StraightPath".equals(mapAdvancedCurveDTO.getClassName())) {
                        // 计算直线路径长度
                        float deltaX = endPos.getPos().getX() - startPos.getPos().getX();
                        float deltaY = endPos.getPos().getY() - startPos.getPos().getY();
                        bezierCurveLength = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                    } else {
                        // 计算贝塞尔曲线路径的长度
                        bezierCurveLength = BezierCurveLengthUtil.getBezierCurveLength(startPos.getPos().getX(), startPos.getPos().getY(),
                                controlPos1.getX(), controlPos1.getY(), controlPos2.getX(), controlPos2.getY(), endPos.getPos().getX(), endPos.getPos().getY());
                    }

                    // 获取起点名称
                    String startPoint = mapAdvancedCurveDTO.getStartPos().getInstanceName();
                    // 获取终点名称
                    String endPoint = mapAdvancedCurveDTO.getEndPos().getInstanceName();
                    // 设置步进路径信息
                    RobotPathVO pathVO = new RobotPathVO();
                    pathVO.setGroupName(robot.getGroupName());
                    pathVO.setStartPoint(startPoint);
                    pathVO.setEndPoint(endPoint);
                    pathVO.setCurveLength(bezierCurveLength);
                    // 将步进路径加入路径列表
                    pathVOList.add(pathVO);
                }
            }
            List<RobotBasicInfo> robotBasicInfoList = findByName(vehicle);
            RobotBasicInfo robotBasicInfo = robotBasicInfoList.get(0);
            // 保存<地图名,站点列表>映射
            robotBasicInfo.setMapNameToPoints(JSON.toJSONString(mapNameToPointsMap));
            //保存机器人全部地图的路径信息
            robotBasicInfo.setPaths(JSON.toJSONString(pathVOList));
            // 保存机器人全部地图的站点信息（去重）
            robotBasicInfo.setPoints(JSON.toJSONString(allPointsByRobot));
            // 保存机器人当前地图名称
            return saveOrUpdate(robotBasicInfo);
        }
    }

    @Override
    public boolean autoConnect(String robotIp) throws Exception {
        String typeName = robotConnectService.getRobotType(robotIp);
        EntityResult result = ProtocolConvert.sendHexMsg(typeName, robotIp, PortConstant.ROBOT_STATUS_PORT, ProtocolConstant.ROBOT_BASIC_INFO);
        RobotConnect robotConnect = robotConnectService.findByIp(robotIp);
        String statusInfoRes = (String) redisUtil.stringWithGet("robot_status_info_res");
        RobotConnect model = JSON.parseObject(statusInfoRes, RobotConnect.class);
        //如果mapMd5值发生变更，则更新地图
        if (!robotConnect.getCurrentMapMd5().equals(model.getCurrentMapMd5())) {
            downloadCurrentRobotMap(robotIp);
            //修改机器人连接信息为修改地图后的数据
            RobotConnect oldRobotConnect = robotConnectService.findByIp(robotIp);
            oldRobotConnect.setCurrentMap(model.getCurrentMap());
            oldRobotConnect.setCurrentMapMd5(model.getCurrentMapMd5());
            robotConnectService.saveOrUpdate(oldRobotConnect);
        }
        InetAddress geek = InetAddress.getByName(robotIp);
        return geek.isReachable(1000) && Objects.requireNonNull(result).isSuccess();
    }

    /**
     * 抢占机器人控制权
     *
     * @return
     */
    @Override
    public boolean acquireControl(RobotBasicInfo robot) {
        String userName = "admin";
        String convertString = "{\"nick_name\":\"" + userName + "\"}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        String instruction = "5A010001000000" + dataLength + "0FA5000000000000" + data;
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        String robotIp = robot.getCurrentIp();
        if (!map.containsKey(robotIp + PortConstant.ROBOT_CONFIGURATION_PORT)) {
            //连接机器人配置端口
            connectTcp(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, instruction);
        return result.isSuccess();
    }

    /**
     * 释放机器人控制权
     *
     * @param robot
     * @return
     */
    @Override
    public boolean releaseControl(RobotBasicInfo robot) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        String robotIp = robot.getCurrentIp();
        if (!map.containsKey(robotIp + PortConstant.ROBOT_CONFIGURATION_PORT)) {
            //连接机器人配置端口
            connectTcp(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, ProtocolConstant.RELEASE_CONTROL);
        return result.isSuccess();
    }

}