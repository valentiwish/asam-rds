package com.robotCore.scheduing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.equipment.entity.IotEquipment;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.scheduing.dto.RobotLockPathAndPoint;
import com.robotCore.scheduing.dto.RobotRunDTO;
import com.robotCore.scheduing.dto.RobotTaskExecuteResDTO;
import com.robotCore.scheduing.entity.RobotWaybill;
import com.robotCore.scheduing.vo.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/8/14
 **/
public interface RobotWaybillService extends IService<RobotWaybill> {

    IPage<RobotWaybill> findPageList(IPage<RobotWaybill> varPage, RobotWaybill robotWaybill);

    List<RobotWaybillVO> dataInit(List<RobotWaybill> list);

    boolean softScram(String vehicleId);

    List<RobotWaybill> getToBeExecutedWaybill();

    boolean pauseNavigation(List<RobotBasicInfo> robots);

    List<RobotWaybill> getBeExecutingWaybill();

    List<RobotWaybill> getUnCompletedWaybill(String vehicleId, String waybillId);

    RobotWaybillResultVO getWaybillExecuteResult(String mesWaybillId, String wmsWaybillId);

    RobotWaybill getWaybillByMesWaybillId(String mesWaybillId);

    RobotWaybill getWaybillByWmsWaybillId(String wmsWaybillId);

    RobotWaybill getWaybillByWaybillId(String waybillId);

    RobotWaybill getWaybillByRobotNameAndTaskStatus(String robotName, Integer waybillStatus);

    List<List<RobotRunDTO>> parsingTaskContent(String taskId, String taskParameterListStr);

    List<RobotRunDTO> getTaskList(String id, String taskParameterListStr);

    List<RobotRunDTO> getLoopTaskList(String taskId, String vehicle, String taskParameterListStr);

    RobotTaskExecuteResDTO executeFirstTask(@NotNull List<RobotRunDTO> list, String waybillId) throws Exception;

    boolean runBackParkPointTask(RobotWaybill robotWaybill) throws Exception;

    RobotCommonResVO setRobotWaybill(String mesWaybillId, String wmsWaybillId, String palletCode, String taskId, String vehicle, String groupName, String taskParameterList, List<RobotRunDTO> taskList, String communicationType);

    boolean continueRobotTask(List<RobotRunDTO> list, String waybillId, String taskId, String vehicle, String groupName) throws Exception;

    RobotCommonResVO createWaybill(String mesWaybillId, String wmsWaybillId, String palletCode, List<RobotRunDTO> taskList, String taskId, String taskParameterList, String communicationType);

    void runRobotWifiTask(RobotWaybill robotWaybill, String vehicle, String groupName) throws Exception;

    boolean runLoopRobotTask(String taskId, String vehicle, RobotWaybill robotWaybill, String taskParameterList) throws Exception;

    boolean runChargeTask(String vehicle, String startPoint, String endPoint) throws Exception;

    void completeTaskSyncToOtherSystem(String mesWaybillId, String wmsWaybillId) throws Exception;

    void setTaskFailData(String waybillId, String key, String vehicle, String errorMsg) throws Exception;

    void setRobotInitialValue(String vehicle);

    void closeCurrentRobotRunThread(String vehicle);

    void setTaskCancelData(String waybillId, String key, String vehicle, List<RobotRunDTO> list) throws Exception;

    void task(String taskId, String waybillId, String key, String robotIp, String vehicle, List<RobotRunDTO> list, String taskParameterList) throws Exception;

    //分步骤运行光通信任务，可以解析任务编辑中任务块并列的情形
    void runRobotDmsTaskByStep(RobotWaybill robotWaybill, String dmsPointIp) throws Exception;

    //运行光通信运行的多机器人任务
    void runMultiRobotsDmsTask(RobotWaybill robotWaybill, String dmsPointIp) throws Exception;

    IotEquipment getAutomaticDoor(String endPort);

    boolean openOven(RobotWaybill robotWaybill, IotEquipment iotEquipment) throws Exception;

    boolean closeOven(RobotWaybill robotWaybill, IotEquipment automaticDoor) throws Exception;

    boolean openAutomaticDoor(RobotWaybill robotWaybill, IotEquipment automaticDoor) throws Exception;

    boolean closeAutomaticDoor(RobotWaybill robotWaybill, IotEquipment automaticDoor) throws Exception;

    void setWaybillResult(RobotWaybill robotWaybill, Integer code, String message);

    //运行光通信任务，只能解析任务编辑中任务块嵌套的情形
    boolean runRobotDmsTask(RobotWaybill robotWaybill);

    Map<String,String> getParameterValueList(String taskParameterListStr);

    void getTaskListInfo(RobotWaybill robotWaybill, String id, String dmsIp) throws Exception;

    List<RobotPathVO> getProcessedPaths(String vehicleId);

    boolean isOnline(RobotBasicInfo robotBasicInfo);

    RobotInfoVO getRobotStatus(RobotBasicInfo robotBasicInfo) throws Exception;

    // 连续路径导航入口
    boolean startContinuousNavigation(String waybillId, String robotId, List<RobotRunDTO> subTaskList) throws Exception;

    void stopRobotThread(String robotId);

    // 程序自动终止
    void stopRobotPollingAndFlushQueues(String robotId, String safetyStopPoint);

    // 手动或意外终止
    void stopRobotPollingAndFlushQueuesByException(String robotId);

    // 释放机器人锁定的所有站点和区域
    List<String> unlockAllPointsAndAreas(String robotId);

    // 获取机器人锁定的所有站点
    List<String> getLockedPointsByRobot(String robotId);

    boolean runNavigationTask(String waybillId, String robotName, List<RobotRunDTO> subTaskList) throws Exception;

    RobotLockPathAndPoint getRobotLockAndPoint(String vehicle);

    void liftRobotLockPathAndPoint(String vehicle);
}
