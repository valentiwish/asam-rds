package com.robotCore.scheduing.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entity.EntityResult;
import com.entity.R;
import com.page.BasePage;
import com.page.FormatPage;
import com.page.JPAPage;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.config.ControllerLog;
import com.robotCore.common.config.OpsLogType;
import com.robotCore.common.constant.ChargeConstant;
import com.robotCore.common.constant.HXPositionNumConstant;
import com.robotCore.common.constant.ProtocolConstant;
import com.robotCore.common.mqtt.inPlant.MqttProcess;
import com.robotCore.common.mqtt.inPlant.dto.PublishRTValue;
import com.robotCore.common.mqtt.inPlant.dto.SendMessage;
import com.robotCore.common.utils.DataConvertUtil;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.service.RobotBasicInfoService;
import com.robotCore.scheduing.common.config.CustomScheduledTaskRegistrar;
import com.robotCore.scheduing.common.enums.RobotStatusEnum;
import com.robotCore.scheduing.common.enums.RobotWaybillStatusEnum;
import com.robotCore.scheduing.dto.RobotErrorInfoDTO;
import com.robotCore.scheduing.dto.RobotRunDTO;
import com.robotCore.scheduing.entity.RobotWaybill;
import com.robotCore.scheduing.entity.RobotWaybillDetail;
import com.robotCore.scheduing.service.RobotWaybillDetailService;
import com.robotCore.scheduing.service.RobotWaybillService;
import com.robotCore.scheduing.vo.*;
import com.robotCore.task.tcpCilent.TcpClientThread;
import com.utils.tools.CopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Des: 机器人运单管理控制器类
 * @author: zxl
 * @date: 2023/8/14
 **/
@Slf4j
@RestController
@Api(value = "RobotWaybillController", description = "机器人运单管理控制器类")
@RequestMapping(value = "waybill")
public class RobotWaybillController extends BaseController {

    @Autowired
    private RobotWaybillService robotWaybillService;

    @Autowired
    private RobotWaybillDetailService robotWaybillDetailService;

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private CustomScheduledTaskRegistrar customScheduledTaskRegistrar;

    @Autowired
    private MqttProcess mqttProcess;

    @ApiOperation(value = "获取机器人运单管理列表")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data) {
        RobotWaybill robotWaybill = null;
        IPage<RobotWaybill> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if(ObjectUtil.isNotEmpty(data)){
            robotWaybill = JSON.parseObject(data,RobotWaybill.class);
        }
        EntityResult varEntityResult = new EntityResult();
        IPage<RobotWaybill> page = robotWaybillService.findPageList(varPage, robotWaybill);
        IPage<RobotWaybillVO> newPage = new Page<>();
        newPage.setCurrent(page.getCurrent());
        newPage.setSize(page.getSize());
        newPage.setTotal(page.getTotal());
        newPage.setRecords(robotWaybillService.dataInit(page.getRecords()));
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(newPage));
        return varEntityResult;
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "删除运单")
    @ApiOperation(value = "根据Id删除运单")
    @RequestMapping(value = "/delete")
    public Object delete(String id) throws Exception {
        if (ObjectUtil.isNotEmpty(id)) {
            boolean flag;
            //在删除运单任务之前，先删除运单中各个子任务的执行数据
            List<RobotWaybillDetail> list = robotWaybillDetailService.getListByWaybillId(id);
            RobotWaybill robotWaybill = robotWaybillService.getById(id);
            if (robotWaybill == null) {
                return R.error("删除失败，当前运单不存在");
            }
            //初始化状态
            robotWaybillService.setTaskCancelData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), null);
            if (list.size() > 0) {
                List<String> ids = new ArrayList<>();
                for (RobotWaybillDetail robotWaybillDetail : list) {
                    ids.add(robotWaybillDetail.getId());
                }
                boolean b = robotWaybillDetailService.removeByIds(ids);
                //删除子任务成功以后，再删除运单
                if (b) {
                    flag =  robotWaybillService.removeById(id);
                } else {
                    return R.error("删除子任务失败");
                }
            } else {
                flag =  robotWaybillService.removeById(id);
            }
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "查看错误详情")
    @RequestMapping(value = "/getErrorMsg")
    public Object getErrorMsg(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotWaybill model = robotWaybillService.getById(id);
            List<RobotErrorInfoVO> list = new ArrayList<>();
            //只有状态是失败的运单才可以查看错误详情
            if (RobotWaybillStatusEnum.TERMINATE.getCode().equals(model.getWaybillStatus()) && model.getErrorMessage() != null && !"".equals(model.getErrorMessage())) {
                //创建前端展示的错误信息数据
                RobotErrorInfoVO errorInfoVO = new RobotErrorInfoVO();
                try {
                    RobotErrorInfoDTO errorInfoDTO = JSON.parseObject(model.getErrorMessage(), RobotErrorInfoDTO.class);
                    //获取错误码
                    String errorCode = model.getErrorMessage().substring(12, 17);
                    //获取错误时间
                    String errorTime = errorInfoDTO.getDesc().substring(1, 11) + " " + errorInfoDTO.getDesc().substring(12, 20);
                    errorInfoVO.setErrorCode(errorCode);
                    errorInfoVO.setErrorFrequency(errorInfoDTO.getTimes());
                    errorInfoVO.setErrorTime(errorTime);
                    errorInfoVO.setErrorDescription(errorInfoDTO.getDesc());
                } catch (Exception e) {
                    errorInfoVO.setErrorCode("400");
                    errorInfoVO.setErrorFrequency(1);
                    errorInfoVO.setErrorTime(model.getErrorTime().toString());
                    errorInfoVO.setErrorDescription(model.getErrorMessage());
                }
                list.add(errorInfoVO);
            }
            return R.ok(list);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "查看运行详情")
    @RequestMapping(value = "/getRunInfo")
    public Object getRunInfo(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            List<RobotWaybillDetail> list = robotWaybillDetailService.getListByWaybillId(id);
            List<RobotWaybillDetailVO> detailVOS = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                RobotWaybillDetail robotWaybillDetail = list.get(i);
                RobotWaybillDetailVO vo = new RobotWaybillDetailVO();
                vo.setBlockId(i + 1);
                vo.setStorageLoc(robotWaybillDetail.getStorageLoc());
                vo.setOperation(robotWaybillDetail.getOperation());
                vo.setWaybillStatus(robotWaybillDetail.getWaybillStatus());
                detailVOS.add(vo);
            }
            return R.ok(detailVOS);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "外部接口--获取运单执行详情")
    @RequestMapping(value = "/getExecuteResult")
    public Object getWaybillExecuteResult(String mesWaybillId, String wmsWaybillId) {
        if (ObjectUtil.isNotEmpty(mesWaybillId)) {
            RobotWaybillResultVO waybillExecuteResult = robotWaybillService.getWaybillExecuteResult(mesWaybillId, wmsWaybillId);
            return waybillExecuteResult != null ? R.ok(waybillExecuteResult) : R.error("获取运单执行详情失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "取消任务，其实走的是取消导航的协议")
    @RequestMapping(value = "/pauseNav")
    public Object pauseNavigate(String id) throws Exception {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotWaybill robotWaybill = robotWaybillService.getById(id);
            String robotName = robotWaybill.getRobotName();
            if (robotName != null) {
                // 取消导航时，关闭执行连续导航任务的机器人相关线程，leo add
                robotWaybillService.stopRobotThread(robotName);
                robotWaybillService.stopRobotPollingAndFlushQueuesByException(robotName);

                List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
                if (robots.size() > 0) {
                    boolean flag = robotWaybillService.pauseNavigation(robots);
                    robotWaybillService.setTaskCancelData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), null);
                    return flag ? R.ok() : R.error("取消当前导航失败：" + robotName);
                } else {
                    robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.CANCEL.getCode());
                    boolean flag = robotWaybillService.saveOrUpdate(robotWaybill);
                    //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                    robotWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                    return flag ? R.ok() : R.error("取消当前导航失败" );
                }
            } else {
                robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.CANCEL.getCode());
                boolean flag = robotWaybillService.saveOrUpdate(robotWaybill);
                //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                robotWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                return flag ? R.ok() : R.error("取消当前导航失败" );
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "返回到待命点")
    @RequestMapping(value = "/goBackParkPoint")
    public Object goBackParkPoint(String id) throws Exception {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotWaybill robotWaybill = robotWaybillService.getById(id);
            if (robotWaybill != null) {
                String robotName = robotWaybill.getRobotName();
                List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
                if (robots.size() > 0) {
                    boolean flag = robotWaybillService.runBackParkPointTask(robotWaybill);
                    return flag ? R.ok() : R.error("返回待命点失败：" + robotName);
                }
                return R.error(robotName + "不存在");
            } else {
                return R.error("当前返回待命点的运单不存在");
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "MES/WMS取消运单任务")
    @RequestMapping(value = "/mesCancelTask")
    public Object mesCancelTask(String mesWaybillId, String wmsWaybillId) throws Exception {
        log.info("MES/WMS取消运单任务,mesWaybillId:{},wmsWaybillId:{}",mesWaybillId,wmsWaybillId);
        if (ObjectUtil.isNotEmpty(mesWaybillId) || ObjectUtil.isNotEmpty(wmsWaybillId)) {
            RobotWaybill robotWaybill;
            if (ObjectUtil.isNotEmpty(mesWaybillId)) {
                robotWaybill = robotWaybillService.getWaybillByMesWaybillId(mesWaybillId);
            } else {
                robotWaybill = robotWaybillService.getWaybillByWmsWaybillId(wmsWaybillId);
            }
            if (robotWaybill != null) {
                if ("WIFI运行".equals(robotWaybill.getCommunicationType())) {
                    String robotName = robotWaybill.getRobotName();
                    if (robotName != null) {
                        List<RobotBasicInfo> robots = robotBasicInfoService.findByName(robotName);
                        if (robots.size() > 0) {
                            boolean flag = robotWaybillService.pauseNavigation(robots);
                            robotWaybillService.setTaskCancelData(robotWaybill.getId(), robotWaybill.getId(), robotWaybill.getRobotName(), null);
                            return flag ? R.ok() : R.error("取消当前导航失败：" + robotName);
                        } else {
                            robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.CANCEL.getCode());
                            boolean flag = robotWaybillService.saveOrUpdate(robotWaybill);
                            //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                            robotWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                            return flag ? R.ok() : R.error("取消当前导航失败" );
                        }
                    } else {
                        robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.CANCEL.getCode());
                        boolean flag = robotWaybillService.saveOrUpdate(robotWaybill);
                        //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                        robotWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                        return flag ? R.ok() : R.error("取消当前导航失败" );
                    }
                } else {
                    //光通信運行
                    //取消任务
                    robotWaybill.setWaybillStatus(RobotWaybillStatusEnum.CANCEL.getCode());
                    boolean flag = robotWaybillService.saveOrUpdate(robotWaybill);
                    //如果由MES下发的指令，则同步完成状态给MES系统;如果是WMS系统，则同步完成状态给WMS系统
                    robotWaybillService.completeTaskSyncToOtherSystem(robotWaybill.getMesWaybillId(), robotWaybill.getWmsWaybillId());
                    return flag ? R.ok() : R.error("取消当前导航失败" );
                }
            } else {
                return R.error("当前取消的运单不存在");
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "继续当前导航")
    @RequestMapping(value = "/continueNav")
    public Object continueNavigate(String id) throws Exception {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotWaybill robotWaybill = robotWaybillService.getById(id);
            String robotName = robotWaybill.getRobotName();
            String robotGroupName = robotWaybill.getRobotGroupName();
            String taskId = robotWaybill.getTaskId();
            //如果当前运单中，机器人已经暂停，则可以继续导航
            if (robotWaybill.getWaybillStatus() == 4) {
                String pendingTask = robotWaybillService.getById(id).getPendingTask();
                List<RobotRunDTO> list = JSON.parseArray(pendingTask, RobotRunDTO.class);
                boolean b = robotWaybillService.continueRobotTask(list, id, taskId, robotName, robotGroupName);
                return b ? R.ok() : R.error("机器人继续导航失败！");
            } else {
                return R.error("机器人当前没有暂停，不能继续导航！");
            }
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "暂停全部机器人导航")
    @RequestMapping(value = "/pauseAllRobotsNavigate")
    public Object pauseAllRobotsNavigate(){
        //获取全部正在执行的运单
        List<RobotWaybill> beExecutingWaybills = robotWaybillService.getBeExecutingWaybill();
        return false;
    }

    @ApiOperation(value = "测试")
    @RequestMapping(value = "/test")
    public Object test(String vehicleId)  {
        List<RobotPathVO> processedPaths = robotWaybillService.getProcessedPaths(vehicleId);
        return R.ok(processedPaths);
    }

    @ApiOperation(value = "測試開門1")
    @RequestMapping(value = "/openDoor1")
    public Object openDoor1() throws InterruptedException {
        SendMessage sendMessage = new SendMessage();
        List<PublishRTValue> publishRTValues = new ArrayList<>();
        PublishRTValue publishRTValue = new PublishRTValue();
        publishRTValue.setName(HXPositionNumConstant.OVEN_1_OPEN);
        publishRTValue.setType(1);
        publishRTValue.setValue("1");
        publishRTValues.add(publishRTValue);
        sendMessage.setRTValue(publishRTValues);
        log.info("測試自動門{}",sendMessage);
        mqttProcess.publish("SUB1", sendMessage);
        Thread.sleep(1000);

        SendMessage sendMessage1 = new SendMessage();
        List<PublishRTValue> publishRTValues1 = new ArrayList<>();
        PublishRTValue publishRTValue1 = new PublishRTValue();
        publishRTValue1.setName(HXPositionNumConstant.OVEN_1_OPEN);
        publishRTValue1.setType(1);
        publishRTValue1.setValue("0");
        publishRTValues1.add(publishRTValue1);
        sendMessage1.setRTValue(publishRTValues1);
        log.info("測試自動門{}",sendMessage1);
        mqttProcess.publish("SUB1", sendMessage1);

        return R.ok();
    }

    @ApiOperation(value = "測試開門1")
    @RequestMapping(value = "/closeDoor1")
    public Object closeDoor1() throws InterruptedException {
        SendMessage sendMessage = new SendMessage();
        List<PublishRTValue> publishRTValues = new ArrayList<>();
        PublishRTValue publishRTValue = new PublishRTValue();
        publishRTValue.setName(HXPositionNumConstant.OVEN_1_CLOSE);
        publishRTValue.setType(1);
        publishRTValue.setValue("1");
        publishRTValues.add(publishRTValue);
        sendMessage.setRTValue(publishRTValues);
        log.info("測試自動門{}",sendMessage);
        mqttProcess.publish("SUB1", sendMessage);
        Thread.sleep(1000);

        SendMessage sendMessage1 = new SendMessage();
        List<PublishRTValue> publishRTValues1 = new ArrayList<>();
        PublishRTValue publishRTValue1 = new PublishRTValue();
        publishRTValue1.setName(HXPositionNumConstant.OVEN_1_CLOSE);
        publishRTValue1.setType(1);
        publishRTValue1.setValue("0");
        publishRTValues1.add(publishRTValue1);
        sendMessage1.setRTValue(publishRTValues1);
        log.info("測試自動門{}",sendMessage1);
        mqttProcess.publish("SUB1", sendMessage1);


        return R.ok();
    }

    @ApiOperation(value = "測試開門1")
    @RequestMapping(value = "/openDoor2")
    public Object openDoor2() throws InterruptedException {
        SendMessage sendMessage = new SendMessage();
        List<PublishRTValue> publishRTValues = new ArrayList<>();
        PublishRTValue publishRTValue = new PublishRTValue();
        publishRTValue.setName(HXPositionNumConstant.OVEN_2_OPEN);
        publishRTValue.setType(1);
        publishRTValue.setValue("1");
        publishRTValues.add(publishRTValue);
        sendMessage.setRTValue(publishRTValues);
        log.info("測試自動門{}",sendMessage);
        mqttProcess.publish("SUB1", sendMessage);
        Thread.sleep(1000);

        SendMessage sendMessage1 = new SendMessage();
        List<PublishRTValue> publishRTValues1 = new ArrayList<>();
        PublishRTValue publishRTValue1 = new PublishRTValue();
        publishRTValue1.setName(HXPositionNumConstant.OVEN_2_OPEN);
        publishRTValue1.setType(1);
        publishRTValue1.setValue("0");
        publishRTValues1.add(publishRTValue1);
        sendMessage1.setRTValue(publishRTValues1);
        log.info("測試自動門{}",sendMessage1);
        mqttProcess.publish("SUB1", sendMessage1);
        return R.ok();
    }

    @ApiOperation(value = "測試開門1")
    @RequestMapping(value = "/closeDoor2")
    public Object closeDoor2() throws InterruptedException {
        SendMessage sendMessage = new SendMessage();
        List<PublishRTValue> publishRTValues = new ArrayList<>();
        PublishRTValue publishRTValue = new PublishRTValue();
        publishRTValue.setName(HXPositionNumConstant.OVEN_2_CLOSE);
        publishRTValue.setType(1);
        publishRTValue.setValue("1");
        publishRTValues.add(publishRTValue);
        sendMessage.setRTValue(publishRTValues);
        log.info("測試自動門{}",sendMessage);
        mqttProcess.publish("SUB1", sendMessage);
        Thread.sleep(1000);

        SendMessage sendMessage1 = new SendMessage();
        List<PublishRTValue> publishRTValues1 = new ArrayList<>();
        PublishRTValue publishRTValue1 = new PublishRTValue();
        publishRTValue1.setName(HXPositionNumConstant.OVEN_2_CLOSE);
        publishRTValue1.setType(1);
        publishRTValue1.setValue("0");
        publishRTValues1.add(publishRTValue1);
        sendMessage1.setRTValue(publishRTValues1);
        log.info("測試自動門{}",sendMessage1);
        mqttProcess.publish("SUB1", sendMessage1);
        return R.ok();
    }

}
