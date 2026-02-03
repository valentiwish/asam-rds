package com.robotCore.scheduing.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beans.redis.RedisUtil;
import com.entity.EntityResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modbus4j.entity.ModbusTcpRead;
import com.modbus4j.entity.ModbusTcpWrite;
import com.modbus4j.util.ConstDataType;
import com.modbus4j.util.ConstFunCode;
import com.modbus4j.util.api.ModbusUtils;
import com.robotCore.common.base.service.PusherLiteService;
import com.robotCore.common.base.vo.BusinessDataVO;
import com.robotCore.common.constant.*;
import com.robotCore.common.mqtt.MqttGateway;
import com.robotCore.common.mqtt.inPlant.MqttProcess;
import com.robotCore.common.mqtt.inPlant.dto.PublishRTValue;
import com.robotCore.common.mqtt.inPlant.dto.RTValue;
import com.robotCore.common.mqtt.inPlant.dto.SendMessage;
import com.robotCore.common.utils.DataConvertUtil;
import com.robotCore.common.utils.DynamicThreadFactory;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.equipment.DTO.ChargePileStatusDTO;
import com.robotCore.equipment.DTO.CrossFloorInfoDTO;
import com.robotCore.equipment.DTO.ElevatorStatusDTO;
import com.robotCore.equipment.DTO.RobotForElevatorFSM;
import com.robotCore.equipment.entity.IotEquipment;
import com.robotCore.equipment.service.IoTEquipmentService;
import com.robotCore.equipment.utils.ElevatorPacketBuilder;
import com.robotCore.equipment.utils.RobotIdToByteManager;
import com.robotCore.robot.dto.RobotControlResDTO;
import com.robotCore.robot.dto.RobotForkResDTO;
import com.robotCore.robot.dto.RobotPathPlanDTO;
import com.robotCore.robot.dto.RobotStatusInfoResDTO;
import com.robotCore.robot.entity.*;
import com.robotCore.robot.service.*;
import com.robotCore.scheduing.common.config.CustomScheduledTaskRegistrar;
import com.robotCore.scheduing.common.enums.*;
import com.robotCore.scheduing.common.utils.DataUtil;
import com.robotCore.scheduing.common.utils.HttpClientUtil;
import com.robotCore.scheduing.common.utils.TimeFormatUtil;
import com.robotCore.scheduing.dto.*;
import com.robotCore.scheduing.entity.*;
import com.robotCore.scheduing.mapper.RobotWaybillDao;
import com.robotCore.scheduing.service.*;
import com.robotCore.scheduing.vo.*;
import com.robotCore.task.tcpCilent.IClientRead1;
import com.robotCore.task.tcpCilent.TcpClientThread;
import com.utils.tools.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/8/14
 **/
@Slf4j
@Service
public class RobotWaybillServiceImpl extends ServiceImpl<RobotWaybillDao, RobotWaybill> implements RobotWaybillService, IClientRead1 {

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CustomScheduledTaskRegistrar customScheduledTaskRegistrar;

    @Autowired
    private RobotTaskStepService robotTaskStepService;

    @Autowired
    private RobotWaybillDetailService robotWaybillDetailService;

    @Autowired
    private RobotAttributeService robotAttributeService;

    @Autowired
    private RobotMonitorService robotMonitorService;

    @Autowired
    private RobotNavigationService robotNavigationService;

    @Autowired
    private RobotTaskService robotTaskService;

    @Autowired
    private RobotChargePileService robotChargePileService;

    @Autowired
    private RobotTaskLogService robotTaskLogService;

    @Autowired
    private RobotDmsService robotDmsService;

    @Autowired
    private RobotDmsRegionRelateService robotDmsRegionRelateService;

    @Autowired
    private RobotRealTimeInfoService robotRealTimeInfoService;

    @Autowired
    private RobotPathPlanningService robotPathPlanningService;

    @Autowired
    private IoTEquipmentService ioTEquipmentService;

    @Autowired
    private RobotDmsEditorService robotDmsEditorService;

    @Autowired
    private PusherLiteService pusherLiteService;

    @Autowired
    private MqttProcess mqttProcess;

    @Resource
    private MqttGateway mqttGateway;

    @Value("${dms.port}")
    private Integer dmsPort;

    @Value("${otherSystem.syncWaybillStatusToMesUrl}")
    private String syncTaskStatusMesUrl;

    @Value("${otherSystem.syncWaybillStatusToWmsUrl}")
    private String syncTaskStatusWmsUrl;

    @Value("${data.serverIP}")
    private String currentServerIp;

    @Value("${359Wms.agvInPlaceUrl}")
    private String agvInPlaceUrl;

    @Value("${359Wms.agvDownPlaceUrl}")
    private String agvDownPlaceUrl;

    @Value("${otherSystem.8610Rms.registerWriteAddress}")
    private Integer registerWriteOffset;

    @Value("${equipment.isIntelligentChargingStation}")
    private Boolean isIntelligentChargingStation;

    @Value("${project.name}")
    private String projectName;

    @Value("${asam.mqtt.inPlant_topic_push}")
    private String inPlantTopicPush;

    //scada类型
    @Value("${asam.mqtt.active}")
    private String scadaName;

    //订阅
    @Value("${asam.mqtt.stateTopic}")
    private String stateTopic;

    //下发
    @Value("${asam.mqtt.setTopic}")
    private String setSubTopic;

    public static Map<String, RobotMonitorVO> map = new HashMap<>();

    //地图上被锁的点位列表
    ConcurrentHashMap<String, String> lockedPoints = new ConcurrentHashMap<>();

    //机器人子任务队列列表
    ConcurrentHashMap<String, List<AlgorithmResResultDTO.Path>> subTasks = new ConcurrentHashMap<>();

    //地图上被锁的点位和路径信息
    ConcurrentHashMap<String, RobotLockPathAndPoint> dmsLockedPointMap = new ConcurrentHashMap<>();

    //由于先执行的机器人的起点跟后执行的机器人的终点相同，需要把后执行的机器人任务锁上,防止两台机器人相遇
    ConcurrentHashMap<String, Boolean> lockedSubTask = new ConcurrentHashMap<>();


    @Override
    public IPage<RobotWaybill> findPageList(IPage<RobotWaybill> varPage, RobotWaybill robotWaybill) {
        QueryWrapper<RobotWaybill> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(robotWaybill)) {
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotWaybill.getId()), RobotWaybill::getId, robotWaybill.getId());
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotWaybill.getRobotName()), RobotWaybill::getRobotName, robotWaybill.getRobotName());
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotWaybill.getWaybillStatus()), RobotWaybill::getWaybillStatus, robotWaybill.getWaybillStatus());
        }
        wrapper.lambda().orderByDesc(RobotWaybill::getCreateTime);
        return page(varPage, wrapper);
    }

    @Override
    public List<RobotWaybillVO> dataInit(List<RobotWaybill> list) {
        List<RobotWaybillVO> robotWaybillVOS = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RobotWaybillVO vo = new RobotWaybillVO();
            RobotWaybill robotWaybill = list.get(i);
            vo.setId(robotWaybill.getId());
            vo.setTaskId(robotWaybill.getTaskId());
            vo.setTaskName(robotWaybill.getTaskName());
            vo.setSerialNumber(i + 1);
            vo.setRobotName(robotWaybill.getRobotName());
            vo.setRobotGroupName(robotWaybill.getRobotGroupName());
            vo.setCommunicationType(robotWaybill.getCommunicationType());
            vo.setPriority(Objects.requireNonNull(RobotTaskPriorityEnum.getByCode(robotWaybill.getTaskPriority())).getName());
            vo.setWaybillStatus(robotWaybill.getWaybillStatus());
            vo.setCreateTime(robotWaybill.getCreateTime().toString().substring(0, robotWaybill.getCreateTime().toString().length() - 2));
            if (robotWaybill.getOrderTime() == null) {
                vo.setOrderTime("");
            } else {
                vo.setOrderTime(robotWaybill.getOrderTime().toString().substring(0, robotWaybill.getOrderTime().toString().length() - 2));
            }
            if (robotWaybill.getCompleteTime() == null) {
                vo.setCompleteTime("");
            } else {
                vo.setCompleteTime(robotWaybill.getCompleteTime().toString().substring(0, robotWaybill.getCompleteTime().toString().length() - 2));
            }
            if (robotWaybill.getExecutionTime() == null) {
                vo.setExecutionTime("");
            } else {
                vo.setExecutionTime(robotWaybill.getExecutionTime() / 1000 + " s");
            }
            robotWaybillVOS.add(vo);
        }
        return robotWaybillVOS;
    }

    /**
     * 暂停当前导航
     *
     * @param robots
     * @return
     */
    @Override
    public boolean pauseNavigation(List<RobotBasicInfo> robots) {
        String robotIp = robots.get(0).getCurrentIp();
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人导航端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, ProtocolConstant.CANCEL_NAVIGATION);
        return result.isSuccess();
    }

    /**
     * 软急停当前机器人
     *
     * @param vehicleId
     * @return
     */
    @Override
    public boolean softScram(String vehicleId) {
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(vehicleId);
        if (robots.size() > 0) {
            String robotIp = robots.get(0).getCurrentIp();
            Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
            //如果当前ip和端口的的机器人已经连接
            if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_OTHER_PORT)) {
                //连接机器人导航端口
                robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_OTHER_PORT);
            }
            EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_OTHER_PORT, ProtocolConstant.SOFT_SCRAM);
            return result.isSuccess();
        } else {
            return false;
        }
    }

    /**
     * 获取等待执行的任务运单
     *
     * @return
     */
    @Override
    public List<RobotWaybill> getToBeExecutedWaybill() {
        long startTime = System.currentTimeMillis();
        //运单处于等待状态
        QueryWrapper<RobotWaybill> wrapper = new QueryWrapper<>();
        //运单处于等待状态
        wrapper.lambda().eq(RobotWaybill::getWaybillStatus, RobotWaybillStatusEnum.WAIT.getCode());
        //先创建的先执行
        wrapper.lambda().orderByAsc(RobotWaybill::getCreateTime);
        //设置任务优先级，优先级大的先执行
        wrapper.lambda().orderByDesc(RobotWaybill::getTaskPriority);
        return list(wrapper);
    }

    /**
     * 获取正在执行的任务运单
     *
     * @return
     */
    @Override
    public List<RobotWaybill> getBeExecutingWaybill() {
        QueryWrapper<RobotWaybill> wrapper = new QueryWrapper<>();
        //运单处于等待状态
        wrapper.lambda().eq(RobotWaybill::getWaybillStatus, RobotWaybillStatusEnum.EXECUTING.getCode());
        //排在前面的优先显示（执行）
        wrapper.lambda().orderByDesc(RobotWaybill::getCreateTime);
        //设置任务优先级，优先级大的先执行
        wrapper.lambda().orderByDesc(RobotWaybill::getTaskPriority);
        return list(wrapper);
    }

    /**
     * 获取当前机器人当前任务未完成的运单
     *
     * @param vehicleId
     * @param taskId
     * @return
     */
    @Override
    public List<RobotWaybill> getUnCompletedWaybill(String vehicleId, String taskId) {
        QueryWrapper<RobotWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(vehicleId), RobotWaybill::getRobotName, vehicleId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(taskId), RobotWaybill::getTaskId, taskId);
        //运单处于等待状态或者运行中
        wrapper.lambda().eq(RobotWaybill::getWaybillStatus, RobotWaybillStatusEnum.WAIT.getCode()).or().eq(RobotWaybill::getWaybillStatus, RobotWaybillStatusEnum.EXECUTING.getCode());
        return list(wrapper);
    }

    /**
     * 获取运单执行结果
     *
     * @return
     */
    @Override
    public RobotWaybillResultVO getWaybillExecuteResult(String mesWaybillId, String wmsWaybillId) {
        QueryWrapper<RobotWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(mesWaybillId), RobotWaybill::getMesWaybillId, mesWaybillId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(wmsWaybillId), RobotWaybill::getWmsWaybillId, wmsWaybillId);
        RobotWaybill waybillExecuteResult = list(wrapper).size() > 0 ? list(wrapper).get(0) : null;
        RobotWaybillResultVO resultVO = new RobotWaybillResultVO();
        if (waybillExecuteResult == null) {
            resultVO = null;
        } else {
            resultVO.setWaybillId(waybillExecuteResult.getId());
            resultVO.setMesWaybillId(mesWaybillId);
            resultVO.setWmsWaybillId(wmsWaybillId);
            resultVO.setPalletCode(waybillExecuteResult.getPalletCode());
            resultVO.setWaybillStatus(waybillExecuteResult.getWaybillStatus());
            if (waybillExecuteResult.getOrderTime() == null) {
                resultVO.setOrderTime("");
            } else {
                resultVO.setOrderTime(waybillExecuteResult.getOrderTime().toString().substring(0, waybillExecuteResult.getOrderTime().toString().length() - 2));
            }
            if (waybillExecuteResult.getCompleteTime() == null) {
                resultVO.setCompleteTime("");
            } else {
                resultVO.setCompleteTime(waybillExecuteResult.getCompleteTime().toString().substring(0, waybillExecuteResult.getCompleteTime().toString().length() - 2));
            }
            if (waybillExecuteResult.getExecutionTime() == null) {
                resultVO.setExecutionTime("");
            } else {
                resultVO.setExecutionTime(waybillExecuteResult.getExecutionTime() / 1000 + "");
            }
        }
        return resultVO;
    }

    /**
     * 根据MES运单ID获取当前运单
     *
     * @param mesWaybillId
     * @return
     */
    @Override
    public RobotWaybill getWaybillByMesWaybillId(String mesWaybillId) {
        QueryWrapper<RobotWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(mesWaybillId), RobotWaybill::getMesWaybillId, mesWaybillId);
        return list(wrapper).size() > 0 ? list(wrapper).get(0) : null;
    }

    /**
     * 根据WMS运单ID获取当前运单
     *
     * @param wmsWaybillId
     * @return
     */
    @Override
    public RobotWaybill getWaybillByWmsWaybillId(String wmsWaybillId) {
        QueryWrapper<RobotWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(wmsWaybillId), RobotWaybill::getWmsWaybillId, wmsWaybillId);
        return list(wrapper).size() > 0 ? list(wrapper).get(0) : null;
    }

    /**
     * 根据运单ID获取当前运单
     *
     * @param waybillId
     * @return
     */
    @Override
    public RobotWaybill getWaybillByWaybillId(String waybillId) {
        QueryWrapper<RobotWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(waybillId), RobotWaybill::getId, waybillId);
        return list(wrapper).size() > 0 ? list(wrapper).get(0) : null;
    }

    @Override
    public RobotWaybill getWaybillByRobotNameAndTaskStatus(String robotName, Integer waybillStatus) {
        QueryWrapper<RobotWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotName), RobotWaybill::getRobotName, robotName);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(waybillStatus), RobotWaybill::getWaybillStatus, waybillStatus)
                .or().eq(RobotWaybill::getWaybillStatus, 6)
                .or().eq(RobotWaybill::getWaybillStatus, 7);
        return list(wrapper).size() > 0 ? list(wrapper).get(0) : null;
    }

    /**
     * 获取任务编辑中的任务配置参数
     *
     * @param id
     * @return
     */
    public List<TaskParameterDTO> getTaskParameterList(String id) {
        String taskParameter = getById(id).getTaskParameter();
        List<TaskParameterDTO> parameters = null;
        if (taskParameter != null) {
            parameters = JSONObject.parseArray(taskParameter, TaskParameterDTO.class);
        }
        return parameters;
    }

    /**
     * 解析任务内容
     *
     * @param taskId
     * @param taskParameterListStr
     * @return
     */
    @Override
    public List<List<RobotRunDTO>> parsingTaskContent(String taskId, String taskParameterListStr) {
        //解析任务配置参数列表
        List<TaskParameterDTO> taskParameterList = JSONObject.parseArray(taskParameterListStr, TaskParameterDTO.class);
        Map<String, String> parameterMap = new HashMap<>();
        if (taskParameterListStr != null) {
            for (TaskParameterDTO taskParameterDTO : taskParameterList) {
                String variableName = taskParameterDTO.getVariableName();
                String defaultValue = taskParameterDTO.getDefaultValue();
                parameterMap.put(variableName, defaultValue);
            }
        }

        //获取任务内容
        String taskContent = robotTaskService.getById(taskId).getTaskContent();
        List<RobotTaskDTO> robotSubTaskList = JSONObject.parseArray(taskContent, RobotTaskDTO.class);
        if (robotSubTaskList == null) {
            return null;
        }
        List<List<RobotRunDTO>> subTaskList = new ArrayList<>();
        for (int i = 0; i < robotSubTaskList.size(); i++) {
            RobotTaskDTO model = robotSubTaskList.get(i);
            RobotTaskDTO[] children = model.getChildren();
            RobotTaskDTO.SubParameter[] parameters = model.getParameters();
            ArrayList<RobotRunDTO> list = new ArrayList<>();
            String vehicle = null;
            String groupName = null;
            String keyRoute = null;
            String targetSiteLabel = null;
            for (RobotTaskDTO.SubParameter parameter : parameters) {
                if ("vehicle".equals(parameter.getId())) {
                    if (parameterMap.containsKey(parameter.getValue())) {
                        vehicle = parameterMap.get(parameter.getValue());
                    } else {
                        vehicle = parameter.getValue();
                    }
                }

                if ("group".equals(parameter.getId())) {
                    if (parameterMap.containsKey(parameter.getValue())) {
                        groupName = parameterMap.get(parameter.getValue());
                    } else {
                        groupName = parameter.getValue();
                    }
                }

                if ("keyRoute".equals(parameter.getId())) {
                    if (parameterMap.containsKey(parameter.getValue())) {
                        keyRoute = parameterMap.get(parameter.getValue());
                    } else {
                        keyRoute = parameter.getValue();
                    }
                }

                if ("targetSiteLabel".equals(parameter.getId())) {
                    if (parameterMap.containsKey(parameter.getValue())) {
                        targetSiteLabel = parameterMap.get(parameter.getValue());
                    } else {
                        targetSiteLabel = parameter.getValue();
                    }
                }
            }
            RobotRunDTO outRobotRunDTO = new RobotRunDTO();
            outRobotRunDTO.setVehicle(vehicle);
            outRobotRunDTO.setStation(keyRoute);
            outRobotRunDTO.setGroupName(groupName);
            outRobotRunDTO.setInstruction(model.getName());
            //把外部的任务添加到要运行的任务列表中
            list.add(outRobotRunDTO);
            for (RobotTaskDTO child : children) {
                String childVehicle = null;
                String childGroupName = null;
                String childKeyRoute = null;
                String childTargetSiteLabel = null;
                String waitDoId = null;
                String waitDoStatus = null;
                String waitDoTime = null;
                String forkLiftHeight = null;
                String waitDiId = null;
                String waitDiStatus = null;
                String forkLoadOrUnload = null;
                String loadOrUnload = null;
                String locationLabel = null;
                RobotTaskDTO.SubParameter[] childParameters = child.getParameters();
                for (RobotTaskDTO.SubParameter parameter : childParameters) {
                    if ("vehicle".equals(parameter.getId())) {
                        childVehicle = parameter.getValue();
                    }

                    if ("group".equals(parameter.getId())) {
                        childGroupName = parameter.getValue();
                    }

                    if ("keyRoute".equals(parameter.getId())) {
                        childKeyRoute = parameter.getValue();
                    }

                    if ("targetSiteLabel".equals(parameter.getId())) {
                        if (parameterMap.containsKey(parameter.getValue())) {
                            childTargetSiteLabel = parameterMap.get(parameter.getValue());
                        } else {
                            childTargetSiteLabel = parameter.getValue();
                        }
                    }

                    if ("waitDoId".equals(parameter.getId())) {
                        waitDoId = parameter.getValue();
                    }

                    if ("waitDoStatus".equals(parameter.getId())) {
                        waitDoStatus = parameter.getValue();
                    }

                    if ("waitDoTime".equals(parameter.getId())) {
                        waitDoTime = parameter.getValue();
                    }

                    if ("forkLiftHeight".equals(parameter.getId())) {
                        forkLiftHeight = parameter.getValue();
                    }

                    if ("waitDiId".equals(parameter.getId())) {
                        waitDiId = parameter.getValue();
                    }

                    if ("waitDiStatus".equals(parameter.getId())) {
                        waitDiStatus = parameter.getValue();
                    }

                    if ("forkLoadOrUnload".equals(parameter.getId())) {
                        forkLoadOrUnload = parameter.getValue();
                    }

                    if ("loadOrUnload".equals(parameter.getId())) {
                        loadOrUnload = parameter.getValue();
                    }

                    if ("locationLabel".equals(parameter.getId())) {
                        if (parameterMap.containsKey(parameter.getValue())) {
                            locationLabel = parameterMap.get(parameter.getValue());
                        } else {
                            locationLabel = parameter.getValue();
                        }
                    }
                }
                RobotRunDTO innerRobotRunDTO = new RobotRunDTO();
                innerRobotRunDTO.setVehicle(vehicle);
                innerRobotRunDTO.setStation(childTargetSiteLabel);
                innerRobotRunDTO.setInstruction(child.getName());
                innerRobotRunDTO.setWaitDoId(waitDoId);
                innerRobotRunDTO.setWaitDoStatus(waitDoStatus);
                innerRobotRunDTO.setWaitDoTime(waitDoTime);
                innerRobotRunDTO.setForkLiftHeight(forkLiftHeight);
                innerRobotRunDTO.setWaitDiId(waitDiId);
                innerRobotRunDTO.setWaitDiStatus(waitDiStatus);
                innerRobotRunDTO.setForkLoadOrUnload(forkLoadOrUnload);
                innerRobotRunDTO.setLoadOrUnload(loadOrUnload);
                innerRobotRunDTO.setLocationLabel(locationLabel);
                list.add(innerRobotRunDTO);
            }

            //创建虚拟待命点，后面任务执行过程会把虚拟待命点替换为实际待命点
            RobotRunDTO firstRunDTO = new RobotRunDTO();
            firstRunDTO.setVehicle(vehicle);
            firstRunDTO.setStation("虚拟待命站点");
            firstRunDTO.setInstruction("机器人路径导航");
            list.add(firstRunDTO);

            RobotTaskStep step = new RobotTaskStep();
            step.setTaskId(taskId);
            step.setRobotName(vehicle);
            step.setTaskStep(JSON.toJSONString(list));
            step.setOperateTime(System.currentTimeMillis());
            robotTaskStepService.save(step);
            subTaskList.add(list);
        }
        return subTaskList;
    }

    /**
     * 获取任务编辑中的任务列表
     *
     * @param taskId
     * @return
     */
    @Override
    public List<RobotRunDTO> getTaskList(String taskId, String taskParameterListStr) {
        //解析任务配置参数列表
        List<TaskParameterDTO> taskParameterList = JSONObject.parseArray(taskParameterListStr, TaskParameterDTO.class);
        Map<String, String> parameterMap = new HashMap<>();
        if (taskParameterListStr != null) {
            for (TaskParameterDTO taskParameterDTO : taskParameterList) {
                String variableName = taskParameterDTO.getVariableName();
                String defaultValue = taskParameterDTO.getDefaultValue();
                parameterMap.put(variableName, defaultValue);
            }
        }

        //获取任务内容
        String taskContent = robotTaskService.getById(taskId).getTaskContent();
        String substring = taskContent.substring(1);
        String taskStr = substring.substring(0, substring.length() - 1);
        RobotTaskDTO model = JSON.parseObject(taskStr, RobotTaskDTO.class);
        if (model == null) {
            return null;
        }
        RobotTaskDTO[] children = model.getChildren();
        RobotTaskDTO.SubParameter[] parameters = model.getParameters();
        ArrayList<RobotRunDTO> list = new ArrayList<>();
        String vehicle = null;
        String groupName = null;
        String keyRoute = null;
        String targetSiteLabel = null;
        for (RobotTaskDTO.SubParameter parameter : parameters) {
            if ("vehicle".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    vehicle = parameterMap.get(parameter.getValue());
                } else {
                    vehicle = parameter.getValue();
                }
            }

            if ("group".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    groupName = parameterMap.get(parameter.getValue());
                } else {
                    groupName = parameter.getValue();
                }
            }

            if ("keyRoute".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    keyRoute = parameterMap.get(parameter.getValue());
                } else {
                    keyRoute = parameter.getValue();
                }
            }

            if ("targetSiteLabel".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    targetSiteLabel = parameterMap.get(parameter.getValue());
                } else {
                    targetSiteLabel = parameter.getValue();
                }
            }
        }
        RobotRunDTO outRobotRunDTO = new RobotRunDTO();
        outRobotRunDTO.setVehicle(vehicle);
        outRobotRunDTO.setStation(keyRoute);
        outRobotRunDTO.setGroupName(groupName);
        outRobotRunDTO.setInstruction(model.getName());
        //把外部的任务添加到要运行的任务列表中
        list.add(outRobotRunDTO);
        for (RobotTaskDTO child : children) {
            String childVehicle = null;
            String childGroupName = null;
            String childKeyRoute = null;
            String childTargetSiteLabel = null;
            String waitDoId = null;
            String waitDoStatus = null;
            String waitDoTime = null;
            String forkLiftHeight = null;
            String waitDiId = null;
            String waitDiStatus = null;
            String forkLoadOrUnload = null;
            String loadOrUnload = null;
            //modbus相关属性
            String modbusIp = null;
            Integer modbusPort = null;
            Integer ipSlaveId = null;
            Integer offset = null;
            Integer operationType = null;
            Integer modbusValue = null;
            RobotTaskDTO.SubParameter[] childParameters = child.getParameters();
            //自定义动作JSON字符串
            String customizedActionsNewJson = null;


            RobotRunDTO innerRobotRunDTO = new RobotRunDTO();
            for (RobotTaskDTO.SubParameter parameter : childParameters) {
                if ("vehicle".equals(parameter.getId())) {
                    childVehicle = parameter.getValue();
                }

                if ("group".equals(parameter.getId())) {
                    childGroupName = parameter.getValue();
                }

                if ("keyRoute".equals(parameter.getId())) {
                    childKeyRoute = parameter.getValue();
                }

                if ("targetSiteLabel".equals(parameter.getId())) {
                    if (parameterMap.containsKey(parameter.getValue())) {
                        childTargetSiteLabel = parameterMap.get(parameter.getValue());
                    } else {
                        childTargetSiteLabel = parameter.getValue();
                    }
                }

                if ("waitDoId".equals(parameter.getId())) {
                    waitDoId = parameter.getValue();
                }

                if ("waitDoStatus".equals(parameter.getId())) {
                    waitDoStatus = parameter.getValue();
                }

                if ("waitDoTime".equals(parameter.getId())) {
                    waitDoTime = parameter.getValue();
                }

                if ("forkLiftHeight".equals(parameter.getId())) {
                    forkLiftHeight = parameter.getValue();
                }

                if ("waitDiId".equals(parameter.getId())) {
                    waitDiId = parameter.getValue();
                }

                if ("waitDiStatus".equals(parameter.getId())) {
                    waitDiStatus = parameter.getValue();
                }

                if ("forkLoadOrUnload".equals(parameter.getId())) {
                    forkLoadOrUnload = parameter.getValue();
                }

                if ("loadOrUnload".equals(parameter.getId())) {
                    loadOrUnload = parameter.getValue();
                }

                /*
                 * modbus相关参数（leo add）
                 */
                if ("ipModbusHost".equals(parameter.getId())) {
                    modbusIp = parameter.getValue();
                }
                if ("port".equals(parameter.getId())) {
                    modbusPort = Integer.parseInt(parameter.getValue());
                    ;
                }
                if ("ipSlaveId".equals(parameter.getId())) {
                    ipSlaveId = Integer.parseInt(parameter.getValue());
                }
                if ("offset".equals(parameter.getId())) {
                    offset = Integer.parseInt(parameter.getValue());
                }
                if ("operationType".equals(parameter.getId())) {
                    operationType = Integer.parseInt(parameter.getValue());
                }
                // 写入值
                if ("modbusWriteValue".equals(parameter.getId())) {
                    String value = parameter.getValue();
                    if (value == null || value.isEmpty()) {
                        //写入值为空则默认置为0
                        modbusValue = 0;
                    } else {
                        try {
                            modbusValue = Integer.parseInt(parameter.getValue());
                        } catch (NumberFormatException e) {
                            modbusValue = 0;
                        }
                    }
                }
                // 等待值
                if ("modbusTargetValue".equals(parameter.getId())) {
                    String value = parameter.getValue();
                    if (value != null && !value.isEmpty()) {
                        try {
                            modbusValue = Integer.parseInt(parameter.getValue());
                        } catch (NumberFormatException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
                /*
                 * 若为自定义动作节点，则执行专用解析方法（leo add）
                 */
                if ("customizedActions".equals(parameter.getId())) {
                    // 获取自定义动作原始JSON字符串
                    String customizedActionsRawJson = parameter.getValue();
                    // 重构自定义动作JSON字符串
                    customizedActionsNewJson = robotTaskService.refactorJson(customizedActionsRawJson, parameterMap);
                }
            }
            innerRobotRunDTO.setVehicle(vehicle);
            innerRobotRunDTO.setStation(childTargetSiteLabel);
            innerRobotRunDTO.setInstruction(child.getName());
            innerRobotRunDTO.setWaitDoId(waitDoId);
            innerRobotRunDTO.setWaitDoStatus(waitDoStatus);
            innerRobotRunDTO.setWaitDoTime(waitDoTime);
            innerRobotRunDTO.setForkLiftHeight(forkLiftHeight);
            innerRobotRunDTO.setWaitDiId(waitDiId);
            innerRobotRunDTO.setWaitDiStatus(waitDiStatus);
            innerRobotRunDTO.setForkLoadOrUnload(forkLoadOrUnload);
            innerRobotRunDTO.setLoadOrUnload(loadOrUnload);
            //modbus任务模块赋值
            innerRobotRunDTO.getModbusParam().setIp(modbusIp);
            innerRobotRunDTO.getModbusParam().setPort(modbusPort);
            innerRobotRunDTO.getModbusParam().setSlaveId(ipSlaveId);
            innerRobotRunDTO.getModbusParam().setOffset(offset);
            innerRobotRunDTO.getModbusParam().setOperationType(operationType);
            innerRobotRunDTO.getModbusParam().setValue(modbusValue);
            // 自定义动作赋值
            innerRobotRunDTO.setCustomizedActions(customizedActionsNewJson);
            list.add(innerRobotRunDTO);
        }

        //创建虚拟待命点，后面任务执行过程会把虚拟待命点替换为实际待命点
        RobotRunDTO firstRunDTO = new RobotRunDTO();
        firstRunDTO.setVehicle(vehicle);
        firstRunDTO.setStation("虚拟待命站点");
        firstRunDTO.setInstruction("机器人路径导航");
        list.add(firstRunDTO);

        RobotTaskStep step = new RobotTaskStep();
        step.setTaskId(taskId);
        step.setRobotName(vehicle);
        step.setTaskStep(JSON.toJSONString(list));
        step.setOperateTime(System.currentTimeMillis());
        robotTaskStepService.save(step);
        return list;
    }

    /**
     * 获取循环任务的列表
     *
     * @param taskId
     * @param vehicle
     * @param taskParameterListStr
     * @return
     */
    @Override
    public List<RobotRunDTO> getLoopTaskList(String taskId, String vehicle, String taskParameterListStr) {
        //解析任务配置参数列表
        List<TaskParameterDTO> taskParameterList = JSONObject.parseArray(taskParameterListStr, TaskParameterDTO.class);
        Map<String, String> parameterMap = new HashMap<>();
        if (taskParameterListStr != null) {
            for (TaskParameterDTO taskParameterDTO : taskParameterList) {
                String variableName = taskParameterDTO.getVariableName();
                String defaultValue = taskParameterDTO.getDefaultValue();
                parameterMap.put(variableName, defaultValue);
            }
        }

        //获取任务内容
        String taskContent = robotTaskService.getById(taskId).getTaskContent();
        String substring = taskContent.substring(1);
        String taskStr = substring.substring(0, substring.length() - 1);
        RobotTaskDTO model = JSON.parseObject(taskStr, RobotTaskDTO.class);
        RobotTaskDTO[] children = model.getChildren();
        RobotTaskDTO.SubParameter[] parameters = model.getParameters();
        ArrayList<RobotRunDTO> list = new ArrayList<>();
        String groupName = null;
        String keyRoute = null;
        String targetSiteLabel = null;
        for (RobotTaskDTO.SubParameter parameter : parameters) {

            if ("group".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    groupName = parameterMap.get(parameter.getValue());
                } else {
                    groupName = parameter.getValue();
                }
            }

            if ("keyRoute".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    keyRoute = parameterMap.get(parameter.getValue());
                } else {
                    keyRoute = parameter.getValue();
                }
            }

            if ("targetSiteLabel".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    targetSiteLabel = parameterMap.get(parameter.getValue());
                } else {
                    targetSiteLabel = parameter.getValue();
                }
            }
        }
        RobotRunDTO outRobotRunDTO = new RobotRunDTO();
        outRobotRunDTO.setVehicle(vehicle);
        outRobotRunDTO.setStation(keyRoute);
        outRobotRunDTO.setGroupName(groupName);
        outRobotRunDTO.setInstruction(model.getName());
        //把外部的任务添加到要运行的任务列表中
        list.add(outRobotRunDTO);
        for (RobotTaskDTO child : children) {
            String childVehicle = null;
            String childGroupName = null;
            String childKeyRoute = null;
            String childTargetSiteLabel = null;
            RobotTaskDTO.SubParameter[] childParameters = child.getParameters();
            for (RobotTaskDTO.SubParameter parameter : childParameters) {
                if ("vehicle".equals(parameter.getId())) {
                    childVehicle = parameter.getValue();
                }

                if ("group".equals(parameter.getId())) {
                    childGroupName = parameter.getValue();
                }

                if ("keyRoute".equals(parameter.getId())) {
                    childKeyRoute = parameter.getValue();
                }

                if ("targetSiteLabel".equals(parameter.getId())) {
                    if (parameterMap.containsKey(parameter.getValue())) {
                        childTargetSiteLabel = parameterMap.get(parameter.getValue());
                    } else {
                        childTargetSiteLabel = parameter.getValue();
                    }
                }
            }
            RobotRunDTO innerRobotRunDTO = new RobotRunDTO();
            innerRobotRunDTO.setVehicle(vehicle);
            innerRobotRunDTO.setStation(childTargetSiteLabel);
            innerRobotRunDTO.setInstruction(child.getName());
            list.add(innerRobotRunDTO);
        }

        List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(vehicle, RobotMapPointAttrEnum.PARK_POINT.getCode());
        if (robotBindAttr.size() > 0) {
            RobotRunDTO firstRunDTO = new RobotRunDTO();
            firstRunDTO.setVehicle(vehicle);
            firstRunDTO.setStation(robotBindAttr.get(0).getPoint());
            firstRunDTO.setInstruction("机器人路径导航");
            list.add(firstRunDTO);
        }
        RobotTaskStep step = new RobotTaskStep();
        step.setTaskId(taskId);
        step.setRobotName(vehicle);
        step.setTaskStep(JSON.toJSONString(list));
        step.setOperateTime(System.currentTimeMillis());
        robotTaskStepService.save(step);
        return list;
    }

    /**
     * 执行任务编辑的第一个子任务
     *
     * @param list
     * @return
     */
    @Override
    public RobotTaskExecuteResDTO executeFirstTask(@NotNull List<RobotRunDTO> list, String waybillId) throws Exception {
        RobotRunDTO robotRunDTO = list.get(0);
        RobotBasicInfo robot = robotBasicInfoService.findByVehicleId(robotRunDTO.getVehicle());
        RobotMonitorVO robotMonitorVO = map.get(robotRunDTO.getVehicle());
        //如果机器人当前站点不为空，则起点设置为当前站点
        if (robotMonitorVO.getCurrentStation() != null && !"".equals(robotMonitorVO.getCurrentStation())) {
            list.get(0).setStation(robotMonitorVO.getCurrentStation());
        }
        String convertString;
        if (list.size() == 1) {
            convertString = "{\"source_id\":\"" + list.get(0).getStation() + "\",\"id\":\"" + list.get(0).getStation() + "\"}";
        } else {
            convertString = "{\"source_id\":\"" + list.get(0).getStation() + "\",\"id\":\"" + list.get(1).getStation() + "\"}";
        }
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        String instruction = "5A010001000000" + dataLength + "0BEB000000000000" + data;
        boolean flag = startContinuousNavigation(waybillId, robot.getVehicleId(), list);
        RobotTaskExecuteResDTO dto = new RobotTaskExecuteResDTO();
        dto.setSuccess(flag);
        dto.setRobotIp(robot.getCurrentIp());
        dto.setInstruction(instruction);
        return dto;
    }

    /**
     * f返回待命点
     *
     * @param robotWaybill
     * @return
     * @throws Exception
     */
    @Override
    public boolean runBackParkPointTask(RobotWaybill robotWaybill) throws Exception {
        RobotMonitorVO robotMonitorVO = map.get(robotWaybill.getRobotName());
        List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(robotWaybill.getRobotName(), RobotMapPointAttrEnum.PARK_POINT.getCode());
        //如果机器人当前站点不为空，则起点设置为当前站点
        String currentPoint;
        String parkPoint = robotBindAttr.get(0).getPoint();
        //如果机器人当前站点不为空，则起点设置为当前站点
        if (robotMonitorVO.getCurrentStation() != null && !"".equals(robotMonitorVO.getCurrentStation())) {
            currentPoint = robotMonitorVO.getCurrentStation();
        } else {
            currentPoint = parkPoint;
        }
        List<RobotRunDTO> taskList = new ArrayList<>();
        RobotRunDTO firstRunDTO = new RobotRunDTO();
        firstRunDTO.setGroupName(robotWaybill.getRobotGroupName());
        firstRunDTO.setVehicle(robotWaybill.getRobotName());
        firstRunDTO.setInstruction("机器人路径导航");
        firstRunDTO.setStation(currentPoint);

        RobotRunDTO secondRunDTO = new RobotRunDTO();
        secondRunDTO.setGroupName(robotWaybill.getRobotGroupName());
        secondRunDTO.setVehicle(robotWaybill.getRobotName());
        secondRunDTO.setInstruction("机器人路径导航");
        secondRunDTO.setStation(parkPoint);

        taskList.add(firstRunDTO);
        taskList.add(secondRunDTO);
        return startContinuousNavigation(robotWaybill.getId(), robotWaybill.getRobotName(), taskList);
    }

    /**
     * 设置任务ID、机器人名字,设置机器人通信方式为WIFI通信
     *
     * @param mesWaybillId      MES运单ID
     * @param wmsWaybillId      WMS运单ID
     * @param palletCode        托盘编号
     * @param taskId            任务id
     * @param vehicle           机器人名称
     * @param groupName         分组名称
     * @param taskParameterList 任务参数列表
     * @param taskList          任务列表
     * @return 创建运单成功，返回运单号
     */
    @Override
    public RobotCommonResVO setRobotWaybill(String mesWaybillId, String wmsWaybillId, String palletCode, String taskId, String vehicle, String groupName, String taskParameterList, List<RobotRunDTO> taskList, String communicationType) {
        RobotTask currentTask = robotTaskService.getById(taskId);
        //创建运单,设置任务ID、机器人名字,设置机器人通信方式为WIFI通信
        RobotWaybill robotWaybill = new RobotWaybill();
        robotWaybill.setMesWaybillId(mesWaybillId);
        robotWaybill.setWmsWaybillId(wmsWaybillId);
        robotWaybill.setPalletCode(palletCode);
        robotWaybill.setCommunicationType(communicationType);
        robotWaybill.setRobotName(vehicle);
        robotWaybill.setTaskId(taskId);
        robotWaybill.setTaskName(currentTask.getTaskName());
        //设置运单优先级
        robotWaybill.setTaskPriority(currentTask.getPriority());
        //初始设置运单运行状态为等待
        robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.WAIT.getCode());
        //设置运单中机器人名称
        if ("".equals(vehicle)) {
            robotWaybill.setRobotName(null);
        } else {
            robotWaybill.setRobotName(vehicle);
        }
        //设置运单中机器人组名称
        if ("".equals(groupName)) {
            robotWaybill.setRobotGroupName(null);
        } else {
            robotWaybill.setRobotGroupName(groupName);
        }
        RobotCommonResVO resVO = new RobotCommonResVO();
        if ("".equals(vehicle) && "".equals(groupName)) {
            resVO.setSuccess(false);
            resVO.setMessage("请填写机器人或者机器人分组信息！");
            return resVO;
        }
        robotWaybill.setCreateTime(new Timestamp(System.currentTimeMillis()));
        //设置运单中的任务参数
        robotWaybill.setTaskParameter(taskParameterList);
        //设置运单的任务列表
        robotWaybill.setTaskList(JSON.toJSONString(taskList));
        boolean save = save(robotWaybill);
        resVO.setSuccess(save);
        if (save) {
            resVO.setMessage("创建运单成功！");
        } else {
            resVO.setMessage("创建运单失败！");
        }
        return resVO;
    }

    /**
     * 继续执行当前任务
     *
     * @param list
     * @param waybillId 运单id
     * @param taskId
     * @param vehicle
     * @param groupName
     * @return
     */
    @Override
    public boolean continueRobotTask(List<RobotRunDTO> list, String waybillId, String taskId, String vehicle, String groupName) throws Exception {
        //根据id当前运单
        RobotWaybill robotWaybill = getById(waybillId);

        //执行任务编辑的第一个子任务
        RobotTaskExecuteResDTO dto = executeFirstTask(list, waybillId);

        //如果导航任务开始（即机器人开始接单），设置机器人的接单时间
        RobotWaybill robotWaybill1 = getById(robotWaybill.getId());
        //设置运单的状态为正在执行
        robotWaybill1.setWaybillStatus(RobotWaybillStatusEnum.EXECUTING.getCode());
        saveOrUpdate(robotWaybill1);

        //机器人执行运单任务，出现错误
        List<RobotWarningInfoDTO> errors = new ArrayList<>();
        List<RobotWarningInfoDTO> errorList = map.get(vehicle).getErrors();
        List<RobotWarningInfoDTO> fatalList = map.get(vehicle).getFatals();
        errors.addAll(errorList);
        errors.addAll(fatalList);
        if (errors.size() > 0) {
            RobotWaybill robotWaybill2 = getById(robotWaybill.getId());
            //设置运单的状态为终止
            robotWaybill2.setWaybillStatus(RobotWaybillStatusEnum.TERMINATE.getCode());
            //设置造成运单状态为终止的错误描述
            robotWaybill2.setErrorMessage(errors.get(errors.size() - 1).getMessage());
            //设置运单完成时间
            robotWaybill2.setCompleteTime(new Timestamp(System.currentTimeMillis()));
            //设置执行耗时
            robotWaybill2.setExecutionTime(robotWaybill2.getCompleteTime().getTime() - robotWaybill2.getOrderTime().getTime());
            saveOrUpdate(robotWaybill2);
        } else {
            //机器人执行运单任务的第一步成功，则把该步骤的运行情况存入到RobotWaybillDetail中
            RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(waybillId);
            if (newDetail != null) {
                newDetail.setWaybillStatus("完成");
                robotWaybillDetailService.saveOrUpdate(newDetail);
            }
            RobotWaybillDetail robotWaybillDetail = new RobotWaybillDetail();
            robotWaybillDetail.setWaybillId(robotWaybill.getId());
            //设置库位、操作和运行状态
            if (list.size() <= 1) {
                robotWaybillDetail.setStorageLoc(list.get(0).getStation());
            } else {
                robotWaybillDetail.setStorageLoc(list.get(1).getStation());
            }
            robotWaybillDetail.setOperation("导航");
            robotWaybillDetail.setWaybillStatus("运行中");
            robotWaybillDetailService.save(robotWaybillDetail);

            //发送导航指令完成后，延迟之后再执行定时任务
            if (dto.isSuccess()) {
                Thread.sleep(2000);
                String cron = "*/1 * * * * ?";
                customScheduledTaskRegistrar.addTriggerTask(robotWaybill.getId(),
                        //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                        () -> {
                            try {
                                task(taskId, waybillId, robotWaybill.getId(), dto.getRobotIp(), vehicle, selectTask(taskId, vehicle, robotWaybill.getId()), null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
            }
        }
        return dto.isSuccess();
    }

    /**
     * 创建待执行的机器人运单
     *
     * @param mesWaybillId      MES运单ID
     * @param wmsWaybillId      WMS运单ID
     * @param palletCode        托盘编号
     * @param taskList          任务编辑中列表
     * @param taskId            任务id
     * @param taskParameterList 当前运单的机器人任务参数列表
     * @return 是否创建运单成功
     * @throws Exception
     */
    @Override
    public RobotCommonResVO createWaybill(String mesWaybillId, String wmsWaybillId, String palletCode, List<RobotRunDTO> taskList, String taskId, String taskParameterList, String communicationType) {
        RobotRunDTO robotRunDTO = taskList.get(0);
        String vehicle = robotRunDTO.getVehicle();
        String groupName = robotRunDTO.getGroupName();

        //创建运单,设置任务ID、机器人名字,设置机器人通信方式为WIFI通信
        return setRobotWaybill(mesWaybillId, wmsWaybillId, palletCode, taskId, vehicle, groupName, taskParameterList, taskList, communicationType);
    }

    /**
     * 运行机器人WIFI任务
     *
     * @param robotWaybill 任务运单
     * @param vehicle      机器人名称
     * @param groupName    分组名称
     * @return
     * @throws Exception
     */
    @Override
    public void runRobotWifiTask(RobotWaybill robotWaybill, String vehicle, String groupName) throws Exception {
        //设置机器人运单状态
        //设置机器人的状态为非空闲
        RobotBasicInfo robotBasicInfo = robotBasicInfoService.findByName(vehicle).get(0);
        robotBasicInfo.setLeisure(Constant.STATE_INVALID);
        robotBasicInfoService.saveOrUpdate(robotBasicInfo);
        //设置机器人的接单时间
        robotWaybill.setOrderTime(new Timestamp(System.currentTimeMillis()));
        //设置运单的状态为正在执行
        robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.EXECUTING.getCode());
        saveOrUpdate(robotWaybill);
        //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
        completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());

        //获取机器人运单
        String taskId = robotWaybill.getTaskId();
        String taskParameterStr = robotWaybill.getTaskParameter();
        String taskListStr = robotWaybill.getTaskList();

        RobotTaskStep taskStep = robotTaskStepService.findOne(taskId, "", null);
        if (taskStep != null) {
            taskStep.setRobotName(vehicle);
            taskStep.setWaybillId(robotWaybill.getId());
            robotTaskStepService.saveOrUpdate(taskStep);
        }

        //执行任务编辑的第一个子任务
        List<RobotRunDTO> taskList = JSON.parseArray(taskListStr, RobotRunDTO.class);

        //设置最新的机器人任务列表
        List<RobotRunDTO> newTaskList = new ArrayList<>();
        for (RobotRunDTO robotRunDTO : taskList) {
            robotRunDTO.setVehicle(vehicle);
            robotRunDTO.setGroupName(groupName);
            newTaskList.add(robotRunDTO);
        }

        RobotTaskExecuteResDTO dto = executeFirstTask(newTaskList, robotWaybill.getId());
        //请求路径规划算法成功，且机器人在线运行
        if (dto.isSuccess()) {
            //机器人执行运单任务，出现错误
            List<RobotWarningInfoDTO> errors = new ArrayList<>();
            List<RobotWarningInfoDTO> errorList = map.get(vehicle).getErrors();
            List<RobotWarningInfoDTO> fatalList = map.get(vehicle).getFatals();
            errors.addAll(errorList);
            errors.addAll(fatalList);
            if (errors.size() > 0) {
                //机器人发生了错误
                setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), vehicle, null);
            } else {
                //机器人执行运单任务的第一步成功，则把该步骤的运行情况存入到RobotWaybillDetail中
                RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybill.getId());
                if (newDetail != null) {
                    newDetail.setWaybillStatus("完成");
                    robotWaybillDetailService.saveOrUpdate(newDetail);
                }
                RobotWaybillDetail robotWaybillDetail = new RobotWaybillDetail();
                robotWaybillDetail.setWaybillId(robotWaybill.getId());
                //设置库位、操作和运行状态
                robotWaybillDetail.setStorageLoc(newTaskList.get(1).getStation());
                robotWaybillDetail.setOperation("导航");
                robotWaybillDetail.setWaybillStatus("运行中");
                robotWaybillDetailService.save(robotWaybillDetail);

                //发送导航指令完成后，延迟之后再执行定时任务
                //此处的定时任务频率与机器人的消息推送频率相关，不能小于机器人的推送频率
                String cron = "*/4 * * * * ?";
                customScheduledTaskRegistrar.addTriggerTask(robotWaybill.getId(),
                        //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                        () -> {
                            try {
                                task(taskId, robotWaybill.getId(), robotWaybill.getId(), dto.getRobotIp(), vehicle, selectTask(taskId, vehicle, robotWaybill.getId()), taskParameterStr);
                            } catch (Exception e) {
                                try {
                                    //导航失败，设置任务执行失败数据
                                    setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), vehicle, e.getMessage());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                e.printStackTrace();
                            }
                        }, triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
            }
        } else {
            //执行路径规划算法失败
            setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), vehicle, "执行路径规划算法失败");
        }
    }

    /**
     * 运行循环任务
     *
     * @param taskId            任务id
     * @param vehicle           机器人名称
     * @param robotWaybill      任务运单
     * @param taskParameterList 任务参数列表
     * @return
     * @throws Exception
     */
    @Override
    public boolean runLoopRobotTask(String taskId, String vehicle, RobotWaybill robotWaybill, String taskParameterList) throws Exception {
        //获取任务编辑中的任务列表
        List<RobotRunDTO> list = getLoopTaskList(taskId, vehicle, taskParameterList);
        if (list.size() <= 0) {
            throw new Exception("请检查任务是否输入！");
        }

        //执行任务编辑的第一个子任务
        RobotTaskExecuteResDTO dto = executeFirstTask(list, robotWaybill.getId());
        //请求路径规划算法成功，且机器人在线运行
        if (dto.isSuccess()) {
            //如果导航任务开始（即机器人开始接单），设置机器人的接单时间
            RobotWaybill robotWaybill1 = getById(robotWaybill.getId());
            //设置运单的状态为正在执行
            robotWaybill1.setWaybillStatus(RobotWaybillStatusEnum.EXECUTING.getCode());
            saveOrUpdate(robotWaybill1);

            //机器人执行运单任务，出现错误
            List<RobotWarningInfoDTO> errors = new ArrayList<>();
            List<RobotWarningInfoDTO> errorList = map.get(vehicle).getErrors();
            List<RobotWarningInfoDTO> fatalList = map.get(vehicle).getFatals();
            errors.addAll(errorList);
            errors.addAll(fatalList);
            if (errors.size() > 0) {
                RobotWaybill robotWaybill2 = getById(robotWaybill.getId());
                log.info("机器人发生了错误，任务已终止：{},错误原因：{}", robotWaybill2.getId(), errors.get(errors.size() - 1).getMessage());
                //设置运单的状态为终止
                robotWaybill2.setWaybillStatus(RobotWaybillStatusEnum.TERMINATE.getCode());
                //设置造成运单状态为终止的错误描述
                robotWaybill2.setErrorMessage(errors.get(errors.size() - 1).getMessage());
                //设置运单完成时间
                robotWaybill2.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                //设置执行耗时
                robotWaybill2.setExecutionTime(robotWaybill2.getCompleteTime().getTime() - robotWaybill2.getOrderTime().getTime());
                saveOrUpdate(robotWaybill2);
            } else {
                //机器人执行运单任务的第一步成功，则把该步骤的运行情况存入到RobotWaybillDetail中
                RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybill.getId());
                if (newDetail != null) {
                    newDetail.setWaybillStatus("完成");
                    robotWaybillDetailService.saveOrUpdate(newDetail);
                }

                RobotWaybillDetail robotWaybillDetail = new RobotWaybillDetail();
                robotWaybillDetail.setWaybillId(robotWaybill.getId());
                //设置库位、操作和运行状态
                robotWaybillDetail.setStorageLoc(list.get(1).getStation());
                robotWaybillDetail.setOperation("导航");
                robotWaybillDetail.setWaybillStatus("运行中");
                robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
                robotWaybillDetailService.save(robotWaybillDetail);

                //发送导航指令完成后，延迟之后再执行定时任务
                if (dto.isSuccess()) {
                    String cron = "*/1 * * * * ?";
                    customScheduledTaskRegistrar.addTriggerTask(robotWaybill.getId(),
                            //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                            () -> {
                                try {
                                    task(taskId, robotWaybill.getId(), robotWaybill.getId(), dto.getRobotIp(), vehicle, selectTask(taskId, vehicle, robotWaybill.getId()), taskParameterList);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }, triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
                }
            }
        }
        return dto.isSuccess();
    }

    /**
     * 运行机器人充电任务
     *
     * @param vehicle
     * @param startPoint
     * @param endPoint
     * @return
     * @throws Exception
     */
    @Override
    public boolean runChargeTask(String vehicle, String startPoint, String endPoint) throws Exception {
        String taskId = "f4bcbe5dce1f83314fc2fe3abdfe140a";
        //获取当前机器人没有完成的充电任务运单，如果有未完成的运单，则停止执行同样充电任务
        List<RobotWaybill> unCompletedWaybills = getUnCompletedWaybill(vehicle, taskId);
        if (unCompletedWaybills.size() > 0) {
            return false;
        }
        //获取任务编辑中的任务列表
        String taskParameterList = robotTaskService.getTaskParameterList(taskId);
        List<RobotRunDTO> taskList = getTaskList(taskId, taskParameterList);
        if (taskList.size() <= 0) {
            throw new Exception("请检查任务是否输入！");
        }
        RobotRunDTO robotRunDTO1 = taskList.get(0);
        robotRunDTO1.setVehicle(vehicle);
        String groupName = robotRunDTO1.getGroupName();
        robotRunDTO1.setStation(startPoint);

        RobotRunDTO robotRunDTO2 = taskList.get(1);
        robotRunDTO2.setStation(endPoint);

        List<RobotRunDTO> newTaskList = new ArrayList<>();
        newTaskList.add(robotRunDTO1);
        newTaskList.add(robotRunDTO2);

        List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(vehicle, RobotMapPointAttrEnum.PARK_POINT.getCode());
        if (robotBindAttr.size() > 0) {
            RobotRunDTO firstRunDTO = new RobotRunDTO();
            firstRunDTO.setVehicle(vehicle);
            firstRunDTO.setStation(robotBindAttr.get(0).getPoint());
            firstRunDTO.setInstruction("机器人路径导航");
            newTaskList.add(firstRunDTO);
        }

        //保存最新的任务运行步骤
        RobotTaskStep step = new RobotTaskStep();
        step.setTaskId(taskId);
        step.setRobotName(vehicle);
        step.setTaskStep(JSON.toJSONString(newTaskList));
        step.setOperateTime(System.currentTimeMillis());
        robotTaskStepService.save(step);

        //创建运单,设置任务ID、机器人名字,设置机器人通信方式为WIFI通信
        RobotCommonResVO resVO = setRobotWaybill(null, null, null, taskId, vehicle, groupName, taskParameterList, newTaskList, "WIFI运行");
        return resVO.isSuccess();
    }

    /**
     * 待执行的任务
     *
     * @return
     */
    public List<RobotRunDTO> selectTask(String taskId, String vehicle, String waybillId) {
        String taskStep = robotTaskStepService.findOne(taskId, vehicle, waybillId).getTaskStep();
        //将json字符串转成的对象集合
        List<RobotRunDTO> list = JSON.parseArray(taskStep, RobotRunDTO.class);
        //由于list任务中机器人名称可能为空，因此在此统一赋值
        for (RobotRunDTO robotRunDTO : list) {
            if (robotRunDTO.getVehicle().equals("")) {
                robotRunDTO.setVehicle(vehicle);
            }
        }
        return list;
    }

    /**
     * 获取机器人顶升机构状态
     *
     * @param robotIp
     * @return
     */
    private String getRobotJackStatus(String robotIp) {
        //打印当前的顶升机构状态
        TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_STATUS_PORT, ProtocolConstant.JACK_STATUS);
        String jackRes = (String) redisUtil.stringWithGet("robot_status_jack_res");
        RobotJackDTO jackDTO = JSON.parseObject(jackRes, RobotJackDTO.class);
        return Objects.requireNonNull(JackStatusEnum.getByCode(jackDTO.getJackState())).getName();
    }

    /* ************* 通用modbus读写 (leo add) *************
     * ---参数说明---
     * ip：设备IP地址，offset：寄存器偏移地址，operationType：操作类型（0：读取线圈，1：写入线圈，2：读取保持寄存器，3：写入保持寄存器，value：写入值（仅写入操作时有效）
     * @param robotRunDTO
     */
    private boolean modbusRwOperation(RobotRunDTO robotRunDTO) {

        String stationPoint = robotRunDTO.getStation();
        String ip = robotRunDTO.getModbusParam().getIp();
        Integer port = robotRunDTO.getModbusParam().getPort();
        Integer slaveId = robotRunDTO.getModbusParam().getSlaveId();
        Integer offset = robotRunDTO.getModbusParam().getOffset();
        Integer operationType = robotRunDTO.getModbusParam().getOperationType();
        Integer value = robotRunDTO.getModbusParam().getValue();

        int functionCode;
        ModbusTcpRead modbusOperation;

        Object modbusRes = null;
        Object modbusResWrite = null;
        boolean isModbusRwOperationOver = false;

        switch (operationType) {
            case 1: // 读取线圈
                functionCode = ConstFunCode.func01;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.BooleanType, ip, port);
                break;
            case 2: // 写入线圈
                functionCode = ConstFunCode.func05;
                modbusOperation = new ModbusTcpWrite(slaveId, functionCode, offset, ConstDataType.BooleanType, ip, port);
                ((ModbusTcpWrite) modbusOperation).setWriteValue(value == null ? "0" : (value == 0 ? "0" : "1"));
                break;
            case 3: // 读取保持寄存器
                functionCode = ConstFunCode.func03;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);
                break;
            case 4: // 写入保持寄存器
                functionCode = ConstFunCode.func06;
                modbusOperation = new ModbusTcpWrite(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);
                ((ModbusTcpWrite) modbusOperation).setWriteValue(value == null ? "0" : String.valueOf(value));
                break;
            default:
                throw new IllegalArgumentException("不受支持的操作类型");
        }

        // 分别处理读和写操作
        if (modbusOperation instanceof ModbusTcpWrite) {
            // 调用写方法
            modbusResWrite = ModbusUtils.writeTcpModbusByFunCode(((ModbusTcpWrite) modbusOperation));
            if (value == Integer.parseInt(modbusResWrite.toString())) {
                log.info("modbus写入结果: {}", modbusResWrite);
                isModbusRwOperationOver = true;

            } else {
                isModbusRwOperationOver = false;
                log.error("modbus写入结果: {}", modbusResWrite);
            }
        } else if (modbusOperation instanceof ModbusTcpRead) {
            // 调用读方法
            modbusRes = ModbusUtils.readModbusByTcp((ModbusTcpRead) modbusOperation);
            if (modbusRes != null && modbusRes.toString().length() > 0) {
                isModbusRwOperationOver = true;
            } else {
                isModbusRwOperationOver = false;
                log.error("modbus读取结果: {}", modbusRes);
            }
        } else {
            throw new IllegalStateException("异常值类型: " + modbusOperation.getClass().getName());
        }

        return isModbusRwOperationOver;
    }

    /* ************* 通用modbus等待 (leo add) *************
     * ---参数说明---
     * ip：设备IP地址，offset：寄存器偏移地址，3：写入保持寄存器，value：目标值
     * @param robotRunDTO
     */
    public boolean modbusWaitOperation(RobotRunDTO robotRunDTO) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);// ThreadPoolExecutor创建线程池容错性更强
        String stationPoint = robotRunDTO.getStation();
        String ip = robotRunDTO.getModbusParam().getIp();
        Integer port = robotRunDTO.getModbusParam().getPort();
        Integer slaveId = robotRunDTO.getModbusParam().getSlaveId();
        Integer offset = robotRunDTO.getModbusParam().getOffset();
        Integer targetValue = robotRunDTO.getModbusParam().getValue();

        final Object[] modbusRes = {null};
        final Object[] modbusResWrite = {null};

        //定义一个读取设备状态的读取器
        ModbusTcpRead modbusTcpRead = new ModbusTcpRead(slaveId, ConstFunCode.func03, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);
        //定义一个读取写入地址的读取器，地址在配置文件中定义
        ModbusTcpRead modbusTcpReadAddressWrite = new ModbusTcpRead(slaveId, ConstFunCode.func03, registerWriteOffset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);

        // 读取设备状态
        modbusRes[0] = ModbusUtils.readModbusByTcp(modbusTcpRead);
        // 读取写入状态
        modbusResWrite[0] = ModbusUtils.readModbusByTcp(modbusTcpReadAddressWrite);
        CountDownLatch latch1 = new CountDownLatch(1);

        // 定义一个Runnable任务来检查设备值和写入值是否一致
        ScheduledExecutorService finalScheduler = scheduler;
        Runnable checkEquipAndWriteValueTask = () -> {
            try {
                // 读取设备状态
                modbusRes[0] = ModbusUtils.readModbusByTcp(modbusTcpRead);
                log.info("modbus设备状态: {}", modbusRes[0]);
                // 读取写入状态
                modbusResWrite[0] = ModbusUtils.readModbusByTcp(modbusTcpReadAddressWrite);
                log.info("modbus写入状态: {}", modbusResWrite[0]);

                // 检查modbus设备值和写入值是否一致
                if (Integer.parseInt(modbusRes[0].toString()) == Integer.parseInt(modbusResWrite[0].toString())) {
                    // 如果一致，则取消后续的定时任务并释放锁
                    latch1.countDown();
                    finalScheduler.shutdown();
                }
            } catch (Exception e) {
                finalScheduler.shutdown();
            }
        };
        // 首先判断一次设备值和写入值是否一致，如果一致，则直接释放锁
        if (Integer.parseInt(modbusRes[0].toString()) == Integer.parseInt(modbusResWrite[0].toString())) {
            latch1.countDown(); // 释放锁
        } else {
            // 否则计划定时任务，2秒后开始执行，之后每间隔2秒执行一次
            if (scheduler.isShutdown()) {
                scheduler = Executors.newScheduledThreadPool(1);
            }
            scheduler.scheduleAtFixedRate(checkEquipAndWriteValueTask, 2, 2, TimeUnit.SECONDS);
        }
        try {
            // 等待任务完成或超时
            boolean completed = latch1.await(90, TimeUnit.SECONDS);
            if (!completed) {
                log.info("任务超时，准备呼叫现场人员检查情况");
                // 呼叫现场人员手动干预

                System.out.println("正在呼叫值班人员检查情况，请等待");//调用呼叫器相关接口
                // 等待现场人员检查完毕
                latch1.await();
                log.info("现场人员检查完毕，当前modbus设备状态值: {}", modbusRes[0]);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 确保调度器在任务完成后关闭
        scheduler.shutdown();

        log.info("modbus设备状态值: {}", modbusRes[0]);

        //————————————————————目标值分割线————————————————————————

        CountDownLatch latch2 = new CountDownLatch(1);

        final AtomicBoolean result = new AtomicBoolean(false);

        // 定义一个Runnable任务来检查Modbus设备状态值与目标值是否一致
        ScheduledExecutorService finalScheduler1 = scheduler;
        Runnable checkModbusReadEquipValueTask = () -> {
            try {
                // 获取modbus设备状态值
                modbusRes[0] = ModbusUtils.readModbusByTcp(modbusTcpRead);
                log.info("modbus设备状态值: {}", modbusRes[0]);
                // 检查modbus读取值是否与期望值相等
                if (targetValue == Integer.parseInt(modbusRes[0].toString())) {
                    // 如果相等，则取消后续的定时任务并设置结果为true
                    result.set(true);
                    latch2.countDown();
                    finalScheduler1.shutdown();
                }
            } catch (Exception e) {
                finalScheduler1.shutdown();
            }
        };
        // 首先读取一次moudbus值，如果与期望值相等，则直接释放阻塞锁
        if (modbusRes[0].equals(targetValue)) {
            result.set(true);
            latch2.countDown();
        } else {
            // 否则计划定时任务，2秒后开始执行，之后每间隔2秒执行一次
            if (scheduler.isShutdown()) {
                scheduler = Executors.newScheduledThreadPool(1);
            }
            scheduler.scheduleAtFixedRate(checkModbusReadEquipValueTask, 2, 2, TimeUnit.SECONDS);
        }

        try {
            // 等待任务完成或超时
            boolean completed = latch2.await(90, TimeUnit.SECONDS);
            if (!completed) {
                log.info("任务超时，准备呼叫现场人员检查情况");
                // 呼叫现场人员手动干预

                System.out.println("正在呼叫值班人员检查情况，请等待");//调用呼叫器相关接口
                // 等待现场人员检查完毕
                latch2.await();
                log.info("现场人员检查完毕，当前modbus设备状态值: {}", modbusRes[0]);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 确保调度器在任务完成后关闭
        scheduler.shutdown();

        log.info("modbus设备状态值: {}", modbusRes[0]);
        // 读取结果通过推流服务推送至前端的响应块
//        RealTimeDataVO realTimeDataVO = new RealTimeDataVO();
//        realTimeDataVO.setCode("200");
//        realTimeDataVO.setMsg("modbus读取成功");
//        realTimeDataVO.setData(modbusRes.toString());
//        pusherLiteService.sendRealTimeMessage(realTimeDataVO);
        return result.get(); // 返回结果

    }


    /**
     * 设置顶升状态数据
     *
     * @param robotIp
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    private void setJackUpData(String robotIp, RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        if (!map.containsKey(robotIp + PortConstant.ROBOT_OTHER_PORT)) {
            //连接机器人其它（顶升、顶降）端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_OTHER_PORT);
        }
        TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_OTHER_PORT, ProtocolConstant.JACK_LOAD);
        //设置运单动作为顶升的子任务
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        robotWaybillDetail.setOperation("顶升");
        robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
        robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        robotWaybillDetail.setWaybillStatus("运行中");
        robotWaybillDetailService.save(robotWaybillDetail);
    }

    /**
     * 设置路径导航配合顶升状态数据
     *
     * @param robotIp
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    private void setJackUpAndNavigationData(String robotIp, RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        String convertString = "{\"source_id\":\"" + robotRunDTO.getStation() + "\",\"id\":\"" + robotRunDTO.getStation() + "\",\"operation\": \"JackLoad\"," + "\"recognize\": false," + "\"use_down_pgv\": false," + "\"use_pgv\": false}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        if (dataLength.length() == 4) {
            dataLength = dataLength;
        } else if (dataLength.length() == 3) {
            dataLength = "0" + dataLength;
        } else if (dataLength.length() == 2) {
            dataLength = "00" + dataLength;
        } else if (dataLength.length() == 1) {
            dataLength = "000" + dataLength;
        }
        String instruction = "5A0100010000" + dataLength + "0BEB000000000000" + data;
        if (!map.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人其它端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        if (!result.isSuccess()) {
            TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        }
        //设置运单动作为顶升的子任务
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        robotWaybillDetail.setOperation("顶升");
        robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
        robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        robotWaybillDetail.setWaybillStatus("运行中");
        robotWaybillDetailService.save(robotWaybillDetail);
    }

    /**
     * 设置路径导航配合顶降状态数据
     *
     * @param robotIp
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    private void setJackUpLoadAndNavigationData(String robotIp, RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        String convertString = "{\"source_id\":\"" + robotRunDTO.getStation() + "\",\"id\":\"" + robotRunDTO.getStation() + "\",\"operation\": \"JackUnload\"," + "\"recognize\": false," + "\"use_down_pgv\": false," + "\"use_pgv\": false}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        if (dataLength.length() == 4) {
            dataLength = dataLength;
        } else if (dataLength.length() == 3) {
            dataLength = "0" + dataLength;
        } else if (dataLength.length() == 2) {
            dataLength = "00" + dataLength;
        } else if (dataLength.length() == 1) {
            dataLength = "000" + dataLength;
        }
        String instruction = "5A0100010000" + dataLength + "0BEB000000000000" + data;
        if (!map.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人其它端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        if (!result.isSuccess()) {
            TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        }
        //设置运单动作为顶升的子任务
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        robotWaybillDetail.setOperation("顶降");
        robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
        robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        robotWaybillDetail.setWaybillStatus("运行中");
        robotWaybillDetailService.save(robotWaybillDetail);
    }

    /**
     * 设置顶降状态数据
     *
     * @param robotIp
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    private void setJackUpLoadData(String robotIp, RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        if (!map.containsKey(robotIp + PortConstant.ROBOT_OTHER_PORT)) {
            //连接机器人其它（顶升、顶降）端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_OTHER_PORT);
        }
        TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_OTHER_PORT, ProtocolConstant.JACK_UPLOAD);
        //设置运单动作为顶降的子任务
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        robotWaybillDetail.setOperation("顶降");
        robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
        robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        robotWaybillDetail.setWaybillStatus("运行中");
        robotWaybillDetailService.save(robotWaybillDetail);
    }

    /**
     * 设置路径导航状态数据
     *
     * @param robotWaybillDetail
     * @param list
     */
    private void setPathNavigationData(String taskId, RobotWaybillDetail robotWaybillDetail, String vehicle, List<RobotRunDTO> list) throws Exception {
        if (list.size() >= 3) {
            list.remove(0);
            //设置运单动作为Navigation的子任务
            RobotWaybillDetail waybillDetail = new RobotWaybillDetail();
            waybillDetail.setWaybillId(robotWaybillDetail.getWaybillId());
            waybillDetail.setStorageLoc(list.get(1).getStation());
            waybillDetail.setOperation("导航");
            //任务执行完成，则删除子任务，并把这个map保存到数据库
            RobotTaskStep step = new RobotTaskStep();
            step.setTaskId(taskId);
            step.setRobotName(vehicle);
            step.setWaybillId(robotWaybillDetail.getWaybillId());
            step.setTaskStep(JSON.toJSONString(list));
            step.setOperateTime(System.currentTimeMillis());
            robotTaskStepService.save(step);

            RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
            if (newDetail != null) {
                newDetail.setWaybillStatus("完成");
                robotWaybillDetailService.saveOrUpdate(newDetail);
            }
            waybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
            waybillDetail.setWaybillStatus("运行中");
            robotWaybillDetailService.save(waybillDetail);
            //运行任务列表中的子任务
            startContinuousNavigation(robotWaybillDetail.getWaybillId(), vehicle, list);
        }
    }

    /**
     * 设置库位检测的数据
     *
     * @param taskId
     * @param robotWaybillDetail
     * @param vehicle
     * @param list
     * @throws Exception
     */
    private void setStorageCheckData(String taskId, RobotWaybillDetail robotWaybillDetail, String vehicle, List<RobotRunDTO> list) throws Exception {
        if (list.size() >= 3) {
            list.remove(0);
            //设置运单动作为Navigation的子任务
            RobotWaybillDetail waybillDetail = new RobotWaybillDetail();
            waybillDetail.setWaybillId(robotWaybillDetail.getWaybillId());
            waybillDetail.setStorageLoc(list.get(1).getStation());
            waybillDetail.setOperation("库位检测");
            //任务执行完成，则删除子任务，并把这个map保存到数据库
            RobotTaskStep step = new RobotTaskStep();
            step.setWaybillId(robotWaybillDetail.getWaybillId());
            step.setTaskId(taskId);
            step.setRobotName(vehicle);
            List<RobotRunDTO> newRunList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (i == 1) {
                    RobotRunDTO robotRunDTO = list.get(1);
                    String substring = robotRunDTO.getStation().substring(2);
                    int locValue = Integer.parseInt(substring) + 100;
                    robotRunDTO.setStation("LM" + locValue);
                    newRunList.add(robotRunDTO);
                } else {
                    newRunList.add(list.get(i));
                }
            }
            step.setTaskStep(JSON.toJSONString(newRunList));
            step.setOperateTime(System.currentTimeMillis());
            robotTaskStepService.save(step);

            RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
            if (newDetail != null) {
                newDetail.setWaybillStatus("完成");
                robotWaybillDetailService.saveOrUpdate(newDetail);
            }
            waybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
            waybillDetail.setWaybillStatus("运行中");
            robotWaybillDetailService.save(waybillDetail);
            //运行任务列表中的子任务
            log.info("进行了库位检查：{}", newRunList);
            startContinuousNavigation(robotWaybillDetail.getWaybillId(), vehicle, newRunList);
        }
    }

    /**
     * 设置modbus读写的操作数据
     *
     * @param taskId
     * @param robotWaybillDetail
     * @param vehicle
     * @param list
     */
    public void setModbusRwOperateData(String taskId, RobotWaybillDetail robotWaybillDetail, String vehicle, List<RobotRunDTO> list) throws Exception {
        if (list.size() >= 3) {
            list.remove(0);
            //设置运单动作为Navigation的子任务
            RobotWaybillDetail waybillDetail = new RobotWaybillDetail();
            waybillDetail.setWaybillId(robotWaybillDetail.getWaybillId());
            waybillDetail.setStorageLoc(list.get(1).getStation());
            waybillDetail.setOperation("Modbus读写");
            //任务执行完成，则删除子任务，并把这个map保存到数据库
            RobotTaskStep step = new RobotTaskStep();
            step.setTaskId(taskId);
            step.setRobotName(vehicle);
            step.setWaybillId(robotWaybillDetail.getWaybillId());
            step.setTaskStep(JSON.toJSONString(list));
            step.setOperateTime(System.currentTimeMillis());
            robotTaskStepService.save(step);

            RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
            if (newDetail != null) {
                newDetail.setWaybillStatus("完成");
                robotWaybillDetailService.saveOrUpdate(newDetail);
            }
            waybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
            waybillDetail.setWaybillStatus("运行中");
            robotWaybillDetailService.save(waybillDetail);
            //运行任务列表中的子任务
            startContinuousNavigation(robotWaybillDetail.getWaybillId(), vehicle, list);
        }
    }

    /**
     * 设置modbus等待操作
     *
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    public void setModbusWaitOperateData(RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        //设置运单动作为modbus等待的子任务
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        robotWaybillDetail.setOperation("Modbus等待");
        robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
        robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        robotWaybillDetail.setWaybillStatus("运行中");
        robotWaybillDetailService.save(robotWaybillDetail);
    }

    /**
     * 设置任务执行完成数据
     *
     * @param waybillId
     * @param key
     */
    private void setTaskCompletedData(String waybillId, String key, String vehicle) throws Exception {
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(waybillId);
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        //所有任务都执行结束，则删除定时任务
        RobotWaybill robotWaybill = getById(waybillId);
        //设置运单的状态为完成
        robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.COMPLETE.getCode());
        //设置运单完成时间
        robotWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
        //设置执行耗时
        if ((robotWaybill.getCompleteTime() != null && robotWaybill.getOrderTime() != null)) {
            robotWaybill.setExecutionTime(robotWaybill.getCompleteTime().getTime() - robotWaybill.getOrderTime().getTime());
        }
        saveOrUpdate(robotWaybill);
        //如果所有任务都执行完成，则删除定时任务
        customScheduledTaskRegistrar.removeTriggerTask(key);
        //关闭机器人连接
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(vehicle);
        TcpClientThread.interruptThread(robots.get(0).getCurrentIp(), PortConstant.ROBOT_PUSH_PORT);
        //任务结束设置机器人初始状态
        setRobotInitialValue(robotWaybill.getRobotName());
        //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
        completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
    }

    /**
     * 如果由MES下发的指令，则同步完成状态给MES系统
     *
     * @param mesWaybillId
     * @param wmsWaybillId
     * @throws Exception
     */
    @Override
    public void completeTaskSyncToOtherSystem(String mesWaybillId, String wmsWaybillId) throws Exception {
        if (mesWaybillId != null || wmsWaybillId != null) {
            log.info("RMS向第三方系统同步运单状态");
            RobotWaybillResultVO waybillExecuteResult = getWaybillExecuteResult(mesWaybillId, wmsWaybillId);
            if (waybillExecuteResult != null) {
                String jsonData = JSON.toJSONString(waybillExecuteResult, SerializerFeature.DisableCircularReferenceDetect);
                log.info("RMS向第三方系统同步运单状态的数据:{}", jsonData);
                try {
                    if (mesWaybillId != null && wmsWaybillId != null) {
                        //同步运单状态到MES
                        String mesResStr = HttpClientUtil.clientPost(syncTaskStatusMesUrl, HttpMethod.POST, jsonData);
                        MESResDTO mesResp = JSON.parseObject(mesResStr, MESResDTO.class);
                        boolean requestMesFlag;
                        if (mesResp.getCode() == 200) {
                            requestMesFlag = true;
                            log.info("同步运单状态到第三方系统成功：{}", mesResp);
                        } else {
                            requestMesFlag = false;
                            log.info("同步运单状态到第三方系统失败：{}", mesResp);
                        }
                        robotTaskLogService.saveSyncMesWaybill(waybillExecuteResult, mesWaybillId, wmsWaybillId, currentServerIp, requestMesFlag, mesResp.getMsg());

                        //同步运单状态到WMS
                        String wmsResStr = HttpClientUtil.clientPost(syncTaskStatusWmsUrl, HttpMethod.POST, jsonData);
                        MESResDTO wmsResp = JSON.parseObject(wmsResStr, MESResDTO.class);
                        boolean requestWmsFlag;
                        if (wmsResp.getCode() == 200) {
                            requestWmsFlag = true;
                            log.info("同步运单状态到第三方系统成功：{}", wmsResp);
                        } else {
                            requestWmsFlag = false;
                            log.info("同步运单状态到第三方系统失败：{}", wmsResp);
                        }
                        robotTaskLogService.saveSyncMesWaybill(waybillExecuteResult, mesWaybillId, wmsWaybillId, currentServerIp, requestWmsFlag, wmsResp.getMsg());
                    }

                    //同步运单状态到MES
                    if (mesWaybillId != null && wmsWaybillId == null) {
                        String mesResStr = HttpClientUtil.clientPost(syncTaskStatusMesUrl, HttpMethod.POST, jsonData);
                        MESResDTO mesResp = JSON.parseObject(mesResStr, MESResDTO.class);
                        boolean requestMesFlag;
                        if (mesResp.getCode() == 200) {
                            requestMesFlag = true;
                            log.info("同步运单状态到第三方系统成功：{}", mesResp);
                        } else {
                            requestMesFlag = false;
                            log.info("同步运单状态到第三方系统失败：{}", mesResp);
                        }
                        robotTaskLogService.saveSyncMesWaybill(waybillExecuteResult, mesWaybillId, wmsWaybillId, currentServerIp, requestMesFlag, mesResp.getMsg());
                    }

                    //同步运单状态到WMS
                    if (mesWaybillId == null) {
                        String wmsResStr = HttpClientUtil.clientPost(syncTaskStatusWmsUrl, HttpMethod.POST, jsonData);
                        MESResDTO wmsResp = JSON.parseObject(wmsResStr, MESResDTO.class);
                        boolean requestWmsFlag;
                        if (wmsResp.getCode() == 200) {
                            requestWmsFlag = true;
                            log.info("同步运单状态到第三方系统成功：{}", wmsResp);
                        } else {
                            requestWmsFlag = false;
                            log.info("同步运单状态到第三方系统失败：{}", wmsResp);
                        }
                        robotTaskLogService.saveSyncMesWaybill(waybillExecuteResult, mesWaybillId, wmsWaybillId, currentServerIp, requestWmsFlag, wmsResp.getMsg());
                    }

                } catch (Exception e) {
                    log.info("同步运单状态到第三方系统失败：{}", e.getMessage());
                    //设置运单执行状态
                    RobotWaybill robotWaybill = getById(waybillExecuteResult.getWaybillId());
                    setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), e.getMessage());
                    robotTaskLogService.saveSyncMesWaybill(waybillExecuteResult, mesWaybillId, wmsWaybillId, currentServerIp, false, e.getMessage());
                    throw new Exception("同步运单状态到第三方系统失败！");
                }
            } else {
                robotTaskLogService.saveSyncMesWaybill(waybillExecuteResult, mesWaybillId, wmsWaybillId, currentServerIp, false, "不存在此运单号！");
            }
        }
    }

    /**
     * 设置任务执行失败数据
     *
     * @param waybillId
     * @param key
     */
    @Override
    public void setTaskFailData(String waybillId, String key, String vehicle, String errorMsg) throws Exception {
        //如果导航失败，则需要修改最后导致失败的子任务的状态为失败
        List<RobotWaybillDetail> listByWaybillId = robotWaybillDetailService.getListByWaybillId(waybillId);
        if (listByWaybillId.size() > 0) {
            RobotWaybillDetail finalRobotWaybillDetail = listByWaybillId.get(listByWaybillId.size() - 1);
            finalRobotWaybillDetail.setWaybillStatus("失败");
            robotWaybillDetailService.saveOrUpdate(finalRobotWaybillDetail);
        }

        //如果导航失败，即机器人执行运单任务，出现错误
        RobotWaybill robotWaybill = getById(waybillId);
        //设置运单的状态为终止
        robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.TERMINATE.getCode());
        //设置运单完成时间
        robotWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
        //设置执行耗时
        if ((robotWaybill.getCompleteTime() != null && robotWaybill.getOrderTime() != null)) {
            robotWaybill.setExecutionTime(robotWaybill.getCompleteTime().getTime() - robotWaybill.getOrderTime().getTime());
        }
        if (errorMsg == null) {
            List<RobotWarningInfoDTO> errors = new ArrayList<>();
            List<RobotWarningInfoDTO> errorList = map.get(robotWaybill.getRobotName()).getErrors();
            List<RobotWarningInfoDTO> fatalList = map.get(robotWaybill.getRobotName()).getFatals();
            errors.addAll(errorList);
            errors.addAll(fatalList);
            if (errors.size() > 0) {
                robotWaybill.setErrorMessage(errors.get(errors.size() - 1).getMessage());
            } else {
                robotWaybill.setErrorMessage("");
            }
        } else {
            robotWaybill.setErrorTime(new Timestamp(System.currentTimeMillis()));
            robotWaybill.setErrorMessage(errorMsg);
        }

        saveOrUpdate(robotWaybill);
        log.info("机器人发生了错误，任务已终止：{},错误原因：{}", robotWaybill.getId(), robotWaybill.getErrorMessage());
        //任务结束设置机器人初始状态
        setRobotInitialValue(vehicle);
        //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
        completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
        //关闭机器人连接
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(vehicle);
        TcpClientThread.interruptThread(robots.get(0).getCurrentIp(), PortConstant.ROBOT_PUSH_PORT);
        //关闭自动门的定时任务
        customScheduledTaskRegistrar.removeTriggerTask(robotWaybill.getRobotName());
        //如果所有任务都执行完成，则删除定时任务
        customScheduledTaskRegistrar.removeTriggerTask(key);
        //关闭当前运行线程
        closeCurrentRobotRunThread(robotWaybill.getRobotName());
    }

    /**
     * 任务结束设置机器人初始状态
     *
     * @param vehicle
     */
    @Override
    public void setRobotInitialValue(String vehicle) {
        if (vehicle != null) {
            //任务取消后，清楚掉当前未执行的运单
            List<RobotNavigation> robotNavigations = robotNavigationService.findByRobotName(vehicle);
            if (robotNavigations.size() > 0) {
                RobotNavigation robotNavigation = robotNavigations.get(0);
                robotNavigationService.removeById(robotNavigation.getId());
            }
            //设置机器人的状态为空闲
            RobotBasicInfo robotBasicInfo = robotBasicInfoService.findByName(vehicle).get(0);
            robotBasicInfo.setLeisure(Constant.STATE_VALID);
            //删除运单时，移除占用的站点
            lockedPoints.remove(vehicle);
            //移除锁定路径和站点
            dmsLockedPointMap.remove(vehicle);
            //-------------------------------------(混合调度)-------------------------------------------------------------------------------
            //移除管控区域占用状态
            //子任务执行完成，释放掉占用的管控区域
            List<RobotDmsEditor> listByAreaTypeAndOccupied = robotDmsEditorService.findListByAreaTypeAndOccupiedAndRobotName(2, 1, vehicle);
            if (listByAreaTypeAndOccupied != null) {
                for (RobotDmsEditor robotDmsEditor : listByAreaTypeAndOccupied) {
                    //设置管控区域的状态为非占用
                    robotDmsEditor.setOccupiedRobotName("");
                    robotDmsEditor.setOccupiedStatus(0);
                    robotDmsEditorService.saveOrUpdate(robotDmsEditor);
                }
            }
            //--------------------------------------------------------------------------------------------------------------------------
            robotBasicInfoService.saveOrUpdate(robotBasicInfo);
        }
    }

    /**
     * 机器人取消、终止时关闭当前机器人运行线程
     *
     * @param vehicle
     */
    @Override
    public void closeCurrentRobotRunThread(String vehicle) {
        //开启新的路径规划之前，关闭之前的任务线程
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);
        // 遍历线程
        for (int i = 0; i < noThreads; i++) {
            String threadName = lstThreads[i].getName();
            if (threadName.startsWith("pathPlanningThread" + vehicle)) {
                //强制关闭线程
                lstThreads[i].stop();
                log.info("关闭了线程：{}", threadName);
            }
        }
    }

    /**
     * 设置任务取消的数据
     *
     * @param waybillId
     * @param key
     */
    @Override
    public void setTaskCancelData(String waybillId, String key, String vehicle, List<RobotRunDTO> list) throws Exception {
        //如果导航失败，则需要修改最后导致失败的子任务的状态为失败
        List<RobotWaybillDetail> listByWaybillId = robotWaybillDetailService.getListByWaybillId(waybillId);
        if (listByWaybillId.size() > 0) {
            RobotWaybillDetail finalRobotWaybillDetail = listByWaybillId.get(listByWaybillId.size() - 1);
            finalRobotWaybillDetail.setWaybillStatus("取消");
            robotWaybillDetailService.saveOrUpdate(finalRobotWaybillDetail);
        }

        //如果导航取消，设置运单执行数据
        RobotWaybill robotWaybill = getById(waybillId);
        //设置运单的状态为暂停
        robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.CANCEL.getCode());
        //设置运单完成时间
        robotWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
        //设置执行耗时
        if ((robotWaybill.getCompleteTime() != null && robotWaybill.getOrderTime() != null)) {
            robotWaybill.setExecutionTime(robotWaybill.getCompleteTime().getTime() - robotWaybill.getOrderTime().getTime());
        }
        //保存剩下的待执行的任务
        robotWaybill.setPendingTask(JSON.toJSONString(list));
        saveOrUpdate(robotWaybill);
        //关闭当前运行线程
        closeCurrentRobotRunThread(robotWaybill.getRobotName());
        //关闭自动门的定时任务
        customScheduledTaskRegistrar.removeTriggerTask(robotWaybill.getRobotName());
        //关闭智能充电桩的定时任务
        //当前是充电任务并且配置自动充电桩开启
        if (ChargeConstant.ROBOT_START_CHARGE.equals(robotWaybill.getTaskName()) && isIntelligentChargingStation) {
            //任务取消或者失败，则关闭开启充电桩的任务
            customScheduledTaskRegistrar.removeTriggerTask(robotWaybill.getRobotName() + ChargeConstant.ROBOT_START_CHARGE_KEY);
        }
        //如果所有任务取消，则删除定时任务
        customScheduledTaskRegistrar.removeTriggerTask(key);
        //任务结束设置机器人初始状态
        setRobotInitialValue(robotWaybill.getRobotName());
        //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
        completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());

        //关闭机器人连接
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(vehicle);
        TcpClientThread.interruptThread(robots.get(0).getCurrentIp(), PortConstant.ROBOT_PUSH_PORT);
    }

    /**
     * 设置等待DO数据
     *
     * @param waybillId
     * @param vehicle
     * @param robotIp
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    private void setWaitDoData(String waybillId, String vehicle, String robotIp, RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        String aDo = map.get(vehicle).getDO();
        List<RobotWaitDoDTO> dos = JSONObject.parseArray(aDo, RobotWaitDoDTO.class);
        for (RobotWaitDoDTO robotWaitDoDTO : dos) {
            if (Integer.parseInt(robotRunDTO.getWaitDoId()) == robotWaitDoDTO.getId()) {
                if (robotWaitDoDTO.getStatus()) {

                } else {
                    String convertString = "{\"id\":" + robotRunDTO.getWaitDoId() + ",\"status\":" + robotRunDTO.getWaitDoStatus() + "}";
                    String data = DataConvertUtil.convertStringToHex(convertString);
                    String dataLength = Integer.toHexString(data.length() / 2);
                    String instruction = "5A010001000000" + dataLength + "1771000000000000" + data;
                    Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
                    if (!map.containsKey(robotIp + PortConstant.ROBOT_OTHER_PORT)) {
                        //连接机器人其它（设置DO）端口
                        robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_OTHER_PORT);
                    }
                    TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_OTHER_PORT, instruction);
                    //设置运单动作为顶降的子任务
                    RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
                    if (newDetail != null) {
                        newDetail.setWaybillStatus("完成");
                        robotWaybillDetailService.saveOrUpdate(newDetail);
                    }
                    robotWaybillDetail.setOperation("等待DO");
                    robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
                    robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    robotWaybillDetail.setWaybillStatus("运行中");
                    robotWaybillDetailService.save(robotWaybillDetail);

                    RobotWaybill robotWaybill = getById(waybillId);
                    robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.WAIT_DO.getCode());
                    saveOrUpdate(robotWaybill);
                }
            }
        }
    }

    /**
     * 设置叉货数据
     *
     * @param robotIp
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    private void setForkLiftData(String robotIp, RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        if (!map.containsKey(robotIp + PortConstant.ROBOT_OTHER_PORT)) {
            //连接机器人其它（顶升、顶降）端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_OTHER_PORT);
        }
        String convertString = "{\"height\":" + robotRunDTO.getForkLiftHeight() + "}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        if (dataLength.length() == 1) {
            dataLength = "0" + dataLength;
        }
        String instruction = "5A010001000000" + dataLength + "1798000000000000" + data;
        TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_OTHER_PORT, instruction);
        //设置运单动作为顶降的子任务
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        robotWaybillDetail.setOperation("叉货");
        robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
        robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        robotWaybillDetail.setWaybillStatus("运行中");
        robotWaybillDetailService.save(robotWaybillDetail);
    }

    /**
     * 设置叉货BinTask数据
     *
     * @param robotIp
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    private void setForkLiftBinTaskData(String robotIp, RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        String substring = robotRunDTO.getStation().substring(2);
        int locValue = Integer.parseInt(substring) - 500;
        String convertString = "{\"binTask\":\"" + robotRunDTO.getForkLoadOrUnload() + "\",\"id\":\"" + "Loc-" + locValue + "\"}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        if (dataLength.length() == 4) {
            dataLength = dataLength;
        } else if (dataLength.length() == 3) {
            dataLength = "0" + dataLength;
        } else if (dataLength.length() == 2) {
            dataLength = "00" + dataLength;
        } else if (dataLength.length() == 1) {
            dataLength = "000" + dataLength;
        }
        String instruction = "5A0100010000" + dataLength + "0BEB000000000000" + data;
        if (!map.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人其它端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        if (!result.isSuccess()) {
            TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        }
        //设置运单动作为顶降的子任务
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        robotWaybillDetail.setOperation("叉货BinTask");
        robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
        robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        robotWaybillDetail.setWaybillStatus("运行中");
        robotWaybillDetailService.save(robotWaybillDetail);
    }

    /**
     * 设置辊筒下料数据
     *
     * @param robotIp
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    private void setRollerUnloadData(String robotIp, RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        if (!map.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人其它（顶升、顶降）端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        String convertString = "{\"name\":\"RollerUnLoad\"}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        if (dataLength.length() == 1) {
            dataLength = "0" + dataLength;
        }
        String instruction = "5A010001000000" + dataLength + "0C22000000000000" + data;
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        //设置运单动作为顶降的子任务
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        robotWaybillDetail.setOperation("辊筒下料");
        robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
        robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        robotWaybillDetail.setWaybillStatus("运行中");
        robotWaybillDetailService.save(robotWaybillDetail);
    }

    /**
     * 设置辊筒人工上料数据
     *
     * @param robotIp
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    private void setRollerLoadData(String robotIp, RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        if (!map.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人其它（顶升、顶降）端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        String convertString = "{\"name\":\"RollerLoad\"}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        if (dataLength.length() == 1) {
            dataLength = "0" + dataLength;
        }
        String instruction = "5A010001000000" + dataLength + "0C22000000000000" + data;
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        //设置运单动作为顶降的子任务
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        robotWaybillDetail.setOperation("辊筒人工上料");
        robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
        robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        robotWaybillDetail.setWaybillStatus("运行中");
        robotWaybillDetailService.save(robotWaybillDetail);
    }


    /**
     * 设置辊筒人工上料数据
     *
     * @param robotIp
     * @param robotWaybillDetail
     * @param robotRunDTO
     */
    private void setRollerAutoLoadData(String robotIp, RobotWaybillDetail robotWaybillDetail, RobotRunDTO robotRunDTO) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        if (!map.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人其它（顶升、顶降）端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        String convertString = "{\"name\":\"RollerAutoLoad\"}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        if (dataLength.length() == 1) {
            dataLength = "0" + dataLength;
        }
        String instruction = "5A010001000000" + dataLength + "0C22000000000000" + data;
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        //设置运单动作为顶降的子任务
        RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
        if (newDetail != null) {
            newDetail.setWaybillStatus("完成");
            robotWaybillDetailService.saveOrUpdate(newDetail);
        }
        robotWaybillDetail.setOperation("辊筒自动上料");
        robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
        robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
        robotWaybillDetail.setWaybillStatus("运行中");
        robotWaybillDetailService.save(robotWaybillDetail);
    }

    /**
     * 执行任务
     *
     * @param taskId            任务id
     * @param waybillId         运单id
     * @param key               定时任务主键
     * @param robotIp           机器人IP
     * @param vehicle           机器人名称
     * @param list              任务列表
     * @param taskParameterList 当前的任务参数列表
     */
    @Override
    public void task(String taskId, String waybillId, String key, String robotIp, String vehicle, List<RobotRunDTO> list, String taskParameterList) throws Exception {
        //设置机器人运单中各个子任务运行情况
        RobotWaybillDetail robotWaybillDetail = new RobotWaybillDetail();
        robotWaybillDetail.setWaybillId(waybillId);
        //如果map为空，则抛出异常
        if (map.isEmpty()) {
            //导航失败，设置任务执行失败数据
            setTaskFailData(waybillId, key, vehicle, "机器人不在线或者机器人没有连接");
            return;
        }

        RobotRunDTO parkRobotRunDTO = list.get(list.size() - 1);
        //获取站点属性是待命点的列表
        List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(vehicle, RobotMapPointAttrEnum.PARK_POINT.getCode());
        if (robotBindAttr.size() > 0) {
            parkRobotRunDTO.setVehicle(vehicle);
            if (Objects.equals(taskId, TaskIdConstant.AUTO_CHARGE_TASK_ID)) {
                list.remove(list.size() - 1);
            } else {
                parkRobotRunDTO.setStation(robotBindAttr.get(0).getPoint());
                parkRobotRunDTO.setInstruction("机器人路径导航");
                list.remove(list.size() - 1);
                list.add(parkRobotRunDTO);
            }
        }
        //获取站点属性是充电点的列表
//        List<RobotAttribute> chargeAttrs = robotAttributeService.getRobotBindAttr(vehicle, RobotMapPointAttrEnum.CHARGE_POINT.getCode());
//        List<String> chargePoints = new ArrayList<>();
//        for (RobotAttribute chargeAttr : chargeAttrs) {
//            chargePoints.add(chargeAttr.getPoint());
//        }
        //获取当前的机器人位置
        String currentStation = map.get(vehicle).getCurrentStation();

        //子任务的导航终点
        String endPoint;

        //配置推送端口之后，开始推送
        robotRealTimeInfoService.getRealTimeStatusInfo(robotIp);
        String taskStatus = map.get(vehicle).getTaskStatus();
        if ("导航失败".equals(taskStatus) || "取消导航".equals(taskStatus)) {
            //加一个延时，用于确保第一个任务已执行
            Thread.sleep(8000);
            taskStatus = map.get(vehicle).getTaskStatus();
        }
        //如果导航完成
        switch (taskStatus) {
            case "导航完成":
            case "没有导航":
                if (list.size() == 2) {
                    if (Objects.equals(taskId, TaskIdConstant.AUTO_CHARGE_TASK_ID)) {
                        endPoint = list.get(1).getStation();
                    } else {
                        endPoint = robotBindAttr.get(0).getPoint();
                    }
                } else {
                    endPoint = list.get(1).getStation();
                }
                String startPoint = list.get(0).getStation();
                String locStation = "";
                if (list.size() > 2) {
                    RobotRunDTO robotRunDTO = list.get(1);
                    if (robotRunDTO.getInstruction().equals("叉货BinTask")) {
                        String substring = robotRunDTO.getStation().substring(2);
                        int locValue = Integer.parseInt(substring) - 500;
                        locStation = "AP" + locValue;
                    }
                    //检查库位是否安全
                    if (robotRunDTO.getInstruction().equals("检查库位")) {
                        String substring = robotRunDTO.getStation().substring(2);
                        int locValue = Integer.parseInt(substring) + 100;
                        locStation = "LM" + locValue;
                    }
                }
                if (endPoint.equals(map.get(vehicle).getCurrentStation()) || locStation.equals(map.get(vehicle).getCurrentStation())) {
                    //如果还有多个任务未执行完成，则一直开启定时任务
                    if (list.size() > 2) {
                        RobotRunDTO robotRunDTO = list.get(1);
                        //获取机器人顶升机构状态
                        String jackStatus = map.get(vehicle).getJackStatus();
                        Map<String, String> dosMap = mapStringToMap(map.get(vehicle).getDosMap());
                        //1.顶升
                        if (robotRunDTO.getInstruction().equals("顶升") && (jackStatus.equals("下降到位") || jackStatus.equals("停止") || (jackStatus.equals("上升中") && "0".equals(dosMap.get("7"))))) {
                            //用于解决机器人执行原地顶升任务时，顶升机构无法正常关闭的问题
                            if (endPoint.equals(startPoint)) {
                                Thread.sleep(5000);
                            }
                            setJackUpAndNavigationData(robotIp, robotWaybillDetail, robotRunDTO);
                            //加一个延时，用于确保机器人的顶升顶降指令已发送成功
                            Thread.sleep(3000);
                        }
                        //2.顶降
                        if (robotRunDTO.getInstruction().equals("顶降") && (jackStatus.equals("上升到位") || jackStatus.equals("停止") || (jackStatus.equals("上升中") && "0".equals(dosMap.get("7"))))) {
                            setJackUpLoadData(robotIp, robotWaybillDetail, robotRunDTO);
                            //加一个延时，用于确保机器人的顶升顶降指令已发送成功
                            Thread.sleep(3000);
                        }
                        //3.等待DO
                        if (robotRunDTO.getInstruction().equals("DO")) {
                            setWaitDoData(waybillId, vehicle, robotIp, robotWaybillDetail, robotRunDTO);
                        }

                        //4.叉货
                        float forkHeight = map.get(vehicle).getForkHeight();

                        if (robotRunDTO.getInstruction().equals("叉货")) {
                            float instructionHeight = Float.parseFloat(robotRunDTO.getForkLiftHeight());
                            if (Math.abs(forkHeight - instructionHeight) > 0.05) {
                                setForkLiftData(robotIp, robotWaybillDetail, robotRunDTO);
                                //加一个延时，用于确保机器人的叉货令已发送成功
                                Thread.sleep(5000);
                            }
                            log.info("RMS向WMS同步取货完成的状态,货叉的高度instructionHeight：{}", instructionHeight);
                            if (instructionHeight > 0.1) {
                                RobotWaybill waybill = getWaybillByWaybillId(waybillId);
                                if (waybill != null && waybill.getWmsWaybillId() != null && waybill.getMesWaybillId() != null) {
                                    log.info("RMS向WMS同步取货完成的状态");
                                    RobotWaybillResultVO waybillExecuteResult = getWaybillExecuteResult(waybill.getMesWaybillId(), waybill.getWmsWaybillId());
                                    if (waybillExecuteResult != null) {
                                        waybillExecuteResult.setWaybillStatus(RobotWaybillStatusEnum.PICK_UP_COMPLETED.getCode());
                                        String jsonData = JSON.toJSONString(waybillExecuteResult, SerializerFeature.DisableCircularReferenceDetect);
                                        String wmsResStr = HttpClientUtil.clientPost(syncTaskStatusWmsUrl, HttpMethod.POST, jsonData);
                                        MESResDTO wmsResp = JSON.parseObject(wmsResStr, MESResDTO.class);
                                        boolean requestWmsFlag;
                                        if (wmsResp.getCode() == 200) {
                                            requestWmsFlag = true;
                                            log.info("同步运单状态到第三方系统成功：{}", wmsResp);
                                        } else {
                                            requestWmsFlag = false;
                                            log.info("同步运单状态到第三方系统失败：{}", wmsResp);
                                        }
                                        robotTaskLogService.saveSyncMesWaybill(waybillExecuteResult, waybill.getMesWaybillId(), waybill.getWmsWaybillId(), currentServerIp, requestWmsFlag, wmsResp.getMsg());
                                    }
                                }
                            }
                        }

                        //叉货BinTask
                        if (robotRunDTO.getInstruction().equals("叉货BinTask") && endPoint.equals(map.get(vehicle).getCurrentStation())) {
                            setForkLiftBinTaskData(robotIp, robotWaybillDetail, robotRunDTO);
                            //加一个延时，用于确保机器人的叉货令已发送成功
                            Thread.sleep(3000);
                        }

                        //6.辊筒下料,(1)任务链处于未执行状态；（2）滚筒上存在货物；（3）辊筒门板处于抬起状态
                        //获取机器人di列表状态
                        Map<String, String> disMap = mapStringToMap(map.get(vehicle).getDisMap());
                        //获取任务链执行状态
                        Integer taskListStatusLoad = map.get(vehicle).getTaskListStatus();
                        if (robotRunDTO.getInstruction().equals("辊筒下料") && (taskListStatusLoad == 4 || taskListStatusLoad == 0 || taskListStatusLoad == 5 || taskListStatusLoad == 6) && "1".equals(disMap.get("0")) && "1".equals(disMap.get("1")) && "1".equals(disMap.get("2")) && "1".equals(disMap.get("5"))) {
                            setRollerUnloadData(robotIp, robotWaybillDetail, robotRunDTO);
                            //加一个延时，用于确保机器人的辊筒下料指令已发送成功
                            Thread.sleep(3000);
                        }

                        //6.辊筒人工上料，（1）执行辊筒上料指令；(2)任务链处于未执行状态；（3）滚筒上没有货物；
                        if (robotRunDTO.getInstruction().equals("辊筒人工上料") && (taskListStatusLoad == 4 || taskListStatusLoad == 0 || taskListStatusLoad == 5 || taskListStatusLoad == 6) && "0".equals(disMap.get("0")) && "0".equals(disMap.get("1")) && "0".equals(disMap.get("2"))) {
                            setRollerLoadData(robotIp, robotWaybillDetail, robotRunDTO);
                            //加一个延时，用于确保机器人的辊筒下料指令已发送成功
                            Thread.sleep(3000);
                        }

                        //6.辊筒自动上料，（1）执行辊筒上料指令；(2)任务链处于未执行状态；（3）滚筒上没有货物；
                        if (robotRunDTO.getInstruction().equals("辊筒自动上料") && (taskListStatusLoad == 4 || taskListStatusLoad == 0 || taskListStatusLoad == 5 || taskListStatusLoad == 6) && "0".equals(disMap.get("0")) && "0".equals(disMap.get("1")) && "0".equals(disMap.get("2"))) {
                            setRollerAutoLoadData(robotIp, robotWaybillDetail, robotRunDTO);
                            //加一个延时，用于确保机器人的辊筒下料指令已发送成功
                            Thread.sleep(3000);
                        }

                        //获取机器人顶升机构状态
                        String jackStatus1 = map.get(vehicle).getJackStatus();
                        //5.顶升到位、顶尖到位、路径导航指令
                        //由于8610重载机器人只根据上升到位或者下降到位进行判断时，会出现顶升机构不能正常关闭的情形，因此还有根据do值判断
                        if ((robotRunDTO.getInstruction().equals("顶升") && "heavy_01".equals(vehicle) && jackStatus1.equals("上升到位") && "0".equals(dosMap.get("7")))
                                || (robotRunDTO.getInstruction().equals("顶降") && "heavy_01".equals(vehicle) && jackStatus1.equals("下降到位") && "0".equals(dosMap.get("1")))
                                || (robotRunDTO.getInstruction().equals("顶升") && jackStatus1.equals("上升到位") && !"heavy_01".equals(vehicle))
                                || (robotRunDTO.getInstruction().equals("顶降") && jackStatus1.equals("下降到位") && !"heavy_01".equals(vehicle))
                                || robotRunDTO.getInstruction().equals("机器人路径导航")) {
                            setPathNavigationData(taskId, robotWaybillDetail, vehicle, list);
                        }

                        //库位检查
                        if (robotRunDTO.getInstruction().equals("检查库位")) {
                            setStorageCheckData(taskId, robotWaybillDetail, vehicle, list);
                        }

                        //6.叉货到位   && map.get(vehicle).isForkHeightInPlace()
                        boolean forkHeightInPlace = map.get(vehicle).isForkHeightInPlace();
                        if (robotRunDTO.getInstruction().equals("叉货") && forkHeightInPlace) {
                            float instructionHeight = Float.parseFloat(robotRunDTO.getForkLiftHeight());
                            float forkHeight1 = map.get(vehicle).getForkHeight();
                            if (Math.abs(forkHeight1 - instructionHeight) <= 0.05) {
                                setPathNavigationData(taskId, robotWaybillDetail, vehicle, list);
                            }
                        }

                        //6.叉货BinTask到位   && map.get(vehicle).isForkHeightInPlace()
                        if (robotRunDTO.getInstruction().equals("叉货BinTask") && map.get(vehicle).isForkHeightInPlace() && locStation.equals(map.get(vehicle).getCurrentStation())) {
                            setPathNavigationData(taskId, robotWaybillDetail, vehicle, list);
                        }

                        //6.辊筒下料完成
                        Map<String, String> disMap1 = mapStringToMap(map.get(vehicle).getDisMap());
                        Integer taskListStatus = map.get(vehicle).getTaskListStatus();
                        //(1)任务链执行完成
                        if (robotRunDTO.getInstruction().equals("辊筒下料") && taskListStatus == 4
                                //（2）门档处于落下状态
                                && "1".equals(disMap1.get("4"))
                                //（3）货物已经从辊筒离开；
                                && "0".equals(disMap1.get("0"))
                                //（4）辊筒和门档的驱动已经关闭
                                && "0".equals(dosMap.get("7")) && "0".equals(dosMap.get("8"))) {
                            setPathNavigationData(taskId, robotWaybillDetail, vehicle, list);
                        }

                        //6.辊筒人工上料完成
                        //(1)任务链执行完成
                        if (robotRunDTO.getInstruction().equals("辊筒人工上料") && taskListStatus == 4
                                //（2）货物已放置到位；
                                && "1".equals(disMap1.get("2"))
                                //（3）门档已升起
                                && "1".equals(disMap1.get("5"))) {
                            setPathNavigationData(taskId, robotWaybillDetail, vehicle, list);
                        }

                        //6.辊筒自动上料完成
                        //(1)任务链执行完成
                        if (robotRunDTO.getInstruction().equals("辊筒自动上料") && taskListStatus == 4
                                //（2）货物已放置到位；
                                && "1".equals(disMap1.get("2"))
                                //（3）门档已升起
                                && "1".equals(disMap1.get("5"))) {
                            setPathNavigationData(taskId, robotWaybillDetail, vehicle, list);
                        }
                        //8.自定义动作执行完成
//                        Map<String, String> taskListMapStatus = (Map<String, String>) redisUtil.stringWithGet("robot_tasklist_status_res");
//                        String taskChainDTOJson = taskListMapStatus.get(vehicle);
//                        if (taskChainDTOJson != null) {
//                        RobotTaskListResDTO taskChainDTO = JSON.parseObject(taskChainDTOJson, RobotTaskListResDTO.class);
                        if (robotRunDTO.getInstruction().equals("自定义动作") && taskListStatus == 4) {
                            setPathNavigationData(taskId, robotWaybillDetail, vehicle, list);
                        }
//                        }

                        //等待DI触发
                        if (robotRunDTO.getInstruction().equals("等待DI")) {
                            log.info("机器人{}等待DI触发，请按下DI键...", vehicle);
                            // 若按下DI键
                            if (robotRunDTO.getWaitDiStatus().equals(disMap.get(robotRunDTO.getWaitDiId()))) {
                                //设置运单动作为等待DI的子任务
                                RobotWaybillDetail newDetail = robotWaybillDetailService.findOne(robotWaybillDetail.getWaybillId());
                                if (newDetail != null) {
                                    newDetail.setWaybillStatus("完成");
                                    robotWaybillDetailService.saveOrUpdate(newDetail);
                                }
                                robotWaybillDetail.setOperation("等待DI");
                                robotWaybillDetail.setStorageLoc(robotRunDTO.getStation());
                                robotWaybillDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
                                robotWaybillDetail.setWaybillStatus("运行中");
                                robotWaybillDetailService.save(robotWaybillDetail);
                                setPathNavigationData(taskId, robotWaybillDetail, vehicle, list);
                            }
                        }
                        // 7.1通用Modbus读写（leo add）
                        if (robotRunDTO.getInstruction().equals("通用Modbus读写")) {

                            // 若是读操作，直接调用方法
                            if (robotRunDTO.getModbusParam().getOperationType() == 3 || robotRunDTO.getModbusParam().getOperationType() == 1) {
                                if (modbusRwOperation(robotRunDTO)) {
                                    //执行进入下一轮的对应方法（xile）
                                    setModbusRwOperateData(taskId, robotWaybillDetail, vehicle, list);
                                }
                            } else {
                                // 若是写操作
                                // 判断待写入值与实际值写入值是否一致，同时判断设备状态值与实际写入值是否一致，若有一个存在不一致，则执行方法
                                if (!isEqualWritingAndWrited(robotRunDTO) || !isEqualEquipAndWrited(robotRunDTO)) {
                                    if (modbusRwOperation(robotRunDTO)) {
                                        //执行进入下一轮的对应方法（xile）
                                        setModbusRwOperateData(taskId, robotWaybillDetail, vehicle, list);
                                        Thread.sleep(1000);
                                    }
                                } else {
                                    setModbusRwOperateData(taskId, robotWaybillDetail, vehicle, list);
                                }
                            }
                        }

                        // 7.2通用Modbus等待（leo add）
                        if (robotRunDTO.getInstruction().equals("通用Modbus等待")) {
                            //设置执行modbus执行等待的数据
                            setModbusWaitOperateData(robotWaybillDetail, robotRunDTO);
                            if (modbusWaitOperation(robotRunDTO)) {
                                //执行进入下一轮的对应方法（xile）
                                setPathNavigationData(taskId, robotWaybillDetail, vehicle, list);
                            }
                            //其它参数视情况而定
                        }
                        // 8 执行自定义动作（leo add）
                        if (robotRunDTO.getInstruction().equals("自定义动作")) {
//                            if (taskListStatus == 4) {
                            // 自定义动作，调用方法
                            boolean executed = executeCustomAction(robotRunDTO);
                            if (executed) {
                                log.info("机器人{}自定义动作执行成功！", vehicle);
                            }
//                            }

                        }

                    } else {
                        //如果当前站点是充电站点，则开启智能充电桩的充电功能
                        // if (chargePoints.contains(currentStation)) {
                        //此处充点电还没有ip
                        if (isIntelligentChargingStation) {
                            //获取当前站点绑定的充电桩信息
                            List<RobotChargePile> chargePileList = robotChargePileService.findListByAttribute(null, null, null, currentStation);
                            RobotChargePile robotChargePile = chargePileList.get(0);
                            Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
                            if (!map.containsKey(robotChargePile.getChargePileIp() + PortConstant.CHARGE_PILE_START)) {
                                EntityResult connectResult = TcpClientThread.start(robotChargePile.getChargePileIp(), PortConstant.CHARGE_PILE_START);
                                Thread.sleep(100L);
                                log.info("TCP客户端启动结果：{}", JSON.toJSON(connectResult));
                            }
                            //如果发送指令成功，则连接成功，然后推送机器人的基本信息给前端处理
                            TcpClientThread.sendHexMsg(robotChargePile.getChargePileIp(), PortConstant.CHARGE_PILE_START, ProtocolConstant.CHARGE_PILE_START);
                        }

                        //获取循环执行的状态,0--不循环执行，1--循环执行
                        Integer enabledState = robotTaskService.getById(taskId).getEnabledState();
                        if (enabledState == 1) {
                            RobotWaybill robotWaybill = getById(waybillId);
                            boolean b = runLoopRobotTask(taskId, vehicle, robotWaybill, taskParameterList);
                            if (!b) {
                                throw new Exception("机器人执行循环任务失败！");
                            }
                        } else {
                            //设置任务执行完成数据
                            setTaskCompletedData(waybillId, key, vehicle);
                        }
                    }
                }
                break;
            case "导航失败":
                //导航失败，设置任务执行失败数据
                setTaskFailData(waybillId, key, vehicle, null);
                break;
            case "取消导航":
                //取消导航，设置取消导航的数据(因为暂停导航之后的继续导航功能不生效，因此这里用取消导航来代替暂停导航)
                setTaskCancelData(waybillId, key, vehicle, list);
                break;
        }
    }

    /**
     * 判断待写入值与实际值写入值是否一致(leo add)
     */
    public boolean isEqualWritingAndWrited(RobotRunDTO robotRunDTO) {
        String ip = robotRunDTO.getModbusParam().getIp();
        Integer port = robotRunDTO.getModbusParam().getPort();
        Integer slaveId = robotRunDTO.getModbusParam().getSlaveId();
        Integer value = robotRunDTO.getModbusParam().getValue();
        //定义一个读取写入地址的读取器，地址在配置文件中定义
        ModbusTcpRead modbusTcpReadAddressWrite = new ModbusTcpRead(slaveId, ConstFunCode.func03, registerWriteOffset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);
        // 读取写入状态
        Object modbusResWrite = ModbusUtils.readModbusByTcp(modbusTcpReadAddressWrite);
        if (value == Integer.parseInt(modbusResWrite.toString())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断设备状态值与实际写入值是否一致(leo add)
     */
    public boolean isEqualEquipAndWrited(RobotRunDTO robotRunDTO) {
        String ip = robotRunDTO.getModbusParam().getIp();
        Integer port = robotRunDTO.getModbusParam().getPort();
        Integer slaveId = robotRunDTO.getModbusParam().getSlaveId();
        Integer offset = robotRunDTO.getModbusParam().getOffset();
        //定义一个读取设备状态的读取器
        ModbusTcpRead modbusTcpRead = new ModbusTcpRead(slaveId, ConstFunCode.func03, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);
        //定义一个读取写入地址的读取器，地址在配置文件中定义
        ModbusTcpRead modbusTcpReadAddressWrite = new ModbusTcpRead(slaveId, ConstFunCode.func03, registerWriteOffset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);

        // 读取设备状态
        Object modbusRes = ModbusUtils.readModbusByTcp(modbusTcpRead);
        // 读取写入状态
        Object modbusResWrite = ModbusUtils.readModbusByTcp(modbusTcpReadAddressWrite);
        if (Integer.parseInt(modbusRes.toString()) == Integer.parseInt(modbusResWrite.toString())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 执行自定义动作 leo add
     *
     * @param robotRunDTO 机器人运行DTO
     * @return true：执行成功，false：执行失败
     */
    public boolean executeCustomAction(RobotRunDTO robotRunDTO) {
        // 根据robotId获取机器人IP
        String robotIp = robotBasicInfoService.findByVehicleId(robotRunDTO.getVehicle()).getCurrentIp();
        // 定义任务链名称:机器人名+自定义动作+随机数（保留4个随机数位）
//        String taskChainName = robotRunDTO.getVehicle() + "_自定义动作_" + RandomUtil.randomNumbers(4);

//        RobotTaskListDTO robotTaskListDTO = robotTaskService.reconstructRobotTask(taskChainName, robotRunDTO.getCustomizedActions());
//        String robotTaskListDTOJsonString = JSON.toJSONString(robotTaskListDTO);
        String commandHex = DataConvertUtil.convertStringToHex(robotRunDTO.getCustomizedActions());
        int byteLength = commandHex.length() / 2;
        String commandLength = String.format("%04x", byteLength);
        // 任务链报文类型：3100
        String instruction = "5A0100000000" + commandLength + "0C1C0C1C00000000" + commandHex;

        // 若未连接到该ip端口，则尝试连接
        Map<String, Thread> IpThreadNettyMap = TcpClientThread.getIpThreadNetty();
        if (!IpThreadNettyMap.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        // 向该ip端口发送tcp指令
        int retryCount = 0;
        do {
            // 发送TCP指令
            EntityResult response = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info("机器人{}自定义动作指令下发成功：{}", robotRunDTO.getVehicle(), robotRunDTO.getCustomizedActions());
                break;
            }
            if (retryCount >= 60) {
                log.error("机器人{}自定义动作指令下发失败，重试次数达到最大值{}", robotRunDTO.getVehicle(), retryCount + 1);
                return false;
            }
            log.info("机器人{}接收自定义动作指令失败，1秒后重试...重试次数:{}", robotRunDTO.getVehicle(), retryCount + 1);
            // 等待1秒后重试
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());
        return true;
    }


    /**
     * 将Map字符串转换为Map
     *
     * @param str Map字符串
     * @return Map
     */
    public static Map<String, String> mapStringToMap(String str) {
        str = str.substring(1, str.length() - 1);
        String[] strs = str.split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (String string : strs) {
            String key = string.split("=")[0];
            String value = string.split("=")[1];
            // 去掉头部空格
            String key1 = key.trim();
            String value1 = value.trim();
            map.put(key1, value1);
        }
        return map;
    }

    /**
     * 分步骤运行机器人光通信任务
     *
     * @param robotWaybill
     * @return
     */
    @Override
    public void runRobotDmsTaskByStep(RobotWaybill robotWaybill, String dmsPointIp) throws Exception {
        //获取任务id
        String id = robotWaybill.getTaskId();
        //获取任务编辑界面的任务内容
        String taskContent = robotTaskService.getById(id).getTaskContent();
        //解析任务内容
        List<RobotTaskDTO> robotSubTaskList = JSONObject.parseArray(taskContent, RobotTaskDTO.class);
        //获取子任务列表
        List<RobotRunListDTO> subTaskList = new ArrayList<>();
        for (int i = 0; i < robotSubTaskList.size(); i++) {
            //封装得到子任务列表
            List<RobotRunDTO> robotRunDTOS = encapsulatingSubTaskList(robotWaybill, robotSubTaskList.get(i));
            //如果是第一条任务，机器人没有在充电，开始站点是待命点；机器人在充电，开始站点是充电点
            if (i == 0) {
                RobotBasicInfo robot = robotBasicInfoService.findByName(robotWaybill.getRobotName()).get(0);
                //机器人充电
                if ((robot.getCharge() == 1 && !ChargeConstant.ROBOT_START_CHARGE.equals(robotWaybill.getTaskName()))
                        || (robot.getCharge() == 0 && ChargeConstant.ROBOT_END_CHARGE.equals(robotWaybill.getTaskName()))) {
                    //获取机器人光通信站充电点属性
                    List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(robot.getVehicleId(), RobotMapPointAttrEnum.CHARGE_POINT.getCode());
                    String chargePoint = robotBindAttr.get(0).getPoint();
                    robotRunDTOS.get(0).setStation(chargePoint);
                    //机器人执行离开充电点任务，设置机器人状态为未充电
                    robot.setCharge(0);
                    robotBasicInfoService.saveOrUpdate(robot);
                } else {
                    //获取机器人光通信站待命点属性
                    List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(robot.getVehicleId(), RobotMapPointAttrEnum.DMS_PARK_POINT.getCode());
                    String parkPoint = robotBindAttr.get(0).getPoint();
                    robotRunDTOS.get(0).setStation(parkPoint);
                }
            }
            //如果是最后一个子任务，则取要把返回待命点添加到最后的子任务中
            if (i == robotSubTaskList.size() - 1) {
                //如果是机器人充电任务和充电完成任务，则不用添加返回待命点的步骤
                if (!ChargeConstant.ROBOT_START_CHARGE.equals(robotWaybill.getTaskName()) && !ChargeConstant.ROBOT_END_CHARGE.equals(robotWaybill.getTaskName())) {
                    //获取当前运单分配的机器人名称
                    String vehicle = robotWaybill.getRobotName();
                    //把待命点站点名称添加到集合中
                    List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(vehicle, RobotMapPointAttrEnum.DMS_PARK_POINT.getCode());
                    RobotRunDTO firstRunDTO = new RobotRunDTO();
                    firstRunDTO.setVehicle(vehicle);
                    firstRunDTO.setStation(robotBindAttr.get(0).getPoint());
                    firstRunDTO.setInstruction("机器人路径导航");
                    robotRunDTOS.add(firstRunDTO);
                }
            }
            RobotRunListDTO runListDTO = new RobotRunListDTO();
            runListDTO.setSubRunList(robotRunDTOS);
            subTaskList.add(runListDTO);
        }
        //设置运单的任务列表
        robotWaybill.setSubTaskList(JSON.toJSONString(subTaskList));
        saveOrUpdate(robotWaybill);
        //存在待执行的子任务
        if (subTaskList.size() > 0) {
            //连接获取机器人导航状态的端口
            Map<String, Thread> threadNetty = TcpClientThread.getIpThreadNetty();
            if (!threadNetty.containsKey(dmsPointIp + dmsPort)) {
                robotBasicInfoService.connectTcp(dmsPointIp, dmsPort);
                Thread.sleep(2000);
            }
            //取消上次未执行完成的导航
            TcpClientThread.sendHexMsg(dmsPointIp, dmsPort, ProtocolConstant.CANCEL_NAVIGATION);

            //获取工作点的自动门设备
            IotEquipment automaticDoor = null;
            boolean isOpenDoor = true;
            for (int i = 0; i < subTaskList.get(0).getSubRunList().size(); i++) {
                String workEndPoint = subTaskList.get(0).getSubRunList().get(i).getStation();
                automaticDoor = getAutomaticDoor(workEndPoint);
                //如果该站点上绑定有自动门,打开自动门
                isOpenDoor = openAutomaticDoor(robotWaybill, automaticDoor);
                //查询到门以后就结束循环，只能用于任务中只包含一个自动门的情况
                if (automaticDoor != null) {
                    break;
                }
            }
            //开始运行光通信任务
            boolean flag = false;
            if (isOpenDoor) {
                flag = runDmsSubTask(robotWaybill, dmsPointIp);
            }
            if (flag) {
                //开启定时任务，执行光通信任务（通过设置定时任务延迟推送频率，可以有效解决光通信模块与控制器断联的情形）
                String cron;
                if ("359厂复合材料法兰件自动热压设备项目".equals(projectName) || "西安航天自动化车间轻载辊筒".equals(projectName)) {
                    cron = "*/3 * * * * ?";
                } else {
                    cron = "*/10 * * * * ?";
                }
                IotEquipment finalAutomaticDoor = automaticDoor;
                customScheduledTaskRegistrar.addTriggerTask(robotWaybill.getId(),
                        //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                        () -> {
                            try {
                                //获取任务链执行结果
                                getTaskChainResult(robotWaybill, finalAutomaticDoor);
                            } catch (Exception e) {
                                log.error("获取任务链执行信息异常，" + e.getMessage());
                            }
                        }, triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
            } else {
                //关闭自动门
                closeAutomaticDoor(robotWaybill, automaticDoor);
                //更新运单执行结果
                setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "运行光通信任务失败!");
                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
            }
        } else {
            //更新运单执行结果
            setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "任务文本内容不存在或者不完整!");
            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
            completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
        }
    }

    //设置任务中的动作指令是否被执行
    Map<String, Boolean> executedConcurrentHashMap = new ConcurrentHashMap<>();

    //由于多个机器人遇到路径冲突时，需要重新惊醒路径规划，因此要禁止后续的子任务不能被多次执行
    Map<String, Boolean> queryExecutedConcurrentHashMap = new ConcurrentHashMap<>();


    /**
     * 运行多机器人的光通信任务
     *
     * @param robotWaybill
     * @param dmsPointIp
     */
    @Override
    public void runMultiRobotsDmsTask(RobotWaybill robotWaybill, String dmsPointIp) throws Exception {
        //初始化动作执行不能被执行
        executedConcurrentHashMap.put(robotWaybill.getRobotName(), false);
        //由于多个机器人遇到路径冲突时，需要重新进行路径规划，因此要禁止后续的子任务不能被多次执行，默认子任务可以被执行
        queryExecutedConcurrentHashMap.put(robotWaybill.getRobotName(), true);
        //获取任务id
        String id = robotWaybill.getTaskId();
        //获取任务编辑界面的任务内容
        String taskContent = robotTaskService.getById(id).getTaskContent();
        //解析任务内容
        List<RobotTaskDTO> robotSubTaskList = JSONObject.parseArray(taskContent, RobotTaskDTO.class);
        //获取子任务列表
        List<RobotRunListDTO> subTaskList = new ArrayList<>();
        for (int i = 0; i < robotSubTaskList.size(); i++) {
            //封装得到子任务列表
            List<RobotRunDTO> robotRunDTOS = encapsulatingSubTaskList(robotWaybill, robotSubTaskList.get(i));
            //如果是第一条任务，机器人没有在充电，开始站点是待命点；机器人在充电，开始站点是充电点
            if (i == 0) {
                RobotBasicInfo robot = robotBasicInfoService.findByName(robotWaybill.getRobotName()).get(0);
                //机器人充电
                if ((robot.getCharge() == 1 && !ChargeConstant.ROBOT_START_CHARGE.equals(robotWaybill.getTaskName()))
                        || (robot.getCharge() == 0 && ChargeConstant.ROBOT_END_CHARGE.equals(robotWaybill.getTaskName()))) {
                    //获取机器人光通信站充电点属性
                    List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(robot.getVehicleId(), RobotMapPointAttrEnum.CHARGE_POINT.getCode());
                    String chargePoint = robotBindAttr.get(0).getPoint();
                    robotRunDTOS.get(0).setStation(chargePoint);
                    //机器人执行离开充电点任务，设置机器人状态为未充电
                    robot.setCharge(0);
                    robotBasicInfoService.saveOrUpdate(robot);
                } else {
                    //获取机器人光通信站待命点属性
                    List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(robot.getVehicleId(), RobotMapPointAttrEnum.DMS_PARK_POINT.getCode());
                    String parkPoint = robotBindAttr.get(0).getPoint();
                    robotRunDTOS.get(0).setStation(parkPoint);
                }
            }

            //如果是最后一个子任务，则取要把返回待命点添加到最后的子任务中
            if (i == robotSubTaskList.size() - 1) {
                //如果是机器人充电任务和充电完成任务，则不用添加返回待命点的步骤
                if (!ChargeConstant.ROBOT_START_CHARGE.equals(robotWaybill.getTaskName()) && !ChargeConstant.ROBOT_END_CHARGE.equals(robotWaybill.getTaskName())) {
                    //获取当前运单分配的机器人名称
                    String vehicle = robotWaybill.getRobotName();
                    //把待命点站点名称添加到集合中
                    List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(vehicle, RobotMapPointAttrEnum.DMS_PARK_POINT.getCode());
                    RobotRunDTO firstRunDTO = new RobotRunDTO();
                    firstRunDTO.setVehicle(vehicle);
                    firstRunDTO.setStation(robotBindAttr.get(0).getPoint());
                    firstRunDTO.setInstruction("机器人路径导航");
                    robotRunDTOS.add(firstRunDTO);
                }
            }
            RobotRunListDTO runListDTO = new RobotRunListDTO();
            runListDTO.setSubRunList(robotRunDTOS);
            subTaskList.add(runListDTO);
        }
        //设置运单的任务列表
        robotWaybill.setSubTaskList(JSON.toJSONString(subTaskList));
        saveOrUpdate(robotWaybill);
        //如果子任务不完整
        if (subTaskList.size() > 0) {
            //连接获取机器人导航状态的端口
            Map<String, Thread> threadNetty = TcpClientThread.getIpThreadNetty();
            if (!threadNetty.containsKey(dmsPointIp + dmsPort)) {
                robotBasicInfoService.connectTcp(dmsPointIp, dmsPort);
                Thread.sleep(2000);
            }
            //取消上次未执行完成的导航
            TcpClientThread.sendHexMsg(dmsPointIp, dmsPort, ProtocolConstant.CANCEL_NAVIGATION);
            //开始运行光通信任务
            boolean flag = runMultiRobotsDmsSubTask(robotWaybill);
            if (flag) {
                //开启定时任务，执行光通信任务（通过设置定时任务延迟推送频率，可以有效解决光通信模块与控制器断联的情形）
                String cron;
                if ("359厂复合材料法兰件自动热压设备项目".equals(projectName) || "西安航天自动化车间轻载辊筒".equals(projectName)) {
                    cron = "*/3 * * * * ?";
                } else {
                    cron = "*/6 * * * * ?";
                }
                customScheduledTaskRegistrar.addTriggerTask(robotWaybill.getId(),
                        //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                        () -> {
                            try {
                                //获取光通信多机器人任务链执行结果
                                getMultiRobotsDmsTaskResult(robotWaybill);
                            } catch (Exception e) {
                                log.error("获取任务链执行信息异常，" + e.getMessage());
                            }
                        }, triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
            } else {
                //更新运单执行结果
                setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "运行光通信任务失败!");
                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
            }
        } else {
            //更新运单执行结果
            setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "任务文本内容不存在或者不完整!");
            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
            completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
        }
    }

    /**
     * 根据站点名称获取当前站点所在区域对应的中心点
     *
     * @param currentPoint
     * @return
     * @throws IOException
     */
    public String getAreaCenterPointByCurrentPoint(String currentPoint) throws IOException {
        //获取光通信普通区域列表
        List<RobotDmsEditor> list = robotDmsEditorService.findListByAreaType(0);
        for (RobotDmsEditor robotDmsEditor : list) {
            //获取当前光通信区域包含的所有站点信息
            String areaContainPoints = robotDmsEditor.getAreaContainPoints();
            ObjectMapper mapper = new ObjectMapper();
            List<String> containPoints = mapper.readValue(areaContainPoints, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
            });
            if (containPoints.contains(currentPoint)) {
                return robotDmsEditor.getAreaCenterPoint();
            }
        }
        return null;
    }

    /**
     * 获取自动门设备
     *
     * @param endPort
     */
    @Override
    public IotEquipment getAutomaticDoor(String endPort) {
        IotEquipment bindingIotEquipment = new IotEquipment(); //待绑定设备
        // 1.获取全部设备列表
        List<IotEquipment> iotEquipmentList = ioTEquipmentService.listByEquipmentType("automaticDoor");
        List<String> adjacentSites;
        // 2.遍历设备列表，获取当前设备的相邻站点字段 adjacentSite
        for (IotEquipment iotEquipment : iotEquipmentList) {
            String adjacentSite = iotEquipment.getAdjacentSite();
            adjacentSites = Arrays.asList(adjacentSite.split(","));
            //3.如果adjacentSite不为空，则判断 adjacentSite 是否包含 beginPort、endPort之一，若包含，则判断设备是否在线
            if (StringUtils.isNotEmpty(adjacentSite) && adjacentSites.contains(endPort)) {
                bindingIotEquipment = iotEquipment;
                break;
            } else {
                bindingIotEquipment = null;
            }
        }
        return bindingIotEquipment;
    }

    /**
     * 打开烘箱
     *
     * @param robotWaybill
     * @param iotEquipment
     * @return
     * @throws Exception
     */
    @Override
    @Async("asyncServiceExecutor")
    public boolean openOven(RobotWaybill robotWaybill, IotEquipment iotEquipment) throws Exception {
        if (iotEquipment != null) {
            log.info("准备打开烘箱门，烘箱门门信息：{}", iotEquipment);
            //判断自动门是否在线
            InetAddress geek = InetAddress.getByName(iotEquipment.getEquipmentIp());
            boolean isOnline = false;
            for (int i = 0; i < 5; i++) {
                if (geek.isReachable(2000)) {
                    isOnline = true;
                    break;
                }
                log.info("打开烘箱门轮询判断第{}次，烘箱不自在线", i + 1);
            }
            //如果轮询五次自动门不在线
            if (!isOnline) {
                log.error("烘箱不在线！");
                setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "烘箱不在线!");
                return false;
            } else {
                //通过MQTT方式
                if ("MQTT".equals(iotEquipment.getCommunicationType())) {
                    //打开烘箱门
                    ioTEquipmentService.controlOvenDoor(iotEquipment.getEquipmentCode(), true);
                    //使用 Instant 和 Duration 来精确计算时间
                    final Instant startTime = Instant.now();
                    final Duration timeout = Duration.ofMinutes(1);

                    while (true) {
                        //todo 业务处理
                        // 检查是否匹配,即门是否完全打开
                        if (HXPositionNumConstant.OVEN_1_CONTROL_41.equals(iotEquipment.getEquipmentCode())) {
                            // 获取当前数据
                            Boolean hx1Open = (Boolean) redisUtil.stringWithGet("HX1_M_R_DW");
                            if (hx1Open) {
                                log.info("烘箱门已经完全打开，可以通行!");
                                return true;
                            }
                        } else if (HXPositionNumConstant.OVEN_2_CONTROL_41.equals(iotEquipment.getEquipmentCode())) {
                            Boolean hx2Open = (Boolean) redisUtil.stringWithGet("HX2_M_R_DW");
                            if (hx2Open) {
                                log.info("烘箱门已经完全打开，可以通行!");
                                return true;
                            }
                        }

                        // 检查是否超时
                        if (Duration.between(startTime, Instant.now()).compareTo(timeout) > 0) {
                            log.error("开启烘箱门超时！");
                            setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "开启烘箱门超时！");
                            return false;
                        }

                        // 短暂休眠避免CPU过载
                        try {
                            TimeUnit.MILLISECONDS.sleep(2000); // 每2000毫秒检查一次
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            log.error("开启烘箱门轮询被中断！");
                            setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "开启烘箱门轮询被中断！");
                            return false;
                        }

                    }
                } else if ("Modbus".equals(iotEquipment.getCommunicationType())) {
                    try {
                        // 3.1.1 调用开门方法
                        //初始化写入状态为0
                        modbusRwOperation(iotEquipment, 4, 0);
                        if (modbusRwOperation(iotEquipment, 4, 1)) {
                            // 3.1.2 调用等待方法
                            if (modbusWaitOperation(iotEquipment, 1)) {
                                //开门到位，设置寄存器为初始化状态
                                modbusRwOperation(iotEquipment, 4, 0);
                                log.info("开烘箱门成功,准备通行");
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        log.error("烘箱门处理发生异常: ", e);
                        setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "开启烘箱门处理发生异常！");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 关闭烘箱门
     *
     * @param automaticDoor
     */
    @Override
    @Async("asyncServiceExecutor")
    public boolean closeOven(RobotWaybill robotWaybill, IotEquipment automaticDoor) throws Exception {
        if (automaticDoor != null) {
            log.info("准备关闭烘箱自动门，自动门信息：{}", automaticDoor);
            //判断自动门是否在线
            InetAddress geek = InetAddress.getByName(automaticDoor.getEquipmentIp());
            boolean isOnline = false;
            for (int i = 0; i < 5; i++) {
                if (geek.isReachable(2000)) {
                    isOnline = true;
                    break;
                }
                log.info("关闭烘箱自动门轮询判断第{}次，自动门不自在线", i + 1);
            }
            //如果轮询五次自动门不在线
            if (!isOnline) {
                log.error("烘箱自动门不在线！");
                setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "烘箱自动门不在线!");
                return false;
            }
            //通过MQTT方式关闭自动门
            if ("MQTT".equals(automaticDoor.getCommunicationType())) {
                //关闭烘箱门
                ioTEquipmentService.controlOvenDoor(automaticDoor.getEquipmentCode(), false);

                //使用 Instant 和 Duration 来精确计算时间
                final Instant startTime = Instant.now();
                final Duration timeout = Duration.ofMinutes(1);

                while (true) {

                    //todo 业务处理
                    // 检查是否匹配,即门是否完全关闭
                    if (HXPositionNumConstant.OVEN_1_CONTROL_41.equals(automaticDoor.getEquipmentCode())) {
                        // 获取当前数据
                        Boolean hx1Close = (Boolean) redisUtil.stringWithGet(HXPositionNumConstant.OVEN_1_CLOSE_41);
                        if (hx1Close) {
                            log.info("烘箱自动门已经完全关闭，任务结束!");
                            return true;
                        }
                    } else if (HXPositionNumConstant.OVEN_2_CONTROL_41.equals(automaticDoor.getEquipmentCode())) {
                        // 获取当前数据
                        Boolean hx2Close = (Boolean) redisUtil.stringWithGet(HXPositionNumConstant.OVEN_2_CLOSE_41);
                        if (hx2Close) {
                            log.info("烘箱自动门已经完全关闭，任务结束!");
                            return true;
                        }
                    }

                    // 检查是否超时
                    if (Duration.between(startTime, Instant.now()).compareTo(timeout) > 0) {
                        log.error("关闭烘箱自动门超时！");
                        setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "关闭自动门超时！");
                        return false;
                    }

                    // 短暂休眠避免CPU过载
                    try {
                        TimeUnit.MILLISECONDS.sleep(2000); // 每2000毫秒检查一次
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("关闭烘箱自动门轮询被中断！");
                        setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "关闭自动门轮询被中断！");
                        return false;
                    }
                }
            } else if ("Modbus".equals(automaticDoor.getCommunicationType())) {
                //通过Modbus方式关闭自动门
                try {
                    //初始化写入状态为0
                    modbusRwOperation(automaticDoor, 4, 0);
                    if (modbusRwOperation(automaticDoor, 4, 2)) {
                        log.info("准备关门");
                        // 3.1.2 调用等待方法
                        if (modbusWaitOperation(automaticDoor, 2)) {
                            //关门到位，设置寄存器为初始化状态
                            if (modbusRwOperation(automaticDoor, 4, 0)) {
                                log.info("门已关门，机器人正在远离自动门");
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("自动门处理发生异常: ", e);
                    setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "自动门处理发生异常！");
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 打开自动门
     *
     * @param robotWaybill
     * @param automaticDoor
     * @return
     * @throws Exception
     */
    @Override
    public boolean openAutomaticDoor(RobotWaybill robotWaybill, IotEquipment automaticDoor) throws Exception {
        if (automaticDoor != null) {
            log.info("准备打开自动门，自动门信息：{}", automaticDoor);
            //判断自动门是否在线
            InetAddress geek = InetAddress.getByName(automaticDoor.getEquipmentIp());
            boolean isOnline = false;
            for (int i = 0; i < 5; i++) {
                if (geek.isReachable(2000)) {
                    isOnline = true;
                    break;
                }
                log.info("打开自动门轮询判断第{}次，自动门不自在线", i + 1);
            }
            //如果轮询五次自动门不在线
            if (!isOnline) {
                log.error("自动门不在线或者自动门没设置为远程状态！");
                setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "自动门" + automaticDoor.getEquipmentCode() + "不在线!");
                return false;
            } else {
                //通过MQTT方式关闭自动门
                if ("MQTT".equals(automaticDoor.getCommunicationType())) {
                    //中控scada下发
                    if ("inPlant".equals(scadaName)) {
                        //判断机器人是否通过自动门
                        //在给359九分厂下发自动门指令时，必须下发两次，一次value是1，再把value置为0
                        //第一次下发
                        SendMessage sendMessage = new SendMessage();
                        List<PublishRTValue> publishRTValues = new ArrayList<>();
                        PublishRTValue publishRTValue = new PublishRTValue();
                        if (HXPositionNumConstant.OVEN_1_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                            publishRTValue.setName(HXPositionNumConstant.OVEN_1_OPEN);
                        } else if (HXPositionNumConstant.OVEN_2_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                            publishRTValue.setName(HXPositionNumConstant.OVEN_2_OPEN);
                        }
                        publishRTValue.setType(1);
                        publishRTValue.setValue("1");
                        publishRTValues.add(publishRTValue);
                        sendMessage.setRTValue(publishRTValues);
                        log.info("中控SCADA下发开门任务，下发指令{}", sendMessage);
                        mqttProcess.publish(inPlantTopicPush, sendMessage);
                        Thread.sleep(1000);

                        //第二次下发
                        SendMessage sendMessage2 = new SendMessage();
                        List<PublishRTValue> publishRTValues2 = new ArrayList<>();
                        PublishRTValue publishRTValue2 = new PublishRTValue();
                        if (HXPositionNumConstant.OVEN_1_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                            publishRTValue2.setName(HXPositionNumConstant.OVEN_1_OPEN);
                        } else if (HXPositionNumConstant.OVEN_2_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                            publishRTValue2.setName(HXPositionNumConstant.OVEN_2_OPEN);
                        }
                        publishRTValue2.setType(1);
                        publishRTValue2.setValue("0");
                        publishRTValues2.add(publishRTValue2);
                        sendMessage2.setRTValue(publishRTValues2);
                        mqttProcess.publish(inPlantTopicPush, sendMessage2);
                        Thread.sleep(1000);
                    }

                    //亚控scada下发
                    if ("king".equals(scadaName)) {
                        //根据卷帘门Id获取开门变量
                        //第一次下发
                        String sendData = ioTEquipmentService.commandSendPackage(automaticDoor.getOpen(), true, 0);
                        log.info("亚控SCADA下发开门任务，下发指令{}", sendData);
                        mqttGateway.sendToMqtt(setSubTopic, sendData);
                        Thread.sleep(1000);
                        //第二次下发初始化
                        String sendDataInit = ioTEquipmentService.commandSendPackage(automaticDoor.getOpen(), false, 0);
                        log.info("亚控SCADA下发开门任务，下发指令{}", sendDataInit);
                        mqttGateway.sendToMqtt(setSubTopic, sendDataInit);
                    }

                    //使用 Instant 和 Duration 来精确计算时间
                    final Instant startTime = Instant.now();
                    final Duration timeout = Duration.ofMinutes(1);

                    while (true) {
                        // 获取当前数据
                        //获取redis存储的自動門的訂閱消息
                        Map<String, Integer> scadaMessagesMap = (Map<String, Integer>) redisUtil.stringWithGet("topic_HX_msg");
                        //todo 业务处理
                        // 检查是否匹配,即门是否完全打开
                        String openStatusStr = (String) redisUtil.stringWithGet(automaticDoor.getOpenEnd());
                        if (Boolean.parseBoolean(openStatusStr)) {
                            log.info("自动门已经完全打开，可以通行!");
                            return true;
                        }

                        if (HXPositionNumConstant.OVEN_1_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                            if (((scadaMessagesMap.get(HXPositionNumConstant.OVEN_1_DOOR_1_OPEN_END_1) == 1)
                                    || (scadaMessagesMap.get(HXPositionNumConstant.OVEN_1_DOOR_1_OPEN_END_2) == 1))
                                    && (((scadaMessagesMap.get(HXPositionNumConstant.OVEN_1_DOOR_2_OPEN_END_1) == 1))
                                    || (scadaMessagesMap.get(HXPositionNumConstant.OVEN_1_DOOR_2_OPEN_END_2) == 1))) {
                                log.info("自动门已经完全打开，可以通行!");
                                return true;
                            }
                        } else if (HXPositionNumConstant.OVEN_2_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                            if (((scadaMessagesMap.get(HXPositionNumConstant.OVEN_2_DOOR_1_OPEN_END_1) == 1)
                                    || (scadaMessagesMap.get(HXPositionNumConstant.OVEN_2_DOOR_1_OPEN_END_2) == 1))
                                    && (((scadaMessagesMap.get(HXPositionNumConstant.OVEN_2_DOOR_2_OPEN_END_1) == 1))
                                    || (scadaMessagesMap.get(HXPositionNumConstant.OVEN_2_DOOR_2_OPEN_END_2) == 1))) {
                                log.info("自动门已经完全打开，可以通行!");
                                return true;
                            }
                        }

                        // 检查是否超时
                        if (Duration.between(startTime, Instant.now()).compareTo(timeout) > 0) {
                            log.error("开启自动门超时！");
                            setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "开启自动门" + automaticDoor.getEquipmentCode() + "超时！");
                            return false;
                        }

                        // 短暂休眠避免CPU过载
                        try {
                            TimeUnit.MILLISECONDS.sleep(2000); // 每2000毫秒检查一次
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            log.error("开启自动门轮询被中断！");
                            setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "开启自动门" + automaticDoor.getEquipmentCode() + "轮询被中断！");
                            return false;
                        }

                    }
                } else if ("Modbus".equals(automaticDoor.getCommunicationType())) {
                    try {
                        // 3.1.1 调用开门方法
                        //初始化写入状态为0
                        modbusRwOperation(automaticDoor, 4, 0);
                        if (modbusRwOperation(automaticDoor, 4, 1)) {
                            // 3.1.2 调用等待方法
                            if (modbusWaitOperation(automaticDoor, 1)) {
                                //开门到位，设置寄存器为初始化状态
                                modbusRwOperation(automaticDoor, 4, 0);
                                log.info("开门成功,准备通行");
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        log.error("自动门处理发生异常: ", e);
                        setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "开启自动门" + automaticDoor.getEquipmentCode() + "处理发生异常！");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 关闭自动门
     *
     * @param automaticDoor
     */
    @Override
    public boolean closeAutomaticDoor(RobotWaybill robotWaybill, IotEquipment automaticDoor) throws Exception {
        if (automaticDoor != null) {
            log.info("准备关闭自动门，自动门信息：{}", automaticDoor);
            //判断自动门是否在线
            InetAddress geek = InetAddress.getByName(automaticDoor.getEquipmentIp());
            boolean isOnline = false;
            for (int i = 0; i < 5; i++) {
                if (geek.isReachable(2000)) {
                    isOnline = true;
                    break;
                }
                log.info("关闭自动门轮询判断第{}次，自动门不自在线", i + 1);
            }
            //如果轮询五次自动门不在线
            if (!isOnline) {
                log.error("自动门不在线或者自动门没设置为远程状态！");
                setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "自动门" + automaticDoor.getEquipmentCode() + "不在线!");
                return false;
            }
            //通过MQTT方式关闭自动门
            if ("MQTT".equals(automaticDoor.getCommunicationType())) {
                //判断机器人是否通过自动门
                //中控scada下发
                if ("inPlant".equals(scadaName)) {
                    //在给359九分厂下发自动门指令时，必须下发两次，一次value是1，再把value置为0
                    //第一次下发
                    SendMessage sendMessage = new SendMessage();
                    List<PublishRTValue> publishRTValues = new ArrayList<>();
                    PublishRTValue publishRTValue = new PublishRTValue();
                    if (HXPositionNumConstant.OVEN_1_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                        publishRTValue.setName(HXPositionNumConstant.OVEN_1_CLOSE);
                    } else if (HXPositionNumConstant.OVEN_2_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                        publishRTValue.setName(HXPositionNumConstant.OVEN_2_CLOSE);
                    }
                    publishRTValue.setType(1);
                    publishRTValue.setValue("1");
                    publishRTValues.add(publishRTValue);
                    sendMessage.setRTValue(publishRTValues);
                    mqttProcess.publish(inPlantTopicPush, sendMessage);
                    Thread.sleep(1000);

                    //第二次下发
                    SendMessage sendMessage2 = new SendMessage();
                    List<PublishRTValue> publishRTValues2 = new ArrayList<>();
                    PublishRTValue publishRTValue2 = new PublishRTValue();
                    if (HXPositionNumConstant.OVEN_1_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                        publishRTValue2.setName(HXPositionNumConstant.OVEN_1_CLOSE);
                    } else if (HXPositionNumConstant.OVEN_2_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                        publishRTValue2.setName(HXPositionNumConstant.OVEN_2_CLOSE);
                    }
                    publishRTValue2.setType(1);
                    publishRTValue2.setValue("0");
                    publishRTValues2.add(publishRTValue2);
                    sendMessage2.setRTValue(publishRTValues2);
                    mqttProcess.publish(inPlantTopicPush, sendMessage2);
                    Thread.sleep(1000);
                }

                //亚控scada下发
                if ("king".equals(scadaName)) {
                    //根据卷帘门Id获取开门变量
                    //第一次下发
                    String sendData = ioTEquipmentService.commandSendPackage(automaticDoor.getClose(), true, 0);
                    log.info("亚控SCADA下发关门任务，下发指令{}", sendData);
                    mqttGateway.sendToMqtt(setSubTopic, sendData);
                    Thread.sleep(1000);
                    //第二次下发初始化
                    String sendDataInit = ioTEquipmentService.commandSendPackage(automaticDoor.getClose(), false, 0);
                    log.info("亚控SCADA下发关门任务，下发指令{}", sendDataInit);
                    mqttGateway.sendToMqtt(setSubTopic, sendDataInit);
                }


                //使用 Instant 和 Duration 来精确计算时间
                final Instant startTime = Instant.now();
                final Duration timeout = Duration.ofMinutes(1);

                while (true) {
                    // 获取当前数据
                    //获取redis存储的自動門的訂閱消息
                    Map<String, Integer> scadaMessagesMap = (Map<String, Integer>) redisUtil.stringWithGet("topic_HX_msg");

                    //todo 业务处理
                    // 检查是否匹配,即门是否完全关闭

                    String closeStatusStr = (String) redisUtil.stringWithGet(automaticDoor.getCloseEnd());
                    if (Boolean.parseBoolean(closeStatusStr)) {
                        log.info("自动门{}已经完全关闭，任务结束!", automaticDoor.getEquipmentCode());
                        return true;
                    }

                    if (HXPositionNumConstant.OVEN_1_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                        if (((scadaMessagesMap.get(HXPositionNumConstant.OVEN_1_DOOR_1_CLOSE_END_1) == 1)
                                || (scadaMessagesMap.get(HXPositionNumConstant.OVEN_1_DOOR_1_CLOSE_END_2) == 1))
                                && (((scadaMessagesMap.get(HXPositionNumConstant.OVEN_1_DOOR_2_CLOSE_END_1) == 1))
                                || (scadaMessagesMap.get(HXPositionNumConstant.OVEN_1_DOOR_2_CLOSE_END_2) == 1))) {
                            log.info("自动门已经完全关闭，任务结束!");
                            return true;
                        }
                    } else if (HXPositionNumConstant.OVEN_2_NUMBER.equals(automaticDoor.getEquipmentCode())) {
                        if (((scadaMessagesMap.get(HXPositionNumConstant.OVEN_2_DOOR_1_CLOSE_END_1) == 1)
                                || (scadaMessagesMap.get(HXPositionNumConstant.OVEN_2_DOOR_1_CLOSE_END_2) == 1))
                                && (((scadaMessagesMap.get(HXPositionNumConstant.OVEN_2_DOOR_2_CLOSE_END_1) == 1))
                                || (scadaMessagesMap.get(HXPositionNumConstant.OVEN_2_DOOR_2_CLOSE_END_2) == 1))) {
                            log.info("自动门已经完全关闭，任务结束!");
                            return true;
                        }
                    } else {
                        log.info("自动门{}已经完全关闭，任务结束!", automaticDoor.getEquipmentCode());
                        return true;
                    }

                    // 检查是否超时
                    if (Duration.between(startTime, Instant.now()).compareTo(timeout) > 0) {
                        log.error("关闭自动门{}超时！", automaticDoor.getEquipmentCode());
                        setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "关闭自动门" + automaticDoor.getEquipmentCode() + "超时！");
                        return false;
                    }

                    // 短暂休眠避免CPU过载
                    try {
                        TimeUnit.MILLISECONDS.sleep(2000); // 每2000毫秒检查一次
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("关闭自动门{}轮询被中断！", automaticDoor.getEquipmentCode());
                        setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "关闭自动门" + automaticDoor.getEquipmentCode() + "轮询被中断！");
                        return false;
                    }
                }
            } else if ("Modbus".equals(automaticDoor.getCommunicationType())) {
                //通过Modbus方式关闭自动门
                try {
                    //初始化写入状态为0
                    modbusRwOperation(automaticDoor, 4, 0);
                    if (modbusRwOperation(automaticDoor, 4, 2)) {
                        log.info("准备关门");
                        // 3.1.2 调用等待方法
                        if (modbusWaitOperation(automaticDoor, 2)) {
                            //关门到位，设置寄存器为初始化状态
                            if (modbusRwOperation(automaticDoor, 4, 0)) {
                                log.info("门已关门，机器人正在远离自动门");
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("自动门处理发生异常: ", e);
                    setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "自动门处" + automaticDoor.getEquipmentCode() + "理发生异常！");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 设置运单执行结果
     *
     * @param robotWaybill
     * @param code
     * @param message
     */
    @Override
    public void setWaybillResult(RobotWaybill robotWaybill, Integer code, String message) {
        robotWaybill.setWaybillStatus(code);
        if (Objects.equals(RobotWaybillStatusEnum.TERMINATE.getCode(), code)) {
            robotWaybill.setErrorTime(new Timestamp(System.currentTimeMillis()));
        }
        if (robotWaybill.getErrorMessage() == null) {
            robotWaybill.setErrorMessage(message);
        } else {
            //用于记录最初始的错误，和初始错误触发的二次运行错误
            robotWaybill.setErrorMessage("1." + robotWaybill.getErrorMessage() + ";2." + message);
        }
        //设置运单完成时间
        robotWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
        //设置执行耗时
        robotWaybill.setExecutionTime(robotWaybill.getCompleteTime().getTime() - robotWaybill.getOrderTime().getTime());
        saveOrUpdate(robotWaybill);
        //如果所有任务都执行完成，则删除定时任务
        customScheduledTaskRegistrar.removeTriggerTask(robotWaybill.getId());
        //任务取消或者失败，设置机器人为未充电状态
        if (Objects.equals(RobotWaybillStatusEnum.TERMINATE.getCode(), code) || Objects.equals(RobotWaybillStatusEnum.CANCEL.getCode(), code)) {
            RobotBasicInfo robot = robotBasicInfoService.findByName(robotWaybill.getRobotName()).get(0);
            //设置机器人不充电
            robot.setCharge(Constant.STATE_INVALID);
            robotBasicInfoService.saveOrUpdate(robot);
        }
        //当前是充电任务并且配置自动充电桩开
        if (ChargeConstant.ROBOT_START_CHARGE.equals(robotWaybill.getTaskName()) && isIntelligentChargingStation) {
            //任务取消或者失败，则关闭开启充电桩的任务
            if (Objects.equals(RobotWaybillStatusEnum.TERMINATE.getCode(), code) || Objects.equals(RobotWaybillStatusEnum.CANCEL.getCode(), code)) {
                customScheduledTaskRegistrar.removeTriggerTask(robotWaybill.getRobotName() + ChargeConstant.ROBOT_START_CHARGE_KEY);
            }
        }
        //任务结束设置机器人初始状态
        setRobotInitialValue(robotWaybill.getRobotName());
    }

    /**
     * 获取任务链执行结果
     *
     * @param robotWaybill
     */
    public void getTaskChainResult(RobotWaybill robotWaybill, IotEquipment automaticDoor) throws Exception {
        //获取运单中的子任务列表
        String subTaskListStr = robotWaybill.getSubTaskList();
        List<RobotRunListDTO> runList = JSON.parseArray(subTaskListStr, RobotRunListDTO.class);

        //存在待执行的子任务
        if (runList.size() > 0) {
            RobotRunListDTO robotRunListDTO = runList.get(0);
            //获取当前站点的光通信站信息
            String station = robotRunListDTO.getSubRunList().get(robotRunListDTO.getSubRunList().size() - 1).getStation();

            //robotWaybill.getId()为任务链的名字
            String str = "{\"task_list_name\":\"" + robotWaybill.getRobotName() + "_" + robotWaybill.getId() + station + "\",\"with_robot_status\":true}";
            String data = DataConvertUtil.convertStringToHex(str);
            String dataLength = Integer.toHexString(data.length() / 2);
            String instruction = "5A010001000000" + dataLength + "0C1D000000000000" + data;

            RobotDms robotDms = robotDmsService.getDmsByDmsPoint(station);
            log.info("当前光通信站的IP：{}", robotDms);
            String dmsIp = robotDms.getDmsIp();
            Map<String, Thread> threadNetty = TcpClientThread.getIpThreadNetty();
            //连接获取机器人导航状态的端口
            if (!threadNetty.containsKey(dmsIp + dmsPort)) {
                robotBasicInfoService.connectTcp(dmsIp, dmsPort);
                Thread.sleep(2000);
            }
            //获取机器人任务链信息
            EntityResult result = TcpClientThread.sendHexMsg(dmsIp, dmsPort, instruction);
            //获取任务链信息成功
            if (result.isSuccess()) {
                //获取机器人任务链信息,为了防止任务链信息还没有在redis中写入，这里加入延迟
                Thread.sleep(1500);
                Map<String, String> taskListMapStatus = (Map<String, String>) redisUtil.stringWithGet("robot_tasklist_status_res");
                String taskListStatus = taskListMapStatus.get(robotWaybill.getRobotName());
                if (taskListStatus != null) {
                    RobotTaskListResDTO model = JSON.parseObject(taskListStatus, RobotTaskListResDTO.class);
                    if (model.getRetCode() == 0 && (robotWaybill.getRobotName() + "_" + robotWaybill.getId() + station).equals(model.getTasklistStatus().getTaskListName())) {
                        //任务链状态：0 = StatusNone；1 = Waiting；2 = Running；3 = Suspended；4 = Completed；5 = Failed；6 = Canceled；7 = OverTime
                        log.info("当前机器人任务链运单状态：{}", model.getTasklistStatus().getTaskListStatus());
                        if (model.getTasklistStatus().getTaskListStatus() == 4) {
                            if ("359厂复合材料法兰件自动热压设备项目".equals(projectName)) {
                                //如果由第三方系统请求,则返回到位信息
                                if (robotWaybill.getMesWaybillId() != null) {
                                    if (runList.size() == 3) {
                                        MESResDTO resp = null;
                                        try {
                                            log.info("机器人运行到位后请求WMS系统");
                                            String mesResStr = HttpClientUtil.clientPost(agvInPlaceUrl, HttpMethod.POST, null);
                                            resp = JSON.parseObject(mesResStr, MESResDTO.class);
                                            if (resp.getCode() != 200) {
                                                log.info("机器人运行到位后请求WMS系统失败：{}", resp);
                                                //更新运单执行结果
                                                setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "机器人运行到位后请求WMS系统失败!");
                                                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                                completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                                                return;
                                            }
                                        } catch (Exception e) {
                                            log.info("机器人运行到位后请求WMS系统失败：{}", resp);
                                            //更新运单执行结果
                                            setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "机器人运行到位后请求WMS系统失败!");
                                            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                            completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                                            return;
                                        }
                                    }

                                    if (runList.size() == 2) {
                                        MESResDTO resp = null;
                                        try {
                                            log.info("机器人运行到位后请求WMS系统");
                                            String mesResStr = HttpClientUtil.clientPost(agvDownPlaceUrl, HttpMethod.POST, null);
                                            resp = JSON.parseObject(mesResStr, MESResDTO.class);
                                            if (resp.getCode() != 200) {
                                                log.info("机器人运行到位后请求WMS系统失败：{}", resp);
                                                //更新运单执行结果
                                                setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "机器人运行到位后请求WMS系统失败!");
                                                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                                completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                                                return;
                                            }
                                        } catch (Exception e) {
                                            log.info("机器人运行到位后请求WMS系统失败：{}", resp);
                                            //更新运单执行结果
                                            setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "机器人运行到位后请求WMS系统失败!");
                                            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                            completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                                            return;
                                        }
                                    }
                                }
                            }

                            if ("359厂外防热自动刮涂成型系统项目".equals(projectName)) {

                            }

                            //移除任务中执行完成的子任务
                            runList.remove(0);
                            //设置运单的任务列表
                            robotWaybill.setSubTaskList(JSON.toJSONString(runList));
                            saveOrUpdate(robotWaybill);
                            boolean flag = true;
                            if (runList.size() > 0) {
                                RobotRunListDTO runListDTO = runList.get(0);
                                RobotDms currentRobotDms = robotDmsService.getDmsByDmsPoint(runListDTO.getSubRunList().get(0).getStation());
                                String currentDmsIp = currentRobotDms.getDmsIp();
                                flag = runDmsSubTask(robotWaybill, currentDmsIp);
                            }
                            if (!flag) {
                                //关闭自动门
                                closeAutomaticDoor(robotWaybill, automaticDoor);
                                //更新运单执行结果
                                setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "执行光通信任务失败!");
                                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                            }
                        } else if (model.getTasklistStatus().getTaskListStatus() == 5) {
                            //关闭自动门
                            closeAutomaticDoor(robotWaybill, automaticDoor);
                            //更新运单执行结果
                            setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "任务执行失败！");
                            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                            completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                        } else if (model.getTasklistStatus().getTaskListStatus() == 6) {
                            //关闭自动门
                            closeAutomaticDoor(robotWaybill, automaticDoor);
                            //更新运单执行结果
                            setWaybillResult(robotWaybill, RobotWaybillStatusEnum.CANCEL.getCode(), "任务取消！");
                            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                            completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                        } else if (model.getTasklistStatus().getTaskListStatus() == 404) {
                            if (!"359厂复合材料法兰件自动热压设备项目".equals(projectName)) {
                                //关闭自动门
                                closeAutomaticDoor(robotWaybill, automaticDoor);
                                //更新运单执行结果
                                setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "控制权被抢占！");
                                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                            }
                        } else if (model.getTasklistStatus().getTaskListStatus() == 2) {
                            //当前是充电任务并且配置自动充电桩开启
                            if (ChargeConstant.ROBOT_START_CHARGE.equals(robotWaybill.getTaskName()) && isIntelligentChargingStation) {
                                //获取当前站点绑定的充电桩信息
                                List<RobotChargePile> chargePileList = robotChargePileService.findListByAttribute(null, null, null, robotDms.getDmsPoint());
                                RobotChargePile robotChargePile = chargePileList.get(0);
                                //防止充电桩插头与机器人相撞
                                Thread.sleep(20000);
                                //开启充电桩端口
                                EntityResult connectChargePort = TcpClientThread.start(robotChargePile.getChargePileIp(), PortConstant.CHARGE_PILE_START);
                                if (connectChargePort.isSuccess()) {
                                    //开启定时任务，执行充电桩插头的开启
                                    String cron = "*/1 * * * * ?";
                                    customScheduledTaskRegistrar.addTriggerTask(robotWaybill.getRobotName() + ChargeConstant.ROBOT_START_CHARGE_KEY,
                                            //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                                            () -> {
                                                try {
                                                    //下发充电指令功能
                                                    log.info("开启自动充电桩{}", robotChargePile.getChargePileIp());
                                                    TcpClientThread.sendHexMsg(robotChargePile.getChargePileIp(), PortConstant.CHARGE_PILE_START, ProtocolConstant.CHARGE_PILE_START);
                                                } catch (Exception e) {
                                                    log.error("开启智能充电桩失败");
                                                    e.printStackTrace();
                                                }
                                            }, triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
                                } else {
                                    //更新运单执行结果
                                    setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "开启充电桩失败!");
                                    //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                    completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                                }
                            }
                        }
                    }
                }
            } else {
                //关闭自动门
                closeAutomaticDoor(robotWaybill, automaticDoor);
                //更新运单执行结果
                setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "给光通信站发送获取任务链信息失败！");
                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
            }
        } else {
            //关闭自动门
            closeAutomaticDoor(robotWaybill, automaticDoor);
            //更新运单执行结果
            setWaybillResult(robotWaybill, RobotWaybillStatusEnum.COMPLETE.getCode(), "SUCCESS");
            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
            completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
        }
    }


    /**
     * 获取任务链执行结果
     *
     * @param robotWaybill
     */
    public void getMultiRobotsDmsTaskResult(RobotWaybill robotWaybill) throws Exception {
        //获取待执行的任务
        String pendingTask = getById(robotWaybill.getId()).getPendingTask();
        List<RobotRunDTO> pendingTaskList = JSON.parseArray(pendingTask, RobotRunDTO.class);
        if (pendingTaskList != null) {
            //存在待执行的子任务
            if (pendingTaskList.size() > 1) {
                //获取子任务结束站点的中心点
                String endPoint = getAreaCenterPointByCurrentPoint(pendingTaskList.get(1).getStation());
                //查询任务链执行结果
                //robotWaybill.getId()为任务链的名字,endingTaskList.get(1).getStation()为当前站点名
                String str = "{\"task_list_name\":\"" + robotWaybill.getRobotName() + "_" + robotWaybill.getId() + endPoint + "\",\"with_robot_status\":true}";
                String data = DataConvertUtil.convertStringToHex(str);
                String dataLength = Integer.toHexString(data.length() / 2);
                String instruction = "5A010001000000" + dataLength + "0C1D000000000000" + data;
                //获取待执行的任务中第一条子任务
                RobotRunDTO robotRunDTO = pendingTaskList.get(1);
                //获取当前站点的光通信站信息
                String areaCenterPointByCurrentPoint = getAreaCenterPointByCurrentPoint(robotRunDTO.getStation());
                RobotDms robotDms = robotDmsService.getDmsByDmsPoint(areaCenterPointByCurrentPoint);
                String dmsIp = robotDms.getDmsIp();
                Map<String, Thread> threadNetty = TcpClientThread.getIpThreadNetty();
                //连接获取机器人导航状态的端口
                robotBasicInfoService.connectTcp(dmsIp, dmsPort);
                Thread.sleep(2000);
                //获取机器人任务链信息
                EntityResult result = TcpClientThread.sendHexMsg(dmsIp, dmsPort, instruction);
                //获取任务链信息成功
                if (result.isSuccess()) {
                    //获取机器人任务链信息,为了防止任务链信息还没有在redis中写入，这里加入延迟
                    Thread.sleep(1500);
                    Map<String, String> taskListMapStatus = (Map<String, String>) redisUtil.stringWithGet("robot_tasklist_status_res");
                    String taskListStatus = taskListMapStatus.get(robotWaybill.getRobotName());
                    if (taskListStatus != null) {
                        RobotTaskListResDTO model = JSON.parseObject(taskListStatus, RobotTaskListResDTO.class);
                        //路径导航任务执行完成
                        if (model.getRetCode() == 0 && (robotWaybill.getRobotName() + "_" + robotWaybill.getId() + endPoint).equals(model.getTasklistStatus().getTaskListName())) {
                            //任务链状态：0 = StatusNone；1 = Waiting；2 = Running；3 = Suspended；4 = Completed；5 = Failed；6 = Canceled；7 = OverTime
                            if (model.getTasklistStatus().getTaskListStatus() == 4) {
                                //到达子任务终点，释放被锁的开始站点，锁住当前站点
                                RobotLockPathAndPoint robotLockPathAndPoint = dmsLockedPointMap.get(robotWaybill.getRobotName());
                                robotLockPathAndPoint.setLockedPoint(endPoint);
                                dmsLockedPointMap.put(robotWaybill.getRobotName(), robotLockPathAndPoint);
                                //pendingTaskList.size() == 2表明机器人任务执行完成，删除pendingTaskList.remove(0)，使条件满足pendingTaskList.size() = 1时任务完成
                                if (pendingTaskList.size() == 2) {
                                    //判断开始站点和结束站点所在的区域是否具有自动门，如果有，到达结束站点时，应该关闭开始站点和结束站点所在区域的自动门
                                    //获取子任务的起点
                                    String subStartPoint = getAreaCenterPointByCurrentPoint(pendingTaskList.get(0).getStation());
                                    //获取子任务的终点
                                    String subEndPoint = getAreaCenterPointByCurrentPoint(pendingTaskList.get(1).getStation());
                                    List<IotEquipment> automaticDoorByEquipmentRegion1 = ioTEquipmentService.getAutomaticDoorByEquipmentRegion(subStartPoint);
                                    List<IotEquipment> automaticDoorByEquipmentRegion2 = ioTEquipmentService.getAutomaticDoorByEquipmentRegion(subEndPoint);
                                    List<IotEquipment> automaticDoorByEquipmentRegion = new ArrayList<>();
                                    if (automaticDoorByEquipmentRegion1.size() > 0) {
                                        if ("是".equals(automaticDoorByEquipmentRegion1.get(0).getEquipmentIsInstallDms())) {
                                            automaticDoorByEquipmentRegion.add(automaticDoorByEquipmentRegion1.get(0));
                                        }
                                    }
                                    if (automaticDoorByEquipmentRegion2.size() > 0) {
                                        automaticDoorByEquipmentRegion.add(automaticDoorByEquipmentRegion2.get(0));
                                    }
                                    for (IotEquipment iotEquipment : automaticDoorByEquipmentRegion) {
                                        closeAutomaticDoor(robotWaybill, iotEquipment);
                                    }
                                    pendingTaskList.remove(0);
                                    robotWaybill.setPendingTask(JSON.toJSONString(pendingTaskList));
                                    saveOrUpdate(robotWaybill);
                                } else {
                                    //执行光通信区域的动作
                                    if (executedConcurrentHashMap.get(robotWaybill.getRobotName())) {
                                        //判断开始站点所在的区域是否具有自动门，如果有，到达结束站点时，应该关闭开始站点所在区域的自动门
                                        //获取上一步骤子任务的起点
                                        RobotWaybill newRobotWaybill = getById(robotWaybill.getId());
                                        //关闭烘箱的方法是异步执行，因此把执行烘箱的方法放在关闭自动门的前面
                                        //判断结束区域是否存烘箱,如果存在，则等机器人到达门口前光通信站开启自动门
                                        String ovenRegion = getAreaCenterPointByCurrentPoint(pendingTaskList.get(1).getStation());
                                        List<IotEquipment> ovenByEquipmentRegion = ioTEquipmentService.getOvenByEquipmentRegion(ovenRegion);
                                        if (ovenByEquipmentRegion.size() > 0) {
                                            log.info("区域动作执行前，区域内存在烘箱设备，则打开烘箱门");
                                            for (IotEquipment ovenEquipment : ovenByEquipmentRegion) {
                                                List<String> adjacentSites = Arrays.asList(ovenEquipment.getAdjacentSite().split(","));
                                                //如果工作站点存在烘箱
                                                if ((Objects.requireNonNull(adjacentSites).contains(pendingTaskList.get(1).getStation()))) {
                                                    openOven(robotWaybill, ovenEquipment);
                                                }
                                            }
                                        }
                                        List<AlgorithmResResultDTO.Path> list = JSON.parseArray(newRobotWaybill.getPathPlanningResult(), AlgorithmResResultDTO.Path.class);
                                        AlgorithmResResultDTO.Path path = list.get(list.size() - 1);
                                        List<IotEquipment> automaticDoorByEquipmentRegion = ioTEquipmentService.getAutomaticDoorByEquipmentRegion(path.getStartPoint());
                                        for (IotEquipment iotEquipment : automaticDoorByEquipmentRegion) {
                                            if ("是".equals(iotEquipment.getEquipmentIsInstallDms())) {
                                                closeAutomaticDoor(robotWaybill, automaticDoorByEquipmentRegion.get(0));
                                            }
                                        }
                                        //判断结束区域是否存在内部不能安装光通信站的自动门,如果存在，则等机器人到达门口前光通信站开启自动门
                                        List<IotEquipment> automaticDoorByEndPoint = ioTEquipmentService.getAutomaticDoorByEquipmentRegion(path.getEndPoint());
                                        for (IotEquipment iotEquipment : automaticDoorByEndPoint) {
                                            if ("否".equals(iotEquipment.getEquipmentIsInstallDms())) {
                                                openAutomaticDoor(robotWaybill, iotEquipment);
                                            }
                                        }
                                        if (runDmsEditorAction(robotWaybill)) {
                                            executedConcurrentHashMap.put(robotWaybill.getRobotName(), false);
                                        }
                                    }
                                }
                            } else if (model.getTasklistStatus().getTaskListStatus() == 5) {
                                //更新运单执行结果
                                setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "任务执行失败！");
                                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                            } else if (model.getTasklistStatus().getTaskListStatus() == 6) {
                                //更新运单执行结果
                                setWaybillResult(robotWaybill, RobotWaybillStatusEnum.CANCEL.getCode(), "任务取消！");
                                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                                //
                            } else if (model.getTasklistStatus().getTaskListStatus() == 404 && queryExecutedConcurrentHashMap.get(robotWaybill.getRobotName())) {
                                String strAction = "{\"task_list_name\":\"" + robotWaybill.getRobotName() + "action" + "_" + robotWaybill.getId() + endPoint + "\",\"with_robot_status\":true}";
                                String dataAction = DataConvertUtil.convertStringToHex(strAction);
                                String dataLengthAction = Integer.toHexString(dataAction.length() / 2);
                                String instructionAction = "5A010001000000" + dataLengthAction + "0C1D000000000000" + dataAction;
                                //连接获取机器人导航状态的端口
                                if (!threadNetty.containsKey(dmsIp + dmsPort)) {
                                    robotBasicInfoService.connectTcp(dmsIp, dmsPort);
                                    Thread.sleep(2000);
                                }
                                //获取机器人任务链信息
                                TcpClientThread.sendHexMsg(dmsIp, dmsPort, instructionAction);

                                //获取机器人任务链信息,为了防止任务链信息还没有在redis中写入，这里加入延迟
                                Thread.sleep(2000);
                                Map<String, String> taskListMapStatus1 = (Map<String, String>) redisUtil.stringWithGet("robot_tasklist_status_res");
                                if (taskListMapStatus1 != null) {
                                    String taskListStatus1 = taskListMapStatus1.get(robotWaybill.getRobotName() + "action");
                                    RobotTaskListResDTO model1 = JSON.parseObject(taskListStatus1, RobotTaskListResDTO.class);
                                    //区域内动作任务执行完成
                                    if (model1 != null && model1.getRetCode() == 0 && (robotWaybill.getRobotName() + "action" + "_" + robotWaybill.getId() + endPoint).equals(model1.getTasklistStatus().getTaskListName())
                                            && model1.getTasklistStatus().getTaskListStatus() == 4) {
                                        if ("顶升".equals(pendingTaskList.get(1).getInstruction())) {
                                            //设置运单的状态为取货完成
                                            robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.PICK_UP_COMPLETED.getCode());
                                            saveOrUpdate(robotWaybill);
                                            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                            completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                                        }

                                        if ("顶降".equals(pendingTaskList.get(1).getInstruction())) {
                                            //设置运单的状态为放货货完成
                                            robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.PICK_DOWN_COMPLETED.getCode());
                                            saveOrUpdate(robotWaybill);
                                            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                            completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                                        }

                                        pendingTaskList.remove(0);
                                        robotWaybill.setPendingTask(JSON.toJSONString(pendingTaskList));
                                        saveOrUpdate(robotWaybill);
                                        //-------------------------------------(混合调度)-------------------------------------------------------------------------------
                                        //子任务执行完成，释放掉占用的管控区域
                                        List<RobotDmsEditor> listByAreaTypeAndOccupied = robotDmsEditorService.findListByAreaTypeAndOccupiedAndRobotName(2, 1, robotWaybill.getRobotName());
                                        if (listByAreaTypeAndOccupied != null) {
                                            for (RobotDmsEditor robotDmsEditor : listByAreaTypeAndOccupied) {
                                                //除了当前站点所在的管控区域，即结束站点所在的管控区域，其它管控区域设置为非占用
                                                if (!endPoint.equals(robotDmsEditor.getAreaCenterPoint())) {
                                                    //设置管控区域的状态为非占用
                                                    robotDmsEditor.setOccupiedRobotName("");
                                                    robotDmsEditor.setOccupiedStatus(0);
                                                    robotDmsEditorService.saveOrUpdate(robotDmsEditor);
                                                }
                                            }
                                        }
                                        //-----------------------------------------------------------------------------------------------------------------------------

                                        log.info("机器人区域内动作任务执行完成，执行后续子任务");
                                        String ovenRegion = getAreaCenterPointByCurrentPoint(pendingTaskList.get(0).getStation());
                                        List<IotEquipment> ovenByEquipmentRegion = ioTEquipmentService.getOvenByEquipmentRegion(ovenRegion);
                                        if (ovenByEquipmentRegion.size() > 0) {
                                            log.info("区域动作执行完成，区域内存在烘箱设备，则关闭烘箱门");
                                            for (IotEquipment ovenEquipment : ovenByEquipmentRegion) {
                                                List<String> adjacentSites = Arrays.asList(ovenEquipment.getAdjacentSite().split(","));
                                                //如果工作站点存在烘箱
                                                if ((Objects.requireNonNull(adjacentSites).contains(pendingTaskList.get(0).getStation()))) {
                                                    closeOven(robotWaybill, ovenEquipment);
                                                }
                                            }
                                        }
                                        boolean flag = runMultiRobotsDmsSubTask(robotWaybill);
                                        if (!flag) {
                                            //更新运单执行结果
                                            setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "运行光通信任务失败!");
                                            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                            completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //此处不能添加获取任务链信息失败。因为机器人离开光通信站以后，此处一定会导致失败
//                else {
//                    //关闭自动门
//                    closeAutomaticDoor(robotWaybill, automaticDoor);
//                    //更新运单执行结果
//                    setWaybillResult(robotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "给光通信站发送获取任务链信息失败！");
//                    //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
//                    completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
//                }
            } else {
                //移除锁定路径
                dmsLockedPointMap.remove(robotWaybill.getRobotName());
                //更新运单执行结果
                setWaybillResult(robotWaybill, RobotWaybillStatusEnum.COMPLETE.getCode(), "SUCCESS");
                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
            }
        }
    }

    /**
     * 查询任务链执行结果指令
     *
     * @param robotWaybill
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean queryTaskChainResult(RobotWaybill robotWaybill, String dmsPoint) throws Exception {
        //查询任务链执行结果
        //robotWaybill.getId()为任务链的名字,endingTaskList.get(1).getStation()为当前站点名
        String str = "{\"task_list_name\":\"" + robotWaybill.getRobotName() + "_" + robotWaybill.getId() + dmsPoint + "\",\"with_robot_status\":true}";
        String data = DataConvertUtil.convertStringToHex(str);
        String dataLength = Integer.toHexString(data.length() / 2);
        String instruction = "5A010001000000" + dataLength + "0C1D000000000000" + data;
        //获取当前站点的光通信站信息
        RobotDms robotDms = robotDmsService.getDmsByDmsPoint(dmsPoint);
        String dmsIp = robotDms.getDmsIp();
        Map<String, Thread> threadNetty = TcpClientThread.getIpThreadNetty();
        //判断当前光通信站是否在线
//        InetAddress geek = InetAddress.getByName(dmsIp);
//        if (!geek.isReachable(3000)) {
//            setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), dmsIp + "光通信站没有在线！执行查询任务链指令！");
//            return false;
//        }
        //连接获取机器人导航状态的端口
        if (!threadNetty.containsKey(dmsIp + dmsPort)) {
            robotBasicInfoService.connectTcp(dmsIp, dmsPort);
            Thread.sleep(2000);
        }
        //获取机器人任务链信息
        EntityResult result = TcpClientThread.sendHexMsg(dmsIp, dmsPort, instruction);
        return result.isSuccess();
    }

    /**
     * 执行光通信区域的动作
     *
     * @param robotWaybill
     * @throws Exception
     */
    public boolean runDmsEditorAction(RobotWaybill robotWaybill) throws Exception {
        //获取待执行的任务
        String pendingTask = getById(robotWaybill.getId()).getPendingTask();
        List<RobotRunDTO> pendingTaskList = JSON.parseArray(pendingTask, RobotRunDTO.class);
        //当前执行的任务列表
        List<RobotRunDTO> currentExecuteTaskList = new ArrayList<>();
        currentExecuteTaskList.add(pendingTaskList.get(1));
        //获取当前站点的光通信站信息
        String endStation = getAreaCenterPointByCurrentPoint(pendingTaskList.get(1).getStation());
        RobotRunDTO endRobotRunDTO = new RobotRunDTO();
        endRobotRunDTO.setVehicle(robotWaybill.getRobotName());
        endRobotRunDTO.setStation(endStation);
        endRobotRunDTO.setInstruction("机器人路径导航");
        //添加机器人返回光通信区域中心点的结束任务
        currentExecuteTaskList.add(endRobotRunDTO);
        if (pendingTaskList.size() > 1) {
            //获取剩余待执行的任务中第一条子任务
            RobotRunDTO currentRobotRunDTO = pendingTaskList.get(1);
            //获取当前站点的光通信站信息
            String areaCenterPointByCurrentPoint = getAreaCenterPointByCurrentPoint(currentRobotRunDTO.getStation());
            RobotDms currentRobotDms = robotDmsService.getDmsByDmsPoint(areaCenterPointByCurrentPoint);
            String currentDmsIp = currentRobotDms.getDmsIp();
            //连接获取机器人导航状态的端口
            if (!robotBasicInfoService.connectTcp(currentDmsIp, dmsPort)) {
                setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), currentDmsIp + "光通信站没有在线！");
                return false;
            }
            Thread.sleep(2000);
            //把子任务列表封装成任务链
            RobotTaskListDTO robotTaskListDTO = encapsulatingSubTaskChain(currentExecuteTaskList, robotWaybill, 2);
            String data = DataConvertUtil.convertStringToHex(JSON.toJSONString(robotTaskListDTO));
            String dataLength = Integer.toHexString(data.length() / 2);
            if (dataLength.length() == 4) {
                dataLength = dataLength;
            } else if (dataLength.length() == 3) {
                dataLength = "0" + dataLength;
            } else if (dataLength.length() == 2) {
                dataLength = "00" + dataLength;
            } else if (dataLength.length() == 1) {
                dataLength = "000" + dataLength;
            }
            String instruction = "5A0100000000" + dataLength + "0C1C0C1C00000000" + data;
            //执行机器人任务指令
            TcpClientThread.sendHexMsg(currentDmsIp, dmsPort, instruction);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            EntityResult result = TcpClientThread.sendHexMsg(currentDmsIp, dmsPort, instruction);
            log.info("执行区域内动作任务:{}", JSON.toJSONString(robotTaskListDTO));
            log.info("执行区域内动作的光通信站IP：{},光通信站port：{},光通信任务指令执行结果：{}", currentDmsIp, dmsPort, result);
            if (!result.isSuccess()) {
                //连接获取机器人导航状态的端口
                robotBasicInfoService.connectTcp(currentDmsIp, dmsPort);
                Thread.sleep(2000);
                EntityResult result2 = TcpClientThread.sendHexMsg(currentDmsIp, dmsPort, instruction);
                if (!result2.isSuccess()) {
                    Thread.sleep(1000);
                    setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "执行机器人指令失败！");
                }
                return result2.isSuccess();
            }
            return result.isSuccess();
        }
        return true;
    }

    /**
     * 一个一个运行任务表中的子任务
     *
     * @param robotWaybill
     * @param dmsIp
     * @return
     */
    public boolean runDmsSubTask(RobotWaybill robotWaybill, String dmsIp) throws InterruptedException, IOException {
        //获取运单中的子任务列表
        String subTaskListStr = robotWaybill.getSubTaskList();
        List<RobotRunListDTO> runList = JSON.parseArray(subTaskListStr, RobotRunListDTO.class);
        //存在待执行的子任务
        RobotRunListDTO robotRunListDTO = runList.get(0);
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //连接获取机器人导航状态的端口
        if (!map.containsKey(dmsIp + dmsPort)) {
            robotBasicInfoService.connectTcp(dmsIp, dmsPort);
            Thread.sleep(2000);
        }
        //把子任务列表封装成任务链
        RobotTaskListDTO robotTaskListDTO = encapsulatingSubTaskChain(robotRunListDTO.getSubRunList(), robotWaybill, 1);
        String data = DataConvertUtil.convertStringToHex(JSON.toJSONString(robotTaskListDTO));
        String dataLength = Integer.toHexString(data.length() / 2);
        if (dataLength.length() == 4) {
            dataLength = dataLength;
        } else if (dataLength.length() == 3) {
            dataLength = "0" + dataLength;
        } else if (dataLength.length() == 2) {
            dataLength = "00" + dataLength;
        } else if (dataLength.length() == 1) {
            dataLength = "000" + dataLength;
        }
        String instruction = "5A0100000000" + dataLength + "0C1C0C1C00000000" + data;
        //获取机器人导航状态
        EntityResult result = TcpClientThread.sendHexMsg(dmsIp, dmsPort, instruction);
        log.info("光通信站IP：{},光通信站port：{},光通信任务指令执行结果：{}", dmsIp, dmsPort, result);
        return result.isSuccess();
    }

    /**
     * 运行多机器人的光通信子任务
     *
     * @param robotWaybill
     * @return
     */
    public boolean runMultiRobotsDmsSubTask(RobotWaybill robotWaybill) throws Exception {
        //获取运单中的子任务列表
        robotWaybill = getById(robotWaybill.getId());
        String subTaskListStr = robotWaybill.getSubTaskList();
        List<RobotRunListDTO> runList = JSON.parseArray(subTaskListStr, RobotRunListDTO.class);
        //存在待执行的子任务
        RobotRunListDTO robotRunListDTO = runList.get(0);
        //获取任务模块中的子任务
        List<RobotRunDTO> subRunList = robotRunListDTO.getSubRunList();
        List<RobotRunDTO> pendingTaskList;
        //如果待执行的任务为null,说明初次执行当前任务
        if (robotWaybill.getPendingTask() == null) {
            //初次执行时，把完整任务赋值给待执行任务列
            pendingTaskList = subRunList;
        } else {
            String pendingTask = robotWaybill.getPendingTask();
            pendingTaskList = JSON.parseArray(pendingTask, RobotRunDTO.class);
        }
        robotWaybill.setPendingTask(JSON.toJSONString(pendingTaskList));
        saveOrUpdate(robotWaybill);
        //存在未执行完成的任务
        if (pendingTaskList.size() > 1) {
            //获取工作站点对应的光通信中心点
            //获取子任务的起点
            String startPoint = getAreaCenterPointByCurrentPoint(pendingTaskList.get(0).getStation());
            //获取子任务的终点
            String endPoint = getAreaCenterPointByCurrentPoint(pendingTaskList.get(1).getStation());
            //获取机器人名字
            String robotName = robotWaybill.getRobotName();
            //获取运单id
            String waybillId = robotWaybill.getId();

            //封装路径规划算法导航的机器人名称、起点和终点
            RobotPathPlanDTO.RobotPath robotPath = robotTaskService.encapsulateData(robotWaybill.getRobotName(), robotWaybill.getRobotGroupName(), startPoint, endPoint);
            List<RobotPathPlanDTO.RobotPath> robotPathList = new ArrayList<>();
            robotPathList.add(robotPath);

            //获取机器人场景拓扑区域地图
            List<RobotPathVO> pathList = getProcessedPaths(robotWaybill.getRobotName());

            //开始请求路径规划算法执行
            log.info("开始请求路径规划算法,请求的任务：{}", JSON.toJSONString(robotPathList));
            AlgorithmRequestResDTO algorithmRequestResDTO = robotTaskService.startPathPlanningTask(pathList, robotPathList, null);
            log.info("开始请求路径规划算法,请求的路径{}", JSON.toJSONString(pathList));
            if ("success".equals(algorithmRequestResDTO.getStatus())) {
                //监测路径规划算法的结果是否返回
                String cron = "*/3 * * * * ?";
                RobotWaybill finalRobotWaybill = robotWaybill;
                customScheduledTaskRegistrar.addTriggerTask(
                        algorithmRequestResDTO.getTaskId(),
                        //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                        () -> {
                            try {
                                runPathPlanningTaskDms(algorithmRequestResDTO.getTaskId(), waybillId, false);
                            } catch (Exception e) {
                                //如果查询到算法执行返回的结果，则删除定时任务
                                log.error("开启线程,运行光通信多机器人导航任务，错误信息{}", e);
                                //更新运单执行结果
                                setWaybillResult(finalRobotWaybill, RobotWaybillStatusEnum.TERMINATE.getCode(), "运行光通信任务失败!");
                                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                                try {
                                    completeTaskSyncToOtherSystem(finalRobotWaybill.getMesWaybillId(), finalRobotWaybill.getWmsWaybillId());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        },
                        triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
            }
            //请求算法成功
            return "success".equals(algorithmRequestResDTO.getStatus());
        } else {
            return true;
        }
    }

    /**
     * 运行任务链指令
     *
     * @param robotWaybill
     * @param dmsIp
     * @param currentExecuteTaskList
     * @return
     */
    public boolean runMultiRobotsDmsSubTaskInstruction(String beginPort, String endPort, float totalCurveLength, RobotWaybill robotWaybill, String dmsIp, List<RobotRunDTO> currentExecuteTaskList) throws Exception {
        //如果起始点和终点的坐标一样，则表示需要再该点等待一定时间(totalCurveLength = 0.表示原地导航)
        if (beginPort.equals(endPort) && totalCurveLength != 0.0) {
            try {
                Thread.sleep((long) (totalCurveLength * 1500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            //连接获取机器人导航状态的端口
            boolean b = robotBasicInfoService.connectTcp(dmsIp, dmsPort);
            //判断当前光通信站是否连接成功
            if (!b) {
                setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), dmsIp + "光通信站没有在线，导致执行机器人任务指令失败！");
                return false;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //把子任务列表封装成任务链，任务链名字的
            RobotTaskListDTO robotTaskListDTO = encapsulatingSubTaskChain(currentExecuteTaskList, robotWaybill, 3);
            String data = DataConvertUtil.convertStringToHex(JSON.toJSONString(robotTaskListDTO));
            String dataLength = Integer.toHexString(data.length() / 2);
            if (dataLength.length() == 4) {
                dataLength = dataLength;
            } else if (dataLength.length() == 3) {
                dataLength = "0" + dataLength;
            } else if (dataLength.length() == 2) {
                dataLength = "00" + dataLength;
            } else if (dataLength.length() == 1) {
                dataLength = "000" + dataLength;
            }
            String instruction = "5A0100000000" + dataLength + "0C1C0C1C00000000" + data;
            TcpClientThread.sendHexMsg(dmsIp, dmsPort, instruction);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            EntityResult result = TcpClientThread.sendHexMsg(dmsIp, dmsPort, instruction);
            log.info("光通信站IP：{},光通信站port：{},光通信任务指令执行结果：{}", dmsIp, dmsPort, result);
            if (!result.isSuccess()) {
                //连接获取机器人导航状态的端口
                robotBasicInfoService.connectTcp(dmsIp, dmsPort);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EntityResult result2 = TcpClientThread.sendHexMsg(dmsIp, dmsPort, instruction);
                if (!result2.isSuccess()) {
                    Thread.sleep(1000);
                    setTaskFailData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), "执行机器人指令失败！");
                }
                return result2.isSuccess();
            }
            return result.isSuccess();
        }
    }

    /**
     * 把任务中的子任务封装成子任务列表
     *
     * @param robotWaybill
     * @param model
     * @return
     */
    public List<RobotRunDTO> encapsulatingSubTaskList(RobotWaybill robotWaybill, RobotTaskDTO model) {
        //获取任务id
        String id = robotWaybill.getTaskId();
        //获取任务参数变量名和默认值map集合
        Map<String, String> parameterMap = getParameterValueList(robotWaybill.getTaskParameter());
        //封装成机器人可以执行的任务链
        RobotTaskDTO[] children = model.getChildren();
        RobotTaskDTO.SubParameter[] parameters = model.getParameters();
        ArrayList<RobotRunDTO> list = new ArrayList<>();
        //从任务内容中获取关键路径（keyRoute）,即机器人停放点
        String keyRoute = null;
        //机器人名称
        String vehicle = null;
        //机器人分组
        String groupName = null;
        for (RobotTaskDTO.SubParameter parameter : parameters) {
            if ("keyRoute".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    keyRoute = parameterMap.get(parameter.getValue());
                } else {
                    keyRoute = parameter.getValue();
                }
            }
            if ("vehicle".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    vehicle = parameterMap.get(parameter.getValue());
                } else {
                    vehicle = parameter.getValue();
                }
            }
            if ("group".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    groupName = parameterMap.get(parameter.getValue());
                } else {
                    groupName = parameter.getValue();
                }
            }
        }

        RobotRunDTO outRobotRunDTO = new RobotRunDTO();
        outRobotRunDTO.setVehicle(vehicle);
        outRobotRunDTO.setStation(keyRoute);
        outRobotRunDTO.setGroupName(groupName);
        outRobotRunDTO.setInstruction(model.getName());
        //把外部的任务添加到要运行的任务列表中
        list.add(outRobotRunDTO);
        for (RobotTaskDTO child : children) {
            String childVehicle = null;
            String childGroupName = null;
            String childKeyRoute = null;
            String childTargetSiteLabel = null;
            String waitDoId = null;
            String waitDoStatus = null;
            String waitDoTime = null;
            String forkLiftHeight = null;
            String waitDiId = null;
            String waitDiStatus = null;
            String waitDiTime = null;
            String loadOrUnload = null;
            String locationLabel = null;
            RobotTaskDTO.SubParameter[] childParameters = child.getParameters();
            //自定义动作JSON字符串
            String customizedActionsNewJson = null;
            for (RobotTaskDTO.SubParameter parameter : childParameters) {
                if ("vehicle".equals(parameter.getId())) {
                    childVehicle = parameter.getValue();
                }

                if ("group".equals(parameter.getId())) {
                    childGroupName = parameter.getValue();
                }

                if ("keyRoute".equals(parameter.getId())) {
                    childKeyRoute = parameter.getValue();
                }

                if ("targetSiteLabel".equals(parameter.getId())) {
                    if (parameterMap.containsKey(parameter.getValue())) {
                        childTargetSiteLabel = parameterMap.get(parameter.getValue());
                    } else {
                        childTargetSiteLabel = parameter.getValue();
                    }
                }

                if ("waitDoId".equals(parameter.getId())) {
                    waitDoId = parameter.getValue();
                }

                if ("waitDoStatus".equals(parameter.getId())) {
                    waitDoStatus = parameter.getValue();
                }

                if ("waitDoTime".equals(parameter.getId())) {
                    waitDoTime = parameter.getValue();
                }

                if ("forkLiftHeight".equals(parameter.getId())) {
                    forkLiftHeight = parameter.getValue();
                }

                if ("waitDiId".equals(parameter.getId())) {
                    waitDiId = parameter.getValue();
                }

                if ("waitDiStatus".equals(parameter.getId())) {
                    waitDiStatus = parameter.getValue();
                }

                if ("waitDiTime".equals(parameter.getId())) {
                    waitDiTime = parameter.getValue();
                }

                if ("loadOrUnload".equals(parameter.getId())) {
                    loadOrUnload = parameter.getValue();
                }

                if ("locationLabel".equals(parameter.getId())) {
                    if (parameterMap.containsKey(parameter.getValue())) {
                        locationLabel = parameterMap.get(parameter.getValue());
                    } else {
                        locationLabel = parameter.getValue();
                    }
                }

                /*xile
                 * 若为自定义动作节点，则执行专用解析方法（leo add）
                 */
                if ("customizedActions".equals(parameter.getId())) {
                    // 获取自定义动作原始JSON字符串
                    String customizedActionsRawJson = parameter.getValue();
                    // 重构自定义动作JSON字符串
                    customizedActionsNewJson = robotTaskService.refactorJson(customizedActionsRawJson, parameterMap);
                }
            }
            RobotRunDTO innerRobotRunDTO = new RobotRunDTO();
            innerRobotRunDTO.setVehicle(vehicle);
            innerRobotRunDTO.setStation(childTargetSiteLabel);
            innerRobotRunDTO.setInstruction(child.getName());
            innerRobotRunDTO.setWaitDoId(waitDoId);
            innerRobotRunDTO.setWaitDoStatus(waitDoStatus);
            innerRobotRunDTO.setWaitDoTime(waitDoTime);
            innerRobotRunDTO.setForkLiftHeight(forkLiftHeight);
            innerRobotRunDTO.setWaitDiId(waitDiId);
            innerRobotRunDTO.setWaitDiStatus(waitDiStatus);
            innerRobotRunDTO.setWaitDiTime(waitDiTime);
            innerRobotRunDTO.setLoadOrUnload(loadOrUnload);
            innerRobotRunDTO.setLocationLabel(locationLabel);
            // 自定义动作赋值xile
            innerRobotRunDTO.setCustomizedActions(customizedActionsNewJson);
            list.add(innerRobotRunDTO);
        }
        return list;
    }

    /**
     * 将子任务列表封装成子任务链
     *
     * @param list
     * @param robotWaybill
     * @param flag         flag == 1 当前任务链站点；flag == 2 执行任务链动作； flag == 3当前站点中心点
     * @return
     */
    public RobotTaskListDTO encapsulatingSubTaskChain(List<RobotRunDTO> list, RobotWaybill robotWaybill, Integer flag) throws IOException {
        //用於解決Bintask任務必須在前置點執行的問題，增加了一個導航到遷至點的任務
        if ("BinTask".equals(list.get(0).getInstruction()) || "等待DI".equals(list.get(0).getInstruction())) {
            List<RobotRunDTO> newList = new ArrayList<>();
            RobotRunDTO runDTO = list.get(0);
            RobotRunDTO navigationRunDTO = new RobotRunDTO();
            navigationRunDTO.setInstruction("机器人路径导航");
            navigationRunDTO.setStation(runDTO.getStation());
            navigationRunDTO.setVehicle(runDTO.getVehicle());
            navigationRunDTO.setLoadOrUnload(runDTO.getLoadOrUnload());
            navigationRunDTO.setLocationLabel(runDTO.getLocationLabel());
            newList.add(navigationRunDTO);
            newList.addAll(list);
            list = newList;
        }
        List<TaskDTO.ActionGroup> actionGroups = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String instruction = list.get(i).getInstruction();
            List<TaskDTO.ActionGroup.Action> actions = new ArrayList<>();
            //构造TaskDTO.ActionGroup.Action
            TaskDTO.ActionGroup.Action action = new TaskDTO.ActionGroup.Action();

            //如果执行的自定义动作
            if ("自定义动作".equals(instruction)) {
                return encapsulatingCustomAction(list, robotWaybill, flag);
            }

            //机器人路径导航
            if ("机器人路径导航".equals(instruction) || "选择执行机器人".equals(instruction)) {
                action.setActionName("move_action");
                action.setPluginName("MoveFactory");
                List<TaskDTO.ActionGroup.Action.Param> params = new ArrayList<>();
                TaskDTO.ActionGroup.Action.Param param1 = new TaskDTO.ActionGroup.Action.Param();
                param1.setKey("skill_name");
                param1.setStringValue("GotoSpecifiedPose");
                TaskDTO.ActionGroup.Action.Param param2 = new TaskDTO.ActionGroup.Action.Param();
                param2.setKey("target_name");
                param2.setStringValue(list.get(i).getStation());
                params.add(param1);
                params.add(param2);
                action.setParams(params);
                action.setSleepTime(0);
            }

            //等待DI
            if ("等待DI".equals(instruction)) {
                action.setActionName("action_waitDI");
                action.setPluginName("TaskManager");
                List<TaskDTO.ActionGroup.Action.Param> params = new ArrayList<>();
                TaskDTO.ActionGroup.Action.Param param1 = new TaskDTO.ActionGroup.Action.Param();
                param1.setKey("index");
                param1.setInt32Value(Integer.parseInt(list.get(i).getWaitDiId()));
                TaskDTO.ActionGroup.Action.Param param2 = new TaskDTO.ActionGroup.Action.Param();
                param2.setKey("value");
                param2.setBoolValue(!"0".equals(list.get(i).getWaitDiStatus()));
                params.add(param1);
                params.add(param2);
                action.setParams(params);
                if (list.get(i).getWaitDiTime() == null || "".equals(list.get(i).getWaitDiTime())) {
                    action.setSleepTime(0);
                } else {
                    action.setSleepTime(Integer.parseInt(list.get(i).getWaitDiTime()) * 1000);
                }
            }

            //等待DO
            if ("等待DO".equals(instruction)) {
                action.setActionName("action_setDO");
                action.setPluginName("DSPChassis");
                List<TaskDTO.ActionGroup.Action.Param> params = new ArrayList<>();
                TaskDTO.ActionGroup.Action.Param param1 = new TaskDTO.ActionGroup.Action.Param();
                param1.setKey("index");
                param1.setInt32Value(Integer.parseInt(list.get(i).getWaitDoId()));
                TaskDTO.ActionGroup.Action.Param param2 = new TaskDTO.ActionGroup.Action.Param();
                param2.setKey("value");
                param2.setBoolValue(!"0".equals(list.get(i).getWaitDoStatus()));
                params.add(param1);
                params.add(param2);
                action.setParams(params);
                if (list.get(i).getWaitDoTime() == null || "".equals(list.get(i).getWaitDoTime())) {
                    action.setSleepTime(0);
                } else {
                    action.setSleepTime(Integer.parseInt(list.get(i).getWaitDoTime()) * 1000);
                }
            }

            //顶升
            if ("顶升".equals(instruction)) {
                action.setActionName("move_action");
                action.setPluginName("MoveFactory");
                List<TaskDTO.ActionGroup.Action.Param> params = new ArrayList<>();
                TaskDTO.ActionGroup.Action.Param param1 = new TaskDTO.ActionGroup.Action.Param();
                param1.setKey("skill_name");
                param1.setStringValue("Action");
                TaskDTO.ActionGroup.Action.Param param2 = new TaskDTO.ActionGroup.Action.Param();
                param2.setKey("body");
                param2.setStringValue("{" + "\"id\": \"" + list.get(i).getStation() + "\"," +
                        "\"jack_height\": 0.06," +
                        "\"operation\": \"JackLoad\"," +
                        "\"recognize\": false," +
                        "\"use_down_pgv\": false," +
                        "\"use_pgv\": false" +
                        "}");
                params.add(param1);
                params.add(param2);
                action.setParams(params);
                action.setSleepTime(0);
            }

            //顶降
            if ("顶降".equals(instruction)) {
                action.setActionName("move_action");
                action.setPluginName("MoveFactory");
                List<TaskDTO.ActionGroup.Action.Param> params = new ArrayList<>();
                TaskDTO.ActionGroup.Action.Param param1 = new TaskDTO.ActionGroup.Action.Param();
                param1.setKey("skill_name");
                param1.setStringValue("Action");
                TaskDTO.ActionGroup.Action.Param param2 = new TaskDTO.ActionGroup.Action.Param();
                param2.setKey("body");
                param2.setStringValue("{" + "\"id\": \"" + list.get(i).getStation() + "\"," +
                        "\"operation\": \"JackUnload\"," +
                        "\"recognize\": false," +
                        "\"use_down_pgv\": false," +
                        "\"use_pgv\": false" +
                        "}");
                params.add(param1);
                params.add(param2);
                action.setParams(params);
                action.setSleepTime(0);
            }
            //BinTask
            if ("BinTask".equals(instruction)) {
                action.setActionName("move_action");
                action.setPluginName("MoveFactory");
                List<TaskDTO.ActionGroup.Action.Param> params = new ArrayList<>();
                TaskDTO.ActionGroup.Action.Param param1 = new TaskDTO.ActionGroup.Action.Param();
                param1.setKey("skill_name");
                param1.setStringValue("Action");
                TaskDTO.ActionGroup.Action.Param param2 = new TaskDTO.ActionGroup.Action.Param();
                param2.setKey("body");
                //例子：把AP7转化成Loc-7
//                String workLocPoint = "Loc-" + list.get(i).getStation().substring(2);
                param2.setStringValue("{" + "\"binTask\": \"" + list.get(i).getLoadOrUnload() + "\"," +
                        "\"id\": \"" + list.get(i).getLocationLabel() + "\"" +
                        "}");
                params.add(param1);
                params.add(param2);
                action.setParams(params);
                action.setSleepTime(0);
            }

            action.setIgnoreReturn(false);
            action.setOvertime(0);
            action.setExternalOverId(-1);
            action.setNeedResult(false);
            action.setActionId(0);
            actions.add(action);
            //构造TaskDTO.ActionGroup
            TaskDTO.ActionGroup actionGroup = new TaskDTO.ActionGroup();
            actionGroup.setActions(actions);
            actionGroup.setActionGroupName("group " + (i + 1));
            actionGroup.setActionGroupId(i);
            actionGroup.setChecked(true);
            actionGroups.add(actionGroup);
        }
        List<TaskDTO> tasks = new ArrayList<>();
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskId(0);
        taskDTO.setDesc("");
        taskDTO.setActionGroups(actionGroups);
        taskDTO.setChecked(true);
        tasks.add(taskDTO);
        //构造机器人任务链
        RobotTaskListDTO taskListDTO = new RobotTaskListDTO();
        //注意：这里的名字必须是字母、数字和下划线组成，其它则会导致错误（这里使用id任务链的名字），后面加上机器人导航的终点点位用来定义当前任务链名称
        if (flag == 1) {
            taskListDTO.setName(robotWaybill.getRobotName() + "_" + robotWaybill.getId() + list.get(list.size() - 1).getStation());
        } else if (flag == 2) {
            //获取子任务结束站点
            String endPoint = getAreaCenterPointByCurrentPoint(list.get(list.size() - 1).getStation());
            taskListDTO.setName(robotWaybill.getRobotName() + "action" + "_" + robotWaybill.getId() + endPoint);
        } else {
            //获取子任务结束站点
            String endPoint = getAreaCenterPointByCurrentPoint(list.get(list.size() - 1).getStation());
            taskListDTO.setName(robotWaybill.getRobotName() + "_" + robotWaybill.getId() + endPoint);
        }
        taskListDTO.setTasks(tasks);
        taskListDTO.setLoop(false);
        return taskListDTO;
    }

    /**
     * 封装自定义动作
     *
     * @param list
     * @param robotWaybill
     * @param flag
     * @return
     */
    public RobotTaskListDTO encapsulatingCustomAction(List<RobotRunDTO> list, RobotWaybill robotWaybill, Integer flag) throws IOException {
        String customizedActions = list.get(0).getCustomizedActions();
        RobotTaskListDTO taskListDTO = JSON.parseObject(customizedActions, RobotTaskListDTO.class);
        //注意：这里的名字必须是字母、数字和下划线组成，其它则会导致错误（这里使用id任务链的名字），后面加上机器人导航的终点点位用来定义当前任务链名称
        if (flag == 1) {
            taskListDTO.setName(robotWaybill.getRobotName() + "_" + robotWaybill.getId() + list.get(list.size() - 1).getStation());
        } else if (flag == 2) {
            //获取子任务结束站点
            String endPoint = getAreaCenterPointByCurrentPoint(list.get(list.size() - 1).getStation());
            taskListDTO.setName(robotWaybill.getRobotName() + "action" + "_" + robotWaybill.getId() + endPoint);
        } else {
            //获取子任务结束站点
            String endPoint = getAreaCenterPointByCurrentPoint(list.get(list.size() - 1).getStation());
            taskListDTO.setName(robotWaybill.getRobotName() + "_" + robotWaybill.getId() + endPoint);
        }
        return taskListDTO;
    }

    /**
     * 运行光通信任务
     *
     * @param robotWaybill
     * @return
     */
    @Override
    public boolean runRobotDmsTask(RobotWaybill robotWaybill) {
        //获取任务id
        String id = robotWaybill.getTaskId();
        //获取任务参数变量名和默认值map集合
        Map<String, String> parameterMap = getParameterValueList(robotWaybill.getTaskParameter());

        //获取任务编辑界面的任务内容
        String taskContent = robotTaskService.getById(id).getTaskContent();
        String substring = taskContent.substring(1);
        String taskStr = substring.substring(0, substring.length() - 1);
        RobotTaskDTO model = JSON.parseObject(taskStr, RobotTaskDTO.class);
        RobotTaskDTO[] children = model.getChildren();
        RobotTaskDTO.SubParameter[] parameters = model.getParameters();
        ArrayList<RobotRunDTO> list = new ArrayList<>();
        //从任务内容中获取关键路径（keyRoute）,即机器人停放点
        String keyRoute = null;
        //机器人名称
        String vehicle = null;
        //机器人分组
        String groupName = null;
        for (RobotTaskDTO.SubParameter parameter : parameters) {
            if ("keyRoute".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    keyRoute = parameterMap.get(parameter.getValue());
                } else {
                    keyRoute = parameter.getValue();
                }
            }
            if ("vehicle".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    vehicle = parameterMap.get(parameter.getValue());
                } else {
                    vehicle = parameter.getValue();
                }
            }
            if ("group".equals(parameter.getId())) {
                if (parameterMap.containsKey(parameter.getValue())) {
                    groupName = parameterMap.get(parameter.getValue());
                } else {
                    groupName = parameter.getValue();
                }
            }
        }

        RobotRunDTO outRobotRunDTO = new RobotRunDTO();
        outRobotRunDTO.setVehicle(vehicle);
        outRobotRunDTO.setStation(keyRoute);
        outRobotRunDTO.setGroupName(groupName);
        outRobotRunDTO.setInstruction(model.getName());
        //把外部的任务添加到要运行的任务列表中
        list.add(outRobotRunDTO);
        for (RobotTaskDTO child : children) {
            String childVehicle = null;
            String childGroupName = null;
            String childKeyRoute = null;
            String childTargetSiteLabel = null;
            String waitDoId = null;
            String waitDoStatus = null;
            String waitDoTime = null;
            String forkLiftHeight = null;
            String waitDiId = null;
            String waitDiStatus = null;
            String waitDiTime = null;
            RobotTaskDTO.SubParameter[] childParameters = child.getParameters();
            for (RobotTaskDTO.SubParameter parameter : childParameters) {
                if ("vehicle".equals(parameter.getId())) {
                    childVehicle = parameter.getValue();
                }

                if ("group".equals(parameter.getId())) {
                    childGroupName = parameter.getValue();
                }

                if ("keyRoute".equals(parameter.getId())) {
                    childKeyRoute = parameter.getValue();
                }

                if ("targetSiteLabel".equals(parameter.getId())) {
                    if (parameterMap.containsKey(parameter.getValue())) {
                        childTargetSiteLabel = parameterMap.get(parameter.getValue());
                    } else {
                        childTargetSiteLabel = parameter.getValue();
                    }
                }

                if ("waitDoId".equals(parameter.getId())) {
                    waitDoId = parameter.getValue();
                }

                if ("waitDoStatus".equals(parameter.getId())) {
                    waitDoStatus = parameter.getValue();
                }

                if ("waitDoTime".equals(parameter.getId())) {
                    waitDoTime = parameter.getValue();
                }

                if ("forkLiftHeight".equals(parameter.getId())) {
                    forkLiftHeight = parameter.getValue();
                }

                if ("waitDiId".equals(parameter.getId())) {
                    waitDiId = parameter.getValue();
                }

                if ("waitDiStatus".equals(parameter.getId())) {
                    waitDiStatus = parameter.getValue();
                }

                if ("waitDiTime".equals(parameter.getId())) {
                    waitDiTime = parameter.getValue();
                }
            }
            RobotRunDTO innerRobotRunDTO = new RobotRunDTO();
            innerRobotRunDTO.setVehicle(vehicle);
            innerRobotRunDTO.setStation(childTargetSiteLabel);
            innerRobotRunDTO.setInstruction(child.getName());
            innerRobotRunDTO.setWaitDoId(waitDoId);
            innerRobotRunDTO.setWaitDoStatus(waitDoStatus);
            innerRobotRunDTO.setWaitDoTime(waitDoTime);
            innerRobotRunDTO.setForkLiftHeight(forkLiftHeight);
            innerRobotRunDTO.setWaitDiId(waitDiId);
            innerRobotRunDTO.setWaitDiStatus(waitDiStatus);
            innerRobotRunDTO.setWaitDiTime(waitDiTime);
            list.add(innerRobotRunDTO);
        }

        //把待命点站点名称添加到集合中
        List<RobotAttribute> robotBindAttr = robotAttributeService.getRobotBindAttr(vehicle, RobotMapPointAttrEnum.DMS_PARK_POINT.getCode());
        RobotRunDTO firstRunDTO = new RobotRunDTO();
        firstRunDTO.setVehicle(vehicle);
        firstRunDTO.setStation(robotBindAttr.get(0).getPoint());
        firstRunDTO.setInstruction("机器人路径导航");
        list.add(firstRunDTO);

        List<TaskDTO.ActionGroup> actionGroups = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String instruction = list.get(i).getInstruction();
            List<TaskDTO.ActionGroup.Action> actions = new ArrayList<>();
            //构造TaskDTO.ActionGroup.Action
            TaskDTO.ActionGroup.Action action = new TaskDTO.ActionGroup.Action();
            if ("机器人路径导航".equals(instruction)) {
                action.setActionName("move_action");
                action.setPluginName("MoveFactory");
                List<TaskDTO.ActionGroup.Action.Param> params = new ArrayList<>();
                TaskDTO.ActionGroup.Action.Param param1 = new TaskDTO.ActionGroup.Action.Param();
                param1.setKey("skill_name");
                param1.setStringValue("GotoSpecifiedPose");
                TaskDTO.ActionGroup.Action.Param param2 = new TaskDTO.ActionGroup.Action.Param();
                param2.setKey("target_name");
                param2.setStringValue(list.get(i).getStation());
                params.add(param1);
                params.add(param2);
                action.setParams(params);
                action.setSleepTime(0);
            }

            if ("等待DI".equals(instruction)) {
                action.setActionName("action_waitDI");
                action.setPluginName("TaskManager");
                List<TaskDTO.ActionGroup.Action.Param> params = new ArrayList<>();
                TaskDTO.ActionGroup.Action.Param param1 = new TaskDTO.ActionGroup.Action.Param();
                param1.setKey("index");
                param1.setInt32Value(Integer.parseInt(list.get(i).getWaitDiId()));
                TaskDTO.ActionGroup.Action.Param param2 = new TaskDTO.ActionGroup.Action.Param();
                param2.setKey("value");
                param2.setBoolValue(!"0".equals(list.get(i).getWaitDiStatus()));
                params.add(param1);
                params.add(param2);
                action.setParams(params);
                if (list.get(i).getWaitDiTime() == null || "".equals(list.get(i).getWaitDoTime())) {
                    action.setSleepTime(0);
                } else {
                    action.setSleepTime(Integer.parseInt(list.get(i).getWaitDiTime()) * 1000);
                }
            }

            if ("等待DO".equals(instruction)) {
                action.setActionName("action_setDO");
                action.setPluginName("DSPChassis");
                List<TaskDTO.ActionGroup.Action.Param> params = new ArrayList<>();
                TaskDTO.ActionGroup.Action.Param param1 = new TaskDTO.ActionGroup.Action.Param();
                param1.setKey("index");
                param1.setInt32Value(Integer.parseInt(list.get(i).getWaitDoId()));
                TaskDTO.ActionGroup.Action.Param param2 = new TaskDTO.ActionGroup.Action.Param();
                param2.setKey("value");
                param2.setBoolValue(!"0".equals(list.get(i).getWaitDoStatus()));
                params.add(param1);
                params.add(param2);
                action.setParams(params);
                if (list.get(i).getWaitDoTime() == null || "".equals(list.get(i).getWaitDoTime())) {
                    action.setSleepTime(0);
                } else {
                    action.setSleepTime(Integer.parseInt(list.get(i).getWaitDoTime()) * 1000);
                }
            }

            action.setIgnoreReturn(false);
            action.setOvertime(0);
            action.setExternalOverId(-1);
            action.setNeedResult(false);
            action.setActionId(0);
            actions.add(action);
            //构造TaskDTO.ActionGroup
            TaskDTO.ActionGroup actionGroup = new TaskDTO.ActionGroup();
            actionGroup.setActions(actions);
            actionGroup.setActionGroupName("group " + (i + 1));
            actionGroup.setActionGroupId(i);
            actionGroup.setChecked(true);
            actionGroups.add(actionGroup);
        }
        List<TaskDTO> tasks = new ArrayList<>();
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskId(0);
        taskDTO.setDesc("");
        taskDTO.setActionGroups(actionGroups);
        taskDTO.setChecked(true);
        tasks.add(taskDTO);
        //构造机器人任务链
        RobotTaskListDTO taskListDTO = new RobotTaskListDTO();
        //注意：这里的名字必须是字母、数字和下划线组成，其它则会导致错误（这里使用id任务链的名字）
        taskListDTO.setName(id);
        taskListDTO.setTasks(tasks);
        taskListDTO.setLoop(false);
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //连接获取机器人导航状态的端口
        List<RobotDms> robotDms = robotDmsService.findListByDmsPoint(null, robotBindAttr.get(0).getPoint());
        String dmsIp = robotDms.get(0).getDmsIp();
        if (!map.containsKey(dmsIp + dmsPort)) {
            robotBasicInfoService.connectTcp(dmsIp, dmsPort);
        }
        String data = DataConvertUtil.convertStringToHex(JSON.toJSONString(taskListDTO));
        String dataLength = Integer.toHexString(data.length() / 2);
        if (dataLength.length() == 4) {
            dataLength = dataLength;
        } else if (dataLength.length() == 3) {
            dataLength = "0" + dataLength;
        } else if (dataLength.length() == 2) {
            dataLength = "00" + dataLength;
        } else if (dataLength.length() == 1) {
            dataLength = "000" + dataLength;
        }
        String instruction = "5A0100000000" + dataLength + "0C1C0C1C00000000" + data;
        log.info("执行光通信任务的指令：{}", instruction);
        //获取机器人导航状态
        EntityResult result = TcpClientThread.sendHexMsg(dmsIp, dmsPort, instruction);
        log.info("光通信站IP：{},光通信站port：{},光通信任务指令执行结果：{}", dmsIp, dmsPort, result);
        //发送光通信指令完成后，延迟之后再执行定时任务
        if (result.isSuccess()) {
            //设置运单的状态为正在执行
            RobotWaybill robotWaybill1 = getById(robotWaybill.getId());
            //设置机器人的接单时间
            robotWaybill1.setOrderTime(new Timestamp(System.currentTimeMillis()));
            robotWaybill1.setWaybillStatus(RobotWaybillStatusEnum.EXECUTING.getCode());
            saveOrUpdate(robotWaybill1);

            String cron = "*/1 * * * * ?";
            customScheduledTaskRegistrar.addTriggerTask(robotWaybill.getId(),  //key
                    () -> {
                        try {
                            getTaskListInfo(robotWaybill, id, dmsIp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
        }
        return result.isSuccess();
    }

    /**
     * 获取任务参数的变量名和默认值的map集合
     *
     * @param taskParameterListStr
     * @return
     */
    @Override
    public Map<String, String> getParameterValueList(String taskParameterListStr) {
        //解析任务配置参数列表
        List<TaskParameterDTO> taskParameterList = JSONObject.parseArray(taskParameterListStr, TaskParameterDTO.class);
        Map<String, String> parameterMap = new HashMap<>();
        if (taskParameterListStr != null) {
            for (TaskParameterDTO taskParameterDTO : taskParameterList) {
                String variableName = taskParameterDTO.getVariableName();
                String defaultValue = taskParameterDTO.getDefaultValue();
                parameterMap.put(variableName, defaultValue);
            }
        }
        return parameterMap;
    }

    /**
     * 获取机器人任务链信息
     *
     * @param robotWaybill
     * @param id
     */
    @Override
    public void getTaskListInfo(RobotWaybill robotWaybill, String id, String dmsIp) throws Exception {
        //id为任务链的名字
        String str = "{\"task_list_name\":\"" + id + "\",\"with_robot_status\":true}";
        String data = DataConvertUtil.convertStringToHex(str);
        String dataLength = Integer.toHexString(data.length() / 2);
        String instruction = "5A010001000000" + dataLength + "0C1D000000000000" + data;
        //加一个延时,防止机器人未离开起始站点就立即建立通信
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, Thread> threadNetty = TcpClientThread.getIpThreadNetty();
        //连接获取机器人导航状态的端口
        if (!threadNetty.containsKey(dmsIp + dmsPort)) {
            robotBasicInfoService.connectTcp(dmsIp, dmsPort);
        }
        //获取机器人任务链信息
        EntityResult result = TcpClientThread.sendHexMsg(dmsIp, dmsPort, instruction);
        if (result.isSuccess()) {
            //加一个延时
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //获取机器人任务链信息
            //获取机器人任务链信息
            Map<String, String> taskListMapStatus = (Map<String, String>) redisUtil.stringWithGet("robot_tasklist_status_res");
            String taskListStatus = taskListMapStatus.get(robotWaybill.getRobotName());
            RobotTaskListResDTO model = JSON.parseObject(taskListStatus, RobotTaskListResDTO.class);
            if (model.getRetCode() == 0) {
                if (Objects.equals(model.getTasklistStatus().getTaskListStatus(), RobotTaskListStatusEnum.COMPLETED.getCode())) {
                    //所有任务都执行结束，则删除定时任务
                    RobotWaybill robotWaybill2 = getById(robotWaybill.getId());
                    //设置运单的状态为完成
                    robotWaybill2.setWaybillStatus(RobotWaybillStatusEnum.COMPLETE.getCode());
                    //设置运单完成时间
                    robotWaybill2.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                    //设置执行耗时
                    if ((robotWaybill2.getCompleteTime() != null && robotWaybill2.getOrderTime() != null)) {
                        robotWaybill2.setExecutionTime(robotWaybill2.getCompleteTime().getTime() - robotWaybill2.getOrderTime().getTime());
                    }
                    saveOrUpdate(robotWaybill2);
                    //如果所有任务都执行完成，则删除定时任务
                    customScheduledTaskRegistrar.removeTriggerTask(robotWaybill.getId());
                    //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                    completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                }

                if (Objects.equals(model.getTasklistStatus().getTaskListStatus(), RobotTaskListStatusEnum.FAILED.getCode())) {
                    //所有任务都执行结束，则删除定时任务
                    RobotWaybill robotWaybill2 = getById(robotWaybill.getId());
                    //设置运单的状态为终止
                    robotWaybill2.setWaybillStatus(RobotWaybillStatusEnum.TERMINATE.getCode());
                    //设置运单完成时间
                    robotWaybill2.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                    //设置执行耗时
                    if ((robotWaybill2.getCompleteTime() != null && robotWaybill2.getOrderTime() != null)) {
                        robotWaybill2.setExecutionTime(robotWaybill2.getCompleteTime().getTime() - robotWaybill2.getOrderTime().getTime());
                    }
                    saveOrUpdate(robotWaybill2);
                    //如果所有任务都执行完成，则删除定时任务
                    customScheduledTaskRegistrar.removeTriggerTask(robotWaybill.getId());
                    //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                    completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                }
            }
        }
    }

    /**
     * 获取当前机器人区域划分后的路径列表
     *
     * @param vehicleId
     * @return
     */
    @Override
    public List<RobotPathVO>
    getProcessedPaths(String vehicleId) {
        //根据名称获取当前机器人
        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(vehicleId);
        RobotBasicInfo robot = robots.get(0);
        /*
         * 获取当前机器人当前地图的站点信息
         */
        // 获取地图名称和站点名称的映射关系
        Map<String, List<String>> mapNameToPointsMap = JSON.parseObject(robot.getMapNameToPoints(),
                new TypeReference<Map<String, List<String>>>() {
                });
        // 获取当前机器人当前地图的站点信息
        List<String> points = mapNameToPointsMap.get(robot.getCurrentMap());

//        List<RobotMapAdvancePointDTO> robotMapAdvancePointDTOS = JSONObject.parseArray(robot.getPoints(), RobotMapAdvancePointDTO.class);
//        //把所有站点放入集合
//        List<String> points = new ArrayList<>();
//        for (RobotMapAdvancePointDTO robotMapAdvancePointDTO : robotMapAdvancePointDTOS) {
//            points.add(robotMapAdvancePointDTO.getInstanceName());
//        }
        List<RobotDmsRegionRelate> relateRegions = robotDmsRegionRelateService.getRelateRegions();
        List<RobotPathVO> pathVOS = new ArrayList<>();
        for (RobotDmsRegionRelate relateRegion : relateRegions) {
            RobotPathVO pathVO = new RobotPathVO();
            String currentRegionPoint = relateRegion.getCurrentRegionPoint();
            String bindRegionPoint = relateRegion.getBindRegionPoint();
            //当前区域和绑定区域的中心点都包含在当前地图中，则可以生成一条处理后的路径
            if (points.contains(currentRegionPoint) && points.contains(bindRegionPoint)) {
                pathVO.setGroupName(robot.getGroupName());
                pathVO.setStartPoint(currentRegionPoint);
                pathVO.setEndPoint(bindRegionPoint);
                pathVO.setCurveLength(1);
                pathVOS.add(pathVO);
            }
        }
        return pathVOS;
    }

    /**
     * 获取机器人是否在线
     *
     * @param robotBasicInfo
     * @return
     */
    @Override
    public boolean isOnline(RobotBasicInfo robotBasicInfo) {
        return !map.isEmpty();
    }

    /**
     * 获取机器人状态信息列表
     *
     * @param robotBasicInfo 机器人实体
     * @return
     * @throws Exception
     */
    @Override
    public RobotInfoVO getRobotStatus(RobotBasicInfo robotBasicInfo) throws InterruptedException {
        RobotInfoVO vo = new RobotInfoVO();
        //机器人的推送频率是2500，为了保证获取到最新的机器人推送信息，此值要大于机器人的推送频率
        Thread.sleep(4000);
        RobotMonitorVO robotMonitorVO = map.get(robotBasicInfo.getVehicleId());
        if (robotMonitorVO == null) {
            return null;
        }
        //如果robotMonitorVO为空，则机器人不在线
        vo.setOnline(true);
        vo.setId(robotBasicInfo.getId());
        vo.setVehicleId(robotBasicInfo.getVehicleId());
        vo.setCurrentIp(robotBasicInfo.getCurrentIp());
        vo.setGroupName(robotBasicInfo.getGroupName());
        vo.setBatteryLevel(robotMonitorVO.getBatteryLevel() + "%");
        vo.setConfidence(DataUtil.format(robotMonitorVO.getConfidence()));
        vo.setCurrentMap(robotBasicInfo.getCurrentMap());
        vo.setRobotType(robotBasicInfo.getRobotType());
        vo.setCharging(robotMonitorVO.isCharging());
        vo.setControlState(Objects.requireNonNull(ControlStatusEnum.getByName(robotMonitorVO.getControlStatus())).getCode());
        vo.setLocationState(Objects.requireNonNull(RobotRelocStatusEnum.getByName(robotMonitorVO.getRelocStatus())).getCode());
        vo.setNavigationState(Objects.requireNonNull(NavigationStatusEnum.getByName(robotMonitorVO.getTaskStatus())).getCode());
        if (ControlStatusEnum.SEIZED_CONTROL.getName().equals(robotMonitorVO.getControlStatus()) && robotMonitorVO.getBatteryLevel() > robotBasicInfo.getBatteryLevel()) {
            if (Objects.equals(robotBasicInfo.getOrderState(), RobotStatusEnum.ORDER_AVAILABLE.getCode())) {
                Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
                //如果已经连接机器人，则获取机器人服务端返回的实时信息
                if (map.containsKey(robotBasicInfo.getCurrentIp() + PortConstant.ROBOT_PUSH_PORT)) {
                    //机器人在电量下降到此值时，需要前往充电桩进行充电，并且在充电过程不可以接单。
                    if (robotMonitorVO.getBatteryLevel() <= robotBasicInfo.getChargeOnly()) {
                        vo.setOrderState(Constant.NOT_ORDER_AVAILABLE_NUMBER);
                    }
                    //chargeOnly值为-1，表示机器人在电量耗尽后才会前往充电，并且过程中不可以接单。
                    if (robotBasicInfo.getChargeOnly() == -1 && robotMonitorVO.getBatteryLevel() < robotBasicInfo.getChargeNeed()) {
                        vo.setOrderState(Constant.NOT_ORDER_AVAILABLE_NUMBER);
                    }
                    //机器人电量大于chargerOnly，但是小于chargeNeed,此时可以接单
                    if (robotBasicInfo.getChargeOnly() < robotMonitorVO.getBatteryLevel() && robotMonitorVO.getBatteryLevel() <= robotBasicInfo.getChargeNeed()) {
                        vo.setOrderState(Constant.ORDER_AVAILABLE_NUMBER);
                    }
                    //chargeNeed值为-1，表示机器人在电量耗尽后才会前往充电，并且过程中可以接单
                    if (robotBasicInfo.getChargeNeed() == -1) {
                        vo.setOrderState(Constant.ORDER_AVAILABLE_NUMBER);
                    }
                    //chargedOk机器人在充电时接到订单，只有电量充到此值时，才可以离开充电桩去接单。
                    if (robotMonitorVO.getBatteryLevel() >= robotBasicInfo.getChargeOk()) {
                        vo.setOrderState(Constant.ORDER_AVAILABLE_NUMBER);
                    }
                    vo.setOrderState(Constant.ORDER_AVAILABLE_NUMBER);
                } else {
                    //机器人不在线，设置机器人不可以接单
                    vo.setOrderState(Constant.NOT_ORDER_AVAILABLE_NUMBER);
                }
            } else {
                vo.setOrderState(Constant.NOT_ORDER_AVAILABLE_NUMBER);
            }
        } else if (ControlStatusEnum.UN_PREEMPTED_CONTROL.getName().equals(robotMonitorVO.getControlStatus()) && robotMonitorVO.getBatteryLevel() > robotBasicInfo.getBatteryLevel()) {
            //无人抢占控制权，DMS可以接单
            vo.setOrderState(Constant.ORDER_AVAILABLE_NUMBER);
        } else {
            //控制权被抢占，不可以接单
            vo.setOrderState(Constant.NOT_ORDER_AVAILABLE_NUMBER);
        }
        return vo;
    }


    /**
     * ******************************** <指定路径导航开始> leo add ********************************
     * -------------------------------- 全局变量 start --------------------------------
     */

    /*
     * ------ 连续路径导航部分 ------
     */
    // 多机器人主任务线程池，处理机器人的初始化及启动等
    private final ExecutorService robotThreadPool = Executors.newCachedThreadPool();
    // 线程安全的多机器人主任务管理器，以<机器人名称，Future对象>的形式存储各个机器人任务的Future对象
    private final Map<String, Future<?>> robotFutureMap = new ConcurrentHashMap<>();
    // 阻塞任务队列
    private final BlockingQueue<OneStepNavTaskDTO> blockingTaskQueue = new LinkedBlockingQueue<>();

    // 单一全局读写锁（可重入锁）
    private final ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock(true);

    // 多机器人步进任务管理器，用来存储各个机器人的步进任务队列 (后期升级服务器集群后可考虑持久化)
    public final Map<String, Deque<OneStepNavTaskDTO>> navTaskQueueMap = new ConcurrentHashMap<>();
    // 多机器人任务状态管理器，以<机器人名称，机器人任务状态DTO>的形式存储各个机器人的任务状态
    public final Map<String, RobotTaskStatusDTO> taskStatusMap = new ConcurrentHashMap<>();
    // 多机器人控制器镜像队列管理器，以<机器人名称，机器人正在运行中的任务队列>的形式映射对应机器人控制器中的任务队列(后期升级服务器集群后可考虑持久化)
    public final Map<String, List<OneStepNavTaskDTO>> runningTaskMap = new ConcurrentHashMap<>();

    // 线程安全的站点锁管理器，存储 <站点,锁定该站点的机器人ID> (后期升级服务器集群后可考虑持久化)
    private final Map<String, String> lockedStations = new ConcurrentHashMap<>();
    // 使用原子引用保存上一次的锁定站点集合快照
    private final AtomicReference<Set<String>> prevLockedPointsRef = new AtomicReference<>(Collections.emptySet());

    // 多机器人轮询线程池管理器，以<机器人名称，机器人轮询线程池>的形式存储各个机器人的轮询线程池
    private final Map<String, ScheduledExecutorService> robotSchedulerMap = new ConcurrentHashMap<>();

    // 自增计数器，用于构建 taskId。taskId 由UUID和自增计数器共同生成，可同时保证唯一性和易读性
    private final AtomicInteger taskIdCounter = new AtomicInteger(1);

    // 配置参数
    private static final double STATION_UNLOCK_THRESHOLD = 0.3; // 解锁阈值
    private static final int ROBOT_TASK_POLL_INTERVAL = 1000; // 轮询间隔（毫秒）

    /*
     * ------ 自动门部分 ------
     */
    // 多机器人用于自动门交互的轮询线程池管理器，以<机器人名称，自动门轮询线程池>的形式存储各个机器人针对自动门的线程池
    private final Map<String, ScheduledExecutorService> robotDoorThreadPollMap = new ConcurrentHashMap<>();
    // 机器人控制的多个自动门开门任务映射：外层robotId->（内层doorId->ScheduledFuture）
    private final Map<String, Map<String, ScheduledFuture<?>>> robotDoorTaskMap = new ConcurrentHashMap<>();

    // 多机器人用于自动门关门及校验的线程池管理器，以<机器人名称，自动门关门及校验线程池>的形式存储各个机器人的关门校验线程池
    private final Map<String, ScheduledExecutorService> robotDoorCloseCheckThreadMap = new ConcurrentHashMap<>();
    // 自动门关门任务映射
    private final Map<String, ScheduledFuture<?>> robotDoorCloseTaskMap = new ConcurrentHashMap<>();

    // 多机器人用于管控自动门集合的管理器，以<机器人名称，正在被管控的自动门集合>的形式存储各机器人正在管控的自动门集合
    private final Map<String, Set<IotEquipment>> robotDoorControlledMap = new ConcurrentHashMap<>();

    /*
     * ------ 自动门/电梯共用部分 ------
     *
     * ·多机器人门闩管理器，以<机器人名称，门闩计数器>的形式存储各个机器人的门闩
     */
    private final Map<String, CountDownLatch> robotLatchByDoorMap = new ConcurrentHashMap<>();

    /*
     * ------ 电梯部分 ------
     */

    // 配置电梯通信协议类型
    @Value("${equipment.elevator.communicationType}")
    private String elevatorCommunicationType;
    // 多机器人用于控制门方位的管理器，以<机器人名称，是否前门>的形式存储各个机器人的门方位控制布尔值（true-前门，false-后门）
    private final Map<String, Boolean> robotFrontOrRearDoorMap = new ConcurrentHashMap<>();

    // 多机器人用于管控电梯对象的管理器，以<机器人名称, 电梯对象>的形式存储各机器人正在管控的电梯对象
    private final Map<String, IotEquipment> robotElevatorControlledMap = new ConcurrentHashMap<>();

    // 多机器人状态机，以<机器人名称，状态机>的形式存储各个机器人的状态阶段
    private final Map<String, Integer> robotForElevatorFSMMap = new ConcurrentHashMap<>();
    // 多机器人跨楼层调度信息管理器，以<机器人名称，跨楼层调度信息>的形式存储各个机器人的跨楼层调度信息
    private final Map<String, CrossFloorInfoDTO> robotCrossFloorInfoMap = new ConcurrentHashMap<>();

    // 多机器人用于电梯交互的轮询线程池管理器，以<机器人名称，电梯轮询线程池>的形式存储各个机器人针对电梯的线程池
    private final Map<String, ScheduledExecutorService> robotElevatorThreadPollMap = new ConcurrentHashMap<>();
    // 电梯守护任务映射
    private final Map<String, ScheduledFuture<?>> robotElevatorWatchTaskMap = new ConcurrentHashMap<>();

    // 电梯运行状态管理器，以<电梯名称，电梯运行状态DTO>的形式存储各个电梯的运行状态
    public final Map<String, ElevatorStatusDTO> elevatorStatusMap = new ConcurrentHashMap<>();

    /* -------------------------------- 全局变量 end -------------------------------- */

    /**
     * <让机器人执行subTaskList任务列表-连续导航: 指定路径导航主入口>
     * <p>
     *
     * @param robotId     机器人ID
     * @param subTaskList 任务编辑模块下发的子任务列表
     * @param waybillId   运单ID
     */
    @Override
    public boolean startContinuousNavigation(String waybillId, String robotId, List<RobotRunDTO> subTaskList) throws Exception {
        // 子任务列表须至少包含2个子任务
        if (subTaskList.size() < 2) {
            throw new Exception("执行机器人任务失败，请检查任务是否完整!");
        }
        // 若起点和终点名称相同，则跳过后续逻辑，直接进入下发任务列表中的下一步任务
        if (subTaskList.get(0).getStation().equals(subTaskList.get(1).getStation())) {
            return true;
        }
        // 终止当前机器人已有且未关闭的主线程（如果存在）
        stopRobotThread(robotId);
        /*
         * *** 终止当前机器人（robotId）已有的未关闭轮询线程（如果存在） ***
         * · 避免在收到下一个针对同一robotId的连续导航任务时（main2线程），因上一个连续导航任务启动的轮询线程还未轮询到自关闭条件，
         * · main2线程已经执行完成且开启轮询线程（因检测到 robotId 的轮询线程已存在，则不开启新的轮询线程）
         * · 原轮询线程此时轮询到了自关闭条件，关闭轮询线程。而此时第二个连续导航任务并未执行完成
         * · 本质上其实是被第一个连续导航任务的自关闭条件关闭了
         *
         * ---传入外部下发任务的起点作为安全停止点---
         */
        stopRobotPollingAndFlushQueues(robotId, subTaskList.get(0).getStation());

        // 提交新机器人任务并记录Future
        Future<?> future = robotThreadPool.submit(() -> {
            // 动态设置线程名称
            Thread.currentThread().setName(robotId + "-ContinuousNavigation");
            log.info("已为机器人创建线程[{}]，开始执行连续导航任务", Thread.currentThread().getName());

            try {
                /**
                 * <业务逻辑>
                 */

                // 根据机器人名称及子任务列表，初始化自身任务队列、跨楼层调度信息，并更新其它运行中的机器人任务队列、跨楼层调度信息
                initializeOrUpdateTaskQueueMap(waybillId, robotId, subTaskList);

                // 获取该机器人任务队列
                Deque<OneStepNavTaskDTO> robotTaskQueue = navTaskQueueMap.get(robotId);

                // 获取待锁定路径的前3个站点（初始任务起点+终点+下一终点，若不足3个则全部锁定）
                List<String> pointsToLock = null;
                try {
                    pointsToLock = getFirstThreeDistinctPoints(robotTaskQueue);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // 尝试对欲锁定站点覆盖的区域进行管控并批量锁定站点直到成功（尝占锁）
                boolean isLockSuccess = false;
                do {
                    isLockSuccess = lockAreasAndPoints(robotId, pointsToLock, waybillId);
                    if (!isLockSuccess) {
                        try {
                            // 若区域或站点被占用，管控或锁定失败，则随机等待5-10秒后重试，通过随机扰动打破多个机器人的同步竞争状态
                            int backoff = new Random().nextInt(5000) + 5000;
                            TimeUnit.MILLISECONDS.sleep(backoff);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("等待重新锁定失败", e);
                        }
                    }
                } while (!isLockSuccess);

                // 初始化运行任务列表（控制器镜像队列）
                runningTaskMap.put(robotId, new ArrayList<>(2));

                // 若初始任务队列 ≥ 2个步进长度
                if (robotTaskQueue.size() >= 2) {
                    // 尝试下发初始的2个步进任务，直到下发成功
                    List<OneStepNavTaskDTO> initialTasks;
                    do {
                        initialTasks = dispatchFirstAndSecondTask(robotId, robotTaskQueue, waybillId);
                        if (initialTasks.size() != 2) {
                            try {
                                log.warn("机器人{}前两个步进任务未下发成功，5秒后重试...", robotId);
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException("任务获取中断", e);
                            }
                        } else {
                            break;
                        }
                    } while (!Thread.currentThread().isInterrupted());
                    // 更新控制器镜像队列
                    runningTaskMap.get(robotId).addAll(initialTasks);

                } else {
                    // 若初始任务队列 = 1个步进长度,则下发初始的1个步进任务
                    handleTaskDispatchingByOccupation(robotId, runningTaskMap.get(robotId), robotTaskQueue, waybillId);
                }

                /**
                 * <启动该机器人的轮询线程>
                 * *** 确保每个 robotId 关联一个唯一的单线程池，避免重复创建 ***
                 * ① 若 robotId 在 Map 中无关联线程池，则通过 Executors 创建一个新的单线程线程池，并存入 Map。
                 * ② 若已有线程池，则直接返回现有实例
                 */
                robotSchedulerMap.computeIfAbsent(robotId,
                        key -> Executors.newSingleThreadScheduledExecutor(new DynamicThreadFactory(robotId, "robotRunning"))
                ).scheduleWithFixedDelay(() -> {
                            try {
                                // 传入外部下发任务的终点作为安全停止点
                                pollRobotOperate(robotId, subTaskList.get(1).getStation(), waybillId);
                            } catch (Exception e) {
                                log.error("轮询机器人{}状态失败:{}", robotId, e);
//                                stopRobotPollingAndFlushQueuesByException(robotId);
                                // 添加运单失败方法
                                RobotWaybill robotWaybill = getById(waybillId);
                                try {
                                    setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), e.getMessage());
                                } catch (Exception ee) {
                                    log.error("更新运单失败，原因：{}", ee.getMessage());
                                }


                            }
                        },
                        1000, ROBOT_TASK_POLL_INTERVAL, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("执行机器人任务失败，原因：{}", e.getMessage());
                // 停止该机器人的轮询线程
                // todo: 异常时设置运单状态为异常并记录原因，待人工解决问题后，人为通过接口解锁异常机器人锁定的站点并关闭相关线程。通过前端页面解锁异常机器人站点。
//                stopRobotPollingAndFlushQueuesByException(robotId);
                // 添加运单失败原因
                RobotWaybill robotWaybill = getById(waybillId);
                try {
                    setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), e.getMessage());
                } catch (Exception ee) {
                    log.error("更新运单失败，原因：{}", ee.getMessage());
                }
            } finally {
                log.info("连续导航任务主线程[{}]任务执行完毕", Thread.currentThread().getName());
            }
        });

        robotFutureMap.put(robotId, future);
        return true;
    }

    // 获取待锁定路径的最多前3个站点（初始任务起点+终点+下一终点）
    private List<String> getFirstThreeDistinctPoints(Deque<OneStepNavTaskDTO> robotTaskQueue) {
        return robotTaskQueue.stream()
                .limit(2)
                .flatMap(task -> Stream.of(task.getStartPoint(), task.getEndPoint()))
                .distinct()
                .limit(3)
                .collect(Collectors.toList());
    }

    /**
     * <根据机器人名称及子任务列表--初始化自身并更新其它运行中的机器人任务队列Map> leo add
     *
     * @param waybillId   运单ID
     * @param robotId     机器人ID
     * @param subTaskList 任务编辑模块下发的运行任务列表
     */
    public void initializeOrUpdateTaskQueueMap(String waybillId, String robotId, List<RobotRunDTO> subTaskList) {
        // 获取写锁（独占操作）
        globalLock.writeLock().lock();
        log.info("************ 获取写锁1成功，开始初始化或更新任务队列Map ************");
        try {
            // 获取自身及其它运行中机器人的<机器人名称，路径>列表，即路径规划结果
            List<AlgorithmResResultDTO> algorithmResultForAllRobots = getCurrentAndOtherRobotStepPathList(robotId, subTaskList, waybillId);
            if (algorithmResultForAllRobots.isEmpty()) {
                log.error("算法返回结果为空");
            } else {
                // 遍历全部运行机器人的<机器人名称，步进路径列表>列表，更新任务队列Map
                algorithmResultForAllRobots.forEach(algorithmResResultDTO -> {
                    // 验证该机器人规划出的路径连续性
                    validatePathContinuity(algorithmResResultDTO.getRobotName(), algorithmResResultDTO.getPathPlanning());
                    // 初始化或更新指定机器人的任务队列Map
                    initializeOrUpdateTaskQueueByRobotId(algorithmResResultDTO.getRobotName(), algorithmResResultDTO.getPathPlanning(), waybillId);
                });
            }
        } finally {
            // 释放写锁
            globalLock.writeLock().unlock();
            log.info("************ 释放写锁1成功 ************");
        }
    }

    /**
     * <更新机器人步进任务队列Map> leo add
     *
     * @param waybillId 运单ID
     */
    public void updateTaskQueueMap(String waybillId) {
        // 获取写锁（独占操作）
        globalLock.writeLock().lock();
        log.info("************ 获取写锁2成功，开始更新任务队列Map ************");

        try {
            // 获取全部运行机器人的<机器人名称，路径>列表，即路径规划结果
            List<AlgorithmResResultDTO> algorithmResultForRunningRobot = getAllRobotStepPathList(waybillId);
            if (algorithmResultForRunningRobot.isEmpty()) {
                log.error("机器人路径列表为空");
            } else {
                // 遍历全部运行机器人的<机器人名称，路径>列表，更新任务队列Map
                for (AlgorithmResResultDTO algorithmResResultDTO : algorithmResultForRunningRobot) {
                    // 验证路径连续性
                    validatePathContinuity(algorithmResResultDTO.getRobotName(), algorithmResResultDTO.getPathPlanning());
                    // 初始化或更新指定机器人的任务队列Map
                    initializeOrUpdateTaskQueueByRobotId(algorithmResResultDTO.getRobotName(), algorithmResResultDTO.getPathPlanning(), waybillId);
                }
            }
        } finally {
            // 释放写锁
            globalLock.writeLock().unlock();
            log.info("************ 释放写锁2成功 ************");
        }
    }

    /**
     * <通过请求路径规划算法返回当前机器人及其它运行中机器人的路径列表> leo add
     * <p>
     * 1.构造算法所需参数
     * 2.请求路径规划算法
     * 3.路径规划算法生成路径列表并转换为Json格式存入数据库
     * 4.查询并获取数据表中Json格式的路径规划算法结果
     * 5.解析Json格式的路径规划算法结果，构造 [起点，终点] 路径列表
     *
     * @param robotId     机器人ID
     * @param subTaskList 任务编辑模块下发的运行任务列表
     * @param waybillId   运单ID
     * @return 当前及其它运行机器人路径列表
     */
    public List<AlgorithmResResultDTO> getCurrentAndOtherRobotStepPathList(String robotId, List<RobotRunDTO> subTaskList, String waybillId) {
        // 定义场景中全部运行中机器人的<机器人名称，路径>列表，即路径规划结果
        List<AlgorithmResResultDTO> algorithmResForRunningRobots = new ArrayList<>();

        /*
         * 1.定义多机器人<起点，终点>路径列表
         */
        List<RobotPathPlanDTO.RobotPath> robotStartToEndList = new ArrayList<>();

        /*
         * 1.1 构造当前机器人<起点，终点>路径
         */
        // 获取当前机器人子任务的起点
        String beginPort = subTaskList.get(0).getStation();
        // 获取当前机器人子任务的终点
        String endPort = subTaskList.get(1).getStation();
        // 封装算法所需当前机器人名称、分组、起点和终点
        RobotPathPlanDTO.RobotPath currentRobotStartToEnd = robotTaskService.encapsulateData(robotId,
                robotBasicInfoService.findByName(robotId).get(0).getGroupName(), beginPort, endPort);
        // 添加当前机器人<起点，终点>到多机器人<起点，终点>列表
        robotStartToEndList.add(currentRobotStartToEnd);

        /*
         * 1.2 构造其它运行中机器人在有新机器人任务加入时的新<起点，终点>
         */
        if (!navTaskQueueMap.isEmpty()) {

            navTaskQueueMap.forEach((robotName, taskQueue) -> {
                if (!robotName.equals(robotId) && taskQueue != null && !taskQueue.isEmpty()) {
                    // 获取机器人剩余路径的最新起点
                    OneStepNavTaskDTO firstStep = taskQueue.peekFirst();
                    String otherRuntimeRobotBeginPort = (firstStep != null) ? firstStep.getStartPoint() : null;

                    // 获取机器人剩余路径的终点
                    OneStepNavTaskDTO lastStep = taskQueue.peekLast();
                    String otherRuntimeRobotEndPort = (lastStep != null) ? lastStep.getEndPoint() : null;

                    if (otherRuntimeRobotBeginPort != null && otherRuntimeRobotEndPort != null) {
                        // 封装算法所需的机器人名称、分组、起点和终点
                        RobotPathPlanDTO.RobotPath otherRuntimeRobotPath = robotTaskService.encapsulateData(robotName, robotBasicInfoService.findByName(robotName).get(0).getGroupName(), otherRuntimeRobotBeginPort, otherRuntimeRobotEndPort);
                        // 合并当前任务机器人<起点，终点>和其它运行中机器人的<起点，终点>
                        robotStartToEndList.add(otherRuntimeRobotPath);
                    }
                }
            });
        }

        // 2.获取场景中全部运行中机器人的地图融合路径列表（运行分组的融合地图）
        List<RobotPathVO> runningRobotMapPathList = getAllMapPathListForRunningRobot(robotId);

        // 3.若场景中存在电梯，则将电梯虚拟路径合并到运行中机器人地图路径列表中
        List<IotEquipment> elevatorList = ioTEquipmentService.listByEquipmentType(IotEquipment.EquipmentType.ELEVATOR);
        try {
            if (!elevatorList.isEmpty()) {
                for (IotEquipment elevator : elevatorList) {
                    // 获取电梯虚拟路径列表Json
                    String virtualPathsJson = elevator.getVirtualPaths();
                    if (virtualPathsJson != null && !virtualPathsJson.isEmpty()) {
                        // 解析虚拟路径列表
                        List<RobotPathVO> virtualPathList = JSONObject.parseArray(virtualPathsJson, RobotPathVO.class);
                        // 将虚拟路径加入运行中机器人地图路径列表中
                        runningRobotMapPathList.addAll(virtualPathList);
                    }
                }
            }
        } catch (Exception e) {
            log.error("机器人{}解析电梯虚拟路径列表失败，原因: {}", robotId, e.getMessage());
        }

        // 4.获取场景中全部运行中机器人的地图融合站点集合
        Set<String> runningRobotMapPointSet = getAllMapPointSetForRunningRobot(robotId);

        /*
         * *** 请求路径规划算法 ***
         * 参数分别为：
         *  1.运行中机器人地图的融合路径列表
         *  2.场景中全部运行中机器人的地图融合站点集合
         *  3.多机器人<起点，终点>路径列表
         *  （管控区域信息在方法内部获取）
         */
        AlgorithmRequestResDTO algorithmRequestResDTO = robotTaskService.executePathPlanningTask(runningRobotMapPathList, runningRobotMapPointSet, robotStartToEndList);
        log.info("开始请求路径规划算法,请求的任务：{}", JSON.toJSONString(robotStartToEndList));

        if ("success".equals(algorithmRequestResDTO.getStatus())) {
            // 定义路径规划算法返回的规划结果列表
            List<RobotPathPlanning> pathPlanningResultList = new ArrayList<>();
            // 添加最大重试次数
            int maxRetries = 60;
            int retryCount = 0;
            while (pathPlanningResultList.isEmpty() && retryCount < maxRetries) {
                // 尝试获取路径规划算法返回的结果，若为空，则等待2秒后重试
                pathPlanningResultList = robotPathPlanningService.getListByTaskId(algorithmRequestResDTO.getTaskId());
                if (pathPlanningResultList.isEmpty()) {
                    log.info("路径规划算法尚未返回规划结果，2秒后重新获取...");
                    try {
                        TimeUnit.MILLISECONDS.sleep(2000);
                    } catch (InterruptedException e) {
                        log.error("线程休眠时发生中断", e);
                        // 重置中断状态
                        Thread.currentThread().interrupt();
                        break;
                    }
                    retryCount++;
                }
            }
            // 若最终路径规划算法返回结果为空，则直接返回空集合
            if (pathPlanningResultList.isEmpty()) {
                log.error("路径规划算法在 {} 次重试后仍未返回结果，请检查算法是否正常", maxRetries);
                // 添加运单失败原因
                RobotWaybill robotWaybill = getById(waybillId);
                try {
                    setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), "执行路径规划算法失败");
                } catch (Exception e) {
                    log.error("更新运单失败，原因：{}", e.getMessage());
                }
            } else {
                log.info("路径规划成功，规划任务ID：{}", algorithmRequestResDTO.getTaskId());
                // 取出路径规划算法返回的第一个结果（有且仅有一个结果）
                RobotPathPlanning pathPlanning = pathPlanningResultList.get(0);
                // 结果使用完成以后，设置算法执行的结果为无效
                pathPlanning.setState(Constant.STATE_INVALID);
                robotPathPlanningService.saveOrUpdate(pathPlanning);
                // 获取算法返回的机器人路径结果
                if ("[]".equals(pathPlanning.getResult())) {
                    // 执行路径规划算法失败
                    log.error("路径规划算法执行失败，任务ID：{}", pathPlanning.getTaskId());
                    // 添加运单失败原因
                    RobotWaybill robotWaybill = getById(waybillId);
                    try {
                        setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), "执行路径规划算法失败");
                    } catch (Exception e) {
                        log.error("更新运单失败，原因：{}", e.getMessage());
                    }
                } else {
                    // 解析Json格式的路径规划算法结果，构造 <机器人名称，步进路径列表> 列表
                    algorithmResForRunningRobots = JSON.parseArray(pathPlanning.getResult(), AlgorithmResResultDTO.class);
                }
            }
        } else {
            // 执行路径规划算法失败
            log.error("路径规划算法执行失败，返回信息：{}", algorithmRequestResDTO);
        }

        return algorithmResForRunningRobots;
    }

    /**
     * <通过请求路径规划算法返回场景中全部运行中机器人的路径列表> leo add
     * <p>
     * 1.构造算法所需参数
     * 2.请求路径规划算法
     * 3.路径规划算法生成路径列表并转换为Json格式存入数据库
     * 4.查询并获取数据表中Json格式的路径规划算法结果
     * 5.解析Json格式的路径规划算法结果，构造 [起点，终点] 路径列表
     *
     * @param waybillId 运单ID
     * @return 场景中全部运行中机器人路径列表
     */
    public List<AlgorithmResResultDTO> getAllRobotStepPathList(String waybillId) {
        // 定义场景中全部运行中机器人的<机器人名称，路径>列表，即路径规划结果
        List<AlgorithmResResultDTO> algorithmResForRunningRobots = new ArrayList<>();

        /*
         * 1.定义多机器人<起点，终点>路径列表
         */
        List<RobotPathPlanDTO.RobotPath> robotStartToEndList = new ArrayList<>();

        /*
         * 1.1 构造运行中机器人在重新请求算法时的新<起点，终点>路径
         */
        navTaskQueueMap.forEach((robotName, taskQueue) -> {
            if (taskQueue != null && !taskQueue.isEmpty()) {
                // 获取该机器人子任务的最新起点
                OneStepNavTaskDTO firstStep = taskQueue.peekFirst();
                String runtimeRobotBeginPort = (firstStep != null) ? firstStep.getStartPoint() : null;

                // 获取该机器人子任务的终点
                OneStepNavTaskDTO lastStep = taskQueue.peekLast();
                String runtimeRobotEndPort = (lastStep != null) ? lastStep.getEndPoint() : null;

                // 封装算法所需该机器人名称、起点和终点
                if (runtimeRobotBeginPort != null && runtimeRobotEndPort != null) {
                    RobotPathPlanDTO.RobotPath runtimeRobotPath = robotTaskService.encapsulateData(robotName, robotBasicInfoService.findByName(robotName).get(0).getGroupName(), runtimeRobotBeginPort, runtimeRobotEndPort);
                    // 合并当前任务机器人路径列表和其它运行中机器人的路径列表
                    robotStartToEndList.add(runtimeRobotPath);
                }
            }
        });

        // 2.获取场景中全部运行中机器人的地图融合路径列表（此方法在路径冲突时被调用，故必然存在运行中的机器人，融合路径列表不会为空）
        List<RobotPathVO> runningRobotMapPathList = getAllMapPathListForRunningRobot(null);
        // 3.若场景中存在电梯，则将电梯虚拟路径合并到运行中机器人地图路径列表中
        List<IotEquipment> elevatorList = ioTEquipmentService.listByEquipmentType(IotEquipment.EquipmentType.ELEVATOR);
        if (!elevatorList.isEmpty()) {
            for (IotEquipment elevator : elevatorList) {
                // 获取电梯虚拟路径列表Json
                String virtualPathsJson = elevator.getVirtualPaths();
                if (virtualPathsJson != null && !virtualPathsJson.isEmpty()) {
                    // 解析虚拟路径列表
                    List<RobotPathVO> virtualPathList = JSONObject.parseArray(virtualPathsJson, RobotPathVO.class);
                    // 将虚拟路径加入运行中机器人地图路径列表中
                    runningRobotMapPathList.addAll(virtualPathList);
                }
            }
        }
        // 4.获取场景中全部运行中机器人的地图融合站点集合
        Set<String> runningRobotMapPointSet = getAllMapPointSetForRunningRobot(null);

        /*
         * *** 请求路径规划算法 ***
         * 参数分别为：
         *  1.运行中机器人地图的融合路径列表
         *  2.多机器人<起点，终点>路径列表
         *  -.管控区域信息在方法内部获取
         */
        AlgorithmRequestResDTO algorithmRequestResDTO = robotTaskService.executePathPlanningTask(runningRobotMapPathList, runningRobotMapPointSet, robotStartToEndList);
        log.info("开始请求路径规划算法,请求的任务：{}", JSON.toJSONString(robotStartToEndList));

        if ("success".equals(algorithmRequestResDTO.getStatus())) {
            // 定义路径规划算法返回的规划结果列表
            List<RobotPathPlanning> pathPlanningResultList = new ArrayList<>();
            // 添加最大重试次数
            int maxRetries = 60;
            int retryCount = 0;
            while (pathPlanningResultList.isEmpty() && retryCount < maxRetries) {
                pathPlanningResultList = robotPathPlanningService.getListByTaskId(algorithmRequestResDTO.getTaskId());
                if (pathPlanningResultList.isEmpty()) {
                    log.info("路径规划算法尚未返回规划结果，2秒后重新获取...");
                    try {
                        TimeUnit.MILLISECONDS.sleep(2000);
                    } catch (InterruptedException e) {
                        log.error("线程休眠时发生中断", e);
                        // 重置中断状态
                        Thread.currentThread().interrupt();
                        break;
                    }
                    retryCount++;
                }
            }

            if (pathPlanningResultList.isEmpty()) {
                log.error("路径规划算法在 {} 次重试后仍未返回结果", maxRetries);
                // 添加运单失败原因
                RobotWaybill robotWaybill = getById(waybillId);
                try {
                    setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), "执行路径规划算法失败");
                } catch (Exception e) {
                    log.error("更新运单失败，原因：{}", e.getMessage());
                }
            } else {
                log.info("路径规划成功，规划任务ID：{}", algorithmRequestResDTO.getTaskId());
                RobotPathPlanning pathPlanning = pathPlanningResultList.get(0);
                //使用完成以后，设置算法执行的结果为无效
                pathPlanning.setState(Constant.STATE_INVALID);
                robotPathPlanningService.saveOrUpdate(pathPlanning);
                //获取算法执行返回的机器人路径结果
                if ("[]".equals(pathPlanning.getResult())) {
                    //执行路径规划算法失败
                    log.error("路径规划算法执行失败，任务ID：{}", pathPlanning.getTaskId());
//                    RobotWaybill robotWaybill = getById(waybillId);
//                    setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), "执行路径规划算法失败");
                } else {
                    // 解析Json格式的路径规划算法结果，构造 <机器人名称，步进路径列表> 列表
                    algorithmResForRunningRobots = JSON.parseArray(pathPlanning.getResult(), AlgorithmResResultDTO.class);
                }
            }
        }

        return algorithmResForRunningRobots;
    }

    /**
     * <获取场景中全部运行中机器人的地图融合路径列表> leo add
     */
    private List<RobotPathVO> getAllMapPathListForRunningRobot(String robotId) {
        // 获取场景中全部运行中的机器人名称
        Set<String> runningRobotIdSet = navTaskQueueMap.keySet();
        // 定义运行中机器人地图融合路径列表
        List<RobotPathVO> runningRobotMapPathList = new ArrayList<>();
        // 若机器人名称集合不为空，则构造运行中机器人地图路径列表
        if (!runningRobotIdSet.isEmpty()) {
            for (String robotName : runningRobotIdSet) {
                // 根据机器人名称获取机器人地图路径列表
                List<RobotPathVO> mapPathListByRobotId = JSONObject.parseArray(robotBasicInfoService.findByName(robotName).get(0).getPaths(), RobotPathVO.class);
                // 合并到运行中机器人地图路径列表中
                runningRobotMapPathList.addAll(mapPathListByRobotId);
            }
        }

        // 若robotId为null，则为路径冲突时的请求，直接返回运行中机器人地图路径列表
        if (robotId == null) {
            return runningRobotMapPathList;
        }

        // 若列表为空，则说明当前场景中没有运行的机器人，等价于首次下发任务，直接获取 robotId 对应的地图路径列表
        if (runningRobotMapPathList.isEmpty()) {
            runningRobotMapPathList = JSONObject.parseArray(robotBasicInfoService.findByName(robotId).get(0).getPaths(), RobotPathVO.class);
        } else {
            // 若列表不为空，则说明当前场景中有运行中的机器人，则合并当前机器人对应的地图路径列表到已有的运行中机器人地图路径列表中
            runningRobotMapPathList.addAll(JSONObject.parseArray(robotBasicInfoService.findByName(robotId).get(0).getPaths(), RobotPathVO.class));
        }

        return runningRobotMapPathList;
    }

    /**
     * <获取场景中全部运行中机器人的地图融合站点集合> leo add
     */
    public Set<String> getAllMapPointSetForRunningRobot(String robotId) {
        // 获取场景中全部运行中的机器人名称
        Set<String> runningRobotIdSet = navTaskQueueMap.keySet();
        // 定义运行中机器人地图融合站点集合
        Set<String> runningRobotMapPointSet = new HashSet<>();
        // 若机器人名称集合不为空，则构造运行中机器人地图站点集合
        if (!runningRobotIdSet.isEmpty()) {
            for (String robotName : runningRobotIdSet) {
                // 根据机器人名称获取该机器人地图站点集合
                HashSet<String> mapPointsSetByRobotId = new HashSet<>(JSONObject.parseArray(robotBasicInfoService.findByName(robotName).get(0).getPoints(), String.class));
                // 合并到运行中机器人地图站点集合中
                runningRobotMapPointSet.addAll(mapPointsSetByRobotId);
            }
        }
        // 若robotId为null，则为路径冲突时的请求，直接返回运行中机器人地图站点集合
        if (robotId == null) {
            return runningRobotMapPointSet;
        }
        // 若集合为空，则说明当前场景中没有运行的机器人，等价于首次下发任务，直接获取 robotId 对应的站点集合
        if (runningRobotMapPointSet.isEmpty()) {
            runningRobotMapPointSet = new HashSet<>(JSONObject.parseArray(robotBasicInfoService.findByName(robotId).get(0).getPoints(), String.class));
        } else {
            // 若集合不为空，则说明当前场景中有运行中的机器人，则合并当前机器人对应的地图站点集合到已有的运行中机器人地图站点集合中
            runningRobotMapPointSet.addAll(JSONObject.parseArray(robotBasicInfoService.findByName(robotId).get(0).getPoints(), String.class));
        }

        return runningRobotMapPointSet;
    }

    /**
     * <验证路径连续性> leo add
     *
     * @param robotName 机器人名称
     * @param path      该机器人路径
     */
    private void validatePathContinuity(String robotName, List<AlgorithmResResultDTO.Path> path) {
        for (int i = 1; i < path.size(); i++) {
            if (!path.get(i).getStartPoint().equals(path.get(i - 1).getEndPoint())) {
                throw new IllegalArgumentException("机器人" + robotName + "的规划路径不连续");
            }
        }
    }

    /**
     * <将算法返回的路径列表初始化为包含 taskId-起始点-结束点-步进长度 的任务队列，并存入多机器人任务队列管理器> leo add
     *
     * @param robotId  机器人ID
     * @param pathList 算法返回的路径列表
     */
    private void initializeOrUpdateTaskQueueByRobotId(String robotId, List<AlgorithmResResultDTO.Path> pathList, String waybillId) {
        // 定义机器人初始任务队列
        Deque<OneStepNavTaskDTO> initialTaskQueue = new ConcurrentLinkedDeque<>();
        // 将 pathList 重构为连续路径导航所需的初始任务队列
        pathList.forEach(stepPathDTO -> {
            initialTaskQueue.add(
                    // 构造全局唯一的任务ID，起点、终点
                    new OneStepNavTaskDTO(robotId,
                            stepPathDTO.getStartPoint(),
                            stepPathDTO.getEndPoint(),
                            stepPathDTO.getTotalCurveLength(),
                            UUID.randomUUID().toString().replaceAll("-", "") + "_" + taskIdCounter.getAndIncrement()));
        });

        // 更新或添加机器人导航任务队列
        navTaskQueueMap.compute(robotId, (k, existingQueue) -> {
            if (existingQueue != null) {
                // 清空原有内容
                existingQueue.clear();
                // 导入新数据
                existingQueue.addAll(initialTaskQueue);
                log.info("更新机器人{}导航任务队列成功:{}", robotId, existingQueue);
                // 保持引用对象不变
                return existingQueue;
            } else {
                // 添加新队列
                log.info("新增机器人{}导航任务队列成功:{}", robotId, initialTaskQueue);
                return new ConcurrentLinkedDeque<>(initialTaskQueue);
            }
        });

        /**
         * <初始化或更新多机器人跨楼层调度信息>
         */

        // 获取起始步进任务
        OneStepNavTaskDTO firstStep = navTaskQueueMap.get(robotId).peekFirst();
        // 获取最后步进任务
        OneStepNavTaskDTO lastStep = navTaskQueueMap.get(robotId).peekLast();
        // 获取任务起点
        String taskStartPoint = (firstStep != null) ? firstStep.getStartPoint() : null;
        // 获取任务终点
        String taskEndPoint = (lastStep != null) ? lastStep.getEndPoint() : null;
        // 防御起点或终点为null
        if (taskStartPoint == null || taskEndPoint == null) {
            return;
        }
        // 获取起点所在地图名
        String mapNameOfStartPoint = getMapNameByTargetPoint(robotId, taskStartPoint);
        // 获取终点所在地图名
        String mapNameOfEndPoint = getMapNameByTargetPoint(robotId, taskEndPoint);

        // 获取起点所在楼层，解析失败返回0
        int startFloor = parseMapNameForFloor(mapNameOfStartPoint);
        // 获取终点所在楼层，解析失败返回0
        int endFloor = parseMapNameForFloor(mapNameOfEndPoint);

        // 若起点或终点所在楼层为 0（即解析失败），则不构造跨楼层调度信息
        if (startFloor == 0 || endFloor == 0) {
            log.error("机器人{}的起点或终点所在楼层为无效值，无法构造跨楼层调度信息，请检查地图是否符合 <地图名前缀>_<floor>_<楼层号> 的命名规范", robotId);
            // 添加运单失败原因
            RobotWaybill robotWaybill = getById(waybillId);
            try {
                setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(),
                        String.format("请检查机器人%s的地图是否符合 <地图名前缀>_<floor>_<楼层号> 的命名规范", robotId));
            } catch (Exception ee) {
                log.error("更新运单失败，原因：{}", ee.getMessage());
            }
            return;
        }

        // 构造跨楼层调度信息对象
        CrossFloorInfoDTO crossFloorInfoDTO = CrossFloorInfoDTO.builder()
                .startFloor(startFloor)
                .startFloorMapName(mapNameOfStartPoint)
                .endFloor(endFloor)
                .endFloorMapName(mapNameOfEndPoint)
                .build();

        // 将跨楼层调度信息对象加入多机器人跨楼层调度信息映射
        robotCrossFloorInfoMap.put(robotId, crossFloorInfoDTO);
        log.info("初始化或更新机器人{}跨楼层调度信息成功:{}", robotId, crossFloorInfoDTO);
    }

    /**
     * <当站点锁更新时刷新前端显示> leo add
     */
    public void refreshFrontEndByStationLock() {

        // 生成当前锁定站点集合的快照
        Set<String> currentKeys = ConcurrentHashMap.newKeySet();
        currentKeys.addAll(lockedStations.keySet());

        // 获取上一次锁定站点集合的快照
        Set<String> prevKeys = prevLockedPointsRef.get();

        // 锁定站点集合变化时触发前端更新
        if (!currentKeys.equals(prevKeys)) {
            // 触发前端更新
            BusinessDataVO businessDataVO = new BusinessDataVO();
            businessDataVO.setCode("200");
            businessDataVO.setMsg("锁定站点更新");
            pusherLiteService.sendBusinessMessage(businessDataVO);
            // 原子更新快照
            prevLockedPointsRef.set(new HashSet<>(currentKeys));
            // 打印日志，集合通过,分割成字符串
            log.info("锁定站点更新，站点列表：{}", String.join(",", currentKeys));
        }
    }

    /**
     * <根据传入的站点列表执行区域管控和锁点逻辑> leo add
     *
     * @param robotId   机器人ID
     * @param points    站点列表
     * @param waybillId 运单ID
     * @return 布尔值：true-成功锁定，false-失败锁定
     */
    private synchronized boolean lockAreasAndPoints(String robotId, List<String> points, String waybillId) {

        /**
         * *** <预防占坑不屎问题> ***
         * 现象描述：
         *  ① 机器人R1待锁定的站点集合之一被其它机器人如R2锁定，导致R1无法锁定这些站点
         *  ② 而R1待锁定的站点集合又覆盖到了某些空闲的管控区域，并锁定了这些R2即将抵达的区域
         *  ③ 导致R1既不能前进，又锁定着区域不让R2前进
         * 解决方案：
         *   *  ① 冲突检测：若待锁定站点集合中至少有一个站点已被其他机器人锁定，则放弃全部站点的锁定尝试
         *   *  ② 若未能通过冲突检测，则不执行后续锁区域和锁点逻辑
         */
        for (String point : points) {
            String currentLocker = lockedStations.get(point);
            if (currentLocker != null && !currentLocker.isEmpty() && !currentLocker.equals(robotId)) {
                log.info("站点{}已被机器人{}锁定，拒绝机器人{}的锁定请求", point, currentLocker, robotId);
                return false;
            }
        }

        /**
         *  <Step1: 先占用区域（若存在）>
         *
         * 1.定义一个整型变量 areaOccupiedFlag 来标识区域冲突状态，初始化为 0。（状态为 2 时跳过后续锁点逻辑，等待下次轮询）
         *      0-不存在管控区域
         *      1-区域被自己占用
         *      2-区域被他人占用
         *      3-区域未被占用
         * 2.根据传入的待锁定站点列表，获取覆盖到的管控区域
         *  2.1.若待锁定站点未覆盖到任何管控区域，则 areaOccupiedFlag 置为 0,直接进入锁点逻辑
         *  2.2.若存在站点位于管控区域内，则获取覆盖到的管控区域，并遍历这些区域（可能是一个或多个），根据区域占用状态执行标记位判断逻辑。
         *      2.2.1.遍历中如果至少有一次 areaOccupiedFlag 被标记为 2 ，则 areaOccupiedFlag 最终置为 2 ；
         *      2.2.2.遍历中如果 areaOccupiedFlag 均被标记为 1，则 areaOccupiedFlag 最终置为1；
         *      2.2.3.遍历中如果 areaOccupiedFlag 只被标记为 1 或 3，且至少出现一次 3，则areaOccupiedFlag最终置为 3。
         * 3.检查 areaOccupiedFlag 的状态
         *  3.1 若为 2，则跳过后续锁点步骤，进入下次轮询或循环
         *  3.2 若为 0 或 1，则直接执行锁点逻辑
         *  3.3 若为 3，则依次管控待锁定站点覆盖的全部区域
         */

        // 1.定义区域冲突标记，初始化为 0（即不存在管控区域）。
        int areaOccupiedFlag = RobotDmsEditor.AreaConflictFlag.NONE_AREA;
        /*
         * 2.根据待锁定站点列表获取覆盖到的目标类型区域
         */

        // Ⅰ 根据待锁定站点获取覆盖到的基础管控区域
        List<RobotDmsEditor> focusAreasByTargetPorts = robotDmsEditorService.getAssignedTypeAreasByTargetPorts(points, RobotDmsEditor.AreaType.FOCUS);
        // Ⅱ 根据待锁定站点获取覆盖到的自动门区域
        List<RobotDmsEditor> automaticDoorAreasByTargetPorts = robotDmsEditorService.getAssignedTypeAreasByTargetPorts(points, RobotDmsEditor.AreaType.AUTO_DOOR);
        // Ⅲ 根据待锁定站点获取覆盖到的电梯区域
        List<RobotDmsEditor> elevatorAreasByTargetPorts = robotDmsEditorService.getAssignedTypeAreasByTargetPorts(points, RobotDmsEditor.AreaType.ELEVATOR);
        /**
         * <若覆盖到自动门区域: 则合并到基础管控区域中>
         */
        if (!automaticDoorAreasByTargetPorts.isEmpty()) {
            focusAreasByTargetPorts.addAll(automaticDoorAreasByTargetPorts);
        }
        /**
         * <若覆盖到电梯区域: 则触发联动管控逻辑>
         *  ① 获取场景中部署的全部指定协议的电梯
         *  ② 获取机器人即将进入的电梯区域
         *  ③ 若实际要进入的电梯区域不为null，则根据电梯区域获取电梯对象，并将该电梯加入< 机器人名称, 电梯对象 >管理器
         *  ④ 根据电梯对象获取电梯关联的全部电梯区域
         *  ⑤ 将全部关联的电梯区域合并到基础管控区域中
         *  ⑥ 将电梯对象绑定的EL站点合并到待锁定站点列表中
         */
        if (!elevatorAreasByTargetPorts.isEmpty()) {
            /*
             * ① 获取场景中部署的全部指定协议的电梯（现场部署时用配置文件的形式定义通信协议类型）
             */

            List<IotEquipment> allElevators = ioTEquipmentService.listByCommunicationTypeAndEquipment(IotEquipment.CommunicationType.TIBOSHI_TCP, IotEquipment.EquipmentType.ELEVATOR);
            /*
             * ② 获取机器人即将进入的电梯区域（判断条件：区域内包含的EL点是否被当前机器人锁定 ）
             */
            RobotDmsEditor enteringElevatorArea = elevatorAreasByTargetPorts.stream()
                    .filter(elevatorArea -> {
                        // a. 解析区域包含的站点集合
                        Set<String> areaPoints = JSON.parseObject(
                                elevatorArea.getAreaContainPoints(),
                                new TypeReference<Set<String>>() {
                                }
                        );
                        // b. 查找EL站点被当前robotId锁定的区域
                        return areaPoints.stream()
                                // 筛选EL站点
                                .filter(point -> point.toUpperCase().startsWith("EL"))
                                // 验证是否被当前机器人锁定
                                .anyMatch(elPoint -> {
                                    String currentLocker = lockedStations.get(elPoint);
                                    return robotId.equals(currentLocker);
                                });
                    })
                    // c. 取第一个符合条件的电梯区域
                    .findFirst()
                    .orElse(null);

            if (enteringElevatorArea != null) {
                /*
                 * ③ 若实际要进入的电梯区域不为null，则根据电梯区域获取电梯对象
                 */
                IotEquipment enteringElevator = allElevators.stream()
                        .filter(elevator -> {
                            // 解析当前电梯绑定的站点
                            Set<String> adjacentSites = new HashSet<>(
                                    Arrays.asList(elevator.getAdjacentSite().split(","))
                            );

                            // 解析目标电梯区域的站点
                            Set<String> elevatorAreaPoints = robotDmsEditorService.parseAreaPoints(enteringElevatorArea);
                            // 根据站点交集判断是否为关联电梯
                            return elevatorAreaPoints.stream()
                                    // 保留交集元素
                                    .filter(adjacentSites::contains)
                                    // 计算交集元素数量是否为2
                                    .count() == 2;
                        })
                        // 取第一个符合条件的电梯
                        .findFirst()
                        // 若不存在返回 null
                        .orElse(null);

                // 将该电梯加入<机器人名称, 电梯对象>管理器
                robotElevatorControlledMap.putIfAbsent(robotId, enteringElevator);

                /*
                 * ④ 再根据电梯对象获取该电梯关联的全部电梯区域
                 */
                if (enteringElevator != null) {
                    // a.首先获取全部的电梯区域
                    List<RobotDmsEditor> allElevatorAreas = robotDmsEditorService.findListByAreaType(RobotDmsEditor.AreaType.ELEVATOR);
                    // b.然后解析待进入电梯的绑定站点集合
                    Set<String> adjacentSites = Arrays.stream(enteringElevator.getAdjacentSite().split(","))
                            .collect(Collectors.toSet());

                    // c.从全部电梯区域中筛选与目标电梯关联的全楼层电梯区域
                    Set<RobotDmsEditor> relatedElevatorAreas = allElevatorAreas.stream()
                            .filter(elevatorArea -> {
                                // 解析区域点集合
                                Set<String> areaPoints = JSON.parseObject(
                                        elevatorArea.getAreaContainPoints(),
                                        new TypeReference<Set<String>>() {
                                        }
                                );
                                // 通过站点交集判断是否为电梯的关联区域
                                return areaPoints.stream()
                                        // 保留交集元素
                                        .filter(adjacentSites::contains)
                                        // 提前终止遍历
                                        .limit(2)
                                        // 计算交集元素数量是否为2
                                        .count() == 2;
                            })
                            .collect(Collectors.toSet());

                    /*
                     * ⑤ 然后将关联的电梯区域合并到基础管控区域中
                     */
                    focusAreasByTargetPorts.addAll(relatedElevatorAreas);

                    /*
                     * *** 联动锁定站点 ***
                     * ⑥ 最后将电梯绑定的EL站点合并到待锁定站点列表中(避免因其它楼层区域被占用但这些楼层区域内不存在被锁定点，导致这些楼层区域被提前释放)
                     */
                    points.addAll(
                            Arrays.stream(enteringElevator.getAdjacentSite().split(","))
                                    .filter(elPoint -> elPoint.toUpperCase().startsWith("EL"))
                                    .collect(Collectors.toList())
                    );
                }
            }
        }
        // 2.1 若待锁定站点未覆盖到任何管控区域
        if (focusAreasByTargetPorts.isEmpty()) {
            // 此时 areaOccupiedFlag 为默认值 0
            log.info("机器人{}的待锁定站点{}不位于任何管控区域内，直接锁定站点", robotId, String.join(",", points));
        } else {
            // 2.2 若存在待锁定站点位于管控区域内，则执行区域管控相应逻辑

            /**
             * <使用状态位辅助判断最终结果>
             * ① 定义是否存在冲突的区域（即被其它机器人占用）
             * ② 定义是否存在空闲区域（即未被任何机器人占用）
             */
            boolean hasConflictWithOthers = false;
            boolean hasFreeArea = false;

            for (RobotDmsEditor focusAreasByTargetPort : focusAreasByTargetPorts) {
                // 区域被占用，分为两种情形
                if (focusAreasByTargetPort.getOccupiedStatus() == 1) {
                    // 2.2.1 若被自己占用，不做处理

                    // 2.2.2 若被其它机器人占用
                    if (!focusAreasByTargetPort.getRobotId().equals(robotId)) {
                        // 立即发现冲突情况，保留状态但继续检查后续区域（保证所有区域的异常数据都被处理）
                        hasConflictWithOthers = true;
                        log.info("机器人{}的待锁定站点{}(之一)位于管控区域[{}]内，区域被机器人{}占用，等待释放......", robotId,
                                String.join(",", points),
                                focusAreasByTargetPort.getAreaName(),
                                focusAreasByTargetPort.getRobotId());

                    }
                } else {
                    // 2.2.3 区域未被占用，标记为空闲
                    hasFreeArea = true;
                    // 固定处理异常数据
                    if (focusAreasByTargetPort.getRobotId() != null && !focusAreasByTargetPort.getRobotId().isEmpty()) {
                        log.error("区域{}未被占用，但 robotId 存在异常值：{}，即将清空该异常值", focusAreasByTargetPort.getAreaName(), focusAreasByTargetPort.getRobotId());
                        focusAreasByTargetPort.setRobotId("");
                        robotDmsEditorService.saveOrUpdate(focusAreasByTargetPort);
                    }
                }
            }

            // 按照条件优先级确定标记的最终结果
            if (hasConflictWithOthers) {
                // 存在冲突区域
                areaOccupiedFlag = RobotDmsEditor.AreaConflictFlag.OCCUPIED_BY_OTHER;
            } else if (hasFreeArea) {
                // 不存在冲突区域，且存在空闲区域
                areaOccupiedFlag = RobotDmsEditor.AreaConflictFlag.FREE_AREA;
            } else {
                // 不存在冲突区域，且不存在空闲区域，即全部区域均被自己占用
                areaOccupiedFlag = RobotDmsEditor.AreaConflictFlag.OCCUPIED_BY_SELF;
            }
        }

        // 若存在区域被其它机器人占用，跳过后续锁定区域及站点步骤，进入下次轮询或循环
        if (areaOccupiedFlag == RobotDmsEditor.AreaConflictFlag.OCCUPIED_BY_OTHER) {
            return false;
        }
        // 若存在空闲区域，则依次管控待锁定站点覆盖的全部区域
        else if (areaOccupiedFlag == RobotDmsEditor.AreaConflictFlag.FREE_AREA) {
            log.info("机器人{}的待锁定站点{}(之一)位于管控区域[{}]内, 区域未被占用...现进行占用", robotId,
                    String.join(",", points),
                    focusAreasByTargetPorts.stream()
                            .map(RobotDmsEditor::getAreaName)
                            .collect(Collectors.joining(", ")));
            // 管控待锁定站点覆盖的全部区域
            focusAreasByTargetPorts.forEach(area -> {
                area.setOccupiedStatus(1);
                area.setRobotId(robotId);
                robotDmsEditorService.saveOrUpdate(area);
            });
        }

        /**
         *  <Step2: 后锁定站点>
         *  areaOccupiedFlag 不为2（即不存在冲突区域）时，可执行锁点逻辑
         */
        lockPoints(robotId, points);

        // 若占用了自动门区域，则执行自动门专用处理逻辑
        if (!automaticDoorAreasByTargetPorts.isEmpty()) {
            /**
             * <Step3.1: 执行基于区域管控的自动门专用处理逻辑>
             */
            Boolean automaticDoorHandleResult = handleAutomaticDoorLogic(robotId, automaticDoorAreasByTargetPorts, waybillId);
            // 当返回true或false时立即中断主流程；否则，其他原有业务逻辑代码继续执行
            if (automaticDoorHandleResult != null) {
                return automaticDoorHandleResult;
            }
        }

        // 若占用了电梯区域，且机器人绑定了电梯对象，则执行电梯专用处理逻辑
        if (robotElevatorControlledMap.get(robotId) != null) {
            /**
             * <Step3.2: 执行基于区域管控的电梯专用处理逻辑>
             */
            Boolean elevatorHandleResult = handleElevatorLogic(robotId, waybillId);
            // 当返回true或false时立即中断主流程；否则，其他原有业务逻辑代码继续执行
            if (elevatorHandleResult != null) {
                return elevatorHandleResult;
            }
        }

        return true;
    }

    /**
     * <锁定传入列表中的未锁定的站点--原子锁定> leo add
     *
     * @param robotId 机器人ID
     * @param points  站点列表
     */
    private synchronized void lockPoints(String robotId, List<String> points) {
        // 冲突检测：若站点集合中至少有一个站点已被其他机器人锁定，则拒绝全部锁定请求
//        for (String point : points) {
//            String currentLocker = lockedStations.get(point);
//            if (currentLocker != null && !currentLocker.isEmpty() && !currentLocker.equals(robotId)) {
//                log.info("站点{}已被机器人{}锁定，拒绝机器人{}的锁定请求", point, currentLocker, robotId);
//                return false;
//            }
//        }
        boolean isNeedRefresh = false;
        // 若通过检测，则执行锁定
        for (String point : points) {
            String currentLocker = lockedStations.get(point);
            if (currentLocker == null || currentLocker.isEmpty()) {
                // 站点未被锁定，直接锁定
                lockedStations.put(point, robotId);
                log.info("站点{}已被机器人{}锁定", point, robotId);
                // 至少有一个站点变更锁定状态时，则刷新标记置为true
                isNeedRefresh = true;
            }
        }
        // 有站点变更锁定状态时，则刷新前端站点锁定状态显示，否则不刷新
        if (isNeedRefresh) {
            refreshFrontEndByStationLock();
        }
        // 输出当前机器人此时锁定的全部站点
        log.info("机器人{}锁定的全部站点：{}",
                robotId,
                getLockedPointsByRobot(robotId).stream().collect(Collectors.joining(", ", "[", "]"))
        );
    }

    /**
     * <基于区域管控的自动门处理逻辑> leo add
     * <p>
     * 说明：此时全部站点及站点覆盖到的区域均被自己占用。首先获取该自动门区域的自动门对象，然后根据是否实际占用物联网设备区域执行后续逻辑
     * Ⅰ.获取目标自动门：
     * ① 获取在线的自动门
     * ② 筛选覆盖到的自动门
     * <p>
     * Ⅱ.通过锁定站点管理器来判断是否实际穿越自动门：
     * ① 若穿越，则执行后续逻辑
     * ② 若不穿越，则不做任何操作
     *
     * @param robotId                         机器人ID
     * @param automaticDoorAreasByTargetPorts 待锁定站点覆盖的自动门区域
     * @param waybillId                       运单ID
     */
    private Boolean handleAutomaticDoorLogic(String robotId, List<RobotDmsEditor> automaticDoorAreasByTargetPorts, String waybillId) {
        // 获取场景中全部在线的自动门
        List<IotEquipment> automaticDoorsOnline = ioTEquipmentService.getOnlineModbusEquipments();
        // 若在线的自动门为空，则跳过后续步骤
        if (automaticDoorsOnline.isEmpty()) {
            log.error("当前场景中无法检测到自动门设备，请检查自动门是否在线");
            return true;
        }
        // 筛选符合条件的自动门设备（即待锁站点覆盖区域的自动门集合）
        Set<IotEquipment> coveredAutomaticDoorSet = automaticDoorsOnline.stream()
                .filter(automaticDoor -> {
                    // 将自动门绑定的相邻站点拆分为集合
                    Set<String> adjacentSites = new HashSet<>(
                            Arrays.asList(
                                    automaticDoor.getAdjacentSite().split(","))
                    );
                    // 检查当前自动门是否关联到目标区域集合中的任意一个区域
                    return automaticDoorAreasByTargetPorts.stream().map(doorArea ->
                                    // 将区域内的站点Json字符串转换为站点集合
                                    JSON.parseObject(doorArea.getAreaContainPoints(), new TypeReference<Set<String>>() {
                                    })
                            )
                            // 短路：存在自动门区域站点包含自动门站点，则获取该自动门对象
                            .anyMatch(doorAreaPoints ->
                                    // 判断该区域是否包含自动门的绑定点
                                    doorAreaPoints.containsAll(adjacentSites)
                            );
                })
                // 获取首个匹配的自动门（满足条件立即终止流）
//                    .findFirst()
                // 获取匹配的全部自动门
                .collect(Collectors.toSet());

        /**
         * <处理覆盖区域自动门的逻辑>
         *
         * ① 若覆盖区域自动门不存在，增加运单失败原因
         * ② 若覆盖区域自动门存在，判断机器人是否准备通过自动门
         */
        if (coveredAutomaticDoorSet.isEmpty()) {
            // 自动门区域中无在线自动门设备，设置运单状态为异常
            log.error("自动门区域中无自动门设备，请检查自动门是否在线");
            // 添加运单失败原因
            RobotWaybill robotWaybill = getById(waybillId);
            try {
                setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(),
                        String.format("自动门区域%s中无自动门设备在线，请检查该区域内的自动门在线状态", automaticDoorAreasByTargetPorts.get(0)));
            } catch (Exception ee) {
                log.error("更新运单失败，原因：{}", ee.getMessage());
            }
            return false;

        } else {
            // 若覆盖区域自动门存在
            /**
             * <判断是否需要穿越自动门: 判断条件--当前机器人锁定的站点是否包含自动门绑定站点?>
             */

            // a.获取当前机器人锁定的站点
            List<String> lockedPointsBySelf = getLockedPointsByRobot(robotId);

            // b.筛选符合条件的自动门: 即实际会穿越的自动门
            IotEquipment passingDoor = coveredAutomaticDoorSet.stream()
                    .filter(coveredAutomaticDoor -> {
                        // 获取当前自动门绑定的站点列表
                        List<String> sitesToAutomaticDoor = Arrays.asList(
                                coveredAutomaticDoor.getAdjacentSite().split(",")
                        );
                        // 判断锁定站点是否包含所有绑定的站点
                        return new HashSet<>(lockedPointsBySelf).containsAll(sitesToAutomaticDoor);
                    })
                    // 有且仅有一个自动门符合条件
                    .findFirst()
                    .orElse(null);

            /**
             * <将被机器人管控的自动门对象存入管理器: 用于后续解除管控>
             *
             * ① 定义目标自动门集合
             * ② 将机器人实际要穿越的自动门加入该机器人管控的自动门集合（为了快速查找，减少性能开销）
             */
            // 定义比较器（按id去重）
            Comparator<IotEquipment> comparator = Comparator.comparing(IotEquipment::getId);
            // 定义目标自动门集合
            Set<IotEquipment> targetAutomaticDoorSet = robotDoorControlledMap.computeIfAbsent(robotId, k -> new TreeSet<>(comparator));

            // 若没有符合条件的自动门，则不做任何操作
            if (passingDoor == null) {
                log.info("机器人{}已占用自动门区域<{}>，但还未准备通过自动门", robotId, automaticDoorAreasByTargetPorts.get(0).getAreaName());
            } else {
                /**
                 * <c.若存在实际要穿越的自动门: 则执行自动门与机器人的交互逻辑>
                 */

                // 将机器人实际要穿越的自动门加入该机器人管控的自动门集合
                targetAutomaticDoorSet.add(passingDoor);
                log.info("机器人{}即将通过自动门<{}>, 尝试下发自动门开门指令", robotId, passingDoor.getEquipmentName());
                // 调用处理自动门与机器人交互的方法
                handleAutomaticDoorForRobot(robotId, passingDoor, waybillId);
            }
        }
        return null;
    }

    /**
     * <处理自动门与机器人交互的逻辑> leo add
     *
     * @param robotId     机器人ID
     * @param passingDoor 需穿行的自动门
     * @param waybillId   运单ID
     */
    private void handleAutomaticDoorForRobot(String robotId, IotEquipment passingDoor, String waybillId) {
        // 获取当前机器人对应的门任务映射
        Map<String, ScheduledFuture<?>> doorMap = robotDoorTaskMap.get(robotId);
        if (doorMap != null) {
            String doorId = passingDoor.getId();
            ScheduledFuture<?> taskFuture = doorMap.get(doorId);

            // 如果存在该门对应的任务，且任务尚未完成（包括尚未取消），则直接返回
            if (taskFuture != null && !taskFuture.isDone()) {
                log.info("机器人{}的打开自动门<{}>任务仍在运行，跳过新任务创建",
                        robotId, passingDoor.getEquipmentName());
                return;
            }
        }

        // 原子性检查并设置状态
        final CountDownLatch doorLatch = new CountDownLatch(1);
        final CountDownLatch existingLatch = robotLatchByDoorMap.putIfAbsent(robotId, doorLatch);
        // 若门未打开，门闩不会释放，不为null，则会跳过后续步骤，避免重复处理
        if (existingLatch != null) {
            log.info("机器人 {} 已在自动门等待队列 中，等待自动门开启", robotId);
            // 已有处理中的自动门等待
            return;
        }

        try {
            // 获取自动门的主键
            String doorId = passingDoor.getId();

            ScheduledExecutorService executor = robotDoorThreadPollMap.computeIfAbsent(robotId, key -> {
                ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(2, new DynamicThreadFactory(robotId, "openDoor"));
                // 立即初始化所有核心线程
                pool.prestartAllCoreThreads();
                return pool;
            });

            // 创建新轮询任务
            ScheduledFuture<?> doorScheduledFuture = executor.scheduleWithFixedDelay(() -> {
                openAutomaticDoorByRobot(robotId, passingDoor, waybillId);
            }, 0, 2000, TimeUnit.MILLISECONDS);

            // 存储自动门任务引用
            Map<String, ScheduledFuture<?>> doorTaskMap = robotDoorTaskMap.computeIfAbsent(robotId,
                    k -> new ConcurrentHashMap<>());
            doorTaskMap.put(doorId, doorScheduledFuture);

            log.info("机器人 {} 开始等待自动门 <{}> 开启", robotId, passingDoor.getEquipmentName());
            // 阻塞当前机器人线程
            doorLatch.await();
            log.info("机器人 {} 自动门 <{}> 已开启，继续执行任务", robotId, passingDoor.getEquipmentName());

        } catch (InterruptedException e) {
            // 添加运单失败原因
            RobotWaybill robotWaybill = getById(waybillId);
            try {
                setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), "创建自动门轮询线程异常");
            } catch (Exception ee) {
                log.error("创建自动门轮询线程异常，原因：{}", ee.getMessage());
            }
        }
    }


    /**
     * <机器人与自动门交互线程> leo add
     * 1.请求开门
     * 2.读取门的状态
     * 3.判断门的状态是否开启:
     * <p>
     * a.若已开启，则释放CountDownLatch，且不再读取门的状态，也不再判断门的状态是否开启。
     * b.若未开启，继续下次轮询
     *
     * @param robotId     机器人ID
     * @param passingDoor 需穿行的自动门
     * @param waybillId   运单ID
     */
    private void openAutomaticDoorByRobot(String robotId, IotEquipment passingDoor, String waybillId) {
        String doorName = passingDoor.getEquipmentName();
        IotEquipment updatedPassingDoor = ioTEquipmentService.getById(passingDoor.getId());
        try {
            // 1. 下发开门指令，不论门是不是已开启都下发
            if (!modbusGenericRw(updatedPassingDoor, IotEquipment.ModbusRegisterType.WRITE_HOLDING_REGISTER,
                    IotEquipment.AutomaticDoorOpenedStatus.AUTOMATIC_DOOR_OPEN).isSuccess()) {
                log.error("机器人 {} 尝试下发给自动门 {} 开门指令，但下发指令失败", robotId, doorName);
            }

            // 2.1 检查数据库的门状态，若数据库门状态为已开启，则跳过后续逻辑
            if (updatedPassingDoor.getDoorOpenedStatus() == 1) {
                return;
            }
            // 2.2 获取门的实时状态
            int doorStatus = modbusGenericRw(updatedPassingDoor, IotEquipment.ModbusRegisterType.READ_HOLDING_REGISTER, null).getValue();
            log.info("机器人 {} 即将通行，自动门 <{}> 的当前状态: {}", robotId, doorName,
                    doorStatus == IotEquipment.AutomaticDoorOpenedStatus.AUTOMATIC_DOOR_OPEN ? "打开" : "关闭");

            // 3. 当门开启后移除门闩
            if (doorStatus == IotEquipment.AutomaticDoorOpenedStatus.AUTOMATIC_DOOR_OPEN) {
                // 设置数据库的门状态为已开启
                updatedPassingDoor.setDoorOpenedStatus(1);
                if (ioTEquipmentService.saveOrUpdate(updatedPassingDoor)) {
                    log.info("已更新数据库自动门<{}>状态为：开启", doorName);
                }
                // 门开启后，移除门闩并释放阻塞
                CountDownLatch latch = robotLatchByDoorMap.remove(robotId);
                if (latch != null) {
                    latch.countDown();
                    log.info("自动门 <{}> 已开启，释放机器人 {} 的阻塞", doorName, robotId);
                }
            }
        } catch (Exception e) {
            log.error("自动门状态轮询异常 - 机器人: {},异常：{}", robotId, e.getMessage());
            // 关闭自动门定时任务
            ScheduledFuture<?> scheduledFuture = robotDoorTaskMap.get(robotId).get(updatedPassingDoor.getId());
            if (scheduledFuture != null) {
                scheduledFuture.cancel(true);
            }
            // 添加运单失败方法
            RobotWaybill robotWaybill = getById(waybillId);
            try {
                setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), "自动门状态发生异常");
            } catch (Exception ee) {
                log.error("更新运单失败，原因：{}", ee.getMessage());
            }
        }
    }

    /**
     * <机器人与电梯交互逻辑> leo add
     *
     * @param robotId   机器人ID
     * @param waybillId 运单ID
     */
    private Boolean handleElevatorLogic(String robotId, String waybillId) {

        // 若状态机不为 Free，则跳过该方法
        if (robotForElevatorFSMMap.get(robotId) != RobotForElevatorFSM.FREE) {
            return null;
        }
        // 初始化机器人控制的电梯门为前门
        robotFrontOrRearDoorMap.put(robotId, true);

        // 状态机设置为 “等待电梯到达起始楼层” 状态
        robotForElevatorFSMMap.put(robotId, RobotForElevatorFSM.WAIT_ELEVATOR_ARRIVE_START);

        // 获取当前机器人的电梯状态守护任务
        ScheduledFuture<?> existElevatorWatchTaskFuture = robotElevatorWatchTaskMap.get(robotId);
        // （防御）若该机器人关门任务正在进行中，则跳过后续逻辑
        if (existElevatorWatchTaskFuture != null && !existElevatorWatchTaskFuture.isDone()) {
            log.info("机器人{}的电梯状态守护任务仍在运行，跳过新任务创建", robotId);
            return null;
        }

        /*
         * 阻塞机器人线程
         */
        // 原子性检查并设置状态
        final CountDownLatch doorLatch = new CountDownLatch(1);
        final CountDownLatch existingLatch = robotLatchByDoorMap.putIfAbsent(robotId, doorLatch);
        // 若电梯门未打开，门闩不会释放，不为null，则会跳过后续步骤，避免重复处理
        if (existingLatch != null) {
            log.info("机器人 {} 已在自动门等待队列", robotId);
            // 已有处理中的等待电梯开门
            return null;
        }
        try {
            // 获取该机器人控制的电梯对象
            IotEquipment bindingElevator = robotElevatorControlledMap.get(robotId);
            // 获取该机器人的跨楼层调度信息
            CrossFloorInfoDTO crossFloorInfoDTO = robotCrossFloorInfoMap.get(robotId);
            // 若不存在跨楼层调度信息，则直接返回
            if (crossFloorInfoDTO == null) {
                log.error("机器人 {} 未初始化跨楼层调度信息，请检查地图命名及配置是否正确", robotId);
                return null;
            }
            // 为该机器人创建新的电梯守护线程池，用于启动电梯守护任务
            ScheduledExecutorService executor = robotElevatorThreadPollMap.computeIfAbsent(robotId,
                    k -> Executors.newSingleThreadScheduledExecutor(new DynamicThreadFactory(robotId, "elevatorWatch")));

            // 创建新的电梯守护任务
            ScheduledFuture<?> elevatorWatchTaskFuture = executor.scheduleWithFixedDelay(() -> {
                        elevatorWatchPollTask(robotId, waybillId, bindingElevator, crossFloorInfoDTO);
                    }, 0, 1500, TimeUnit.MILLISECONDS
            );
            // 电梯守护任务加入到任务映射管理器中
            robotElevatorWatchTaskMap.putIfAbsent(robotId, elevatorWatchTaskFuture);

            log.info("机器人 {} 开始等待电梯 <{}> 的门开启", robotId, bindingElevator.getEquipmentName());
            // 阻塞当前机器人线程
            doorLatch.await();
            log.info("机器人 {} 即将进入的电梯 <{}> 的门已开启，继续执行任务", robotId, bindingElevator.getEquipmentName());

        } catch (InterruptedException e) {
            // 添加运单失败原因
            RobotWaybill robotWaybill = getById(waybillId);
            try {
                setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), "创建电梯状态守护线程异常");
            } catch (Exception ee) {
                log.error("创建电梯状态守护线程异常，原因：{}", ee.getMessage());
            }
        }

        return null;
    }

    /**
     * <电梯守护轮询任务> leo add
     *
     * @param robotId           机器人ID
     * @param waybillId         运单ID
     * @param bindingElevator   机器人控制的电梯
     * @param crossFloorInfoDTO 跨楼层调度信息
     */
    private void elevatorWatchPollTask(String robotId, String waybillId, IotEquipment bindingElevator, CrossFloorInfoDTO crossFloorInfoDTO) {
        /*
         * Ⅰ: <查询电梯状态>
         */
        // 发送查询电梯状态请求
        EntityResult statusResult = ioTEquipmentService.queryElevatorStatus(robotId, bindingElevator);
        if (!statusResult.isSuccess()) {
            log.error("查询电梯<{}>的状态失败", bindingElevator.getEquipmentName());
            // 若查询失败，跳过该次轮询
            return;
        }
        // 若查询成功，线程等待 0.5 秒，等待任务状态更新
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error("线程等待异常：{}", e.getMessage());
        }

        /*
         * Ⅱ: <获取电梯状态>
         */
        ElevatorStatusDTO elevatorStatusDTO = elevatorStatusMap.get(bindingElevator.getEquipmentName());
        if (elevatorStatusDTO == null) {
            // 若电梯状态DTO为空，则跳过该次轮询
            return;
        }

        /*
         * Ⅲ: <根据[机器人状态机]和[电梯状态]执行对应逻辑>
         */
        // 获取当前机器人的状态机
        Integer robotFSM = robotForElevatorFSMMap.get(robotId);
        // 根据当前状态机执行对应逻辑
        switch (robotFSM) {
            // 1 等待电梯到达起始楼层
            case RobotForElevatorFSM.WAIT_ELEVATOR_ARRIVE_START:
                // 更新机器人控制的前后门信息
                updateElevatorCurrentDoorToRobot(robotId);

                // 若进电梯开门条件成立：①电梯当前楼层==机器人起始楼层；②电梯停止
                if (elevatorStatusDTO.getCurrentFloor() == crossFloorInfoDTO.getStartFloor() && elevatorStatusDTO.isStopped()) {
                    // 则状态机设置为 “等待进电梯开门” 状态
                    robotForElevatorFSMMap.put(robotId, RobotForElevatorFSM.WAIT_IN_ELEVATOR_OPEN);
                    log.info("机器人{}的状态机已切换为：[等待进电梯开门]", robotId);
                    log.info("电梯<{}>已到达机器人{}的起始楼层{}层，等待电梯门开启", bindingElevator.getEquipmentName(), robotId, crossFloorInfoDTO.getStartFloor());
                }
                // 发送登记楼层指令：起始楼层及门方位
                ioTEquipmentService.registerFloor(robotId, bindingElevator, crossFloorInfoDTO.getStartFloor(), robotFrontOrRearDoorMap.get(robotId));

                break;

            // 2 等待进电梯开门
            case RobotForElevatorFSM.WAIT_IN_ELEVATOR_OPEN:

                // 发送电梯开门指令
                ioTEquipmentService.openElevatorDoor(robotId, bindingElevator, robotFrontOrRearDoorMap.get(robotId));

                // 若满足安全进入条件：①电梯静止，②门开启
                if (elevatorStatusDTO.isStopped() && elevatorStatusDTO.isDoorOpen(robotFrontOrRearDoorMap.get(robotId))) {
                    // 若阻塞锁存在，则释放阻塞锁
                    CountDownLatch latch = robotLatchByDoorMap.remove(robotId);
                    if (latch != null) {
                        latch.countDown();
                        log.info("电梯<{}>门已开启，释放机器人 {} 的阻塞，机器人即将进入电梯", bindingElevator.getEquipmentName(), robotId);
                    }
                    // 状态机设置为 “正在进入电梯” 状态
                    robotForElevatorFSMMap.put(robotId, RobotForElevatorFSM.ENTERING_ELEVATOR);
                    log.info("机器人{}的状态机已切换为：[正在进入电梯]", robotId);
                }

                break;

            // 3 正在进入电梯
            case RobotForElevatorFSM.ENTERING_ELEVATOR:
                // 发送电梯开门指令
                ioTEquipmentService.openElevatorDoor(robotId, bindingElevator, robotFrontOrRearDoorMap.get(robotId));
                log.info("机器人 {} 正在驶入电梯<{}>中......", robotId, bindingElevator.getEquipmentName());

                break;

            // 4 等待电梯运行
            case RobotForElevatorFSM.WAIT_ELEVATOR_RUNNING:
                // 若电梯门关闭
                if (elevatorStatusDTO.isDoorClosed()) {
                    // 若电梯开始移动
                    if (elevatorStatusDTO.isMoving()) {
                        // 状态机设置为 “等待电梯到达目标楼层” 状态
                        robotForElevatorFSMMap.put(robotId, RobotForElevatorFSM.WAIT_ELEVATOR_ARRIVE_TARGET);
                        log.info("机器人 {} 乘坐电梯<{}>正在前往目标楼层{}层，状态机已切换为：[等待电梯到达目标楼层]",
                                robotId, bindingElevator.getEquipmentName(), crossFloorInfoDTO.getEndFloor());
                    }
                    // 若电梯还未移动
                    else {
                        // 更新机器人控制的前后门信息
                        updateElevatorCurrentDoorToRobot(robotId);
                        // 发送 登记楼层指令：目标楼层及门方位
                        ioTEquipmentService.registerFloor(robotId, bindingElevator, crossFloorInfoDTO.getEndFloor(), robotFrontOrRearDoorMap.get(robotId));
                    }
                } else {
                    // 若电梯门未关闭，则发送电梯关门指令（从电梯门方位管理器中获取前门还是后门）
                    ioTEquipmentService.closeElevatorDoor(robotId, bindingElevator, robotFrontOrRearDoorMap.get(robotId), false);
                }

                break;

            // 5 等待电梯到达目标楼层
            case RobotForElevatorFSM.WAIT_ELEVATOR_ARRIVE_TARGET:
                // 切换机器人地图为目标楼层地图
                switchRobotMap(robotId, crossFloorInfoDTO.getEndFloorMapName());

                // 若出电梯开门条件成立：①电梯当前楼层==机器人目标楼层；②电梯停止
                if (elevatorStatusDTO.getCurrentFloor() == crossFloorInfoDTO.getEndFloor() && elevatorStatusDTO.isStopped()) {
                    // 状态机设置为“等待出电梯开门”状态
                    robotForElevatorFSMMap.put(robotId, RobotForElevatorFSM.WAIT_OUT_ELEVATOR_OPEN);
                    log.info("机器人 {} 已乘坐电梯<{}>到达目标楼层{}层，等待电梯门打开", robotId, bindingElevator.getEquipmentName(), crossFloorInfoDTO.getEndFloor());
                }

                break;

            // 6 等待出电梯开门
            case RobotForElevatorFSM.WAIT_OUT_ELEVATOR_OPEN:
                // 发送电梯开门指令
                ioTEquipmentService.openElevatorDoor(robotId, bindingElevator, robotFrontOrRearDoorMap.get(robotId));

                // 若指定门已打开
                if (elevatorStatusDTO.isDoorOpen(robotFrontOrRearDoorMap.get(robotId))) {
                    List<OneStepNavTaskDTO> runningTasks = runningTaskMap.get(robotId);
                    // 若控制器镜像队列不为空且为电梯虚拟任务
                    if (!runningTasks.isEmpty() && runningTasks.get(0).getStartPoint().toUpperCase().startsWith("EL") && runningTasks.get(0).getEndPoint().toUpperCase().startsWith("EL")) {
                        // 则移除控制器镜像队列中的电梯虚拟任务
                        OneStepNavTaskDTO removedTask = runningTasks.remove(0);
                        log.info("机器人 {} 的控制器镜像队列中移除电梯虚拟任务【{}→{}】，剩余镜像队列长度为{}", robotId, removedTask.getStartPoint(), removedTask.getEndPoint(), runningTasks.size());

                        // 状态机设置为 “正在出电梯门” 状态
                        robotForElevatorFSMMap.put(robotId, RobotForElevatorFSM.OUTING_ELEVATOR_DOOR);
                    }
                }

                break;

            // 7 正在出电梯门
            case RobotForElevatorFSM.OUTING_ELEVATOR_DOOR:
                // 发送电梯开门指令
                ioTEquipmentService.openElevatorDoor(robotId, bindingElevator, robotFrontOrRearDoorMap.get(robotId));
                log.info("机器人 {} 正在驶离电梯<{}>", robotId, bindingElevator.getEquipmentName());

                break;

            // 8 已经离开电梯
            case RobotForElevatorFSM.LEAVE_ELEVATOR:
                // ① 发送 电梯关门指令
                ioTEquipmentService.closeElevatorDoor(robotId, bindingElevator, robotFrontOrRearDoorMap.get(robotId), false);
                // ② 关闭 电梯状态守护任务
                ScheduledFuture<?> future = robotElevatorWatchTaskMap.remove(robotId);
                if (future != null) {
                    // 立即中断任务执行
                    future.cancel(true);
                    log.info("机器人{}的电梯<{}>守护任务已取消", robotId, bindingElevator.getEquipmentName());
                } else {
                    log.info("机器人{}的电梯<{}>守护任务不存在", robotId, bindingElevator.getEquipmentName());
                }
                // ③ 关闭 电梯守护线程池
                if (robotElevatorWatchTaskMap.isEmpty()) {
                    // 移除映射中该机器人电梯守护线程池
                    ScheduledExecutorService executor = robotElevatorThreadPollMap.remove(robotId);
                    // 关闭电梯守护线程池
                    cleanAssignedThreadPoll(robotId, executor, "电梯守护");
                }

                // ④ 解除 机器人与电梯的绑定关系
                if (null != robotElevatorControlledMap.remove(robotId)) {
                    log.info("解除机器人{}与电梯<{}>的绑定关系", robotId, bindingElevator.getEquipmentName());
                }
                // ⑤ 移除该电梯的多电梯运行状态管理器
                if (null != elevatorStatusMap.remove(bindingElevator.getEquipmentName())) {
                    log.info("移除电梯{}的运行状态管理器", bindingElevator.getEquipmentName());
                }
                // ⑥ 设置机器人状态机为 FREE 状态
                robotForElevatorFSMMap.put(robotId, RobotForElevatorFSM.FREE);
                log.info("机器人{}的状态机已切换为：[FREE]", robotId);

                break;

            default:
                break;
        }
    }

    /**
     * <连续步进导航区域管控处理逻辑: 基于传入的站点执行站点解锁与区域释放逻辑> leo add
     *
     * @param robotId 机器人ID
     * @param point   站点名称
     */
    private synchronized void unlockPointAndArea(String robotId, String point) {
        // 站点未被锁定
        if (lockedStations.get(point) == null) {
            // 不做任何操作
        }
        // 站点已被自己锁定
        else if (robotId.equals(lockedStations.get(point))) {
            /**
             *  <Step1: 根据是否为EL点执行相应的站点解锁逻辑>
             *
             *   ① 若为EL点，则执行EL点联动解锁逻辑
             *   ② 若不为EL点，则执行常规解锁逻辑
             */
            if (point.toUpperCase().startsWith("EL")) {
                /*
                 * ① 若为EL点，则解锁机器人绑定电梯的全部EL点
                 */

                // 根据机器人ID获取该机器人绑定的电梯
                IotEquipment elevatorByRobotId = robotElevatorControlledMap.get(robotId);
                if (elevatorByRobotId == null) {
                    log.error("机器人{}尚未绑定电梯，无法解锁关联EL点", robotId);
                } else {
                    // 筛选出当前电梯绑定的EL站点，用于联动解锁
                    Set<String> elPointsToUnlock = Arrays.stream(elevatorByRobotId.getAdjacentSite().split(","))
                            .filter(p -> p.toUpperCase().startsWith("EL"))
                            .collect(Collectors.toSet());

                    // 解锁当前机器人所在电梯的全部EL点
                    elPointsToUnlock.forEach(lockedStations::remove);
                }

            } else {
                // ② 若不为EL站点，则直接解锁该站点
                lockedStations.remove(point);
                // 刷新“机器人管理”Web页面的当前机器人锁定的站点信息
                refreshFrontEndByStationLock();
            }

            log.info("机器人{}已解锁站点{}", robotId, point);
            // 输出当前机器人此时锁定的全部站点
            log.info("机器人{}锁定的剩余站点：{}",
                    robotId,
                    getLockedPointsByRobot(robotId).stream().collect(Collectors.joining(", ", "[", "]"))
            );

            /**
             *  <Step2: 后解除区域>
             */

            // 根据机器人管控区域内的站点解锁情况，有条件地解除区域管控状态
            unlockAreaByPointStatus(robotId);
        }
        // 站点被其它机器人锁定
        else {
//            log.info("站点{}已被机器人{}锁定，机器人{}无法解锁", point, lockedStations.get(point), robotId);
        }
    }

    /**
     * <连续步进导航区域管控处理逻辑: 根据站点解锁情况执行区域释放逻辑> leo add
     *
     * @param robotId 机器人ID
     */
    private synchronized void unlockAreaByPointStatus(String robotId) {

        // 1. 根据机器人ID获取该机器人占用的管控区域
        List<RobotDmsEditor> currentRobotFocusAreas = robotDmsEditorService.findFocusAreasByRobotId(robotId);
        // 1.1 若该机器人未占用任何管控区域，则不做任何操作
        if (currentRobotFocusAreas.isEmpty()) {
            log.info("机器人{}未占用任何管控区域，无需释放", robotId);
            return;
        }

        /**
         * 1.2 <若该机器人占用了某些管控区域: 则遍历这些待解除区域并根据区域类型执行相应解除逻辑>
         *  ① 若占用的区域为基础管控区域，则执行常规解除逻辑
         *  ② 若占用的区域为自动门区域，执行常规区域解除逻辑，并执行自动门专用关门解绑等处理逻辑
         *  ③ 若占用的区域为电梯区域，执行常规区域解除逻辑，并执行电梯专用关门解绑等处理逻辑
         */
        for (RobotDmsEditor robotFocusArea : currentRobotFocusAreas) {

            /**
             * Ⅰ: <先执行通用区域解除逻辑: 适用基础管控区域、自动门区域、电梯区域>
             */

            // 获取该区域内的全部站点
            final String areaContainPointsJson = robotFocusArea.getAreaContainPoints();
            final List<String> areaPoints = JSON.parseArray(areaContainPointsJson, String.class);

            // 2. 检查该区域是否包含至少一个已锁定站点（即区域内的站点是否在站点锁管理器中）
            final boolean hasLockedStations = areaPoints.parallelStream()
                    .anyMatch(lockedStations::containsKey);

            if (!hasLockedStations) {
                try {
                    /*
                     * 2.1 若区域中无锁定点，则解除对该区域的管控状态：
                     *  a.设置区域占用状态为0；
                     *  b.将该区域的robotId设置为空
                     */
                    robotFocusArea.setOccupiedStatus(0).setRobotId("");
                    // 更新区域信息
                    if (robotDmsEditorService.saveOrUpdate(robotFocusArea)) {
                        log.info("解除区域[{}]占用状态成功 | 机器人:{}", robotFocusArea.getAreaName(), robotId);
                    } else {
                        log.error("区域状态更新失败 | 区域:{} | 机器人:{}", robotFocusArea.getAreaName(), robotId);
                    }
                } catch (Exception e) {
                    log.error("区域状态更新异常 | 区域:{} | 原因:{}", robotFocusArea.getAreaName(), e.getMessage());
                }

                /**
                 * Ⅱ: <再根据解除的区域类型执行物联设备特有逻辑>
                 *
                 *   Case1 解除区域为自动门区域时
                 *   Case2 解除区域为电梯区域时
                 */

                if (robotFocusArea.getAreaType() == RobotDmsEditor.AreaType.AUTO_DOOR) {
                    /**
                     * Case1 <若解除的为自动门区域: 执行关闭自动门及相关解绑逻辑>
                     *
                     *   ① 获取机器人实际要执行操作的自动门
                     *   ② 关闭该机器人开门定时任务并移除对应线程
                     *   ③ 为该机器人创建新的关门任务轮询线程池
                     *   ④ 开启关门定时任务，并监听门状态
                     *   ⑤ 关门后关闭关门定时任务并移除，并解绑该机人操作的自动门
                     */

                    // ① 获取实际要执行操作的自动门
                    // 定义待操作的目标自动门
                    final IotEquipment autoMaticDoorToClose;
                    // 通过该机器人ID获取该机器人管控的自动门集合
                    Set<IotEquipment> doorSetByRobotId = robotDoorControlledMap.get(robotId);
                    if (doorSetByRobotId == null || doorSetByRobotId.isEmpty()) {
                        // 若未管控，跳过后续逻辑
                        continue;
                    }
                    if (doorSetByRobotId.size() == 1) {
                        // 若该机器人管控了 1 个自动门，则直接获取
                        autoMaticDoorToClose = doorSetByRobotId.iterator().next();
                    } else {
                        // 若该机器人同时管控了 2 个或以上自动门（实际场景最多为2），则先筛选符合条件的自动门
                        autoMaticDoorToClose = doorSetByRobotId.stream()
                                // 过滤出符合条件的唯一自动门
                                .filter(automaticDoor -> {
                                    // 获取自动门绑定点
                                    List<String> doorPoints = Arrays.asList(automaticDoor.getAdjacentSite().split(","));
                                    // 对比区域包含关系
                                    return new HashSet<>(areaPoints).containsAll(doorPoints);
                                })
                                .findFirst()
                                .orElse(null);
                    }
                    if (autoMaticDoorToClose == null) {
                        // 管控的自动门均为非解除区域的门时
                        log.info("机器人{}没有需要关闭的自动门", robotId);
                        continue;
                    }
                    // ② 关闭机器人的指定自动门开门定时任务，并根据条件自动关闭对应线程池
                    cleanupThreadsForAutomaticDoor(robotId, autoMaticDoorToClose);
                    // 获取门的当前开关状态
                    int doorOpenedStatus = modbusGenericRw(autoMaticDoorToClose, IotEquipment.ModbusRegisterType.READ_HOLDING_REGISTER, null).getValue();
                    if (doorOpenedStatus != IotEquipment.AutomaticDoorOpenedStatus.AUTOMATIC_DOOR_OPEN) {
                        // 若门已关，则跳过后续步骤
                        continue;
                    }

                    ScheduledFuture<?> doorCloseScheduledFuture = robotDoorCloseTaskMap.get(robotId);
                    // （防御）若该机器人关门任务正在进行中，则跳过后续逻辑
                    if (doorCloseScheduledFuture != null && !doorCloseScheduledFuture.isDone()) {
                        log.info("机器人{}的关门任务仍在运行，跳过新关门任务创建", robotId);
                        continue;
                    }

                    // ③ 为该机器人创建新的关门任务轮询线程池，用于启动关门定时任务
                    ScheduledExecutorService executor = robotDoorCloseCheckThreadMap.computeIfAbsent(robotId,
                            k -> Executors.newSingleThreadScheduledExecutor(new DynamicThreadFactory(robotId, "closeDoor")));

                    // ④ 创建新的关门定时任务
                    ScheduledFuture<?> doorClosedScheduledFuture = executor.scheduleWithFixedDelay(() -> {
                                closeAutomaticDoorByRobot(robotId, autoMaticDoorToClose);
                            }, 0, 2000, TimeUnit.MILLISECONDS
                    );
                    // 关门定时任务加入管理器
                    robotDoorCloseTaskMap.putIfAbsent(robotId, doorClosedScheduledFuture);
                }

                if (robotFocusArea.getAreaType() == RobotDmsEditor.AreaType.ELEVATOR) {
                    /**
                     * Case2 <若解除的为电梯区域>
                     *  判断该机器人占用的电梯区域是否为空（跨楼层）
                     *    ·若为空，则执行电梯专用解除逻辑，状态机设置为“已经离开电梯”
                     *    ·若不为空，则跳过此轮循环
                     */
                    // 获取该机器人管控的电梯区域
                    List<RobotDmsEditor> areasByRobotIdAndAreaType = robotDmsEditorService.findAreasByRobotIdAndAreaType(robotId, RobotDmsEditor.AreaType.ELEVATOR);

                    // 若该机器人管控的电梯区域为空，则执行电梯专用解除逻辑
                    if (areasByRobotIdAndAreaType.isEmpty()) {
                        // 设置机器人乘梯状态机为“已经离开电梯”
                        robotForElevatorFSMMap.put(robotId, RobotForElevatorFSM.LEAVE_ELEVATOR);
                        log.info("机器人{}已解除全部电梯区域的管控，状态机设置为<已经离开电梯>", robotId);
                    }
                }

            } else {
                // 2.2 若包含，则不做任何操作
                log.info("机器人{}暂不具备解除区域:{}的条件 | 域内剩余锁定站点数:{}",
                        robotId,
                        robotFocusArea.getAreaName(),
                        areaPoints.stream().filter(lockedStations::containsKey).count());
            }
        }
    }

    /**
     * <关闭自动门> leo add
     *
     * @param robotId              机器人ID
     * @param autoMaticDoorToClose 待关闭的自动门
     */
    private void closeAutomaticDoorByRobot(String robotId, IotEquipment autoMaticDoorToClose) {
        try {
            // 1.检查待关闭自动门的开启状态
            int doorStatus = modbusGenericRw(autoMaticDoorToClose, IotEquipment.ModbusRegisterType.READ_HOLDING_REGISTER, null).getValue();
            // 1.1 若自动门处于开启状态
            if (doorStatus == IotEquipment.AutomaticDoorOpenedStatus.AUTOMATIC_DOOR_OPEN) {
                log.info("自动门 <{}> 处于开启状态，机器人 {} 正在关闭该自动门", autoMaticDoorToClose.getEquipmentName(), robotId);
                // 下发关门指令
                if (!modbusGenericRw(autoMaticDoorToClose, IotEquipment.ModbusRegisterType.WRITE_HOLDING_REGISTER,
                        IotEquipment.AutomaticDoorOpenedStatus.AUTOMATIC_DOOR_CLOSE).isSuccess()) {
                    log.error("机器人 {} 尝试下发给自动门 <{}> 关门指令，但下发指令失败", robotId, autoMaticDoorToClose.getEquipmentName());
                }
            }
            //1.2 若自动门处于关闭状态
            else {
                // 设置数据库的门状态为已关闭
                autoMaticDoorToClose.setDoorOpenedStatus(2);
                if (ioTEquipmentService.saveOrUpdate(autoMaticDoorToClose)) {
                    log.info("机器人 {} 已关闭自动门：<{}>，且更新了数据库状态", robotId, autoMaticDoorToClose.getEquipmentName());
                }

                // 1. 关闭机器人关门定时任务
                ScheduledFuture<?> future = robotDoorCloseTaskMap.remove(robotId);
                if (future != null) {
                    // 立即中断任务执行
                    future.cancel(true);
                    log.info("机器人{}的自动门<{}>关门任务已取消", robotId, autoMaticDoorToClose.getEquipmentName());
                } else {
                    log.warn("机器人{}的自动门<{}>关门任务不存在", robotId, autoMaticDoorToClose.getEquipmentName());
                }
                // 2. 检查是否需要关闭关门线程池（当关门任务完成时）
                if (robotDoorCloseTaskMap.isEmpty()) {
                    // 移除机器人的关门线程池
                    ScheduledExecutorService executor = robotDoorCloseCheckThreadMap.remove(robotId);
                    // 关闭线程池
                    cleanAssignedThreadPoll(robotId, executor, "自动门");
                }

                // 解绑机器人正在管控自动门集合中完成任务的自动门
                try {
                    // 获取机器人对应的自动门集合（允许返回null）
                    Set<IotEquipment> doorSet = robotDoorControlledMap.get(robotId);
                    log.info("机器人{}当前绑定的自动门<{}>", robotId, doorSet);
                    // 检查集合是否存在
                    if (doorSet == null) {
                        log.info("机器人{}解绑正在管控的自动门<{}>失败", robotId, autoMaticDoorToClose.getEquipmentName());
                    } else {
                        // 解绑集合中的指定自动门
                        boolean removed = doorSet.remove(autoMaticDoorToClose);
                        if (removed) {
                            log.info("机器人{}解绑正在管控的自动门<{}>成功", robotId, autoMaticDoorToClose.getEquipmentName());
                            // 集合为空后自动清理键值对
                            if (doorSet.isEmpty()) {
                                robotDoorControlledMap.remove(robotId);
                            }
                        } else {
                            log.info("机器人{}解绑正在管控的自动门 <{}> 失败", robotId, autoMaticDoorToClose.getEquipmentName());
                        }
                    }
                } catch (Exception e) {
                    log.error("机器人{}解绑正在管控的自动门<{}>时出现错误，错误原因：{}",
                            robotId, autoMaticDoorToClose.getEquipmentName(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("机器人{}尝试下发给自动门 {} 关门指令，但下发指令失败", robotId, autoMaticDoorToClose.getEquipmentName());
        }
    }

    /**
     * <关闭指定机器人的指定自动门线程> leo add
     * *** 若Map为空，触发自清理机制 ***
     *
     * @param robotId              机器人ID
     * @param toShutDownThreadDoor 要关闭的自动门对象
     */
    private void cleanupThreadsForAutomaticDoor(String robotId, IotEquipment toShutDownThreadDoor) {
        String doorId = toShutDownThreadDoor.getId();

        Map<String, ScheduledFuture<?>> doorMap = robotDoorTaskMap.get(robotId);
        if (doorMap == null) {
            log.warn("机器人{}没有注册的自动门任务", robotId);
            return;
        }

        // 1. 关闭指定门任务（按加入顺序关闭最老任务）
        ScheduledFuture<?> future = doorMap.remove(doorId);
        if (future != null) {
            // 立即中断任务执行
            future.cancel(true);
            log.info("机器人{}的自动门<{}>开门任务已取消", robotId, toShutDownThreadDoor.getEquipmentName());
        } else {
            log.warn("机器人{}的自动门<{}>开门任务不存在", robotId, toShutDownThreadDoor.getEquipmentName());
        }

        // 2. 检查是否需要关闭线程池（当所有门任务都完成时）
        if (doorMap.isEmpty()) {
            robotDoorTaskMap.remove(robotId);
            // 移除线程池
            ScheduledExecutorService executor = robotDoorThreadPollMap.remove(robotId);
            // 关闭线程池
            cleanAssignedThreadPoll(robotId, executor, "自动门");
        }
    }

    /**
     * <关闭指定机器人的线程池> leo add
     *
     * @param robotId  机器人ID
     * @param executor 要关闭的线程池
     * @param type     线程池类型
     */
    private void cleanAssignedThreadPoll(String robotId, ScheduledExecutorService executor, String type) {
        if (executor != null) {
            // 优雅关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    log.info("机器人{}的{}线程池已强制关闭", robotId, type);
                } else {
                    log.info("机器人{}的{}线程池已正常关闭", robotId, type);
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                log.error("线程池关闭中断异常", e);
            }
        }
    }

    /**
     * <关闭自动门的特定任务> leo add
     *
     * @param robotId         机器人ID
     * @param toCloseTaskDoor 待关闭任务的自动门
     */
    public void stopDoorTask(String robotId, IotEquipment toCloseTaskDoor) {
        Map<String, ScheduledFuture<?>> doorTasks = robotDoorTaskMap.get(robotId);
        if (doorTasks == null) return;

        ScheduledFuture<?> future = doorTasks.remove(toCloseTaskDoor.getId());
        if (future != null) {
            future.cancel(true); // 中断正在执行的任务

            // 清理空Map
            if (doorTasks.isEmpty()) {
                robotDoorTaskMap.remove(robotId);
                ScheduledExecutorService executor = robotDoorThreadPollMap.remove(robotId);
                shutdownExecutor(executor, robotId);
            }
        }
    }

    /**
     * <关闭机器人的所有自动门任务> leo add
     *
     * @param robotId 机器人ID
     */
    //
    public void cleanupRobotDoorTasks(String robotId) {
        Map<String, ScheduledFuture<?>> doorTasks = robotDoorTaskMap.remove(robotId);
        if (doorTasks != null) {
            doorTasks.values().forEach(future -> future.cancel(true));
        }

        ScheduledExecutorService executor = robotDoorThreadPollMap.remove(robotId);
        shutdownExecutor(executor, robotId);
    }

    private void shutdownExecutor(ScheduledExecutorService executor, String robotId) {
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * <基于区域及站点占用情况处理2个步进的任务调度> leo add
     *
     * @param robotId   机器人名称
     * @param taskQueue 该机器人的任务队列
     * @param waybillId 运单ID
     * @return 该机器人的前两个步进任务
     */
    private List<OneStepNavTaskDTO> dispatchFirstAndSecondTask(String robotId, Deque<OneStepNavTaskDTO> taskQueue, String waybillId) {
        // 获取读锁（等待全局更新完成）
        globalLock.readLock().lock();
//        log.info("机器人{}获取读锁two", robotId);

        try {
            // 初始化运行任务列表
            List<OneStepNavTaskDTO> initialTasks = new ArrayList<>(2);
            // 获取待检查任务列表
            List<OneStepNavTaskDTO> candidateTasks = taskQueue.stream()
                    .limit(2)
                    .collect(Collectors.toList());
            /* ————————————————————————————防御ABA问题处理逻辑开始———————————————————————————— */
            /**
             * <防御ABA问题: 检查当前机器人是否处于ABA问题状态>
             * (方法内已天然满足 剩余步进任务队列长度 ≥ 2)
             * 若还满足如下条件：
             *  ① 第一个步进任务的起点 ≠ 第一个步进任务的终点
             *  ② 第一个步进任务的起点 = 第二个步进任务的终点
             *
             * 则说明当前机器人处于ABA问题状态，把ABA状态标记置为 true，并进行如下操作：
             *  ① 若ABA标记为true，尝试下发一步任务
             *  ② 并补充控制器镜像队列为 1 个任务
             */

            // 定义是否处于ABA问题状态的标志
            boolean isABAProblem = !candidateTasks.get(0).getStartPoint().equals(candidateTasks.get(0).getEndPoint())
                    && candidateTasks.get(0).getStartPoint().equals(candidateTasks.get(1).getEndPoint());
            // 若处于ABA问题状态，则尝试下发一步任务
            if (isABAProblem) {
                log.info("机器人{}处于ABA问题状态，由原本的下发二步任务改为尝试下发一步任务[{}→{}]", robotId, candidateTasks.get(0).getStartPoint(), candidateTasks.get(0).getEndPoint());
                // 尝试锁定目标站点
                if (lockAreasAndPoints(robotId, Collections.singletonList(candidateTasks.get(0).getEndPoint()), waybillId)) {
                    // 若锁定成功，补充镜像列表为 1 个任务，并下发一步任务
                    initialTasks.add(taskQueue.poll());
                    EntityResult result = sendAssignedNavCommand(robotId, initialTasks);
                    if (result.isSuccess()) {
                        log.info("机器人{}下发ABA状态下的任务[{}→{}]成功", robotId, initialTasks.get(0).getStartPoint(), initialTasks.get(0).getEndPoint());
                    }
                } else {
                    log.info("机器人{}即将执行的ABA状态步进任务{}→{}的中存在被其它机器人占用的区域或站点，暂缓下发", robotId, candidateTasks.get(0).getStartPoint(), candidateTasks.get(0).getEndPoint());
                }
            }
            /* ————————————————————————————防御ABA问题处理逻辑结束———————————————————————————— */

            /* ————————————————————————————防御原地导航虚拟路径逻辑开始———————————————————————————— */
            /**
             * <防御原地导航虚拟任务: 若剩余任务队列的第一个路径为原地导航虚拟路径，则只下发一步任务>
             *
             * · 若剩余任务队列的第一个路径的起点和终点相同，则说明该路径为原地导航虚拟路径
             * · 此时只下发一步步进任务：即原地导航虚拟任务
             */
            else if (candidateTasks.get(0).getStartPoint().equals(candidateTasks.get(0).getEndPoint())) {
                log.info("机器人{}的第一个步进任务是原地虚拟导航任务，由原本的下发二步任务改为下发一步任务[{}→{}]", robotId, candidateTasks.get(0).getStartPoint(), candidateTasks.get(0).getEndPoint());
                // 补充镜像列表为 1 个原地导航虚拟任务
                initialTasks.add(taskQueue.poll());
            }
            /* ————————————————————————————防御原地导航虚拟路径逻辑结束———————————————————————————— */

            // 若不处于ABA问题状态且不存在原地导航虚拟路径，则正常执行前两步任务下发逻辑
            else {
                // 提取需要检查锁定的站点集合
                List<String> endpoints = candidateTasks.stream()
                        .map(OneStepNavTaskDTO::getEndPoint)
                        .collect(Collectors.toList());

                // 进行区域可用性检查及原子性批量锁定检查，若全部检查通过，则执行管控和锁定，并返回true，否则返回false
                boolean allEndpointsLocked = lockAreasAndPoints(robotId, endpoints, waybillId);

                // 批量下发符合条件的前两个任务，否则暂缓下发
                if (allEndpointsLocked) {
                    if (candidateTasks.size() == 2) {
                        // 取出前两个任务并加入运行任务列表
                        initialTasks.addAll(Stream.generate(taskQueue::poll).limit(2).collect(Collectors.toList()));
                        if (!initialTasks.isEmpty()) {
                            EntityResult result = sendAssignedNavCommand(robotId, initialTasks);
                            // 若两个步进任务中至少有一个原地等待
                            if (result.getCode().equals("000")) {
                                log.info("****** 为避让其它机器人，机器人{}将在点{}原地等待{}秒 ******", robotId, initialTasks.get(0).getEndPoint(), initialTasks.get(0).getTotalCurveLength());
                            } else if (result.isSuccess()) {
                                log.info("机器人{}下发2个连续步进任务成功", robotId);
                            }
                        }
                    }
                } else {
                    log.info("机器人{}即将执行的前{}个任务中存在被其它机器人占用的区域或站点，暂缓下发", robotId, candidateTasks.size());
                }
            }

            return initialTasks;

        } finally {
            // 释放读锁
            globalLock.readLock().unlock();
//            log.info("机器人{}释放读锁two", robotId);
        }
    }

    /**
     * <基于防御规则发送指定路径导航指令> leo add
     *
     * @param robotId 机器人名称
     * @param tasks   步进任务列表
     * @return 响应结果
     */
    private EntityResult sendAssignedNavCommand(String robotId, List<OneStepNavTaskDTO> tasks) {
        EntityResult response = new EntityResult();
        // 根据robotId获取机器人IP
        String robotIp = robotBasicInfoService.findByVehicleId(robotId).getCurrentIp();
        // 1. 构建MoveTaskListDTO对象
        List<MoveTaskListDTO.MoveTaskItem> moveTaskItems = new ArrayList<>();
        // 是否需要原地等待
        boolean isNeedWaitInPlace = false;

        // 遍历任务列表生成连续路径
        for (OneStepNavTaskDTO currentTask : tasks) {
            /**
             * <防御原地导航虚拟任务: 即起点和终点相同，则跳过此轮循环，不构建实际指令>
             */
            if (currentTask.getStartPoint().equals(currentTask.getEndPoint())) {
                // 则需要原地等待
                isNeedWaitInPlace = true;
                // 直接跳过当前循环的后续处理（可能导致指令为空，在后面做防御）
                continue;
            }
            /**
             * <防御电梯虚拟任务: 即起点和终点均以EL开头，则跳过此轮循环，不构建实际指令>
             */
            if (currentTask.getStartPoint().toUpperCase().startsWith("EL") && currentTask.getEndPoint().toUpperCase().startsWith("EL")) {
                continue;
            }

            // 生成路径节点（每个节点的source_id是前序节点的id）
            MoveTaskListDTO.MoveTaskItem item = MoveTaskListDTO.MoveTaskItem.builder()
                    // 步进终点
                    .id(currentTask.getEndPoint())
                    // 步进起点
                    .sourceId(currentTask.getStartPoint())
                    // 步进任务ID（唯一）
                    .taskId(currentTask.getTaskId())
                    .build();
            // 加入路径节点列表
            moveTaskItems.add(item);
        }

        // （防御）若 moveTaskItems 为空，则说明需要原地等待，不进行指令下发，跳过方法的后续逻辑
        if (moveTaskItems.isEmpty()) {
            return response.setSuccess(true).setCode("000");
        }

        // 构建完整DTO对象
        MoveTaskListDTO moveTaskListDTO = MoveTaskListDTO.builder()
                .moveTaskList(moveTaskItems)
                .build();

        // 2. 生成JSON字符串
        String moveTaskListJsonString = JSON.toJSONString(moveTaskListDTO);

        /*
         * 3. 构建并发送指令
         */
        String navCommandHex = DataConvertUtil.convertStringToHex(moveTaskListJsonString);
        // 报文长度：实际传输或处理时，数据是以字节为单位的。length()方法返回的是字符数（4位），而一个字节是八位，占两个字符，所以除以 2
        int byteLength = navCommandHex.length() / 2;
        // 格式化报文长度为16进制字符串，长度为4位
        String navCommandLength = String.format("%04x", byteLength);
        // 构建导航指令
        String navInstruction = "5A0100010000" + navCommandLength + "0BFA000000000000" + navCommandHex;
        // 若未连接到该ip端口，则尝试连接
        Map<String, Thread> IpThreadNettyMap = TcpClientThread.getIpThreadNetty();
        if (!IpThreadNettyMap.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }

        // 向该ip端口发送tcp指令
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, navInstruction);
            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                if (tasks.size() == 2) {
                    log.info("机器人{}导航指令下发成功:由{}到{}再到{}", robotId,
                            tasks.get(0).getStartPoint(),
                            tasks.get(0).getEndPoint(),
                            tasks.get(1).getEndPoint());
                } else if (tasks.size() == 1) {
                    log.info("机器人{}导航指令下发成功:由{}到{}", robotId, tasks.get(0).getStartPoint(), tasks.get(0).getEndPoint());
                } else {
                    log.info("机器人{}导航队列数量异常", robotId);
                }
                break;
            }
            if (retryCount >= 60) {
                log.error("机器人{}导航指令下发失败，重试次数达到最大值{}", robotId, retryCount + 1);
                break;
            }
            log.info("机器人{}接收导航指令失败，1秒后重试...重试次数:{}", robotId, retryCount + 1);
            // 等待1秒后重试
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

        // 若指令中有至少一个任务需要原地等待，则设置响应码为000，表示需要原地等待
        if (isNeedWaitInPlace) {
            response.setCode("000");
        }

        return response;
    }


    /**
     * ********* <轮询机器人操作> leo add *********
     * 1.获取机器人相关状态信息
     * 1.1 获取控制器镜像队列（运行任务列表）
     * 1.2 获取剩余任务队列
     * 1.3 更新机器人任务状态
     * <p>
     * 2.根据机器人运行状态执行相应逻辑
     * 2.1 根据机器人当前任务状态，判断是否需要解锁站点
     * 2.1.1 若当前状态小于解锁阈值，则不做处理，等待下次轮询
     * 2.1.2 若当前状态大于解锁阈值，则解锁当前任务的起始站点
     * <p>
     * 2.2 判断机器人当前任务状态是否已完成，然后根据控制器镜像队列和剩余任务队列的长度分类讨论
     * 2.2.1 首先判断虚拟任务（起始点相同的情况）
     * 2.2.2 防御因【步进任务完成时间＜轮询间隔】导致的站点未及时解锁
     * 2.2.3 若当前任务已完成，且控制器镜像队列长度=2
     * 2.2.4 若当前任务已完成，且控制器镜像队列长度=1
     * <p>
     * 2.3 控制器镜像队列为空 （此时运行任务全部完成）
     * 2.3.1 若剩余任务队列≥2
     * 2.3.1.1 尝试下发两步任务，并补充运行任务列表为2个任务
     * 2.3.1.2 若已成功下发和补充
     * 2.3.1.3 若未成功下发和补充
     * <p>
     * 2.3.2 若剩余任务队列=1，尝试下发最后一步任务
     * <p>
     * 2.3.3 若剩余任务队列为空，终止轮询线程并清空任务队列
     *
     * @param robotId         机器人名称
     * @param safetyStopPoint 安全停止点（此线程中外部下发任务的终点作为安全停止点）
     */
    private void pollRobotOperate(String robotId, String safetyStopPoint, String waybillId) {

        /**
         * <Step1: 获取机器人相关状态信息>
         */

        // 获取控制器镜像队列和剩余任务队列
        List<OneStepNavTaskDTO> runningTasks = runningTaskMap.getOrDefault(robotId, new ArrayList<>(2));
        Deque<OneStepNavTaskDTO> remainingTasks = navTaskQueueMap.get(robotId);

        String runningTasksString = runningTasks.isEmpty() ? "【控制器镜像队列为空】" :
                runningTasks.stream()
                        .map(task -> task.getStartPoint() + "→" + task.getEndPoint())
                        .collect(Collectors.joining("； "));

        log.info("*** 机器人{}当前控制器镜像队列：【{}】 ***", robotId, runningTasksString);

        // 若机器人步进任务队列为 null，则报错并跳过该次轮询
        if (remainingTasks == null) {
            log.error("机器人{}步进任务队列为null，请检查任务队列更新是否出现异常", robotId);
            return;
        }

        // 查询机器人任务状态
        EntityResult result = queryTaskStatus(robotId);
        if (!result.isSuccess()) {
            log.error("查询机器人{}任务状态失败", robotId);
            // 若查询失败，跳过该次轮询
            return;
        }
        // 若查询成功，线程等待 0.5 秒，等待任务状态更新
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 获取机器人最新任务状态
        RobotTaskStatusDTO taskStatusDTO = taskStatusMap.get(robotId);

        // 若 taskStatusDTO 或其子成员对象 taskStatusPackage 为 null，则直接跳过该次轮询
        if (taskStatusDTO == null || taskStatusDTO.getTaskStatusPackage() == null) {
            return;
        }

        // ——————————————————————————————————————————“ 状态获取 / 任务处理 ”分割线————————————————————————————————————————————

        // 判断当前正在运行的步进任务是否为原地导航虚拟任务
        boolean isVirtualTask = false;
        if (!runningTasks.isEmpty()) {
            isVirtualTask = runningTasks.get(0).getStartPoint().equals(runningTasks.get(0).getEndPoint());
        }


        /**
         * <Step2: 根据机器人运行状态执行相应逻辑>
         */

        /*
         * 1.根据机器人任务状态解锁站点（非虚拟任务）
         *
         * · 由于电梯虚拟任务不入控制器，因此第三个条件天然不满足，不会进入此分支，原因：
         *      · 控制器没有正在运行的队列时sourceName显示最后一个已完成步进任务的起点，不可能和电梯虚拟任务的起点相同
         *
         * 1.1 若当前状态小于解锁阈值，则不做处理，等待下次轮询
         * 1.2 若当前状态大于解锁阈值，则解锁当前任务的起始站点
         */
        if (!isVirtualTask && !runningTasks.isEmpty() && taskStatusDTO.getTaskStatusPackage().getSourceName().equals(runningTasks.get(0).getStartPoint())) {
            // 获取控制器镜像队列第一个步进任务的起始站点
            String startPoint = runningTasks.get(0).getStartPoint();
            // 打印进度信息，非虚拟任务
            log.info("机器人{}步进任务【{}→{}】已完成{}%", robotId,
                    startPoint,
                    runningTasks.get(0).getEndPoint(),
                    (int) (taskStatusDTO.getTaskStatusPackage().getPercentage() * 100));
            // 若进度达到解锁阈值时，有被自己锁定的站点，则尝试解锁(避免重复解锁)
            if (taskStatusDTO.getTaskStatusPackage().getPercentage() > STATION_UNLOCK_THRESHOLD &&
                    lockedStations.entrySet().stream().anyMatch(entry -> startPoint.equals(entry.getKey()) && robotId.equals(entry.getValue()))) {
                log.info("机器人{}步进任务{}：【{}→{}】已完成{}%，尝试解锁站点{}", robotId,
                        runningTasks.get(0).getTaskId(),
                        startPoint,
                        runningTasks.get(0).getEndPoint(),
                        (int) (taskStatusDTO.getTaskStatusPackage().getPercentage() * 100),
                        startPoint);
                // 解锁当前任务的起始站点并尝试释放占用的管控区域
                unlockPointAndArea(robotId, startPoint);
            }
        }

        /*
         * 2.判断机器人当前步进任务状态是否已完成，然后根据控制器镜像队列 runningTasks 和剩余任务队列 remainingTasks 的长度分类讨论
         */
        if (!runningTasks.isEmpty()) {
            // 定义控制器当前步进任务是否已完成的标志
            boolean isCurrentStepTaskCompleted = false;

            // 若为虚拟任务
            /**
             * 2.1 <原地导航虚拟任务处理 - 当步进任务起末点相同时>
             * ① 起末点相同时未真正下发步进任务给控制器，因此无法通过任务id判断完成状态
             * ② 若当前控制器镜像队列中的第一个任务是虚拟任务，则通过线程等待模拟虚拟任务执行时长
             * ③ 模拟执行时长后，将当前任务完成状态更新为已完成
             * ④ 若当前控制器镜像队列中的第一个任务不是虚拟任务，则不做处理
             */
            if (isVirtualTask) {
                try {
                    // 根据模拟执行时长进行停留
                    log.info("*** 为避让其它机器人，机器人{}将在{}站点等待{}秒 ***", robotId,
                            runningTasks.get(0).getStartPoint(), 0.7 * runningTasks.get(0).getTotalCurveLength());
                    TimeUnit.MILLISECONDS.sleep((long) (0.7 * 1000 * runningTasks.get(0).getTotalCurveLength()));
//                    TimeUnit.MILLISECONDS.sleep(5000);
                    log.info("机器人{}已在{}站点等待结束", robotId, runningTasks.get(0).getStartPoint());
                } catch (InterruptedException e) {
                    log.error("线程休眠时发生中断", e);
                    // 重置中断状态
                    Thread.currentThread().interrupt();
                }
                // 模拟完成后，更新当前任务完成状态为已完成
                isCurrentStepTaskCompleted = true;
            } else {
                // 若是真实任务，则通过任务id判断完成状态（任务为电梯虚拟任务时也会进入此分支，但由于找不到匹配的任务id，一定会返回false，符合需求）
                isCurrentStepTaskCompleted = judgeStepTaskCompleted(taskStatusDTO, runningTasks.get(0));
            }

            // 若当前步进任务已完成
            if (isCurrentStepTaskCompleted) {
                // 非虚拟任务时，进行防御性站点解锁和区域释放
                if (!isVirtualTask) {
                    /**
                     * 2.2.2 <防御因 | 步进任务完成时间 ＜ 轮询间隔 | 导致的站点未及时解锁问题>>
                     * ① 若是虚拟任务，则不解锁（否则会使得原地解锁，导致放行其它机器人试图抵达该点，从而识别为路障）
                     * ② 若已解锁，方法内部不做处理
                     * ③ 若未解锁，则方法尝试解锁当前任务的起始站点并释放占用的管控区域
                     */
                    unlockPointAndArea(robotId, runningTasks.get(0).getStartPoint());

                    // 若当前 已完成 步进任务的起点不为EL点且终点为EL点，则状态机设置为 等待电梯运行 状态
                    if (!runningTasks.get(0).getStartPoint().toUpperCase().startsWith("EL") && runningTasks.get(0).getEndPoint().toUpperCase().startsWith("EL")) {
                        robotForElevatorFSMMap.put(robotId, RobotForElevatorFSM.WAIT_ELEVATOR_RUNNING);
                    }
                }

                // 2.2.3 若当前步进任务已完成，且控制器镜像队列长度=2 （能进入此次轮询，说明补充任务的结束站点未被其它机器人占用）
                if (runningTasks.size() == 2) {
                    // 如果完成，移除控制器镜像队列runningTasks中的第一个任务
                    OneStepNavTaskDTO completedTask = runningTasks.remove(0);

                    log.info("机器人{}的步进任务{}已完成：已从{}抵达{}", robotId, completedTask.getTaskId(), completedTask.getStartPoint(), completedTask.getEndPoint());
                    if (runningTasks.size() == 1) {
                        log.info("机器人{}的剩余控制器镜像队列：【{}】", robotId, runningTasks.get(0).getStartPoint() + "→" + runningTasks.get(0).getEndPoint());
                    }

                    // 检查剩余任务队列是否为空
                    if (!remainingTasks.isEmpty()) {
                        // 若不为空，则基于站点占用情况处理任务调度
                        handleTaskDispatchingByOccupation(robotId, runningTasks, remainingTasks, waybillId);
                    }
                }
                // 2.2.4 若当前步进任务已完成，且控制器镜像队列长度=1 （此步骤不做复杂逻辑处理，等到下一个轮询由2.3处理）
                else if (runningTasks.size() == 1) {
                    // 如果完成，移除控制器镜像队列runningTasks中仅存的一个步进任务
                    OneStepNavTaskDTO completedTask = runningTasks.remove(0);
                    log.info("机器人{}的步进任务{}已完成：已从{}抵达{}，控制器镜像队列已无步进任务，等待补充...此时剩余任务队列长度为{}",
                            robotId,
                            completedTask.getTaskId(),
                            completedTask.getStartPoint(),
                            completedTask.getEndPoint(),
                            remainingTasks.size());
                }
            }
        }
        // 2.3 控制器镜像队列为空 （此时运行任务全部完成）
        else {
            if (remainingTasks.isEmpty()) {
                /**
                 * <step3: 终止条件:剩余队列为空且运行任务为空>
                 *
                 * ---传入安全停止点---
                 */
                stopRobotPollingAndFlushQueues(robotId, safetyStopPoint);
            } else {
                /* ————————————————————————————相向而行情形1处理逻辑开始———————————————————————————— */
                /**
                 * *** <处理相向而行的逻辑1: 2个机器人相邻，且剩余任务队列中携带了至少1个步进任务> ***
                 * 1. 若相向而行，控制器镜像队列经过若干次轮询后必然会为空
                 * 2. 此时要确定是否相向而行，只需判断多机器人步进任务队列管理器中是否存在某个机器人与当前机器人互为反向步进任务：即是否存在某个机器人的第一个步进任务的起点、终点正好和当前机器人步进任务队列的第一个步进任务的起点、终点相反
                 * 3. 若存在，则调用 updateTaskQueueMap() 方法重新请求路径规划算法，更新该机器人的步进任务队列
                 * 4. 等待下次轮询，若重新规划后的路径合理，则不会再进入此逻辑
                 */

                // 获取携带任务的其它机器人名称
                List<String> otherRobotIds = navTaskQueueMap.keySet().stream()
                        .filter(key -> !key.equals(robotId))
                        .collect(Collectors.toList());

                // 获取当前机器人剩余步进任务队列的第一个任务
                OneStepNavTaskDTO currentFirstTask = remainingTasks.peekFirst();
                // 定义是否存在相向而行情形1的机器人标志
                boolean foundOpposite = false;
                // 遍历所有其它机器人任务队列
                for (String otherRobotId : otherRobotIds) {
                    // 排除当前机器人自己
                    if (otherRobotId.equals(robotId)) {
                        continue;
                    }
                    Deque<OneStepNavTaskDTO> otherOneStepTasks = navTaskQueueMap.get(otherRobotId);
                    // 检查其它机器人任务队列是否非空
                    if (otherOneStepTasks != null && !otherOneStepTasks.isEmpty()) {
                        // 获取其它机器人剩余步进任务队列的第一个任务
                        OneStepNavTaskDTO otherFirstTask = otherOneStepTasks.peekFirst();

                        // 关键条件判断：互为反向路径，存在相向而行的机器人则将标记置为true
                        if (otherFirstTask.getEndPoint().equals(currentFirstTask.getStartPoint())
                                && otherFirstTask.getStartPoint().equals(currentFirstTask.getEndPoint())) {
                            /*
                             * 防御存在机器人还有两个步进任务未执行完成就提前规划新路径的情况
                             */

                            // 与当前机器人互为反向路径的机器人完全静止，则改变标记
                            if (!runningTaskMap.containsKey(otherRobotId)) {
                                foundOpposite = true;
                            }
                            // 与当前机器人互为反向路径的机器人未完全静止但控制器中仅剩一个步进任务未执行完成，则改变标记
                            else {
                                if (runningTaskMap.get(otherRobotId).size() <= 1) {
                                    foundOpposite = true;
                                }
                            }

                            log.info("机器人{}步进任务【{}→{}】与机器人{}步进任务【{}→{}】互为反向路径，尝试通过重新请求路径规划算法更新任务队列",
                                    robotId,
                                    currentFirstTask.getStartPoint(),
                                    currentFirstTask.getEndPoint(),
                                    otherRobotId,
                                    otherFirstTask.getStartPoint(),
                                    otherFirstTask.getEndPoint());
                            // 找到即跳出循环
                            break;
                        }
                    }
                }
                // 发现相向而行的机器人则通过重新请求路径规划算法更新步进任务队列
                if (foundOpposite) {
                    updateTaskQueueMap(waybillId);
                }
                /* ————————————————————————————相向而行情形1处理逻辑结束———————————————————————————— */

                // ① 剩余任务队列≥2
                if (remainingTasks.size() >= 2) {
                    /* ————————————————————————————相向而行情形2处理逻辑开始———————————————————————————— */
                    /**
                     * *** <处理相向而行的逻辑2: 2个机器人不相邻，都已停止移动，仅间隔一个站点，且同时请求了路径规划算法> ***
                     * 特别说明：
                     * ① 同时请求路径规划算法时，算法会认为当前场景中仅有1台涉事机器人，因此可能会出现2个机器人的前两个步进任务互相重叠的情况；
                     * ② 若重叠，则涉事的2个机器人的前两个步进任务一定互为反向任务，即机器人R1的步进1和机器人R2的步进2互为反向任务，机器人R1的步进2和机器人R2的步进1互为反向任务；
                     * ③ 此时两台机器人的前三个待锁定站点集合完全相同。
                     * 处理方式：
                     * 1. 筛选出步进任务队列长度 ≥ 2 的其它机器人
                     * 2. 比较符合条件的每一个机器人的前三个待锁定站点集合与当前机器人的前三个待锁定站点集合是否相同
                     * 3. 若相同，则说明相向而行条件成立，重新请求路径规划算法并更新步进任务队列
                     * 4. 获取当前机器人更新后的步进任务队列的地址引用
                     */

                    // 判断其它机器人与当前机器人的前三个待锁定站点是否相同
                    List<String> currentPoints2Lock = getFirstThreeDistinctPoints(navTaskQueueMap.get(robotId));
                    Set<String> currentPointSet2Lock = new HashSet<>(currentPoints2Lock);
                    boolean found2StepOpposite = otherRobotIds.stream()
                            // 转换为任务队列对象
                            .map(navTaskQueueMap::get)
                            .filter(tasks -> tasks != null && tasks.size() >= 2)
                            // 核心校验：机器人前三个不同特征点数=3 且 集合相同
                            .anyMatch(otherTasks -> {
                                List<String> otherPoints = getFirstThreeDistinctPoints(otherTasks);
                                return currentPointSet2Lock.size() == 3 && new HashSet<>(otherPoints).equals(currentPointSet2Lock);
                            });
                    // 若相同，则重新请求路径规划算法并更新步进任务队列
                    if (found2StepOpposite) {
                        log.info("机器人{}步进任务【{}→{},{}→{}】与其它机器人互为反向路径，尝试通过重新请求路径规划算法更新任务队列", robotId,
                                currentPoints2Lock.get(0), currentPoints2Lock.get(1), currentPoints2Lock.get(1), currentPoints2Lock.get(2));

                        updateTaskQueueMap(waybillId);
                    }
                    /* ————————————————————————————相向而行情形2处理逻辑结束———————————————————————————— */

                    /**
                     * 基于区域及站点占用情况处理2个步进的任务调度，并更新镜像队列（若存在ABA问题，则补充1个任务，否则补充2个任务）
                     */
                    List<OneStepNavTaskDTO> addedRunningTasks = dispatchFirstAndSecondTask(robotId, remainingTasks, waybillId);
                    runningTasks.addAll(addedRunningTasks);
                    if (runningTasks.size() == 2) {
                        // 若已成功下发和补充2个任务
                        log.info("机器人{}控制器镜像队列已由0个任务补充到2个任务，由{}到{}再到{}", robotId, runningTasks.get(0).getStartPoint(), runningTasks.get(0).getEndPoint(), runningTasks.get(1).getEndPoint());
                    } else if (runningTasks.size() == 1) {
                        // 若已成功下发和补充1个任务
                        log.info("因ABA问题或原地虚拟路径问题，机器人{}控制器镜像队列已由0个任务补充到1个任务，由{}到{}", robotId, runningTasks.get(0).getStartPoint(), runningTasks.get(0).getEndPoint());
                    } else {
                        // 若未成功下发和补充
                        log.info("由于后续两个任务的结束站点被其它机器人占用，机器人{}继续在{}站点等待", robotId, remainingTasks.peek() != null ? remainingTasks.peek().getStartPoint() : "未知");
                    }
                }
                // ② 剩余任务队列=1，尝试下发最后一步任务
                else {
                    handleTaskDispatchingByOccupation(robotId, runningTasks, remainingTasks, waybillId);
                }
            }
        }
    }

    /**
     * <根据机器人路径更新机器人前门后门管理器> leo add
     * 获取当前机器人锁定的站点列表：
     * ① 若列表中包含"EF"站点，则开前门，isFrontDoor=true；
     * ② 若列表中包含"ER"站点，则开后门，isFrontDoor=false；
     * ③ 均不包含，则检查命名是否规范
     *
     * @param robotId 机器人名称
     */
    public void updateElevatorCurrentDoorToRobot(String robotId) {
        List<String> lockedPointsByRobot = getLockedPointsByRobot(robotId);
        if (lockedPointsByRobot.isEmpty()) {
            log.error("机器人{}未锁定任何站点，无法更新前门后门状态", robotId);
            return;
        }
        boolean isAllPointsEl = true;
        for (String point : lockedPointsByRobot) {
            if (point.toUpperCase().startsWith("EF")) {
                // 若路径指向前门，则设置前门为待开门
                robotFrontOrRearDoorMap.put(robotId, true);
                return;
            } else if (point.toUpperCase().startsWith("ER")) {
                // 若路径指向后门，则设置后门为待开门
                robotFrontOrRearDoorMap.put(robotId, false);
                return;
            } else {
                if (!point.toUpperCase().startsWith("EL")) {
                    isAllPointsEl = false;
                }
            }
        }
        if (isAllPointsEl) {
            /*
             * 若锁定的站点均为EL站点，则获取剩余任务队列中第一个步进任务的终点：
             *  ① 若终点为"EF"开头，则开前门，isFrontDoor=true；
             *  ② 若终点为"ER"开头，则开后门，isFrontDoor=false；
             *  ③ 若均不包含，则检查命名是否规范
             */
            OneStepNavTaskDTO firstTask = navTaskQueueMap.get(robotId).peekFirst();
            if (firstTask != null && firstTask.getEndPoint() != null) {
                // 获取电梯门前点
                String endPoint = firstTask.getEndPoint();
                if (endPoint.toUpperCase().startsWith("EF")) {
                    robotFrontOrRearDoorMap.put(robotId, true);
                    return;
                } else if (endPoint.toUpperCase().startsWith("ER")) {
                    robotFrontOrRearDoorMap.put(robotId, false);
                    return;
                }
            }
        }
        log.error("机器人{}的电梯门绑定站点命名不符合规范，请检查站点名称", robotId);
    }

    /**
     * <根据目标站点获取楼层> leo add
     *
     * @param robotId     机器人名称
     * @param targetPoint 目标站点
     * @return 楼层
     */

    public int getFloorByTargetPoint(String robotId, String targetPoint) {
        return Optional.ofNullable(getMapNameByTargetPoint(robotId, targetPoint))
                .map(this::parseMapNameForFloor)
                .orElse(0);
    }

    /**
     * <根据目标站点获取地图名称> leo add
     *
     * @param robotId     机器人名称
     * @param targetPoint 目标站点
     * @return 地图名称
     */
    public String getMapNameByTargetPoint(String robotId, String targetPoint) {
        return robotBasicInfoService.findByName(robotId)
                .stream().findFirst()
                // 获取存储<地图名，站点列表>映射关系的JSON
                .map(RobotBasicInfo::getMapNameToPoints)
                // 解析JSON为映射关系
                .map(json -> JSON.parseObject(
                        json, new TypeReference<Map<String, List<String>>>() {
                        }))
                // 获取目标站点所在的地图名称
                .flatMap(map -> map.entrySet().stream()
                        .filter(entry -> entry.getValue().contains(targetPoint))
                        .findFirst()
                        // 提取地图名称
                        .map(Map.Entry::getKey)
                )
                // 若未找到，则返回null
                .orElse(null);
    }

    /**
     * <将地图名解析为楼层> leo add
     *
     * @param mapName 地图名称
     * @return 楼层
     */
    private int parseMapNameForFloor(String mapName) {
        // 保留空字段
        String[] parts = mapName.split("_", -1);
        // 至少需要两个字段
        if (parts.length < 2) {
            log.info("地图名'{}'格式不符合要求，请修改地图名。", mapName);
            return 0;
        }
        // 取最后一个字段作为楼层
        int lastIndex = parts.length - 1;
        // 检验是否存在楼层标识位
        String floorIndicator = parts[lastIndex - 1];

        if (!"floor".equalsIgnoreCase(floorIndicator)) {
            log.info("地图名'{}'不包含楼层信息，请修改地图名。", mapName);
            return 0;
        }

        try {
            // 返回楼层值
            return Integer.parseInt(parts[lastIndex]);
        } catch (NumberFormatException e) {
            log.info("地图名'{}'的楼层值'{}'不是有效数字", mapName, parts[lastIndex]);
            return 0;
        }
    }

    /**
     * <切换机器人当前地图> leo add
     *
     * @param robotId 机器人名称
     * @param mapName 地图名称
     * @return tcp响应结果
     */
    public void switchRobotMap(String robotId, String mapName) {
        EntityResult result = new EntityResult();
        // 根据robotId获取机器人IP
        String robotIp = robotBasicInfoService.findByVehicleId(robotId).getCurrentIp();
        /*
         * *** 获取在线所有机器人IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 机器人IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> robotIpThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!robotIpThreadNettyMap.containsKey(robotIp + PortConstant.ROBOT_CONTROL_PORT)) {
            ioTEquipmentService.connectTcp(robotIp, PortConstant.ROBOT_CONTROL_PORT);
        }
        // 构造切换地图指令
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("map_name", mapName);
        String jsonString = JSON.toJSONString(jsonObject);
        String data = DataConvertUtil.convertStringToHex(jsonString);
        String dataLength = String.format("%04x", data.length() / 2);
        // 拼接最终指令
        String instruction = "5A 01 00 01 00 00" + dataLength + "07 E6 00 00 00 00 00 00" + data;
        /*
         * 发送请求指令
         */
        int retryCount = 0;
        do {
            result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_CONTROL_PORT, instruction);
            // 成功时或已达最大重试次数时退出循环
            if (result.isSuccess() || retryCount >= 5) {
                log.info("机器人{}切换地图<{}>请求指令发送成功", robotId, mapName);
                break;
            }
            // 等待0.5秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(500, TimeUnit.MILLISECONDS)) {
                    log.info("机器人{}切换地图<{}>指令发送失败，重试次数：{}", robotId, mapName, retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());
    }

    /**
     * <查询机器人任务状态> leo add
     *
     * @param robotId 机器人名称
     * @return tcp响应结果
     */
    public EntityResult queryTaskStatus(String robotId) {
        // 根据robotId获取机器人IP
        String robotIp = robotBasicInfoService.findByVehicleId(robotId).getCurrentIp();
        /*
         * *** 获取在线所有机器人IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 机器人IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> robotIpThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!robotIpThreadNettyMap.containsKey(robotIp + PortConstant.ROBOT_STATUS_PORT)) {
            ioTEquipmentService.connectTcp(robotIp, PortConstant.ROBOT_STATUS_PORT);
        }

        // 定义查询指令：[最后已完成任务和所有未完成任务列表]
        String statusInstruction = ProtocolConstant.ROBOT_TASK_STATUS;
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_STATUS_PORT, statusInstruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess() || retryCount >= 3) {
                break;
            }

            // 等待0.5秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(500, TimeUnit.MILLISECONDS)) {
                    log.info("查询机器人{}任务状态超时，重试次数：{}", robotId, retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

        return response;
    }

    /**
     * <查询指定电梯运行状态> leo add
     *
     * @param robotId        机器人名称
     * @param targetElevator 电梯对象
     * @return 梯控tcp响应结果
     */
    public EntityResult queryElevatorStatus(String robotId, IotEquipment targetElevator) {
        /*
         * *** 获取在线所有远程Tcp设备IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 远程Tcp设备IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
            ioTEquipmentService.connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
        }

        // 获取字节ID
        byte robotByteId = RobotIdToByteManager.getInstance().getRobotByteId(robotId);

        // 构建查询数据
        byte[] data = {
                // 命令字: 查询
                0x01,
                // ID号
                robotByteId,
                // 固定值
                (byte) 0xFF
        };
        // 构建BNK
        byte bnk = ElevatorPacketBuilder.convertToByte(targetElevator.getGroupCode());
        // 构建NOD
        byte nod = ElevatorPacketBuilder.convertToByte(targetElevator.getEquipmentCode());
        // 构建查询指令
        String instruction = ElevatorPacketBuilder.buildPacket(bnk, nod, data);
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, instruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info(">>>>>>机器人{}查询电梯<{}>运行状态指令发送成功，Hex指令：{}", robotId, targetElevator.getEquipmentName(), instruction);
                break;
            }
            if (retryCount >= 60) break;

            // 等待1秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                    log.info("查询机器人{}控制的电梯<{}>任务状态超时，重试次数：{}", robotId, targetElevator.getEquipmentName(), retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

        return response;
    }

    /**
     * <登记电梯楼层> leo add
     *
     * @param robotId        机器人名称
     * @param targetElevator 电梯对象
     * @param targetFloor    目标楼层
     * @param isFrontDoor    是否前门
     * @return 梯控tcp响应结果
     */
    public void registerFloor(String robotId, IotEquipment targetElevator, int targetFloor, boolean isFrontDoor) {
        /*
         * *** 获取在线所有远程Tcp设备IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 远程Tcp设备IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
            ioTEquipmentService.connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
        }

        // 获取字节ID
        byte robotByteId = RobotIdToByteManager.getInstance().getRobotByteId(robotId);

        // 确保楼层在有效范围内
        if (targetFloor < 0 || targetFloor > 127) {
            throw new IllegalArgumentException("目标楼层必须在0-127范围内");
        }

        // 构建楼层数据字节 (D7:门标志, D6-D0:楼层)
        byte floorData = (byte) ((isFrontDoor ? 0x00 : 0x80) | (targetFloor & 0x7F));

        // 构建登记数据
        byte[] data = {
                // 命令字：登记楼层
                0x02,
                // ID号
                robotByteId,
                // 预留字节
                0x00,
                // 数据字节
                floorData
        };
        // 构建BNK
        byte bnk = ElevatorPacketBuilder.convertToByte(targetElevator.getGroupCode());
        // 构建NOD
        byte nod = ElevatorPacketBuilder.convertToByte(targetElevator.getEquipmentCode());
        // 构建查询指令
        String instruction = ElevatorPacketBuilder.buildPacket(bnk, nod, data);
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, instruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info(">>>>>>机器人{}登记到电梯<{}>{}层指令发送成功，Hex指令：{}", robotId, targetElevator.getEquipmentName(), targetFloor, instruction);
                break;
            }
            if (retryCount >= 60) break;

            // 等待1 秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                    log.info("发送机器人{}控制的电梯<{}>登记楼层指令失败，重试次数：{}", robotId, targetElevator.getEquipmentName(), retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

    }

    /**
     * <电梯开门请求> leo add
     *
     * @param robotId        机器人名称
     * @param targetElevator 电梯对象
     * @param isFrontDoor    是否前门
     */
    public void openElevatorDoor(String robotId, IotEquipment targetElevator, boolean isFrontDoor) {
        /*
         * *** 获取在线所有远程Tcp设备IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 远程Tcp设备IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
            ioTEquipmentService.connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
        }

        // 获取字节ID
        byte robotByteId = RobotIdToByteManager.getInstance().getRobotByteId(robotId);

        // 正确构建开门数据 (前门D4位:0x10, 后门D5位:0x20)
        byte doorCommand = (byte) (isFrontDoor ? 0x10 : 0x20);

        byte[] data = {
                // 命令字: 开门请求
                0x03,
                // id号
                robotByteId,
                // 数据字节
                doorCommand
        };

        // 构建BNK
        byte bnk = ElevatorPacketBuilder.convertToByte(targetElevator.getGroupCode());
        // 构建NOD
        byte nod = ElevatorPacketBuilder.convertToByte(targetElevator.getEquipmentCode());
        // 构建查询指令
        String instruction = ElevatorPacketBuilder.buildPacket(bnk, nod, data);
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, instruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info(">>>>>>机器人{}请求电梯<{}>开门指令发送成功，Hex指令：{}", robotId, targetElevator.getEquipmentName(), instruction);
                break;
            }
            if (retryCount >= 60) break;

            // 等待1秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                    log.info("发送机器人{}控制的电梯<{}>开门指令失败，重试次数：{}", robotId, targetElevator.getEquipmentName(), retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

    }

    /**
     * <电梯关门请求> leo add
     *
     * @param robotId        机器人名称
     * @param targetElevator 电梯对象
     * @param isFrontDoor    是否前门
     * @return 梯控tcp响应结果
     */
    public void closeElevatorDoor(String robotId, IotEquipment targetElevator, boolean isFrontDoor, boolean exitSpecialMode) {
        /*
         * *** 获取在线所有远程Tcp设备IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 远程Tcp设备IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
            ioTEquipmentService.connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
        }

        // 获取字节ID
        byte robotByteId = RobotIdToByteManager.getInstance().getRobotByteId(robotId);

        // 构建关门数据 (前门D4位:0x10, 后门D5位:0x20)
        byte doorCommand = (byte) (isFrontDoor ? 0x10 : 0x20);
        if (exitSpecialMode) {
            // 设置D0位
            doorCommand |= 0x01;
        }

        byte[] data = {
                // 命令字: 关门请求
                0x04,
                // ID号
                robotByteId,
                // 数据字节
                doorCommand
        };

        // 构建BNK
        byte bnk = ElevatorPacketBuilder.convertToByte(targetElevator.getGroupCode());
        // 构建NOD
        byte nod = ElevatorPacketBuilder.convertToByte(targetElevator.getEquipmentCode());
        // 构建查询指令
        String instruction = ElevatorPacketBuilder.buildPacket(bnk, nod, data);
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, instruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info(">>>>>>机器人{}请求电梯<{}>关门指令发送成功，Hex指令：{}", robotId, targetElevator.getEquipmentName(), instruction);
                break;
            }
            if (retryCount >= 60) break;

            // 等待1秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                    log.info("发送机器人{}控制的电梯<{}>关门指令失败，重试次数：{}", robotId, targetElevator.getEquipmentName(), retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

    }

    /**
     * <电梯指令取消请求> leo add
     *
     * @param robotId        机器人名称
     * @param targetElevator 电梯对象
     * @return 梯控tcp响应结果
     */
    public EntityResult cancelElevatorRequest(String robotId, IotEquipment targetElevator) {
        /*
         * *** 获取在线所有远程Tcp设备IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 远程Tcp设备IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
            ioTEquipmentService.connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
        }

        // 获取字节ID
        byte robotByteId = RobotIdToByteManager.getInstance().getRobotByteId(robotId);

        // 构建取消数据
        byte[] data = {
                // 命令字: 取消
                0x05,
                robotByteId,
                // 预留字节
                0x00
        };

        // 构建BNK
        byte bnk = ElevatorPacketBuilder.convertToByte(targetElevator.getGroupCode());
        // 构建NOD
        byte nod = ElevatorPacketBuilder.convertToByte(targetElevator.getEquipmentCode());
        // 构建查询指令
        String instruction = ElevatorPacketBuilder.buildPacket(bnk, nod, data);
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, instruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info(">>>>>>机器人{}取消电梯<{}>指令：{}", robotId, targetElevator.getEquipmentName(), instruction);
                break;
            }
            if (retryCount >= 60) break;

            // 等待1秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                    log.info("发送机器人{}控制的电梯<{}>取消指令失败，重试次数：{}", robotId, targetElevator.getEquipmentName(), retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

        return response;
    }

    /**
     * <Tcp通道数据解析> leo update
     *
     * @param ctx 通道处理器上下文
     * @param msg 已转换为字符串的报文数据
     */
    @Override
    public void parseData(ChannelHandlerContext ctx, String msg) {
        //将机器人返回的hex字符串转换成byte数组
        byte[] bytes = DataConvertUtil.HexStringToBytes(msg);
        /*
         * 根据报文特定标识符判断msg类型
         */
        if ((DataConvertUtil.byteToHex(bytes[0]).equals("32") && DataConvertUtil.byteToHex(bytes[1]).equals("35"))) {
            // 获取电梯远程地址信息
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            // 获取电梯IP
            String elevatorIP = socketAddress.getAddress().getHostAddress();
            // 输出心跳
            log.info("♥♥♥♥♥♥ 收到来自电梯<{}>乘梯控制机的心跳 ♥♥♥♥♥♥", ioTEquipmentService.getEquipmentByIp(elevatorIP).getEquipmentName());
        }

        if (DataConvertUtil.byteToHex(bytes[0]).equals("2B") && DataConvertUtil.byteToHex(bytes[1]).equals("66")) {
            // 当msg类型为任务状态信息时，调用解析任务状态信息方法
            parseRobotTaskStatusData(ctx, bytes);
        }
        if (DataConvertUtil.byteToHex(bytes[0]).equals("4B") && DataConvertUtil.byteToHex(bytes[1]).equals("65")) {
            // 当msg类型为机器人基本信息时，推送解析机器人基本信息方法
            parseRobotBasicInfoData(ctx, bytes);
        }
        if (DataConvertUtil.byteToHex(bytes[0]).equals("AB") && DataConvertUtil.byteToHex(bytes[1]).equals("66")) {
            // 当msg类型为电梯响应时，调用解析电梯响应方法
            parseElevatorResponse(ctx, bytes);
        }
    }

    /**
     * <解析机器人任务状态数据> leo add
     *
     * @param ctx   通道处理器上下文
     * @param bytes 已转换为字节数组的报文数据
     */
    private void parseRobotTaskStatusData(ChannelHandlerContext ctx, byte[] bytes) {
        String taskStatusData = DataConvertUtil.BytesToString(bytes);
        // 获取远程机器人远程地址信息
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        // 调用parseData方法前已保证了hostAddress不为空
        String clientIP = socketAddress.getAddress().getHostAddress();
        // 根据机器人IP获取机器人ID
        String robotId = robotBasicInfoService.findByIp(clientIP).getVehicleId();
        // 获取机器人任务状态Json字符串
        String taskStatusJson = taskStatusData.substring(taskStatusData.indexOf("{"));
        // 解析机器人任务状态
        RobotTaskStatusDTO taskStatusDTO = JSON.parseObject(taskStatusJson, RobotTaskStatusDTO.class);
        // 更新状态，将<机器人ID, 任务状态DTO>存入多机器人任务状态管理器
        taskStatusMap.put(robotId, Optional.ofNullable(taskStatusDTO).orElse(new RobotTaskStatusDTO()));
    }

    /**
     * <解析电梯响应并根据响应类型分发给具体的解析方法> leo add
     *
     * @param ctx   通道处理器上下文
     * @param bytes 已转换为字节数组的报文数据
     */
    private void parseElevatorResponse(ChannelHandlerContext ctx, byte[] bytes) {

        // 最小长度检查
        if (bytes.length < 7) {
            throw new IllegalArgumentException("无效的电梯响应: 长度不足");
        }
        // 获取LEN字段值，该值存储在报文索引4的位置，并进行负数补位
        int len = bytes[4] & 0xFF;

        // 总长度 = 2 (STX) + 1 (BNK) + 1 (NOD) + 1 (LEN) + LEN (DATA) + 1 (SUM) + 1 (ETX) = 7 + LEN
        if (bytes.length != 7 + len) {
            throw new IllegalArgumentException(String.format(
                    "响应长度不匹配: 期望长度=%d, 实际长度=%d",
                    7 + len, bytes.length
            ));
        }

        // 提取电梯返回的数据内容（从索引5开始，长度为len）：由 命令字 + ID号 + 内容 组成
        byte[] data = Arrays.copyOfRange(bytes, 5, 5 + len);

        // 获取响应中的字节ID号
        byte responseRobotByteId = data[1];

        // 通过响应字节ID号查找对应的机器人ID
        String responseRobotId = RobotIdToByteManager.getInstance().getRobotId(responseRobotByteId);
        /*
         * ---<获取响应的电梯名称>---
         *  获取电梯远程地址信息
         *  获取电梯IP
         *  根据电梯IP获取电梯名称
         */
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String elevatorIP = socketAddress.getAddress().getHostAddress();
        String elevatorName = ioTEquipmentService.getEquipmentByIp(elevatorIP).getEquipmentName();

//        log.info("收到电梯<{}>对机器人<{}>的乘梯响应", elevatorName, responseRobotId);
        if (responseRobotId == null) {
            log.error("收到电梯<{}>未知字节ID的响应: {}", elevatorName, responseRobotByteId);
            responseRobotId = "unknown";
        }
        // 根据命令字路由
        switch (data[0] & 0xFF) {
            case 0x81:
                // 解析电梯运行状态响应
                parseElevatorStatusRes(ctx, data, responseRobotId, elevatorName);
                break;
            case 0x82:
                // 解析电梯登记楼层响应
                parseElevatorRegisterRes(ctx, data, responseRobotId, elevatorName);
                break;
            case 0x83:
                // 解析电梯开门响应
                parseElevatorOpenDoorRes(ctx, data, responseRobotId, elevatorName);
                break;
            case 0x84:
                // 解析电梯关门响应
                parseElevatorCloseDoorRes(ctx, data, responseRobotId, elevatorName);
                break;
            case 0x85:
                // 解析电梯取消响应
                parseElevatorCancelRes(ctx, data, responseRobotId, elevatorName);
                break;
            default:
                throw new IllegalArgumentException("未知的电梯响应类型");
        }

    }

    /**
     * <解析电梯运行状态响应> leo add
     *
     * @param ctx             通道处理器上下文
     * @param data            电梯返回的数据内容
     * @param responseRobotId 响应指向的机器人ID
     */
    private void parseElevatorStatusRes(ChannelHandlerContext ctx, byte[] data, String responseRobotId, String elevatorName) {

        /**
         * 协议格式: [0x81, ID, 预留, 楼层, 状态]
         */
        // 电梯当前楼层
        byte floor = data[3];
        // 状态字节
        byte status = data[4];

        ElevatorStatusDTO elevatorStatusDTO = ElevatorStatusDTO.builder()
                // 控制电梯的机器人ID
                .robotId(responseRobotId)
                // 电梯当前楼层
                .currentFloor(floor)

                // 状态位解析
                .movingUp((status & 0x01) != 0)          // D0: 上行状态 (00000001)
                .movingDown((status & 0x02) != 0)        // D1: 下行状态 (00000010)
                .rearDoorOpen((status & 0x04) != 0)      // D2: 后门状态 (00000100)，不存在后门时为到达状态
                .frontDoorOpen((status & 0x08) != 0)     // D3: 前门状态 (00001000)
                .floorCalibrated((status & 0x10) != 0)   // D4: 1层校准 (00010000)
                .floorDetect2((status & 0x20) != 0)      // D5: 楼层检测2 (00100000)
                .floorDetect1((status & 0x40) != 0)      // D6: 楼层检测1 (01000000)
                .build();

        // 方案一：更新电梯运行状态，将<电梯名称, 电梯运行状态DTO>存入多电梯运行任务状态管理器
        elevatorStatusMap.put(elevatorName, Optional.ofNullable(elevatorStatusDTO).orElse(new ElevatorStatusDTO()));
        log.info("收到电梯<{}>运行状态的响应: {}", elevatorName, elevatorStatusDTO);
        // 方案二：更新电梯运行状态，将<机器人名称, 电梯运行状态DTO>存入多电梯运行任务状态管理器
//        elevatorStatusMap.put(responseRobotId, Optional.ofNullable(elevatorStatusDTO).orElse(new ElevatorStatusDTO()));
    }

    /**
     * <解析电梯登记楼层响应> leo add
     *
     * @param ctx             通道处理器上下文
     * @param data            电梯返回的数据内容
     * @param responseRobotId 响应指向的机器人ID
     */
    private void parseElevatorRegisterRes(ChannelHandlerContext ctx, byte[] data, String responseRobotId, String elevatorName) {
        // 协议格式: [0x82, ID, 成功标志(D0)]
        boolean registerSuccess = (data[2] & 0x01) != 0;
        if (registerSuccess) {
            log.info("收到电梯<{}>对机器人{}的响应：*** 登记楼层指令执行成功 ***", elevatorName, responseRobotId);
        } else {
            log.error("收到电梯<{}>对机器人{}的响应：*** 登记楼层指令执行失败 ***", elevatorName, responseRobotId);
        }
    }

    /**
     * <解析电梯开门响应> leo add
     *
     * @param ctx             通道处理器上下文
     * @param data            电梯返回的数据内容
     * @param responseRobotId 响应指向的机器人ID
     */
    private void parseElevatorOpenDoorRes(ChannelHandlerContext ctx, byte[] data, String responseRobotId, String elevatorName) {
        // 协议格式: [0x83, ID, 预留, 楼层, 状态]
        // 返回电梯所处楼层int值
        int floor = data[3];
        byte status = data[4]; // 状态字节
        // 开门保持状态在D2位 (0x04)
        boolean doorHoldSuccess = (status & 0x04) != 0;
        if (doorHoldSuccess) {
            log.info("收到电梯<{}>对机器人{}的响应：*** {}层的门已打开 ***", elevatorName, responseRobotId, floor);
        } else {
            log.info("收到电梯<{}>对机器人{}的响应：*** {}层的门尚未打开 ***", elevatorName, responseRobotId, floor);
        }
    }

    /**
     * <解析电梯关门响应> leo add
     *
     * @param ctx             通道处理器上下文
     * @param data            电梯返回的数据内容
     * @param responseRobotId 响应指向的机器人ID
     */
    private void parseElevatorCloseDoorRes(ChannelHandlerContext ctx, byte[] data, String responseRobotId, String elevatorName) {
        // 协议格式，[0x83, ID, 预留, 楼层, 状态]
        // 返回电梯所处楼层int值
        int floor = data[3];
        log.info("收到电梯<{}>对机器人{}的响应：*** 正在关闭{}层的门 ***", elevatorName, responseRobotId, floor);
    }

    /**
     * <解析电梯取消响应> leo add
     *
     * @param ctx             通道处理器上下文
     * @param data            电梯返回的数据内容
     * @param responseRobotId 响应指向的机器人ID
     */
    private void parseElevatorCancelRes(ChannelHandlerContext ctx, byte[] data, String responseRobotId, String elevatorName) {
        // 协议格式: [0x85, ID, 预留, 楼层, 状态]
        byte status = data[4];
        // 上行状态
        boolean movingUp = (status & 0x01) != 0;
        if (movingUp) {
            log.info("收到电梯<{}>对机器人{}的响应：*** 电梯正在上行 ***", elevatorName, responseRobotId);
        }
        // 下行状态
        boolean movingDown = (status & 0x02) != 0;
        if (movingDown) {
            log.info("收到电梯<{}>对机器人{}的响应：*** 电梯正在下行 ***", elevatorName, responseRobotId);

        }
        // 取消状态
        boolean cancelSuccess = (status & 0x04) != 0;
        if (cancelSuccess) {
            log.info("收到电梯<{}>对机器人{}的响应：*** 指令取消成功 ***", elevatorName, responseRobotId);
        } else {
            log.error("收到电梯<{}>对机器人{}的响应：*** 指令取消失败 ***", elevatorName, responseRobotId);
        }
    }


    /**
     * <判断当前步进任务是否已完成> leo add
     *
     * @param taskStatusDTO 机器人任务状态DTO
     * @param runningTask   控制器镜像队列中的第一个任务
     * @return true：已完成，false：未完成
     */
    public boolean judgeStepTaskCompleted(RobotTaskStatusDTO taskStatusDTO, OneStepNavTaskDTO runningTask) {
        return Optional.ofNullable(taskStatusDTO) // 防御 taskStatusDTO 为 null
                .map(RobotTaskStatusDTO::getTaskStatusPackage)
                .map(pkg -> {
                    // 从数据包中提取任务状态列表
                    List<RobotTaskStatusDTO.TaskStatus> list = pkg.getTaskStatusList();
                    // 截取列表末尾的最后两个元素，即最后两步的任务状态（控制器任务查询接口返回的列表默认呈现全部任务的状态）
                    // 1. Math.max(0, list.size() - 2) 确保起始位置不会出现负数
                    // 2. 当列表元素不足两个时，从第一个元素开始截取
                    // 3. 最终保证返回最多最后两个任务状态记录
                    return list.subList(Math.max(0, list.size() - 2), list.size());
                })
                // 确保后续可操作
                .orElseGet(Collections::emptyList)
                .stream()
                .anyMatch(taskStatus ->
                        // runningTask 非空且 taskId 非空
                        runningTask != null &&
                                runningTask.getTaskId() != null &&
                                // taskStatus 的 taskId 和 status 非空
                                taskStatus.getTaskId() != null &&
                                taskStatus.getStatus() != null &&
                                // 核心条件判断：控制器中与镜像列表第一条任务id相同的控制器任务的状态为已完成（即任务状态为4）
                                taskStatus.getTaskId().equals(runningTask.getTaskId()) &&
                                taskStatus.getStatus() == 4
                );
    }

    /**
     * <基于区域及站点占用情况处理1个步进的任务调度> leo add
     *
     * @param robotId        机器人ID
     * @param remainingTasks 剩余任务队列（可能未及时更新）
     * @param runningTasks   运行中任务列表
     * @param waybillId      运单Id
     */
    private void handleTaskDispatchingByOccupation(String robotId, List<OneStepNavTaskDTO> runningTasks, Deque<OneStepNavTaskDTO> remainingTasks, String waybillId) {
        // 获取读锁（等待全局更新完成）
        globalLock.readLock().lock();
//        log.info("机器人{}获取读锁one", robotId);
        try {
            // 读取剩余步进任务队列的第一个任务
            OneStepNavTaskDTO nextTask = remainingTasks.peek();

            // 检查下一个任务及其结束站点是否为 null
            if (nextTask != null && nextTask.getStartPoint() != null && nextTask.getEndPoint() != null) {
                /* ————————————————————————————防御ABA问题处理逻辑开始———————————————————————————— */
                /** <防御ABA问题: 若存在ABA问题则直接跳过该方法>
                 * 若满足如下条件：
                 *  ① 控制器镜像队列不为空
                 *  ② 剩余步进任务队列不为空
                 *  ③ 剩余步进任务队列第一个任务的起点 ≠ 剩余步进任务队列第一个任务的终点
                 *  ④ 控制器镜像队列最后一个任务的起点 = 剩余步进任务队列第一个任务的终点
                 *
                 * 则说明存在ABA问题，直接跳过该方法，不下发步进任务
                 */
                if (!runningTasks.isEmpty() && !remainingTasks.isEmpty() && !nextTask.getStartPoint().equals(nextTask.getEndPoint()) &&
                        runningTasks.get(runningTasks.size() - 1).getStartPoint().equals(nextTask.getEndPoint())) {
                    log.info("机器人{}在路径<{}→{}→{}>存在ABA问题，暂缓下发下一步步进任务", robotId,
                            runningTasks.get(runningTasks.size() - 1).getStartPoint(), nextTask.getStartPoint(), nextTask.getEndPoint());
                    return;
                }
                /* ————————————————————————————防御ABA问题处理逻辑结束———————————————————————————— */

                /* ————————————————————————————防御原地导航虚拟路径逻辑开始———————————————————————————— */
                /**
                 * <防御原地导航虚拟任务: 若控制器镜像队列最后的路径为原地导航虚拟路径，则直接跳过该方法>
                 *
                 * · 若runningTasks长度为1，且该任务的起始点、结束点相同，则说明存在原地导航虚拟路径，不补充新的步进任务
                 * · 当下次轮询时，模拟原地等待，结束后移除该虚拟任务
                 */
                if (runningTasks.size() == 1 && runningTasks.get(0).getStartPoint().equals(runningTasks.get(0).getEndPoint())) {
                    log.info("机器人{}存在原地导航虚拟路径<{}→{}>，暂缓下发下一步步进任务", robotId,
                            runningTasks.get(0).getStartPoint(), runningTasks.get(0).getEndPoint());
                    return;
                }
                /* ————————————————————————————防御原地导航虚拟路径逻辑结束———————————————————————————— */

                /* ————————————————————————————防御电梯虚拟路径逻辑开始———————————————————————————— */
                /**
                 * <防御电梯虚拟任务: 若控制器镜像队列最后的路径为电梯虚拟路径，则直接跳过该方法>
                 *
                 * · 若runningTasks长度为1，且该任务的起始点、结束点均为 EL 开头的站点，则说明存在电梯虚拟路径，不补充新的步进任务
                 * · 之后的轮询不执行任何实际操作
                 * · 当电梯到达目标楼层并开门后，由"电梯守护线程"主动移除该机器人的电梯虚拟任务，使镜像队列为0，下次轮询将恢复正常轮询流程
                 */
                if (runningTasks.size() == 1 && runningTasks.get(0).getStartPoint().toUpperCase().startsWith("EL") &&
                        runningTasks.get(0).getEndPoint().toUpperCase().startsWith("EL")) {

                    log.info("机器人{}存在电梯虚拟路径<{}→{}>，暂缓下发下一步步进任务", robotId,
                            runningTasks.get(0).getStartPoint(), runningTasks.get(0).getEndPoint());
                    return;
                }
                /* ————————————————————————————防御电梯虚拟路径逻辑结束———————————————————————————— */

                // 构造待锁定站点列表
                List<String> pointsToLock = new ArrayList<>();
                pointsToLock.add(nextTask.getStartPoint());
                pointsToLock.add(nextTask.getEndPoint());
                /*
                 * *** 尝试管控待锁定点所在区域（若存在），并尝试锁定补充任务队列中第一个步进任务的开始及结束点，并获取锁定结果 ***
                 * （第二版：防御ABA问题）
                 * 注意：将第一版的锁定结束点改为锁定起始和结束点                                                                                  ，以适应不同步进任务的情况：
                 * 如A→B;B→A;A→B;B→A，若只锁定结束点，则可能存在机器人未锁定任何点的时刻，导致发生多机器人路径冲突。
                 */
                boolean isEndPointLockedBySelf = lockAreasAndPoints(robotId, pointsToLock, waybillId);
                // 若成功锁定（则给控制器和镜像队列一式两份的补充剩余任务队列的列首步进任务）
                if (isEndPointLockedBySelf) {
                    // 首先补充控制器镜像队列：+1个剩余任务队列中的列首任务
                    runningTasks.add(nextTask);
                    // 再下发该任务给控制器，即保持控制器任务与镜像队列任务的一致
                    sendAssignedNavCommand(robotId, Collections.singletonList(remainingTasks.poll()));
                    // 根据镜像队列长度打印日志信息
                    if (runningTasks.isEmpty()) {
                        log.info("机器人{}的控制器镜像队列为空", robotId);
                    } else if (runningTasks.size() == 1) {
                        log.info("机器人{}的控制器镜像队列已更新为：从{}到{}",
                                robotId,
                                runningTasks.get(0).getStartPoint(),
                                runningTasks.get(0).getEndPoint());
                    } else {
                        log.info("机器人{}的控制器镜像队列已更新为：从{}到{}再到{}", robotId,
                                runningTasks.get(0).getStartPoint(),
                                runningTasks.get(0).getEndPoint(),
                                runningTasks.get(1).getEndPoint());
                    }
                } else {
                    log.info("机器人{}的目标点{}尚未被{}释放", robotId, nextTask.getEndPoint(), lockedStations.get(nextTask.getEndPoint()));
                }
            }
        } finally {
            // 释放读锁
            globalLock.readLock().unlock();
//            log.info("机器人{}释放读锁one", robotId);
        }
    }

    /**
     * <关闭该机器人的主线程> leo add
     *
     * @param robotId 机器人名称
     */
    @Override
    public void stopRobotThread(String robotId) {

        robotFutureMap.computeIfPresent(robotId, (k, future) -> {
            // 区分已关闭/运行中两种状态
            if (!future.isDone()) {
                // 记录取消结果
                boolean cancelResult = future.cancel(true);
                log.warn("线程[{}] 中断请求已发送，取消状态:{}（剩余机器人线程:{}）",
                        k + "-ContinuousNavigation",
                        cancelResult ? "成功" : "失败",
                        robotFutureMap.size() - 1);
            } else {
                log.debug("线程[{}] 已自然终止，执行清理", k + "-ContinuousNavigation");
            }

            return null;
        });

        // 防御日志
        if (!robotFutureMap.containsKey(robotId)) {
            log.trace("robotId[{}] 未注册或已完成清理", robotId);
        }
    }


    /**
     * <程序自动终止---关闭该机器人的轮询线程并清空属于该机器人的队列及锁>
     * <p>
     * <step3: 终止条件---剩余队列为空且运行任务为空> leo add
     *
     * @param robotId         机器人名称
     * @param safetyStopPoint 安全停止点
     */
    @Override
    public void stopRobotPollingAndFlushQueues(String robotId, String safetyStopPoint) {
        // 获取写锁（独占操作）
        globalLock.writeLock().lock();
        try {
            ScheduledExecutorService schedulerById = robotSchedulerMap.get(robotId);
            if (schedulerById != null) {
                // 记录当前轮询线程状态
                if (!schedulerById.isShutdown()) {
                    log.info("机器人[{}] 轮询线程池状态 ▶▶ 运行中（等待关闭请求）", robotId);
                } else if (schedulerById.isShutdown() && !schedulerById.isTerminated()) {
                    log.info("机器人[{}] 轮询线程池状态 ▶▶ 已关闭（正在处理剩余任务）", robotId);
                } else {
                    log.info("机器人[{}] 轮询线程池状态 ▶▶ 已完全终止", robotId);
                }
                // 发送关闭请求
                schedulerById.shutdown();
                // 线程沉睡 0.01 秒，等待轮询线程关闭
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
                // 关闭后状态跟踪
                if (!schedulerById.isTerminated()) {
//                log.warn("机器人[{}] 轮询线程正在关闭中，等待0.2秒后强制关闭", robotId);
                    try {
                        if (!schedulerById.awaitTermination(200, TimeUnit.MILLISECONDS)) {
                            log.info("机器人[{}] 轮询线程池关闭超时，强制关闭", robotId);
                            schedulerById.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    log.info("机器人[{}]轮询线程已正常关闭", robotId);
                }

                log.info("机器人{}的本轮任务全部完成", robotId);
                log.info("--------------------------");
                // 移除该机器人状态轮询线程池执行管理器
                if (null != robotSchedulerMap.remove(robotId)) {
                    log.info("移除机器人{}轮询线程池执行管理器", robotId);
                }

                // 移除该机器人任务状态管理器
                if (null != taskStatusMap.remove(robotId)) {
                    log.info("移除机器人{}任务状态管理器", robotId);
                }
                // 移除该机器人导航任务队列管理器
                if (null != navTaskQueueMap.remove(robotId)) {
                    log.info("移除机器人{}导航任务队列管理器", robotId);
                }
                // 移除该机器人控制器镜像列表管理器
                if (null != runningTaskMap.remove(robotId)) {
                    log.info("移除机器人{}控制器镜像列表管理器", robotId);
                }
                // 移除该机器人跨楼层调度信息管理器
                if (null != robotCrossFloorInfoMap.remove(robotId)) {
                    log.info("移除机器人{}跨楼层调度信息管理器", robotId);
                }
                // 移除该机器人锁定的除安全停止点safetyStopPoint以外的站点
                if (lockedStations.entrySet().removeIf(entry ->
                        entry.getValue().equals(robotId) && !entry.getKey().equals(safetyStopPoint))) {
                    log.info("移除机器人{}锁定的站点（安全停止点【{}】除外）", robotId, safetyStopPoint);
                }

                log.info("--------------------------");
            }

        } finally {
            // 释放写锁
            globalLock.writeLock().unlock();
        }
    }

    /**
     * <手动或意外终止---关闭该机器人的轮询线程并清空属于该机器人的队列及锁>
     * <p>
     * <step3: 终止条件---剩余队列为空且运行任务为空> leo add
     *
     * @param robotId 机器人名称
     */
    @Override
    public void stopRobotPollingAndFlushQueuesByException(String robotId) {
        // 获取写锁（独占操作）
        globalLock.writeLock().lock();

        try {
            ScheduledExecutorService schedulerById = robotSchedulerMap.get(robotId);
            if (schedulerById != null) {
                // 记录当前轮询线程状态
                if (!schedulerById.isShutdown()) {
                    log.info("机器人[{}] 轮询线程池状态 ▶▶ 运行中（等待关闭请求）", robotId);
                } else if (schedulerById.isShutdown() && !schedulerById.isTerminated()) {
                    log.info("机器人[{}] 轮询线程池状态 ▶▶ 已关闭（正在处理剩余任务）", robotId);
                } else {
                    log.info("机器人[{}] 轮询线程池状态 ▶▶ 已完全终止", robotId);
                }
                // 发送关闭请求
                schedulerById.shutdown();
                // 线程沉睡 0.01 秒，等待轮询线程关闭
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
                // 关闭后状态跟踪
                if (!schedulerById.isTerminated()) {
//                log.warn("机器人[{}] 轮询线程正在关闭中，等待0.2秒后强制关闭", robotId);
                    try {
                        if (!schedulerById.awaitTermination(0, TimeUnit.MILLISECONDS)) {
                            log.info("机器人[{}] 轮询线程池关闭超时，强制关闭", robotId);
                            schedulerById.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    log.info("机器人[{}]轮询线程已正常关闭", robotId);
                }

                log.info("机器人{}的本轮任务由于人为或异常原因被终止", robotId);
                log.info("--------------------------");
                // 移除该机器人状态轮询线程池执行管理器
                robotSchedulerMap.remove(robotId);
                log.info("移除机器人{}轮询线程池执行管理器", robotId);

                // 移除该机器人任务状态管理器
                if (null != taskStatusMap.remove(robotId)) {
                    log.info("移除机器人{}任务状态管理器", robotId);
                }
                // 移除该机器人导航任务队列管理器
                if (null != navTaskQueueMap.remove(robotId)) {
                    log.info("移除机器人{}导航任务队列管理器", robotId);
                }
                // 移除该机器人控制器镜像列表管理器
                if (null != runningTaskMap.remove(robotId)) {
                    log.info("移除机器人{}控制器镜像列表管理器", robotId);
                }
                // 移除该机器人跨楼层调度信息管理器
                if (null != robotCrossFloorInfoMap.remove(robotId)) {
                    log.info("移除机器人{}跨楼层调度信息管理器", robotId);
                }
                // 释放该机器人锁定的全部站点及区域
                unlockAllPointsAndAreas(robotId);
            }
        } finally {
            // 释放写锁
            globalLock.writeLock().unlock();
        }
    }

    /**
     * <释放当前机器人锁定的全部站点及区域>
     *
     * @param robotId 机器人名称
     * @return
     */
    @Override
    public List<String> unlockAllPointsAndAreas(String robotId) {
        // 释放该机器人锁定的全部站点及区域
        List<String> lockedPointsByRobot = getLockedPointsByRobot(robotId);
        if (!lockedPointsByRobot.isEmpty()) {
            // 根据机器人名称和站点释放
            lockedPointsByRobot.forEach(point -> unlockPointAndArea(robotId, point));
        }
        log.info("*** 已手动释放机器人{}锁定的全部站点及区域 ***", robotId);
        // 返回解锁的站点
        return lockedPointsByRobot;
    }

    /**
     * <获取当前机器人锁定的全部站点>
     *
     * @param robotId 机器人名称
     */
    @Override
    public List<String> getLockedPointsByRobot(String robotId) {
        if (robotId == null) {
            return Collections.emptyList();
        }
        // 获取当前机器人锁定的全部站点
        return lockedStations.entrySet().stream()
                .filter(entry -> robotId.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * <销毁线程池> leo add
     */
    @PreDestroy
    public void shutdown() {
        robotSchedulerMap.values().forEach(executor -> {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    // 日志告警
                    log.warn("线程池[{}] 关闭超时，强制关闭", executor.toString());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        robotSchedulerMap.clear();
    }

    /**
     * <锁定点位信息存入数据库> leo add (后续可优化为持久化到数据库)
     */

    /**
     * ************ <连续路径导航-通用modbus读写> (leo add) *************
     *
     * @param modbusEquipment modbus设备对象：
     *                        ip--设备IP地址
     *                        SlaveId--从机ID（默认为1），
     *                        equipStateOffset--寄存器设备状态位偏移地址，
     *                        writeOffset--寄存器写入位偏移地址 ，
     * @param operationType   操作类型：见IotEquipment的静态内部类ModbusRegisterType
     * @param writeValue      写入值（仅用于写操作）,读操作时传入null
     */

    private IotEquipment.ModbusRwResult modbusGenericRw(IotEquipment modbusEquipment, int operationType, Integer writeValue) {

        String ip = modbusEquipment.getEquipmentIp();
        int slaveId = modbusEquipment.getSlaveId();
        int offset;
        if (operationType == 4 || operationType == 2) {
            offset = modbusEquipment.getWriteOffset();
        } else {
            offset = modbusEquipment.getEquipStateOffset();
        }

        int functionCode;
        ModbusTcpRead modbusOperation;

        Object modbusRes = null;
        Object modbusResWrite = null;

        IotEquipment.ModbusRwResult modbusRwResult = new IotEquipment.ModbusRwResult();
        modbusRwResult.setSuccess(false);

        switch (operationType) {
            case 1: // 读取线圈
                functionCode = ConstFunCode.func01;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.BooleanType, ip, 502);
                break;
            case 2: // 写入线圈
                functionCode = ConstFunCode.func05;
                modbusOperation = new ModbusTcpWrite(slaveId, functionCode, offset, ConstDataType.BooleanType, ip, 502);
                ((ModbusTcpWrite) modbusOperation).setWriteValue(writeValue == null ? "0" : (writeValue == 0 ? "0" : "1"));
                break;
            case 3: // 读取保持寄存器
                functionCode = ConstFunCode.func03;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, 502);
                break;
            case 4: // 写入保持寄存器
                functionCode = ConstFunCode.func06;
                modbusOperation = new ModbusTcpWrite(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, 502);
                ((ModbusTcpWrite) modbusOperation).setWriteValue(writeValue == null ? "0" : java.lang.String.valueOf(writeValue));
                break;
            default:
                throw new IllegalArgumentException("不受支持的操作类型");
        }
        try {
            // 分别处理读和写操作
            if (modbusOperation instanceof ModbusTcpWrite) {
                // 调用写方法
                modbusResWrite = ModbusUtils.writeTcpModbusByFunCode(((ModbusTcpWrite) modbusOperation));
                if (writeValue != null) {
                    if (writeValue == java.lang.Integer.parseInt(modbusResWrite.toString())) {
//                        log.info("modbus写入结果: {}", modbusResWrite);
                        modbusRwResult.setSuccess(true);
                        modbusRwResult.setValue(writeValue);
                    } else {
                        log.error("modbus写入结果: {}", modbusResWrite);
                    }
                }
            } else {
                // 调用读方法
                modbusRes = ModbusUtils.readModbusByTcp((ModbusTcpRead) modbusOperation);
                if (modbusRes != null && modbusRes.toString().length() > 0) {
                    modbusRwResult.setSuccess(true);
                    modbusRwResult.setValue(Integer.parseInt(modbusRes.toString()));
                } else {
                    log.error("modbus读取结果: {}", modbusRes);
                }
            }
        } catch (Exception e) {
            log.error("获取modbus设备异常：{}", e);
        }

        return modbusRwResult;
    }

    /* ********************************* 指定路径导航结束 ********************************* */

    /*
     * 获取自动充电桩设备状态Demo
     */
    public void testGetChargePileStatusDemo() {
        List<RobotChargePile> chargePileList = robotChargePileService.list();
        if (!chargePileList.isEmpty()) {
            RobotChargePile chargePile = chargePileList.get(0);
            // 读取自动充电桩状态寄存器
            IotEquipment.ModbusRwResult modbusRwResult = modbusForChargePile(chargePile, IotEquipment.ModbusRegisterType.READ_INPUT_REGISTER, chargePile.getStatusRegisterOffset(), null);
            // 获取状态值
            int registerValue = modbusRwResult.getValue();
            // 解析状态值
            ChargePileStatusDTO chargePileStatusDTO = parseStatusFromRegister(registerValue);
            log.info("自动充电桩状态：{}", chargePileStatusDTO);
            // 获取伸到位状态
            boolean extendInPosition = chargePileStatusDTO.getExtendInPosition();
            // 获取缩到位状态
            boolean shrinkInPosition = chargePileStatusDTO.getShrinkInPosition();
            // 根据伸缩到位状态执行后续逻辑
            if (extendInPosition) {

            }
            if (shrinkInPosition) {

            }


        }
    }

    /**
     * <自动充电桩-解析寄存器状态值为DTO对象>
     *
     * @param registerValue 从Modbus寄存器读取的原始int返回值
     * @return 包含解析后状态信息的DTO对象
     */
    public ChargePileStatusDTO parseStatusFromRegister(int registerValue) {
        try {
            // 软件层面模拟无符号short类型
            int normalizedValue = registerValue & 0xFFFF;
            return ChargePileStatusDTO.builder()
                    .dryContactEnable((normalizedValue & 0x0001) != 0)      // Bit0: 干接点使能
                    .infraredReceived((normalizedValue & 0x0002) != 0)      // Bit1: 红外接收
                    .extendInPosition((normalizedValue & 0x0004) != 0)      // Bit2: 伸到位
                    .shrinkInPosition((normalizedValue & 0x0008) != 0)      // Bit3: 缩到位
                    .wifiConnected((normalizedValue & 0x0080) != 0)         // Bit7: wifi连接
                    .build();

        } catch (Exception e) {
            log.error("解析寄存器状态值时发生错误: {}", e.getMessage());
            return null;
        }
    }

    /**
     * ************ <自动充电桩-modbus读写> (leo add) *************
     * ---参数说明---
     * ip：设备IP地址，
     * SlaveId：从机ID（默认为1）
     *
     * @param chargePile    自动充电桩设备信息
     * @param operationType 操作类型（1：读取线圈，2：写入线圈，3：读取保持寄存器，4：写入保持寄存器，5：读取输入寄存器）
     * @param offset        寄存器偏移地址
     * @param writeValue    写入值（仅写入操作时有效）
     * @return 返回Modbus读写结果对象，包含成功与否、读取或写入的原始值等信息
     */

    private IotEquipment.ModbusRwResult modbusForChargePile(RobotChargePile chargePile, int operationType, int offset, Integer writeValue) {
        String ip = chargePile.getChargePileIp();
        int slaveId = chargePile.getSlaveId();

        int functionCode;
        ModbusTcpRead modbusOperation;

        Object modbusRes = null;
        Object modbusResWrite = null;

        IotEquipment.ModbusRwResult modbusRwResult = new IotEquipment.ModbusRwResult();
        modbusRwResult.setSuccess(false);

        switch (operationType) {
            case 1: // 读取线圈
                functionCode = ConstFunCode.func01;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.BooleanType, ip, 502);
                break;
            case 2: // 写入线圈
                functionCode = ConstFunCode.func05;
                modbusOperation = new ModbusTcpWrite(slaveId, functionCode, offset, ConstDataType.BooleanType, ip, 502);
                ((ModbusTcpWrite) modbusOperation).setWriteValue(writeValue == null ? "0" : (writeValue == 0 ? "0" : "1"));
                break;
            case 3: // 读取保持寄存器
                functionCode = ConstFunCode.func03;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, 502);
                break;
            case 4: // 写入保持寄存器
                functionCode = ConstFunCode.func06;
                modbusOperation = new ModbusTcpWrite(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, 502);
                ((ModbusTcpWrite) modbusOperation).setWriteValue(writeValue == null ? "0" : java.lang.String.valueOf(writeValue));
                break;
            case 5: // 读取输入寄存器
                functionCode = ConstFunCode.func04;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, 502);
                break;
            default:
                throw new IllegalArgumentException("不受支持的操作类型");
        }
        try {
            // 分别处理读和写操作
            if (modbusOperation instanceof ModbusTcpWrite) {
                // 调用写方法
                modbusResWrite = ModbusUtils.writeTcpModbusByFunCode(((ModbusTcpWrite) modbusOperation));
                if (writeValue != null) {
                    if (writeValue == java.lang.Integer.parseInt(modbusResWrite.toString())) {
//                        log.info("modbus写入结果: {}", modbusResWrite);
                        modbusRwResult.setSuccess(true);
                        modbusRwResult.setValue(writeValue);
                    } else {
                        log.error("modbus写入结果: {}", modbusResWrite);
                    }
                }
            } else {
                // 调用读方法
                modbusRes = ModbusUtils.readModbusByTcp((ModbusTcpRead) modbusOperation);
                if (modbusRes != null && modbusRes.toString().length() > 0) {
                    modbusRwResult.setSuccess(true);
                    modbusRwResult.setValue(Integer.parseInt(modbusRes.toString()));
                } else {
                    log.error("modbus读取结果: {}", modbusRes);
                }
            }
        } catch (Exception e) {
            log.error("获取modbus设备异常：{}", e);
        }

        return modbusRwResult;

    }


    /**
     * <让机器人执行subTaskList任务列表>
     *
     * @param robotName   机器人名称
     * @param subTaskList 运行任务列表
     * @throws Exception 异常
     */
    @Override
    public boolean runNavigationTask(String waybillId, String robotName, List<RobotRunDTO> subTaskList) throws
            Exception {
        //封装路径规划算法所需的数据
        //解析机器人地图，获取机器人地图的路径列表
        List<RobotBasicInfo> robotBasicInfoList = robotBasicInfoService.findByName(robotName);
        List<RobotPathVO> pathList = JSONObject.parseArray(robotBasicInfoList.get(0).getPaths(), RobotPathVO.class);
        if (subTaskList.size() < 2) {
            throw new Exception("执行机器人任务失败，请检查任务是否完整!");
        }
        //获取子任务的起点
        String beginPort = subTaskList.get(0).getStation();
        //获取子任务的终点
        String endPort = subTaskList.get(1).getStation();
        //封装路径规划算法导航的机器人名称、起点和终点
        RobotPathPlanDTO.RobotPath robotPath = robotTaskService.encapsulateData(robotName, robotBasicInfoList.get(0).getGroupName(), beginPort, endPort);
        List<RobotPathPlanDTO.RobotPath> robotPathList = new ArrayList<>();
        robotPathList.add(robotPath);

        List<RobotNavigation> robotNavigations = robotNavigationService.findByRobotName(robotName);
        RobotNavigation byRobotName;
        if (robotNavigations.size() <= 0) {
            byRobotName = new RobotNavigation();
        } else {
            byRobotName = robotNavigations.get(0);
        }
        byRobotName.setBeginPort(beginPort);
        byRobotName.setNextBeginPort(endPort);
        byRobotName.setEndPort(endPort);
        byRobotName.setRobotName(robotName);
        byRobotName.setTaskList(JSON.toJSONString(subTaskList));
        boolean b = robotNavigationService.saveOrUpdate(byRobotName);
        log.info("新增机器人导航任务是否成功：{}", b);

        List<RobotNavigation> robotNavigationList = robotNavigationService.list();
        for (RobotNavigation robotNavigation : robotNavigationList) {
            //导航任务中存在未执行完的任务，并且当前任务的起点和终点不一样（任务未执行完成）
            if (!robotNavigation.getRobotName().equals(robotName) && !robotNavigation.getBeginPort().equals(robotNavigation.getEndPort())) {
                RobotBasicInfo robot = robotBasicInfoService.findByName(robotNavigation.getRobotName()).get(0);
                //如果机器人在线，则加入导航
                InetAddress geek = InetAddress.getByName(robot.getCurrentIp());
                if (geek.isReachable(1000)) {
                    String taskListStr = robotNavigation.getTaskList();
                    List<RobotRunDTO> taskList = JSON.parseObject(taskListStr, new TypeReference<List<RobotRunDTO>>() {
                    });
                    if (taskList.size() > 1) {
                        String currentBeginPort = robotNavigation.getBeginPort();
                        String currentEndPort = robotNavigation.getEndPort();
                        RobotBasicInfo robotBasicInfo = robotBasicInfoService.findByName(robotNavigation.getRobotName()).get(0);
                        List<RobotPathVO> subPathList = JSONObject.parseArray(robotBasicInfo.getPaths(), RobotPathVO.class);
                        RobotPathPlanDTO.RobotPath currentRobotPath = robotTaskService.encapsulateData(robotNavigation.getRobotName(), robotBasicInfo.getGroupName(), currentBeginPort, currentEndPort);
                        //如果两个机器人导航的终点相同，必须等待上一个机器人任务执行完成，另外一个机器人才可以执行
                        if (currentEndPort.equals(endPort)) {
                            robotPathList.remove(0);
                        }
                        robotPathList.add(currentRobotPath);
                        //加入混合场景地图的路径信息
                        pathList.addAll(subPathList);
                    }
                } else {
                    //如果机器人不在线，则删除导航任务
                    robotNavigationService.removeById(robotNavigation.getId());
                    log.info("机器人不在线，移除当前导航任务：{}", robotNavigation.getRobotName());
                }
            }
        }
        //开始请求路径规划算法执行
        log.info("开始请求路径规划算法,请求的任务：{}", JSON.toJSONString(robotPathList));
        AlgorithmRequestResDTO algorithmRequestResDTO = robotTaskService.startPathPlanningTask(pathList, robotPathList, null);
        log.info("pathList{}", JSON.toJSONString(pathList));
        log.info("开始请求路径规划算法,请求的路径{}", JSON.toJSONString(pathList));
        if ("success".equals(algorithmRequestResDTO.getStatus())) {
            //监测路径规划算法的结果是否返回
            String cron = "*/1 * * * * ?";
            customScheduledTaskRegistrar.addTriggerTask(
                    algorithmRequestResDTO.getTaskId(),
                    //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                    () -> {
                        try {
                            runPathPlanningTask(algorithmRequestResDTO.getTaskId(), waybillId, pathList);
                        } catch (Exception e) {
                            //如果查询到算法执行返回的结果，则删除定时任务
                            log.error("执行路径规划算法失败，错误信息{}", e.getMessage());
                            customScheduledTaskRegistrar.removeTriggerTask(algorithmRequestResDTO.getTaskId());
                            try {
                                setTaskFailData(waybillId, waybillId, robotName, "执行路径规划算法失败");
                            } catch (Exception ex) {
                                log.error("向第三方系统同步运单状态失败，错误信息{}", e.getMessage());
                            }
                            e.printStackTrace();
                        }
                    },
                    triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
        }
        //请求算法成功
        return "success".equals(algorithmRequestResDTO.getStatus());
    }

    /**
     * 运行算法返回的机器人路径规划
     *
     * @param taskId   算法的任务ID
     * @param pathList
     */
    public void runPathPlanningTask(String taskId, String waybillId, List<RobotPathVO> pathList) throws
            Exception {
        List<RobotPathPlanning> pathPlanningList = robotPathPlanningService.getListByTaskId(taskId);
        //查询到路径规划算法执行的结果
        if (pathPlanningList.size() == 1) {
            //如果查询到算法执行返回的结果，则删除定时任务
            customScheduledTaskRegistrar.removeTriggerTask(taskId);
            RobotPathPlanning pathPlanning = pathPlanningList.get(0);
            //使用完成以后，设置算法执行的结果为无效
            pathPlanning.setState(Constant.STATE_INVALID);
            robotPathPlanningService.saveOrUpdate(pathPlanning);
            //获取算法执行返回的为机器人规划的路径result
            if ("[]".equals(pathPlanning.getResult())) {
                //执行路径规划算法失败
                RobotWaybill robotWaybill = getById(waybillId);
                setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), "执行路径规划算法失败");
            } else {
                List<AlgorithmResResultDTO> list = JSON.parseArray(pathPlanning.getResult(), AlgorithmResResultDTO.class);
                log.info("完成路径规划算法请求,请求的结果{}", JSON.toJSONString(list));
                //开启新的路径规划之前，关闭之前的任务线程
                ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
                int noThreads = currentGroup.activeCount();
                Thread[] lstThreads = new Thread[noThreads];
                currentGroup.enumerate(lstThreads);
                // 遍历线程
                for (int i = 0; i < noThreads; i++) {
                    String threadName = lstThreads[i].getName();
                    if (threadName.startsWith("pathPlanningThread")) {
                        //强制关闭线程
                        lstThreads[i].stop();
                    }
                }
                //开启线程
                List<Thread> threads = multiThreadRunTask(list, pathList, taskId, waybillId);
                for (int i = 0; i < threads.size(); i++) {
                    threads.get(i).setName("pathPlanningThread" + list.get(i).getRobotName());
                    threads.get(i).start();
                }
            }
        }
    }

    /**
     * 运行路径规划光通信任务
     *
     * @param taskId
     * @param waybillId
     * @throws Exception
     */
    public void runPathPlanningTaskDms(String taskId, String waybillId, boolean isRePathPlanning) throws Exception {
        List<RobotPathPlanning> pathPlanningList = robotPathPlanningService.getListByTaskId(taskId);
        //查询到路径规划算法执行的结果
        if (pathPlanningList.size() == 1) {
            //如果查询到算法执行返回的结果，则删除定时任务
            customScheduledTaskRegistrar.removeTriggerTask(taskId);
            RobotPathPlanning pathPlanning = pathPlanningList.get(0);
            //使用完成以后，设置算法执行的结果为无效
            pathPlanning.setState(Constant.STATE_INVALID);
            robotPathPlanningService.saveOrUpdate(pathPlanning);
            RobotWaybill robotWaybill = getById(waybillId);
            //获取算法执行返回的为机器人规划的路径result
            if ("[]".equals(pathPlanning.getResult())) {
                //执行路径规划算法失败
                setTaskFailData(waybillId, waybillId, robotWaybill.getRobotName(), "执行路径规划算法失败");
            } else {
                List<AlgorithmResResultDTO> list = JSON.parseArray(pathPlanning.getResult(), AlgorithmResResultDTO.class);
                log.info("完成路径规划算法请求,请求的结果{}", JSON.toJSONString(list));
                //开启新的路径规划之前，关闭之前的任务线程
                ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
                int noThreads = currentGroup.activeCount();
                Thread[] lstThreads = new Thread[noThreads];
                currentGroup.enumerate(lstThreads);
                // 遍历线程
                for (int i = 0; i < noThreads; i++) {
                    String threadName = lstThreads[i].getName();
                    if (!isRePathPlanning) {
                        //只关闭将要执行任务的机器人
                        if (threadName.startsWith("pathPlanningThread" + robotWaybill.getRobotName())) {
                            //强制关闭线程
                            log.info("关闭了执行任务的线程{}", threadName);
                            lstThreads[i].stop();
                        }
                    } else {
                        //关闭全部执行任务的机器人线程
                        if (threadName.startsWith("pathPlanningThread")) {
                            //强制关闭线程
                            log.info("关闭了执行任务的线程{}", threadName);
                            lstThreads[i].stop();
                        }
                    }
                }
                //开启线程,运行光通信多机器人导航任务
                multiThreadRunTaskDms(list);
            }
        }
    }

    /**
     * 根据value值获取对应的key
     *
     * @param map
     * @param value
     * @return
     */
    private static String getKey(Map<String, String> map, String value) {
        String key = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                key = entry.getKey();
            }
        }
        return key;
    }

    /**
     * 获取当前机器人锁定的路径和点位信息
     *
     * @param vehicle
     * @return
     */
    @Override
    public RobotLockPathAndPoint getRobotLockAndPoint(String vehicle) {
        return dmsLockedPointMap.get(vehicle);
    }

    /**
     * 异常机器人复位后解除该机器人的路径和点位占用
     *
     * @param vehicle
     * @return
     */
    @Override
    public void liftRobotLockPathAndPoint(String vehicle) {
        dmsLockedPointMap.remove(vehicle);
    }

    /**
     * 开启多个线程并行执行机器人任务
     *
     * @param list     路径规划算法返回的机器人任务列表
     * @param pathList
     * @return
     */
    public List<Thread> multiThreadRunTask
    (List<AlgorithmResResultDTO> list, List<RobotPathVO> pathList, String taskId, String waybillId) {
        //因为多个机器人需要并行执行，因此这里需要创建多个线程，并分配不同的子列表给每个线程
        List<Thread> threads = new ArrayList<>();
        //多线程关闭的标志
        AtomicBoolean closeThreadFlag = new AtomicBoolean(false);
        for (AlgorithmResResultDTO dto : list) {

            //构建虚拟路径，用于在执行虚拟路径的时候删除上次被锁定的站点
            List<AlgorithmResResultDTO.Path> pathPlanning = dto.getPathPlanning();
            AlgorithmResResultDTO.Path path = new AlgorithmResResultDTO.Path();
            path.setStartPoint("TEMP");
            path.setEndPoint("TEMP");
            path.setTotalCurveLength(0);
            pathPlanning.add(path);

            RobotBasicInfo robot = robotBasicInfoService.findByName(dto.getRobotName()).get(0);
            String robotIp = robot.getCurrentIp();
            //开启多线程分别去执行不同的机器人任务
            Thread thread = new Thread(() -> {
                log.info("机器人运行线程已创建{}", robotIp);
                log.info("机器人运行线程已创建,执行机器人[{}],运单ID[{}]", robot.getVehicleId(), waybillId);
                //使用for循环去单步骤执行机器人任务
                for (int j = 0; j < pathPlanning.size(); j++) {
                    //获取机器人执行单步骤任务的起始点和终点
                    String beginPort = pathPlanning.get(j).getStartPoint();
                    String endPort = pathPlanning.get(j).getEndPoint();

                    //如果导航完成(因为第一步执行任务的时候，机器人的状态可能不是导航完成，因此需要把j==0加入条件中）
                    if (map.get(robot.getVehicleId()).getTaskStatus().equals("导航完成") &&
                            beginPort.equals(map.get(robot.getVehicleId()).getCurrentStation()) || beginPort.equals("TEMP") || j == 0) {
                        //需要释放掉的机器人点位和路径
                        if (j != 0) {
                            String lastEndPort = pathPlanning.get(j - 1).getEndPoint();
                            lockedPoints.remove(lastEndPort);
                            //如果是构建的虚拟路径，那么就终止循环
                            if (j == pathPlanning.size() - 1) {
                                break;
                            }
                        }

                        //其它机器人任务执行的多线程关闭标志为false
                        if (!closeThreadFlag.get()) {
                            //记录当前导航的信息，用于下次全局规划时，
                            RobotNavigation robotNavigation;
                            List<RobotNavigation> robotNavigations = robotNavigationService.findByRobotName(dto.getRobotName());
                            if (robotNavigations.size() == 0) {
                                robotNavigation = new RobotNavigation();
                            } else {
                                robotNavigation = robotNavigations.get(0);
                            }
                            robotNavigation.setRobotName(dto.getRobotName());
                            robotNavigation.setBeginPort(beginPort);
                            robotNavigation.setNextBeginPort(endPort);
                            if (pathPlanning.size() == 2) {
                                robotNavigation.setEndPort(endPort);
                            } else {
                                robotNavigation.setEndPort(pathPlanning.get(pathPlanning.size() - 2).getEndPoint());
                            }
                            robotNavigation.setTaskId(taskId);
                            robotNavigationService.saveOrUpdate(robotNavigation);

                            //当前站点是被哪个机器人锁上的
                            String vehicleId = getKey(lockedPoints, endPort);
                            //判断将要执行的站点是否被锁定
                            if (lockedPoints.containsValue(endPort) && !dto.getRobotName().equals(vehicleId)) {
                                //如果发现站点被锁定，则设置标志为true
                                closeThreadFlag.set(true);
                                //遇到路径冲突，需要重新请求路径规划
                                boolean flag = true;
                                while (flag) {
                                    try {
                                        //每隔两秒执行一次循环，防止重新规划路径方法被多次执行
                                        Thread.sleep(4000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (vehicleId != null && map.get(vehicleId).getTaskStatus().equals("导航完成")) {
                                        log.info("--------调度系统重新规划了机器人运行路径-----------");
                                        //设置标志位为false
                                        flag = false;
                                        //重新规划前，移除当前被锁的目标点
                                        lockedPoints.remove(vehicleId);
                                        try {
                                            rePathPlanning(pathList, vehicleId, waybillId);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                break;
                            }

                            //需要锁上掉的机器人名称和点位
                            lockedPoints.put(robot.getVehicleId(), endPort);
                            //获取当前路径的长度
                            float totalCurveLength = pathPlanning.get(j).getTotalCurveLength();
                            //运行子任务中的起始点到终点的导航任务
                            runPathNavigate(beginPort, endPort, robotIp, totalCurveLength);

                            List<RobotNavigation> newRobotNavigations = robotNavigationService.findByRobotName(dto.getRobotName());
                            //一个任务链中一个子任务执行完成，则移除当前子任务
                            if (j == pathPlanning.size() - 2) {
                                for (int k = 0; k < newRobotNavigations.size(); k++) {
                                    if (newRobotNavigations.get(k).getEndPort().equals(map.get(robot.getVehicleId()).getCurrentStation())
                                            && map.get(robot.getVehicleId()).getTaskStatus().equals("导航完成")) {
                                        try {
                                            RobotNavigation oldRobotNavigation = newRobotNavigations.get(k);
                                            String taskListStr = oldRobotNavigation.getTaskList();
                                            List<RobotRunDTO> taskList = JSON.parseObject(taskListStr, new TypeReference<List<RobotRunDTO>>() {
                                            });
                                            //移除当前子任务
                                            taskList.remove(0);
                                            oldRobotNavigation.setTaskList(JSON.toJSONString(taskList));
                                            robotNavigationService.saveOrUpdate(oldRobotNavigation);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        k--;
                                    }
                                }
                            }
                        } else {
                            //如果关闭标志是true,则跳出该循环
                            break;
                        }
                    } else {
                        //如果当前导航正在进行，需要等待上一段任务执行完成
                        j--;
                    }
                }
            });
            //开始执行线程任务
            threads.add(thread);
        }
        return threads;
    }

    /**
     * <运行子任务中的起始点到终点的导航任务> leo update
     * 开启多个线程执行光通信任务
     *
     * @param list
     * @return
     */
    public void multiThreadRunTaskDms(List<AlgorithmResResultDTO> list) {
        //多线程关闭的标志
        AtomicBoolean closeThreadFlag = new AtomicBoolean(false);

        for (AlgorithmResResultDTO dto : list) {
            //构建虚拟路径，用于在执行虚拟路径的时候删除上次被锁定的站点
            List<AlgorithmResResultDTO.Path> pathPlanning = dto.getPathPlanning();
            //-------------------------------------(混合调度)-------------------------------------------------------------------------------
            //获取子任务要经过的所有站点
            Set<String> passedPoints = new HashSet<>();
            for (AlgorithmResResultDTO.Path path : pathPlanning) {
                passedPoints.add(path.getStartPoint());
                passedPoints.add(path.getEndPoint());
            }
            //-----------------------------------------------------------------------------------------------------------------------------
            //先清除掉当前机器人未执行完成的任务
            subTasks.remove(dto.getRobotName());
            //然后添加当前机器人新的任务队列
            subTasks.put(dto.getRobotName(), pathPlanning);
            //获取当前执行运单任务的机器人信息
            RobotBasicInfo robot = robotBasicInfoService.findByName(dto.getRobotName()).get(0);
            RobotWaybill robotWaybill = getWaybillByRobotNameAndTaskStatus(dto.getRobotName(), 1);
            String waybillId = robotWaybill.getId();
            //记录当前运单的路径规划算法结果
            robotWaybill.setPathPlanningResult(JSON.toJSONString(dto.getPathPlanning()));
            saveOrUpdate(robotWaybill);
            //开启多线程分别去执行不同的机器人任务
            //因为多个机器人需要并行执行，因此这里需要创建多个线程，并分配不同的子列表给每个线程
            Thread thread = new Thread(() -> {
                log.info("机器人运行线程已创建,执行机器人[{}],运单ID[{}]", robot.getVehicleId(), waybillId);
                //初始化
                lockedSubTask.put(robot.getVehicleId(), true);
                //执行机器人任务
                //使用for循环去单步骤执行机器人任务
                //使用标签（Label）来直接引用并跳出外层循环
                Label:
                for (int j = 0; j < pathPlanning.size(); j++) {
                    //-------------------------------------(混合调度)-------------------------------------------------------------------------------
                    //执行多品牌机器人混合调度时，判断路径规划算法规划的站点是否存在被其它机器人锁定；如果锁定，则不能执行任务，直到锁定的站点被释放
                    //获取当前被锁定的管控区域
//                    List<RobotDmsEditor> listByAreaTypeAndOccupied = robotDmsEditorService.findListByAreaTypeAndOccupiedAndRobotName(2, 1, null);
//                   if (listByAreaTypeAndOccupied != null) {
//                       //判断任务执行过程中是否要经过已经占用的管控区域
//                       boolean isPassOccupiedControlledArea = false;
//                       for (RobotDmsEditor robotDmsEditor : listByAreaTypeAndOccupied) {
//                           //该管控区域不是被同一个机器人锁定
//                           if (!robot.getVehicleId().equals(robotDmsEditor.getOccupiedRobotName())) {
//                               boolean isContain = passedPoints.contains(robotDmsEditor.getAreaCenterPoint());
//                               isPassOccupiedControlledArea = isPassOccupiedControlledArea || isContain;
//                           }
//                       }
//                       //如果要经过的管控区域已经被其他机器人占用
//                       if (isPassOccupiedControlledArea) {
//                           j--;
//                           try {
//                               Thread.sleep(3000);
//                           } catch (InterruptedException e) {
//                               e.printStackTrace();
//                           }
//                           continue ;
//                       }
//                   }
//                    //获取当未被锁定的管控区域,如果当前任务的路径点经过对应管控区域，则锁定该管控区域
//                    List<RobotDmsEditor> listByAreaTypeAndNoOccupied = robotDmsEditorService.findListByAreaTypeAndOccupiedAndRobotName(2, 0, null);
//                    if (listByAreaTypeAndNoOccupied != null) {
//                        for (RobotDmsEditor robotDmsEditor : listByAreaTypeAndNoOccupied) {
//                            boolean isContain = passedPoints.contains(robotDmsEditor.getAreaCenterPoint());
//                            if (isContain) {
//                                //锁定当前管控区域
//                                robotDmsEditor.setOccupiedStatus(1);
//                                robotDmsEditor.setOccupiedRobotName(robot.getVehicleId());
//                                robotDmsEditorService.saveOrUpdate(robotDmsEditor);
//                            }
//                        }
//                    }
                    //----------------------------------------------------------------------------------------------------------------------------
                    //获取机器人执行单步骤任务的起始点和终点，这里的起点和终点是区域中心点（光通信站点）
                    String beginPort = pathPlanning.get(j).getStartPoint();
                    String endPort = pathPlanning.get(j).getEndPoint();
                    //查询任务链执行结果
                    boolean queryTaskChainFlag = false;
                    try {
                        queryTaskChainFlag = queryTaskChainResult(robotWaybill, beginPort);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //查询任务链成功，说明当前机器人到达光通信站
                    if (queryTaskChainFlag) {
                        Map<String, String> taskListMapStatus = (Map<String, String>) redisUtil.stringWithGet("robot_tasklist_status_res");
                        String taskListStatus = taskListMapStatus.get(robot.getVehicleId());
                        RobotTaskListResDTO model = JSON.parseObject(taskListStatus, RobotTaskListResDTO.class);

                        //在执行第一条任务前，没有任务链的状态，因此把j==0加入初始判断条件
                        if (model.getRetCode() == 0 && model.getTasklistStatus().getTaskListStatus() == 4 &&
                                (robot.getVehicleId() + "_" + waybillId + beginPort).equals(model.getTasklistStatus().getTaskListName())
                                || j == 0) {
                            //对于初始站点包含自动门，并且该自动门属于内部不能安装光通信站类型，则在运行前直接关闭自动门
                            if (j == 0) {
                                //判断开始站点所在的区域是否具有自动门，如果有，到达结束站点时，应该关闭开始站点所在区域的自动门
                                List<IotEquipment> automaticDoorByEquipmentRegion = ioTEquipmentService.getAutomaticDoorByEquipmentRegion(beginPort);
                                for (IotEquipment iotEquipment : automaticDoorByEquipmentRegion) {
                                    //如果自动门内部没有安装光通信站，则在任务开始时，即关闭自动门
                                    if ("否".equals(iotEquipment.getEquipmentIsInstallDms())) {
                                        try {
                                            closeAutomaticDoor(robotWaybill, iotEquipment);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            //对于非初始站点包含自动门
                            if (j != 0) {
                                //判断开始站点所在的区域是否具有自动门，如果有，到达结束站点时，应该关闭开始站点所在区域的自动门
                                List<IotEquipment> automaticDoorByEquipmentRegion = ioTEquipmentService.getAutomaticDoorByEquipmentRegion(beginPort);
                                for (IotEquipment iotEquipment : automaticDoorByEquipmentRegion) {
                                    String adjacentSite = iotEquipment.getAdjacentSite();
                                    List<String> adjacentSites = Arrays.asList(adjacentSite.split(","));
                                    if (!(Objects.requireNonNull(adjacentSites).contains(beginPort) && adjacentSites.contains(endPort))) {
                                        try {
                                            closeAutomaticDoor(robotWaybill, iotEquipment);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                //获取机器人执行单步骤任务的起始点和终点，这里的起点和终点是区域中心点（光通信站点）
                                String frontBeginPort = pathPlanning.get(j - 1).getStartPoint();
                                String frontEndPort = pathPlanning.get(j - 1).getEndPoint();
                                List<IotEquipment> frontAutomaticDoorByEquipmentRegion = ioTEquipmentService.getAutomaticDoorByEquipmentRegion(frontBeginPort);
                                for (IotEquipment frontIotEquipment : frontAutomaticDoorByEquipmentRegion) {
                                    List<String> adjacentSites;
                                    String adjacentSite = frontIotEquipment.getAdjacentSite();
                                    adjacentSites = Arrays.asList(adjacentSite.split(","));
                                    //如果自动门内部安装光通信站，则在机器人到达下个区域，再关闭上个区域内的自动门
                                    if ("是".equals(frontIotEquipment.getEquipmentIsInstallDms())) {
                                        if (Objects.requireNonNull(adjacentSites).contains(frontBeginPort) && adjacentSites.contains(frontEndPort)) {
                                            try {
                                                closeAutomaticDoor(robotWaybill, frontIotEquipment);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                            //需要锁上掉的机器人名称和路径
                            AlgorithmResResultDTO.Path willExecutePath = new AlgorithmResResultDTO.Path();
                            willExecutePath.setStartPoint(beginPort);
                            willExecutePath.setEndPoint(endPort);
                            RobotLockPathAndPoint willExecutePathAndPoint = new RobotLockPathAndPoint();
                            willExecutePathAndPoint.setLockedPath(willExecutePath);
                            willExecutePathAndPoint.setLockedPoint(beginPort);
                            dmsLockedPointMap.put(robot.getVehicleId(), willExecutePathAndPoint);

                            List<String> keyList = new ArrayList<>(dmsLockedPointMap.keySet());
                            //当前站点是被哪个机器人锁上的
                            for (int i = 0; i < keyList.size(); i++) {
                                String vehicleIdFirst = keyList.get(i);
                                if (!robot.getVehicleId().equals(vehicleIdFirst)) {
                                    //如果两台机器人任务的起点和终点刚好相反，需要重新进行路径规划
                                    if (dmsLockedPointMap.get(vehicleIdFirst) != null
                                            && endPort.equals(dmsLockedPointMap.get(vehicleIdFirst).getLockedPath().getStartPoint())
                                            && beginPort.equals(dmsLockedPointMap.get(vehicleIdFirst).getLockedPath().getEndPoint())) {
                                        //其它机器人任务执行的多线程关闭标志为false
                                        if (!closeThreadFlag.get()) {
                                            //如果发现站点被锁定，则设置标志为true
                                            closeThreadFlag.set(true);
                                            //在重新进行路径规划算法前，设置该机器人后续的子任务不可以被执行
                                            queryExecutedConcurrentHashMap.put(vehicleIdFirst, false);
                                            //遇到路径冲突，需要重新请求路径规划
                                            boolean flag = true;
                                            while (flag) {
                                                //每隔四秒执行一次循环，防止重新规划路径方法被多次执行
                                                try {
                                                    Thread.sleep(4000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                //获取对当前站点锁定的机器人的任务状态信息
                                                Map<String, String> taskListMapStatusLockedVehicleId = (Map<String, String>) redisUtil.stringWithGet("robot_tasklist_status_res");
                                                String taskListStatusLockedVehicleId = taskListMapStatusLockedVehicleId.get(vehicleIdFirst + "action");
                                                RobotTaskListResDTO modelLockedVehicleId = JSON.parseObject(taskListStatusLockedVehicleId, RobotTaskListResDTO.class);
                                                //获取先锁定该站点对应机器人所执行任务的运单信息
                                                RobotWaybill robotWaybillVehicleIdFirst = getWaybillByRobotNameAndTaskStatus(vehicleIdFirst, 1);
                                                //先锁定该站点的机器人应当执行完成光通信区域内的动作任务；
                                                if (vehicleIdFirst != null && robotWaybillVehicleIdFirst != null && modelLockedVehicleId.getRetCode() == 0 && modelLockedVehicleId.getTasklistStatus().getTaskListStatus() == 4
                                                        && (vehicleIdFirst + "action" + "_" + robotWaybillVehicleIdFirst.getId() + endPort).equals(modelLockedVehicleId.getTasklistStatus().getTaskListName())
                                                        && (robot.getVehicleId() + "_" + waybillId + beginPort).equals(model.getTasklistStatus().getTaskListName())) {
                                                    log.info("--------调度系统重新规划了机器人运行路径-----------");
                                                    log.info("请求的机器人[{}],起点[{}]，终点[{}]", robot.getVehicleId(), beginPort, endPort);
                                                    //设置标志位为false
                                                    flag = false;
                                                    //重新规划前，移除当前被锁的目标点
                                                    dmsLockedPointMap.remove(vehicleIdFirst);
                                                    //-------------------------------------(混合调度)-------------------------------------------------------------------------------
                                                    //子任务执行完成，释放掉占用的管控区域
                                                    List<RobotDmsEditor> listByAreaTypeAndOccupied1 = robotDmsEditorService.findListByAreaTypeAndOccupiedAndRobotName(2, 1, vehicleIdFirst);
                                                    if (listByAreaTypeAndOccupied1 != null) {
                                                        for (RobotDmsEditor robotDmsEditor : listByAreaTypeAndOccupied1) {
                                                            //除了当前站点所在的管控区域，即结束站点所在的管控区域，其它管控区域设置为非占用
                                                            if (!endPort.equals(robotDmsEditor.getAreaCenterPoint())) {
                                                                //设置管控区域的状态为非占用
                                                                robotDmsEditor.setOccupiedRobotName("");
                                                                robotDmsEditor.setOccupiedStatus(0);
                                                                robotDmsEditorService.saveOrUpdate(robotDmsEditor);
                                                            }
                                                        }
                                                    }
                                                    //-----------------------------------------------------------------------------------------------------------------------------
                                                    try {
                                                        rePathPlanningDms(vehicleIdFirst, waybillId);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                        break Label;
                                        //如果两台机器人导航的终点相同，则后执行的机器人等待先执行的机器人执行完
                                    } else if (dmsLockedPointMap.get(vehicleIdFirst) != null && endPort.equals(dmsLockedPointMap.get(vehicleIdFirst).getLockedPath().getEndPoint())) {
                                        i--;
                                        //如果先执行的机器人的起点跟后执行的机器人的终点相同
                                    } else if (dmsLockedPointMap.get(vehicleIdFirst) != null && endPort.equals(dmsLockedPointMap.get(vehicleIdFirst).getLockedPath().getStartPoint())) {
                                        //如果结束站点被锁，则不能执行对应的子任务
                                        if (endPort.equals(dmsLockedPointMap.get(vehicleIdFirst).getLockedPoint())) {
                                            lockedSubTask.put(robot.getVehicleId(), false);
                                        } else {
                                            lockedSubTask.put(robot.getVehicleId(), true);
                                        }
                                    } else {
                                        lockedSubTask.put(robot.getVehicleId(), true);
                                    }
                                }
                            }

                            //由于先执行的机器人的起点跟后执行的机器人的终点相同，需要把后执行的机器人任务锁上,防止两台机器人相遇
                            //如果当前机器人没有被锁
                            if (lockedSubTask.get(robot.getVehicleId())) {
                                //获取当前路径的长度
                                float totalCurveLength = pathPlanning.get(j).getTotalCurveLength();
                                //获取开始站点绑定的光通信站信息
                                RobotDms robotDms = robotDmsService.getDmsByDmsPoint(beginPort);
                                String dmsIp = robotDms.getDmsIp();

                                //封装开始站点
                                List<RobotRunDTO> currentExecuteTaskList = new ArrayList<>();
                                RobotRunDTO firstRunDTO = new RobotRunDTO();
                                firstRunDTO.setVehicle(robot.getVehicleId());
                                firstRunDTO.setStation(beginPort);
                                firstRunDTO.setInstruction("机器人路径导航");
                                currentExecuteTaskList.add(firstRunDTO);

                                //封装结束站点
                                RobotRunDTO secondRunDTO = new RobotRunDTO();
                                secondRunDTO.setVehicle(robot.getVehicleId());
                                secondRunDTO.setStation(endPort);
                                secondRunDTO.setInstruction("机器人路径导航");
                                currentExecuteTaskList.add(secondRunDTO);

                                try {
                                    //获取工作点的自动门设备
                                    List<IotEquipment> automaticDoors = new ArrayList<>();
                                    List<IotEquipment> automaticDoors1 = ioTEquipmentService.getAutomaticDoorByEquipmentRegion(beginPort);
                                    List<IotEquipment> automaticDoors2 = ioTEquipmentService.getAutomaticDoorByEquipmentRegion(endPort);
                                    automaticDoors.addAll(automaticDoors1);
                                    automaticDoors.addAll(automaticDoors2);
                                    List<String> adjacentSites;
                                    for (IotEquipment automaticDoor : automaticDoors) {
                                        String adjacentSite = automaticDoor.getAdjacentSite();
                                        adjacentSites = Arrays.asList(adjacentSite.split(","));
                                        if ("是".equals(automaticDoor.getEquipmentIsInstallDms())) {
                                            if (Objects.requireNonNull(adjacentSites).contains(beginPort) && adjacentSites.contains(endPort)) {
                                                openAutomaticDoor(robotWaybill, automaticDoor);
                                            }
                                        }
                                    }
                                    //运行子任务中的起始点到终点的导航任务
                                    runMultiRobotsDmsSubTaskInstruction(beginPort, endPort, totalCurveLength, robotWaybill, dmsIp, currentExecuteTaskList);
                                    //在重新进行路径规划算法后，设置该机器人后续的子任务可以被执行
                                    queryExecutedConcurrentHashMap.put(robot.getVehicleId(), true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //一个任务链中一个子任务执行完成，则移除当前子任务
                                if (j == pathPlanning.size() - 1) {
                                    //当每个子任务执行完成，设置可以执行动作为true
                                    //设置动作可以被执行
                                    executedConcurrentHashMap.put(robotWaybill.getRobotName(), true);
                                }
                            } else {
                                //如果当前机器人被锁，等待被释放
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                j--;
                            }
                        } else {
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //如果当前导航正在进行，需要等待上一段任务执行完成
                            j--;
                        }
                    } else {
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //如果当前没有到达光通信站点，需要等待上一段任务执行完成
                        j--;
                    }
                }
            });
            //开始执行线程任务
            thread.setName("pathPlanningThread" + dto.getRobotName());
            thread.start();
        }
    }

    /**
     * 运行子任务中的起始点到终点的导航任务
     *
     * @param beginPort
     * @param endPort
     * @param robotIp
     * @param totalCurveLength
     */
    public void runPathNavigate(String beginPort, String endPort, String robotIp, float totalCurveLength) {
        //如果起始点和终点的坐标一样，则表示需要在该点等待一定时间(totalCurveLength = 0.表示原地导航)
        if (beginPort.equals(endPort) && totalCurveLength != 0.0) {
            try {
                Thread.sleep((long) (totalCurveLength * 1500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            //根据ip获取机器人
            RobotBasicInfo robot = robotBasicInfoService.findByIp(robotIp);

            /*
             * Modbus处理逻辑（leo add）
             * 1.获取全部设备列表
             * 2.遍历设备列表，获取当前设备的相邻站点字段 adjacentSite
             * 3.如果adjacentSite不为空，则判断 adjacentSite 是否包含 beginPort、endPort之一，若包含，则退出循环，拿到该设备对象。
             *  3.1 如果adjacentSite两者都包含，则说明该路径经过 门，调用开门方法→（开门指令下发成功后）调用等待方法
             *  3.2 如果只有beginPort包含，则说明该路径远离 设备，调用关门方法
             *  3.3 如果只有endPort包含，则说明该路径接近门，不做处理（后期可以考虑提前调用开门方法）
             */
            IotEquipment bindingModbusEquipment = new IotEquipment(); //待绑定设备
            // 1.获取全部设备列表
            List<IotEquipment> modbusEquipmentList = ioTEquipmentService.listByEquipmentType("automaticDoor");
            List<String> adjacentSites = null;
            // 2.遍历设备列表，获取当前设备的相邻站点字段 adjacentSite
            for (IotEquipment modbusEquipment : modbusEquipmentList) {
                String adjacentSite = modbusEquipment.getAdjacentSite();
                adjacentSites = Arrays.asList(adjacentSite.split(","));
                //3.如果adjacentSite不为空，则判断 adjacentSite 是否包含 beginPort、endPort之一，若包含，则判断设备是否在线
                if (StringUtils.isNotEmpty(adjacentSite) && Objects.requireNonNull(adjacentSites).contains(beginPort) || adjacentSites.contains(endPort)) {

                    ModbusTcpRead checkOnLineModbusTcpRead = new ModbusTcpRead(modbusEquipment.getSlaveId(), ConstFunCode.func03,
                            modbusEquipment.getEquipStateOffset(), ConstDataType.TWO_BYTE_INT_SIGNED, modbusEquipment.getEquipmentIp(), modbusEquipment.getEquipmentPort());
                    Object isPlcOnline = ModbusUtils.readModbusByTcp(checkOnLineModbusTcpRead);
                    //如果在线，则获取该设备对象，并退出循环；如果不在线，则设置bindingModbusEquipment为null，并退出循环
                    if (isPlcOnline != null) {
                        bindingModbusEquipment = modbusEquipment;
                    } else {
                        bindingModbusEquipment = null;
                        log.error("设备{}不在线", modbusEquipment.getEquipmentName());
                    }
                    break;

                } else {
                    bindingModbusEquipment = null;
                }
            }
            try {
                // 3.1 如果adjacentSite两者都包含，则说明该路径经过 门，调用开门方法→（开门指令下发成功后）调用等待方法
                if (bindingModbusEquipment != null && adjacentSites != null && Objects.requireNonNull(adjacentSites).contains(beginPort) && adjacentSites.contains(endPort)) {
                    // 判断待写入值与实际值写入值是否一致，同时判断设备状态值与实际写入值是否一致，若有一个存在不一致，则执行方法
                    if (!isEqualWritingAndWrited(bindingModbusEquipment, 1) || !isEqualEquipAndWrited(bindingModbusEquipment)) {
                        // 3.1.1 调用开门方法
                        if (modbusGenericRw(bindingModbusEquipment, 4, 1).isSuccess()) {
                            // 3.1.2 调用等待方法
                            if (modbusWaitOperation(bindingModbusEquipment, 1)) {
                                log.info("开门成功,准备通行");
                            }
                        }
                        //开启定时任务，如果当前的站点不等于结束站点，则每隔一秒发一次开门指令
                        String cron = "*/1 * * * * ?";
                        IotEquipment finalBindingModbusEquipment = bindingModbusEquipment;
                        //移除同名定时任务再开启
                        customScheduledTaskRegistrar.removeTriggerTask(robot.getVehicleId());
                        customScheduledTaskRegistrar.addTriggerTask(robot.getVehicleId(),
                                //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                                () -> {
                                    try {
                                        //获取机器人实时状态
                                        RobotMonitorVO robotMonitorVO = map.get(robot.getVehicleId());
                                        if (!endPort.equals(robotMonitorVO.getCurrentStation())) {
                                            // 3.1.1 调用开门方法
                                            modbusGenericRw(finalBindingModbusEquipment, 4, 1);
                                        } else {//这个判断条件在网络丢包失去当前点位置时会永远为false，导致该线程无法关闭
                                            //机器人到达终点以后，则删除定时任务
                                            customScheduledTaskRegistrar.removeTriggerTask(robot.getVehicleId());
                                        }
                                    } catch (Exception e) {
                                        //发生异常，则删除定时任务
                                        customScheduledTaskRegistrar.removeTriggerTask(robot.getVehicleId());
                                        e.printStackTrace();
                                    }
                                }, triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
                    }
                }
                // 3.2 如果只有beginPort被包含，则说明该路径远离 设备，调用关门方法
                else if (bindingModbusEquipment != null && adjacentSites != null && adjacentSites.contains(beginPort)) {
                    if (modbusGenericRw(bindingModbusEquipment, 4, 2).isSuccess()) {
                        log.info("正在远离设备，准备关门");
                    }
                }

            } catch (Exception e) {
                log.error("自动门处理发生异常: ", e);
            }
            // 执行单点步进导航的区域管控处理逻辑
            handleFocusAreaForOneStep(beginPort, endPort, robot.getVehicleId());
        }
    }

    /**
     * <单点步进导航区域管控处理逻辑入口> leo add
     *
     * @param beginPort 步进开始站点
     * @param endPort   步进结束站点
     * @param robotId   机器人id
     */
    public void handleFocusAreaForOneStep(String beginPort, String endPort, String robotId) {

        // 根据机器人id获取机器人信息
        RobotBasicInfo robotInfo = robotBasicInfoService.findByVehicleId(robotId);
        // 根据机器人id获取管控区域信息
        RobotDmsEditor currentRobotFocusArea = robotDmsEditorService.findFocusAreaByRobotId(robotId);

        try {
            // 获取目标点所在的管控区域
            RobotDmsEditor endPortArea = robotDmsEditorService.getFocusAreaByTargetPort(endPort);
            if (currentRobotFocusArea != null) {
                // 若机器人位于管控区域内，则判断结束点是否在管控区域内
                if (endPortArea == null) {
                    // 若结束点不在管控区域内，直接下发导航指令
                    log.info("机器人<{}>将驶离管控区域<{}>，准备导航至目标点<{}>", robotId, currentRobotFocusArea.getAreaName(), endPort);
                    sendPathNavigateInstruction(beginPort, endPort, robotId);
                    // 并在机器人抵达结束点后将区域占用状态置为0
                    setFreeFocusAreaByTargetPort(endPort, robotInfo, currentRobotFocusArea);
                } else if (endPortArea.getId().equals(currentRobotFocusArea.getId())) {
                    // 结束点在当前机器人所在管控区域内，直接下发导航指令
                    if (currentRobotFocusArea.getOccupiedStatus() != 1) {
                        currentRobotFocusArea.setOccupiedStatus(1);
                        robotDmsEditorService.saveOrUpdate(currentRobotFocusArea);
                    }
                    sendPathNavigateInstruction(beginPort, endPort, robotId);
                } else {
                    // 结束点位于另一管控区域，按管控规则导航至管控区域
                    log.info("机器人<{}>将由当前管控区域<{}>进入另一管控区域<{}>，准备导航至目标点<{}>", robotId, currentRobotFocusArea.getAreaName(), endPortArea.getAreaName(), endPort);
                    navFocusAreaByRule(beginPort, endPort, robotId, robotInfo, endPortArea);
                    // 并在机器人抵达结束点后将区域占用状态置为0
                    setFreeFocusAreaByTargetPort(endPort, robotInfo, currentRobotFocusArea);
                }
            } else { // 若机器人不在管控区域内
                if (endPortArea != null) {
                    log.info("机器人<{}>即将进入管控区域<{}>，准备导航至目标点<{}>", robotId, endPortArea.getAreaName(), endPort);
                    // 若结束点位于管控区域内，按管控规则导航至管控区域
                    navFocusAreaByRule(beginPort, endPort, robotId, robotInfo, endPortArea);
                } else {
                    // 若结束点不在管控区域内，直接下发导航指令
                    sendPathNavigateInstruction(beginPort, endPort, robotId);
                }
            }
        } catch (Exception e) {
            log.error("区域管控处理发生异常: ", e);
        }
    }


    /**
     * <根据规则导航至管控区域> (leo add)
     *
     * @param beginPort   步进起始站点
     * @param endPort     步进目标站点
     * @param robotId     机器人id
     * @param robotInfo   机器人基本信息
     * @param endPortArea 目标站点所在管控区域
     */
    public void navFocusAreaByRule(String beginPort, String endPort, String robotId, RobotBasicInfo
            robotInfo, RobotDmsEditor endPortArea) {
        //若区域未被占用
        if (endPortArea.getOccupiedStatus() == null || endPortArea.getOccupiedStatus() == 0) {

            //将区域占用状态置为1
            endPortArea.setOccupiedStatus(1);
            //将该区域的robotId设置为当前机器人id
            endPortArea.setRobotId(robotId);
            //更新区域信息
            if (robotDmsEditorService.saveOrUpdate(endPortArea)) {
                log.info("机器人<{}>已占用区域：<{}>", robotId, endPortArea.getAreaName());
            } else {
                log.error("更新区域信息失败");
            }
            //下发导航指令
            sendPathNavigateInstruction(beginPort, endPort, robotId);
        }
        //若区域被占用
        else {
            //等待占用状态解除
            log.info("机器人<{}>正在等待区域<{}>占用解除......", robotId, endPortArea.getAreaName());
            if (waitFreeFocusAreaByTargetPort(robotInfo, endPortArea)) {
                log.info("区域<{}>占用解除，机器人<{}>准备导航至目标点<{}>", endPortArea.getAreaName(), robotId, endPort);
                //若解除成功，将区域占用状态置为1
                endPortArea.setOccupiedStatus(1);
                //将该区域的robotId设置为当前机器人id
                endPortArea.setRobotId(robotId);
                //更新区域信息
                if (robotDmsEditorService.saveOrUpdate(endPortArea)) {
                    log.info("机器人<{}>已占用区域：<{}>", robotId, endPortArea.getAreaName());
                } else {
                    log.error("更新区域信息失败");
                }
                //下发导航指令
                sendPathNavigateInstruction(beginPort, endPort, robotId);
            }
        }
    }

    /**
     * <等待区域占用状态解除> (leo add)
     *
     * @param robotInfo   机器人基本信息
     * @param endPortArea 目标站点所在管控区域
     */
    public boolean waitFreeFocusAreaByTargetPort(RobotBasicInfo robotInfo, RobotDmsEditor endPortArea) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        CountDownLatch latch = new CountDownLatch(1);

        final boolean[] result = {false};

        // 定义一个Runnable任务来检查区域占用状态是否解除
        Runnable checkFocusAreaOccupiedTask = () -> {
            try {
                // 获取区域占用状态
                int occupiedStatus = robotDmsEditorService.getById(endPortArea.getId()).getOccupiedStatus();
                // 若区域占用状态为0，则说明解除成功
                if (occupiedStatus == 0) {
                    //关闭定时任务
                    scheduler.shutdown();
                    result[0] = true;
                    latch.countDown();
                }

            } catch (Exception e) {
                scheduler.shutdown();
                e.printStackTrace();
            }
        };
        // 每4秒查询一次区域状态占用情况
        scheduler.scheduleWithFixedDelay(checkFocusAreaOccupiedTask, 4, 4, TimeUnit.SECONDS);
        try {
            // 等待任务完成或超时
            boolean completed = latch.await(120, TimeUnit.SECONDS);
            if (!completed) {
                log.warn("机器人<{}>等待区域占用解除超时，请检查目标区域<{}>内机器人运行状态", robotInfo.getVehicleId(), endPortArea.getAreaName());
                // 等待现场人员检查完毕
                latch.await();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 确保调度器在任务完成后关闭
        scheduler.shutdown();
        return result[0];
    }

    /**
     * <根据目标站点解除管控区域占用状态> (leo add)
     * <p>
     *
     * @param targetPort            步进目标站点
     * @param robotInfo             机器人基本信息
     * @param currentRobotFocusArea 当前机器人所管控区域
     */
    public void setFreeFocusAreaByTargetPort(String targetPort, RobotBasicInfo robotInfo, RobotDmsEditor
            currentRobotFocusArea) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        CountDownLatch latch = new CountDownLatch(1);

        // 定义一个Runnable任务来检查机器人位置
        Runnable checkRobotPositionTask = () -> {
            try {
                // 获取机器人当前位置
                String currentStation = map.get(robotInfo.getVehicleId()).getCurrentStation();
//                String closestTarget = taskStatusMap.get(robotInfo.getVehicleId()).getTaskStatusPackage().getClosestTarget(); //连续任务导航时的判断条件
                // 如果机器人当前位置已经位于targetPort
                if (targetPort.equals(currentStation)) {
                    //关闭定时任务
                    scheduler.shutdown();
                    latch.countDown();
                    //设置区域占用状态为0
                    currentRobotFocusArea.setOccupiedStatus(0);
                    //将该区域的robotId设置为空
                    currentRobotFocusArea.setRobotId("");
                    //更新区域信息
                    if (robotDmsEditorService.saveOrUpdate(currentRobotFocusArea)) {
                        log.info("机器人<{}>已抵达目标点<{}>，解除区域<{}>占用", robotInfo.getVehicleId(), targetPort, currentRobotFocusArea.getAreaName());
                    } else {
                        log.error("更新区域信息失败");
                    }
                }

            } catch (Exception e) {
                scheduler.shutdown();
                e.printStackTrace();
            }
        };
        if (map != null && map.containsKey(robotInfo.getVehicleId())) {
            // 每4秒查询一次机器人当前位置
            scheduler.scheduleWithFixedDelay(checkRobotPositionTask, 4, 4, TimeUnit.SECONDS);
            try {
                // 等待重置区域状态完成
                latch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            log.error("获取机器人位置失败，请检查推送服务是否正常运行");
        }
        // 确保调度器在任务完成后关闭
        scheduler.shutdown();

    }

    /**
     * <下发相邻站点路径导航指令> (leo add)
     *
     * @param beginPort 步进起始点
     * @param endPort   步进结束点
     * @param robotId   机器人Id
     */
    public void sendPathNavigateInstruction(String beginPort, String endPort, String robotId) {

        // 根据机器人ID获取机器人IP
        String robotIp = robotBasicInfoService.findByVehicleId(robotId).getCurrentIp();

        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        String convertString = "{\"source_id\":\"" + beginPort + "\",\"id\":\"" + endPort + "\"}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        String instruction = "5A010001000000" + dataLength + "0BEB000000000000" + data;
        if (!map.containsKey(robotIp + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人其它端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
        if (!result.isSuccess()) {
            for (int i = 0; i < 3; i++) {
                log.error("第{}次执行机器人{}路径导航失败，起点：{}，终点：{}，时间戳：{}\"", i, robotId, beginPort, endPort, System.currentTimeMillis());
                //连接机器人其它端口
                robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
                EntityResult newResult = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, instruction);
                log.info("第{}次再次执行机器人{}路径导航，起点：{}，终点：{}, 时间戳：{}", i, robotId, beginPort, endPort, System.currentTimeMillis());
                if (newResult.isSuccess()) {
                    break;
                } else {
                    if (i == 2) {
                        log.error("运行机器人导航任务失败:{}", convertString);
                    }
                    try {
                        //每隔3秒执行一次导航任务下发
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            log.info("机器人<{}>正在由<{}>运行至<{}>", robotId, beginPort, endPort);
        }
    }

    /**
     * 遇到路径冲突，需要重新请求路径规划
     *
     * @param pathList
     * @param vehicleId
     * @throws Exception
     */
    public void rePathPlanning(List<RobotPathVO> pathList, String vehicleId, String waybillId) throws Exception {
        List<RobotNavigation> navigationList = robotNavigationService.list();
        List<RobotPathPlanDTO.RobotPath> robotPathList = new ArrayList<>();
        for (RobotNavigation robotNavigation : navigationList) {
            RobotPathPlanDTO.RobotPath robotPath;
            String groupName = robotBasicInfoService.findByName(robotNavigation.getRobotName()).get(0).getGroupName();
            if (robotNavigation.getRobotName().equals(vehicleId)) {
                robotPath = robotTaskService.encapsulateData(robotNavigation.getRobotName(), groupName, robotNavigation.getNextBeginPort(), robotNavigation.getEndPort());
            } else {
                robotPath = robotTaskService.encapsulateData(robotNavigation.getRobotName(), groupName, robotNavigation.getBeginPort(), robotNavigation.getEndPort());
            }
            robotPathList.add(robotPath);
        }
        //开始请求路径规划算法执行
        log.info("开始请求路径规划算法,请求的任务：{}", JSON.toJSONString(robotPathList));
        AlgorithmRequestResDTO algorithmRequestResDTO = robotTaskService.startPathPlanningTask(pathList, robotPathList, null);
        log.info("开始请求路径规划算法,请求的路径{}", JSON.toJSONString(pathList));
        if ("success".equals(algorithmRequestResDTO.getStatus())) {
            String cron = "*/1 * * * * ?";
            customScheduledTaskRegistrar.addTriggerTask(
                    algorithmRequestResDTO.getTaskId(),
                    //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                    () -> {
                        try {
                            runPathPlanningTask(algorithmRequestResDTO.getTaskId(), waybillId, pathList);
                        } catch (Exception e) {
                            //如果查询到算法执行返回的结果，则删除定时任务
                            log.error("执行路径规划算法失败，错误信息{}", e.getMessage());
                            customScheduledTaskRegistrar.removeTriggerTask(algorithmRequestResDTO.getTaskId());
                            try {
                                setTaskFailData(waybillId, waybillId, vehicleId, "执行路径规划算法失败");
                            } catch (Exception ex) {
                                log.error("向第三方系统同步运单状态失败，错误信息{}", e.getMessage());
                            }
                            e.printStackTrace();
                        }
                    },
                    triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
        }
    }


    /**
     * 遇到路径冲突，需要重新请求路径规划
     *
     * @param vehicleId
     * @throws Exception
     */
    public void rePathPlanningDms(String vehicleId, String waybillId) throws Exception {
        //由于先锁定站点的机器人子任务已经执行完成，因此这里不能从任务队列中取待执行任务
        RobotWaybill robotWaybillVehicleIdFirst = getWaybillByRobotNameAndTaskStatus(vehicleId, 1);
        String pendingTask = robotWaybillVehicleIdFirst.getPendingTask();
        List<RobotRunDTO> pendingTaskList = JSON.parseArray(pendingTask, RobotRunDTO.class);

        //获取机器人场景拓扑区域地图
        List<RobotPathVO> pathList = new ArrayList<>();
        //封装请求路径规划算法的任务
        List<RobotPathPlanDTO.RobotPath> robotPathList = new ArrayList<>();
        for (String key : subTasks.keySet()) {
            RobotPathPlanDTO.RobotPath robotPath;
            String groupName = robotBasicInfoService.findByName(key).get(0).getGroupName();
            List<AlgorithmResResultDTO.Path> paths = subTasks.get(key);
            if (key.equals(vehicleId)) {
                //获取子任务的起点
                String startPoint = getAreaCenterPointByCurrentPoint(pendingTaskList.get(0).getStation());
                //获取子任务的终点
                String endPoint = getAreaCenterPointByCurrentPoint(pendingTaskList.get(1).getStation());
                robotPath = robotTaskService.encapsulateData(key, groupName, startPoint, endPoint);
            } else {
                robotPath = robotTaskService.encapsulateData(key, groupName, paths.get(0).getStartPoint(), paths.get(paths.size() - 1).getEndPoint());
            }
            robotPathList.add(robotPath);
            List<RobotPathVO> processedPaths = getProcessedPaths(key);
            pathList.addAll(processedPaths);
        }
        //开始请求路径规划算法执行
        log.info("开始请求路径规划算法,请求的任务：{}", JSON.toJSONString(robotPathList));
        AlgorithmRequestResDTO algorithmRequestResDTO = robotTaskService.startPathPlanningTask(pathList, robotPathList, null);
        log.info("开始请求路径规划算法,请求的路径{}", JSON.toJSONString(pathList));
        if ("success".equals(algorithmRequestResDTO.getStatus())) {
            String cron = "*/2 * * * * ?";
            customScheduledTaskRegistrar.addTriggerTask(
                    algorithmRequestResDTO.getTaskId(),
                    //()->log.info("key:{},开始执行定时任务---{}", instruction, cron),
                    () -> {
                        try {
                            runPathPlanningTaskDms(algorithmRequestResDTO.getTaskId(), waybillId, true);
                        } catch (Exception e) {
                            //如果查询到算法执行返回的结果，则删除定时任务
                            log.error("执行路径规划算法失败，错误信息{}", e.getMessage());
                            customScheduledTaskRegistrar.removeTriggerTask(algorithmRequestResDTO.getTaskId());
                            try {
                                setTaskFailData(waybillId, waybillId, vehicleId, "执行路径规划算法失败");
                            } catch (Exception ex) {
                                log.error("向第三方系统同步运单状态失败，错误信息{}", e.getMessage());
                            }
                            e.printStackTrace();
                        }
                    },
                    triggerContext -> new CronTrigger(cron).nextExecutionTime(triggerContext));
        }
    }

    /**
     * <解析机器人基本信息数据> leo update
     *
     * @param ctx   通道处理器上下文
     * @param bytes 已转换为字节数组的报文数据
     */
    private void parseRobotBasicInfoData(ChannelHandlerContext ctx, byte[] bytes) {
        String message = DataConvertUtil.BytesToString(bytes);
        int beginIndex = message.indexOf("{");
        String data = message.substring(beginIndex);
        RobotStatusInfoResDTO dto = JSON.parseObject(data, RobotStatusInfoResDTO.class);
        //推送所有机器人的x，y,以及电池和置信度信息
        RobotMonitorVO robotMonitorVO = new RobotMonitorVO();
        robotMonitorVO.setVehicleId(dto.getVehicleId());
        Object[] diArr = dto.getDI();
        ArrayList<String> dis = new ArrayList<>();
        Map<String, String> disMap = new HashMap<>();
        for (int i = 0; i < dto.getDI().length; i++) {
            DiOrDoDTO diDto = JSON.parseObject(diArr[i].toString(), DiOrDoDTO.class);
            String str = diDto.getId() + ":" + diDto.getStatus();
            dis.add(str);
            disMap.put(Integer.toString(diDto.getId()), java.lang.Integer.toString(diDto.getStatus()));
        }
        robotMonitorVO.setDI(dis.toString());
        Object[] doArr = dto.getDO();
        ArrayList<String> dos = new ArrayList<>();
        Map<String, String> dosMap = new HashMap<>();
        for (int i = 0; i < dto.getDO().length; i++) {
            DiOrDoDTO doDto = JSON.parseObject(doArr[i].toString(), DiOrDoDTO.class);
            String str = doDto.getId() + ":" + doDto.getStatus();
            dos.add(str);
            dosMap.put(Integer.toString(doDto.getId()), java.lang.Integer.toString(doDto.getStatus()));
        }
        robotMonitorVO.setDO(dos.toString());

        //设置di、do数组
        robotMonitorVO.setDisMap(disMap.toString());
        robotMonitorVO.setDosMap(dosMap.toString());
        robotMonitorVO.setRobotNote(dto.getRobotNote());
        robotMonitorVO.setCurrentMap(dto.getCurrentMap());
        robotMonitorVO.setX(dto.getX());
        robotMonitorVO.setY(dto.getY());
        robotMonitorVO.setAngle(DataUtil.format(Float.parseFloat(dto.getAngle()) / Math.PI * 180) + "°");
        robotMonitorVO.setOdo(DataUtil.format(dto.getOdo() / 1000) + "km");
        robotMonitorVO.setTodayOdo(DataUtil.format(dto.getTodayOdo()) + "m");
        robotMonitorVO.setTotalTime(TimeFormatUtil.formatTime((long) dto.getTotalTime()));
        robotMonitorVO.setTime(TimeFormatUtil.formatTime((long) dto.getTime()));
        robotMonitorVO.setBlocked(dto.isBlocked());
        robotMonitorVO.setVx(DataUtil.format(dto.getVx()) + "m/s");
        robotMonitorVO.setVy(DataUtil.format(dto.getVy()) + "m/s");
        robotMonitorVO.setW(DataUtil.format(dto.getW()) + "°/s");
        robotMonitorVO.setCurrentStation(dto.getCurrentStation());
        robotMonitorVO.setBrake(dto.isBrake());
        robotMonitorVO.setVoltage(DataUtil.format(dto.getVoltage()) + "V");
        robotMonitorVO.setCurrent(DataUtil.format(dto.getCurrent()) + "A");
        if (dto.getRelocStatus() != null) {
            robotMonitorVO.setRelocStatus(Objects.requireNonNull(RobotRelocStatusEnum.getByCode(dto.getRelocStatus())).getName());
        }
        robotMonitorVO.setSoftEmc(dto.isSoftEmc());
        robotMonitorVO.setConfidence(dto.getConfidence());
        robotMonitorVO.setBatteryLevel(Float.parseFloat(DataUtil.format(dto.getBatteryLevel() * 100)));
        robotMonitorVO.setCharging(dto.isCharging());
        robotMonitorVO.setTaskStatus(Objects.requireNonNull(NavigationStatusEnum.getByCode(dto.getTaskStatus())).getName());
        robotMonitorVO.setJackStatus(Objects.requireNonNull(JackStatusEnum.getByCode(dto.getJack().getJackState())).getName());
        if (dto.getCurrentLock().isLocked()) {
            if (dto.getCurrentLock().getIp().equals(currentServerIp)) {
                robotMonitorVO.setControlStatus(ControlStatusEnum.SEIZED_CONTROL.getName());
            } else {
                robotMonitorVO.setControlStatus(ControlStatusEnum.LOSS_OF_CONTROL.getName());
            }
        } else {
            robotMonitorVO.setControlStatus(ControlStatusEnum.UN_PREEMPTED_CONTROL.getName());
        }
        //控制器湿度、温度、电压
        robotMonitorVO.setControllerTemp(dto.getControllerTemp());
        robotMonitorVO.setControllerHumi(dto.getControllerHumi());
        robotMonitorVO.setControllerVoltage(dto.getControllerVoltage());

        //机器人控制权详情
        RobotControlResDTO currentLock = dto.getCurrentLock();
        robotMonitorVO.setControlDesc(currentLock.getDesc());
        robotMonitorVO.setControlIp(currentLock.getIp());
        robotMonitorVO.setControlLocked(currentLock.isLocked());
        robotMonitorVO.setControlNickName(currentLock.getNickName());
        robotMonitorVO.setControlPort(currentLock.getPort());
        robotMonitorVO.setControlTimeT(currentLock.getTimeT());
        robotMonitorVO.setControlType(currentLock.getType());

        //机器人叉车详情
        RobotForkResDTO fork = dto.getFork();
        if (fork != null) {
            robotMonitorVO.setForkHeight(fork.getForkHeight());
            robotMonitorVO.setForkHeightInPlace(fork.isForkHeightInPlace());
            robotMonitorVO.setForkAutoFlag(fork.isForkAutoFlag());
            robotMonitorVO.setForwardVal(fork.getForwardVal());
            robotMonitorVO.setForwardInPlace(fork.isForwardInPlace());
            robotMonitorVO.setForkPressureActual(fork.getForkPressureActual());
        }

        //任务链信息
        RobotTaskListStatusDTO tasklistStatus = dto.getTasklistStatus();
        robotMonitorVO.setTaskListName(tasklistStatus.getTaskListName());
        robotMonitorVO.setTaskListStatus(tasklistStatus.getTaskListStatus());

        //急停按钮处于激活状态
        robotMonitorVO.setEmergency(dto.isEmergency());
        //单舵轮机器人当前的舵轮角度, 单位 rad
        robotMonitorVO.setSteer(dto.getSteer());
        //机器人严重错误+机器人普通错误
        List<String> fatalsAndErrors = new ArrayList<>();
        //机器人严重错误
        List<RobotWarningInfoDTO> fatals = new ArrayList<>();
        for (int i = 0; i < dto.getFatals().length; i++) {
            RobotWarningInfoDTO robotWarningInfoDTO = new RobotWarningInfoDTO();
            robotWarningInfoDTO.setVehicleId(dto.getVehicleId());
            robotWarningInfoDTO.setMessage(dto.getFatals()[i].toString());
            fatals.add(robotWarningInfoDTO);
            fatalsAndErrors.add(dto.getFatals()[i].toString());
        }
        robotMonitorVO.setFatals(fatals);

        //机器人普通错误
        List<RobotWarningInfoDTO> errors = new ArrayList<>();
        for (int i = 0; i < dto.getErrors().length; i++) {
            RobotWarningInfoDTO robotWarningInfoDTO = new RobotWarningInfoDTO();
            robotWarningInfoDTO.setVehicleId(dto.getVehicleId());
            robotWarningInfoDTO.setMessage(dto.getErrors()[i].toString());
            errors.add(robotWarningInfoDTO);
            fatalsAndErrors.add(dto.getErrors()[i].toString());
        }
        robotMonitorVO.setErrors(errors);

        //机器人警告
        List<RobotWarningInfoDTO> warnings = new ArrayList<>();
        for (int i = 0; i < dto.getWarnings().length; i++) {
            RobotWarningInfoDTO robotWarningInfoDTO = new RobotWarningInfoDTO();
            robotWarningInfoDTO.setVehicleId(dto.getVehicleId());
            robotWarningInfoDTO.setMessage(dto.getWarnings()[i].toString());
            warnings.add(robotWarningInfoDTO);
        }
        robotMonitorVO.setWarnings(warnings);

        //机器人通知
        List<RobotWarningInfoDTO> notices = new ArrayList<>();
        for (int i = 0; i < dto.getNotices().length; i++) {
            RobotWarningInfoDTO infoDTO = new RobotWarningInfoDTO();
            infoDTO.setVehicleId(dto.getVehicleId());
            infoDTO.setMessage(dto.getNotices()[i].toString());
            notices.add(infoDTO);
        }
        robotMonitorVO.setNotices(notices);

        //当前导航路径上最后已经经过的站点
        String[] finishedPath = dto.getFinishedPath();
        if (finishedPath.length > 0) {
            robotMonitorVO.setEndFinishedPath(finishedPath[finishedPath.length - 1]);
        }
        //获取机器人实时信息，放进map
        map.put(dto.getVehicleId(), robotMonitorVO);
        //如果推送的数据中，机器人的名字相同，即key相同，则新的value之前的value
        //在界面展示的全部机器人的实时信息,每次从数据库中
        Map<String, RobotMonitorVO> robotMonitorMap;
        if (redisUtil.stringWithGet("robot_push_less_res") != null) {
            robotMonitorMap = (Map<String, RobotMonitorVO>) redisUtil.stringWithGet("robot_push_less_res");
        } else {
            robotMonitorMap = new HashMap<>();
        }
        robotMonitorMap.put(dto.getVehicleId(), robotMonitorVO);
        redisUtil.stringWithSet("robot_push_less_res", robotMonitorMap);

        //只推送在分组中存在的机器人数据
        BusinessDataVO businessDataVO = new BusinessDataVO();
        businessDataVO.setData(robotMonitorMap.values());
        businessDataVO.setCode("robot_push_less_res");
        pusherLiteService.sendBusinessMessage(businessDataVO);

//            //获取redis存储的当前在分组中存在的机器人名字
//            List<String> robots = (List<String>) redisUtil.stringWithGet("robots");
//            //获取在分组中存在的机器人信息
//            Map<String, RobotMonitorVO> monitorVOMap = new HashMap<>();
//            for (String robot : robots) {
//                Set<String> robotNames = map.keySet();
//                if (robotNames.contains(robot)) {
//                    RobotMonitorVO monitorVO = map.get(robot);
//                    monitorVOMap.put(robot, monitorVO);
//                }
//                //如果推送的数据中，机器人的名字相同，即key相同，则新的value之前的value
//                map.put(dto.getVehicleId(), robotMonitorVO);
//            }
    }

    /* ************* 通用modbus读写 (leo add) *************
     * ---参数说明---
     * ip：设备IP地址，
     * SlaveId：从机ID（默认为1），
     * equipStateOffset：寄存器设备状态位偏移地址，
     * writeOffset:寄存器写入位偏移地址 ，operationType
     */
    private boolean modbusRwOperation(IotEquipment modbusEquipment, int operationType, Integer writeValue) {

        String ip = modbusEquipment.getEquipmentIp();
        Integer port = modbusEquipment.getEquipmentPort();
        int slaveId = modbusEquipment.getSlaveId();
        int offset;
        if (operationType == 4 || operationType == 2) {
            offset = modbusEquipment.getWriteOffset();
        } else {
            offset = modbusEquipment.getEquipStateOffset();
        }
//        Integer value = writeValue;

        int functionCode;
        ModbusTcpRead modbusOperation;

        Object modbusRes = null;
        Object modbusResWrite = null;
        boolean isModbusRwOperationOver = false;

        switch (operationType) {
            case 1: // 读取线圈
                functionCode = ConstFunCode.func01;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.BooleanType, ip, port);
                break;
            case 2: // 写入线圈
                functionCode = ConstFunCode.func05;
                modbusOperation = new ModbusTcpWrite(slaveId, functionCode, offset, ConstDataType.BooleanType, ip, port);
                ((ModbusTcpWrite) modbusOperation).setWriteValue(writeValue == null ? "0" : (writeValue == 0 ? "0" : "1"));
                break;
            case 3: // 读取保持寄存器
                functionCode = ConstFunCode.func03;
                modbusOperation = new ModbusTcpRead(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);
                break;
            case 4: // 写入保持寄存器
                functionCode = ConstFunCode.func06;
                modbusOperation = new ModbusTcpWrite(slaveId, functionCode, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);
                ((ModbusTcpWrite) modbusOperation).setWriteValue(writeValue == null ? "0" : java.lang.String.valueOf(writeValue));
                break;
            default:
                throw new IllegalArgumentException("不受支持的操作类型");
        }

        // 分别处理读和写操作
        if (modbusOperation instanceof ModbusTcpWrite) {
            // 调用写方法
            modbusResWrite = ModbusUtils.writeTcpModbusByFunCode(((ModbusTcpWrite) modbusOperation));
            if (writeValue == java.lang.Integer.parseInt(modbusResWrite.toString())) {
                log.info("modbus写入结果: {}", modbusResWrite);
                isModbusRwOperationOver = true;

            } else {
                isModbusRwOperationOver = false;
                log.error("modbus写入结果: {}", modbusResWrite);
            }
        } else if (modbusOperation instanceof ModbusTcpRead) {
            // 调用读方法
            modbusRes = ModbusUtils.readModbusByTcp((ModbusTcpRead) modbusOperation);
            if (modbusRes != null && modbusRes.toString().length() > 0) {
                isModbusRwOperationOver = true;
            } else {
                isModbusRwOperationOver = false;
                log.error("modbus读取结果: {}", modbusRes);
            }
        } else {
            throw new IllegalStateException("异常值类型: " + modbusOperation.getClass().getName());
        }

        return isModbusRwOperationOver;
    }

    /* ************* 通用modbus等待 (leo add) *************
     * ---参数说明---
     * ip：设备IP地址，
     * SlaveId：从机ID（默认为1），
     * equipStateOffset：寄存器设备状态位偏移地址，
     * writeOffset:寄存器写入位偏移地址 ，operationType
     */
    public boolean modbusWaitOperation(IotEquipment modbusEquipment, int targetValue) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);// ThreadPoolExecutor创建线程池容错性更强
        String ip = modbusEquipment.getEquipmentIp();
        int port = modbusEquipment.getEquipmentPort();
        int slaveId = modbusEquipment.getSlaveId();
        int offset = modbusEquipment.getEquipStateOffset();
        int registerWriteOffset = modbusEquipment.getWriteOffset();

        final Object[] modbusRes = {null};
        final Object[] modbusResWrite = {null};

        //定义一个读取设备状态的读取器
        ModbusTcpRead modbusTcpRead = new ModbusTcpRead(slaveId, ConstFunCode.func03, offset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);
        //定义一个读取写入地址的读取器，地址在配置文件中定义
        ModbusTcpRead modbusTcpReadAddressWrite = new ModbusTcpRead(slaveId, ConstFunCode.func03, registerWriteOffset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);

        // 读取设备状态
        modbusRes[0] = ModbusUtils.readModbusByTcp(modbusTcpRead);
        // 读取写入状态
        modbusResWrite[0] = ModbusUtils.readModbusByTcp(modbusTcpReadAddressWrite);
        CountDownLatch latch1 = new CountDownLatch(1);

        // 定义一个Runnable任务来检查设备值和写入值是否一致
        ScheduledExecutorService finalScheduler = scheduler;
        Runnable checkEquipAndWriteValueTask = () -> {
            try {
                log.info("正在获取modbus设备状态值。。。。。。。");
                // 读取设备状态
                modbusRes[0] = ModbusUtils.readModbusByTcp(modbusTcpRead);
                log.info("modbus设备状态: {}", modbusRes[0]);
                // 读取写入状态
                modbusResWrite[0] = ModbusUtils.readModbusByTcp(modbusTcpReadAddressWrite);
                log.info("modbus写入状态: {}", modbusResWrite[0]);

                // 检查modbus设备值和写入值是否一致
                if (Integer.parseInt(modbusRes[0].toString()) == java.lang.Integer.parseInt(modbusResWrite[0].toString())) {
                    // 如果一致，则取消后续的定时任务并释放锁
                    latch1.countDown();
                    finalScheduler.shutdown();
                }
            } catch (Exception e) {
                finalScheduler.shutdown();
            }
        };
        // 首先判断一次设备值和写入值是否一致，如果一致，则直接释放锁
        if (Integer.parseInt(modbusRes[0].toString()) == java.lang.Integer.parseInt(modbusResWrite[0].toString())) {
            latch1.countDown(); // 释放锁
        } else {
            // 否则计划定时任务，2秒后开始执行，之后每间隔2秒执行一次
            if (scheduler.isShutdown()) {
                scheduler = Executors.newScheduledThreadPool(1);
            }
            scheduler.scheduleAtFixedRate(checkEquipAndWriteValueTask, 2, 2, TimeUnit.SECONDS);
//            ScheduledFuture<?> future = (ScheduledFuture<?>) scheduler.scheduleAtFixedRate(checkEquipAndWriteValueTask, 2, 2, TimeUnit.SECONDS);
//            future.cancel(true);
        }

        try {
            // 等待任务完成或超时
            boolean completed = latch1.await(90, TimeUnit.SECONDS);
            if (!completed) {
                log.info("任务超时，准备呼叫现场人员检查情况");
                // 呼叫现场人员手动干预

                System.out.println("正在呼叫值班人员检查情况，请等待");//调用呼叫器相关接口
                // 等待现场人员检查完毕
                latch1.await();
                log.info("现场人员检查完毕，当前modbus设备状态值: {}", modbusRes[0]);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 确保调度器在任务完成后关闭
        scheduler.shutdown();

        log.info("modbus设备状态值: {}", modbusRes[0]);

        //————————————————————目标值分割线————————————————————————

        CountDownLatch latch2 = new CountDownLatch(1);

        final AtomicBoolean result = new AtomicBoolean(false);

        // 定义一个Runnable任务来检查Modbus设备状态值与目标值是否一致
        ScheduledExecutorService finalScheduler1 = scheduler;
        Runnable checkModbusReadEquipValueTask = () -> {
            try {
                // 获取modbus设备状态值
                modbusRes[0] = ModbusUtils.readModbusByTcp(modbusTcpRead);
                log.info("modbus设备状态值: {}", modbusRes[0]);
                // 检查modbus读取值是否与期望值相等
                if (targetValue == Integer.parseInt(modbusRes[0].toString())) {
                    // 如果相等，则取消后续的定时任务并设置结果为true
                    result.set(true);
                    latch2.countDown();
                    finalScheduler1.shutdown();

                }
            } catch (Exception e) {
                finalScheduler1.shutdown();
            }
        };
        // 首先读取一次moudbus值，如果与期望值相等，则直接释放阻塞锁
        if (modbusRes[0].equals(targetValue)) {
            result.set(true);
            latch2.countDown();
        } else {
            // 否则计划定时任务，2秒后开始执行，之后每间隔2秒执行一次
            if (scheduler.isShutdown()) {
                scheduler = Executors.newScheduledThreadPool(1);
            }
            scheduler.scheduleAtFixedRate(checkModbusReadEquipValueTask, 2, 2, TimeUnit.SECONDS);
        }

        try {
            // 等待任务完成或超时
            boolean completed = latch2.await(90, TimeUnit.SECONDS);
            if (!completed) {
                log.info("任务超时，准备呼叫现场人员检查情况");
                // 呼叫现场人员手动干预

                System.out.println("正在呼叫值班人员检查情况，请等待");//调用呼叫器相关接口
                // 等待现场人员检查完毕
                latch2.await();
                log.info("现场人员检查完毕，当前modbus设备状态值: {}", modbusRes[0]);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 确保调度器在任务完成后关闭
        scheduler.shutdown();
        log.info("modbus设备状态值: {}", modbusRes[0]);
        return result.get(); // 返回结果
    }

    /**
     * <判断待写入值与实际值写入值是否一致> (leo add)
     * <p>
     * ---参数说明---
     * ip：设备IP地址，
     * SlaveId：从机ID（默认为1），
     * equipStateOffset：寄存器设备状态位偏移地址，
     * writeOffset:寄存器写入位偏移地址 ，operationType
     */
    public boolean isEqualWritingAndWrited(IotEquipment modbusEquipment, Integer writingValue) {
        String ip = modbusEquipment.getEquipmentIp();
        int port = modbusEquipment.getEquipmentPort();
        int slaveId = modbusEquipment.getSlaveId();
        int registerWriteOffset = modbusEquipment.getWriteOffset();
        // 定义一个读取写入地址的读取器
        ModbusTcpRead modbusTcpReadAddressWrite = new ModbusTcpRead(slaveId, ConstFunCode.func03, registerWriteOffset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);
        // 读取写入状态
        Object modbusResWrite = ModbusUtils.readModbusByTcp(modbusTcpReadAddressWrite);
        return writingValue == java.lang.Integer.parseInt(modbusResWrite.toString());
    }

    /**
     * <判断设备状态值与实际写入值是否一致> (leo add)
     * <p>
     * ---参数说明---
     * ip：设备IP地址，
     * SlaveId：从机ID（默认为1），
     * equipStateOffset：寄存器设备状态位偏移地址，
     * writeOffset:寄存器写入位偏移地址 ，operationType
     */
    public boolean isEqualEquipAndWrited(IotEquipment modbusEquipment) {
        String ip = modbusEquipment.getEquipmentIp();
        int port = modbusEquipment.getEquipmentPort();
        int slaveId = modbusEquipment.getSlaveId();
        int equipStateOffset = modbusEquipment.getEquipStateOffset();
        int registerWriteOffset = modbusEquipment.getWriteOffset();
        //定义一个读取设备状态地址的读取器
        ModbusTcpRead modbusTcpRead = new ModbusTcpRead(slaveId, ConstFunCode.func03, equipStateOffset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);
        //定义一个读取写入地址的读取器
        ModbusTcpRead modbusTcpReadAddressWrite = new ModbusTcpRead(slaveId, ConstFunCode.func03, registerWriteOffset, ConstDataType.TWO_BYTE_INT_SIGNED, ip, port);

        // 读取设备状态
        Object modbusRes = ModbusUtils.readModbusByTcp(modbusTcpRead);
        // 读取写入状态
        Object modbusResWrite = ModbusUtils.readModbusByTcp(modbusTcpReadAddressWrite);
        return Integer.parseInt(modbusRes.toString()) == java.lang.Integer.parseInt(modbusResWrite.toString());
    }
}