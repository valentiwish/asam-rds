package com.robotCore.multiScheduling.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.multiScheduling.dto.*;
import com.robotCore.multiScheduling.entity.RobotMultiWaybill;
import com.robotCore.multiScheduling.mapper.RobotMultiWaybillDao;
import com.robotCore.multiScheduling.service.RobotMultiWaybillService;
import com.robotCore.multiScheduling.vo.RobotMultiWaybillVO;
import com.robotCore.robot.entity.RobotDmsEditor;
import com.robotCore.robot.service.RobotDmsEditorService;
import com.robotCore.scheduing.common.enums.RobotTaskPriorityEnum;
import com.robotCore.scheduing.common.enums.RobotWaybillStatusEnum;
import com.robotCore.scheduing.common.utils.HttpClientUtil;
import com.robotCore.scheduing.dto.MESResDTO;
import com.robotCore.scheduing.dto.RobotTaskRequestDTO;
import com.robotCore.scheduing.service.RobotTaskLogService;
import com.robotCore.scheduing.vo.RobotWaybillResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Des:
 * @author: zxl
 * @date: 2025/6/10
 **/
@Slf4j
@Service
public class RobotMultiWaybillServiceImpl extends ServiceImpl<RobotMultiWaybillDao, RobotMultiWaybill> implements RobotMultiWaybillService {

    @Value("${data.serverIP}")
    private String currentServerIp;

    @Autowired
    private RobotDmsEditorService robotDmsEditorService;

    @Autowired
    private RobotTaskLogService robotTaskLogService;

    @Value("${otherSystem.syncWaybillStatusToMesUrl}")
    private String syncTaskStatusMesUrl;

    @Value("${otherSystem.syncWaybillStatusToWmsUrl}")
    private String syncTaskStatusWmsUrl;


    @Override
    public IPage<RobotMultiWaybill> findPageList(IPage<RobotMultiWaybill> varPage, RobotMultiWaybill robotMultiWaybill) {
        QueryWrapper<RobotMultiWaybill> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(robotMultiWaybill)) {
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotMultiWaybill.getId()), RobotMultiWaybill::getId, robotMultiWaybill.getId());
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotMultiWaybill.getRobotName()), RobotMultiWaybill::getRobotName, robotMultiWaybill.getRobotName());
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotMultiWaybill.getWaybillStatus()), RobotMultiWaybill::getWaybillStatus, robotMultiWaybill.getWaybillStatus());
        }
        wrapper.lambda().orderByDesc(RobotMultiWaybill::getCreateTime);
        return page(varPage, wrapper);
    }

    /**
     * 设置给米玛叉车调度系统下发机器人任务的参数
     * @param reqDto
     * @return
     */
    @Override
    public RobotMultiWaybill setMiMaTaskData(RobotTaskRequestParam reqDto){
        RobotMultiWaybill robotMultiWaybill = new RobotMultiWaybill();
        robotMultiWaybill.setMesWaybillId(reqDto.getMesWaybillId());
        robotMultiWaybill.setWmsWaybillId(reqDto.getWmsWaybillId());
        robotMultiWaybill.setRobotName(reqDto.getAgvCode());
        robotMultiWaybill.setAgvType(reqDto.getAgvType());
        robotMultiWaybill.setTaskNumber(reqDto.getTaskNumber());
        robotMultiWaybill.setStartPoint(reqDto.getStartPoint());
        robotMultiWaybill.setEndPoint(reqDto.getEndPoint());
        return robotMultiWaybill;
    }

    /**
     * 创建多机器人运行运单
     * @param reqDto
     * @return
     */
    @Override
    public boolean createRobotMultipleWaybill(RobotTaskRequestParam reqDto) {
        RobotMultiWaybill robotMultiWaybill = new RobotMultiWaybill();
        robotMultiWaybill.setMesWaybillId(reqDto.getMesWaybillId());
        robotMultiWaybill.setWmsWaybillId(reqDto.getWmsWaybillId());
        //任务ID
        robotMultiWaybill.setMissionWorkId(reqDto.getTaskNumber());
        robotMultiWaybill.setRobotName(reqDto.getAgvCode());
        robotMultiWaybill.setAgvType(reqDto.getAgvType());
        robotMultiWaybill.setTaskNumber(reqDto.getTaskNumber());
        robotMultiWaybill.setStartPoint(reqDto.getStartPoint());
        robotMultiWaybill.setEndPoint(reqDto.getEndPoint());
        //设置运单状态为等待
        robotMultiWaybill.setWaybillStatus(RobotWaybillStatusEnum.WAIT.getCode());
        robotMultiWaybill.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return save(robotMultiWaybill);
    }

    /**
     * 根据MES运单ID获取当前运单
     *
     * @param mesWaybillId
     * @return
     */
    @Override
    public RobotMultiWaybill getWaybillByMesWaybillId(String mesWaybillId) {
        QueryWrapper<RobotMultiWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(mesWaybillId), RobotMultiWaybill::getMesWaybillId, mesWaybillId);
        return list(wrapper).size() > 0 ? list(wrapper).get(0) : null;
    }

    /**
     * 根据WMS运单ID获取当前运单
     *
     * @param wmsWaybillId
     * @return
     */
    @Override
    public RobotMultiWaybill getWaybillByWmsWaybillId(String wmsWaybillId) {
        QueryWrapper<RobotMultiWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(wmsWaybillId), RobotMultiWaybill::getWmsWaybillId, wmsWaybillId);
        return list(wrapper).size() > 0 ? list(wrapper).get(0) : null;
    }

    /**
     * 运行多机器人运单
     */
    @Override
    public void runRobotMultipleWaybill() {
        //获取等待执行的运单
        List<RobotMultiWaybill> waitExecutingWaybills = getWaitExecuteWaybill();
        if (waitExecutingWaybills.size() > 0) {
            RobotMultiWaybill robotMultiWaybill = waitExecutingWaybills.get(0);
            //机器人类型 1--米松叉车，2--金陵,3--西安航天
           if (robotMultiWaybill.getAgvType() == 1) {
               //调用米玛叉车接口
               log.info("执行米松叉车机器人任务");
               //获取不属于该品牌机器人，但是正在执行的运单
               List<RobotMultiWaybill> executingWaybills = getExecutingWaybillByNotAgvType(robotMultiWaybill.getAgvType());
               //存放所有运单中包含的起始点信息
               List<String> lockedPoints = new ArrayList<>();
               //判断正在执行的运单中起始点是否跟当前运单的起始点冲突（存在点位相同）
               if (executingWaybills.size() > 0) {
                   //当前正在运行的运单的起始点
                   for (RobotMultiWaybill executingWaybill : executingWaybills) {
                       lockedPoints.add(executingWaybill.getStartPoint());
                       lockedPoints.add(executingWaybill.getEndPoint());
                   }
               }
               //如果存在任务起始点中任意一个站点被其他品牌机器人的运单占用，则不能下发该运单
               if (lockedPoints.contains(robotMultiWaybill.getStartPoint()) || lockedPoints.contains(robotMultiWaybill.getEndPoint())) {
                   return;
               }
               //调用米松叉车接口
               MissionWorksResDTO missionWorksResDTO = runMissionTask(robotMultiWaybill);
               if (missionWorksResDTO == null) {
                   //运单执行失败
                   robotMultiWaybill.setWaybillStatus(RobotWaybillStatusEnum.TERMINATE.getCode());
                   //设置运单执行完成时间
                   robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                   //设置运单执行失败信息
                   robotMultiWaybill.setErrorMessage("调用Mission任务接口失败！");
                   robotMultiWaybill.setErrorTime(new Timestamp(System.currentTimeMillis()));
               } else {
                   //创建作业时调度系统自动生成的唯一id
                   robotMultiWaybill.setMissionWorkId(missionWorksResDTO.getId());
                   //设置任务未正在运行
                   robotMultiWaybill.setWaybillStatus(RobotWaybillStatusEnum.EXECUTING.getCode());
               }
               saveOrUpdate(robotMultiWaybill);
           } else if (robotMultiWaybill.getAgvType() == 2) {
               //调用金陵接口
               log.info("执行金陵机器人任务");
           } else if (robotMultiWaybill.getAgvType() == 3) {
               log.info("执行西安航天机器人任务");
               //获取被其他品牌机器人锁定的管控区域
               List<RobotDmsEditor> robotDmsEditorList = robotDmsEditorService.findListByAreaTypeAndOccupiedAndNotRobotType(2, 1, 3);
               //判断该区域是否被其他机器人锁住,如果被其他机器人锁住，则等待该区域释放以后才可以执行任务执行接口
               if (robotDmsEditorList.size() == 0) {
                   //调用西安航天接口
                   HttpResDTO httpResDTO = runASAMTask(robotMultiWaybill);
                   if (httpResDTO.getCode() != 200) {
                       //运单执行失败
                       robotMultiWaybill.setWaybillStatus(RobotWaybillStatusEnum.TERMINATE.getCode());
                       //设置运单执行完成时间
                       robotMultiWaybill.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                       //设置运单执行失败信息
                       robotMultiWaybill.setErrorMessage(httpResDTO.getMsg());
                       robotMultiWaybill.setErrorTime(new Timestamp(System.currentTimeMillis()));
                   } else {
                       //设置运单为已下发
                       robotMultiWaybill.setWaybillStatus(RobotWaybillStatusEnum.ISSUED.getCode());
                   }
                   saveOrUpdate(robotMultiWaybill);
               }
           }
        }
    }

    /**
     * 取消不同调度系统下发的机器人任务
     * @param robotMultiWaybill
     * @return
     */
    @Override
    public boolean cancelTask(RobotMultiWaybill robotMultiWaybill) {
        //机器人类型 1--米松叉车，2--金陵,3--西安航天
        if (robotMultiWaybill.getAgvType() == 1) {
            //调用米玛叉车接口
            MissionCancelResDTO missionCancelResDTO = cancelMissionTask(robotMultiWaybill);
            return "SUCCESS".equals(missionCancelResDTO.getStatus());
        }
        return true;
    }

    /**
     * 运行ASAM任务
     * @param robotMultiWaybill
     * @return
     */
    @Override
    public HttpResDTO runASAMTask(RobotMultiWaybill robotMultiWaybill) {
        RobotTaskRequestDTO robotTaskRequestDTO = new RobotTaskRequestDTO();
        //设置任务编号
        robotTaskRequestDTO.setTaskNumber(robotMultiWaybill.getTaskNumber());
        //创建任务参数
        List<RobotTaskRequestDTO.SubParameter> taskParameter = new ArrayList<>();

        RobotTaskRequestDTO.SubParameter robotNameParameter = new RobotTaskRequestDTO.SubParameter();
        robotNameParameter.setName("robotName");
        robotNameParameter.setValue(robotMultiWaybill.getRobotName());
        robotNameParameter.setType("字符串");

        RobotTaskRequestDTO.SubParameter startPointParameter = new RobotTaskRequestDTO.SubParameter();
        startPointParameter.setName("startPoint");
        startPointParameter.setValue(robotMultiWaybill.getStartPoint());
        startPointParameter.setType("字符串");

        RobotTaskRequestDTO.SubParameter endPointParameter = new RobotTaskRequestDTO.SubParameter();
        endPointParameter.setName("endPoint");
        endPointParameter.setValue(robotMultiWaybill.getEndPoint());
        endPointParameter.setType("字符串");

        taskParameter.add(robotNameParameter);
        taskParameter.add(startPointParameter);
        taskParameter.add(endPointParameter);
        //设置任务参数
        robotTaskRequestDTO.setTaskParameter(taskParameter);

        String jsonData = JSON.toJSONString(robotTaskRequestDTO, SerializerFeature.DisableCircularReferenceDetect);
        String resStr = HttpClientUtil.clientPost("http://" + currentServerIp + ":8091/task/runDmsTask", HttpMethod.POST, jsonData);
        return JSONObject.parseObject(resStr, HttpResDTO.class);
    }

    /**
     * 运行Mission任务
     * @param robotMultiWaybill
     * @return
     */
    @Override
    public MissionWorksResDTO runMissionTask(RobotMultiWaybill robotMultiWaybill) {
        log.info("调用Mission机器人接口");
        MissionWorksReqDTO missionWorksReqDTO = new MissionWorksReqDTO();
        //设置任务id
//        missionWorksReqDTO.setMissionId(robotMultiWaybill.getMissionWorkId());
        //设置任务编号
//        missionWorksReqDTO.setMissionCode(robotMultiWaybill.getTaskNumber());
        //设置机器人编号
//        missionWorksReqDTO.setAgvCode(robotMultiWaybill.getRobotName());
        //设置回调url
        missionWorksReqDTO.setCallbackUrl("http://" + currentServerIp + ":8091/multiTask/missionUpdateWaybillStatus");
        //设置任务参数{\"endPoint\":\"3540\",\"startPoint\":\"3694\"}
        String runtimeParam = "{\"startPoint\":\"" + robotMultiWaybill.getStartPoint() + "\",\"endPoint\":\"" + robotMultiWaybill.getEndPoint() + "\"}";
        missionWorksReqDTO.setRuntimeParam(runtimeParam);
        String jsonData = JSON.toJSONString(missionWorksReqDTO, SerializerFeature.DisableCircularReferenceDetect);
        log.info("调用Mission机器人任务接口请求的任务参数内容：{}",jsonData);
        String resStr = HttpClientUtil.clientPost("http://10.158.11.13:4001/missionWorksFree", HttpMethod.POST, jsonData);
        MissionWorksResDTO missionWorksResDTO = JSONObject.parseObject(resStr, MissionWorksResDTO.class);
        log.info("调用Mission机器人任务接口返回的任务参数内容：{}",JSON.toJSONString(missionWorksResDTO));
        robotMultiWaybill.setMissionWaybillId(missionWorksResDTO.getId());
        robotMultiWaybill.setCreateTime(missionWorksResDTO.getCreateTime());
        robotMultiWaybill.setTaskName(missionWorksResDTO.getName());
        robotMultiWaybill.setTaskPriority(missionWorksResDTO.getCurrentActionSequence());
        saveOrUpdate(robotMultiWaybill);

        return JSONObject.parseObject(resStr, MissionWorksResDTO.class);
    }

    /**
     * 取消Mission任务
     * @param robotMultiWaybill
     * @return
     */
    @Override
    public MissionCancelResDTO cancelMissionTask(RobotMultiWaybill robotMultiWaybill) {
        String resStr = HttpClientUtil.clientPost("http://10.158.11.13:4001/missionWorks/"+ robotMultiWaybill.getMissionWaybillId() + "/controls/stop", HttpMethod.POST, null);
        return JSONObject.parseObject(resStr, MissionCancelResDTO.class);
    }

    @Override
    public List<RobotMultiWaybillVO> dataInit(List<RobotMultiWaybill> list) {
        List<RobotMultiWaybillVO> robotWaybillVOS = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RobotMultiWaybillVO vo = new RobotMultiWaybillVO();
            RobotMultiWaybill robotMultiWaybill = list.get(i);
            vo.setId(robotMultiWaybill.getId());
            vo.setTaskName(robotMultiWaybill.getTaskName());
            vo.setSerialNumber(i + 1);
            //设置机器人类型
            if (robotMultiWaybill.getAgvType() == 1) {
                vo.setAgvType("米松");
            } else if (robotMultiWaybill.getAgvType() == 2) {
                vo.setAgvType("金陵");
            } else if (robotMultiWaybill.getAgvType() == 3) {
                vo.setAgvType("西安航天");
            }
            vo.setRobotName(robotMultiWaybill.getRobotName());
            vo.setRobotGroupName(robotMultiWaybill.getRobotGroupName());
            vo.setMesWaybillId(robotMultiWaybill.getMesWaybillId());
            vo.setWmsWaybillId(robotMultiWaybill.getWmsWaybillId());
            if (robotMultiWaybill.getTaskPriority() != null) {
                vo.setPriority(Objects.requireNonNull(RobotTaskPriorityEnum.getByCode(robotMultiWaybill.getTaskPriority())).getName());
            }
            vo.setWaybillStatus(robotMultiWaybill.getWaybillStatus());
            vo.setCreateTime(robotMultiWaybill.getCreateTime().toString().substring(0, robotMultiWaybill.getCreateTime().toString().length() - 2));
            if (robotMultiWaybill.getOrderTime() == null) {
                vo.setOrderTime("");
            } else {
                vo.setOrderTime(robotMultiWaybill.getOrderTime().toString().substring(0, robotMultiWaybill.getOrderTime().toString().length() - 2));
            }
            if (robotMultiWaybill.getCompleteTime() == null) {
                vo.setCompleteTime("");
            } else {
                vo.setCompleteTime(robotMultiWaybill.getCompleteTime().toString().substring(0, robotMultiWaybill.getCompleteTime().toString().length() - 2));
            }
            if (robotMultiWaybill.getExecutionTime() == null) {
                vo.setExecutionTime("");
            } else {
                vo.setExecutionTime(robotMultiWaybill.getExecutionTime() / 1000 + " s");
            }
            robotWaybillVOS.add(vo);
        }
        return robotWaybillVOS;
    }

    /**
     * 获取等待执行的任务运单
     *
     * @return
     */
    @Override
    public List<RobotMultiWaybill> getWaitExecuteWaybill() {
        QueryWrapper<RobotMultiWaybill> wrapper = new QueryWrapper<>();
        //运单处于等待状态
        wrapper.lambda().eq(RobotMultiWaybill::getWaybillStatus, RobotWaybillStatusEnum.WAIT.getCode());
        //排在前面的优先显示（执行）
        wrapper.lambda().orderByDesc(RobotMultiWaybill::getCreateTime);
        return list(wrapper);
    }

    /**
     * 获取正在执行的任务运单
     *
     * @return
     */
    @Override
    public List<RobotMultiWaybill> getExecutingWaybill() {
        QueryWrapper<RobotMultiWaybill> wrapper = new QueryWrapper<>();
        //运单处于等待状态
        wrapper.lambda().eq(RobotMultiWaybill::getWaybillStatus, RobotWaybillStatusEnum.EXECUTING.getCode());
        //排在前面的优先显示（执行）
        wrapper.lambda().orderByDesc(RobotMultiWaybill::getCreateTime);
        return list(wrapper);
    }

    /**
     * 获取正在执行的任务运单,但是不属于本机器人类型
     *
     * @return
     */
    @Override
    public List<RobotMultiWaybill> getExecutingWaybillByNotAgvType(Integer agvType) {
        QueryWrapper<RobotMultiWaybill> wrapper = new QueryWrapper<>();
        //运单处于等待状态
        wrapper.lambda().eq(RobotMultiWaybill::getWaybillStatus, RobotWaybillStatusEnum.EXECUTING.getCode());
        //不属于本机器人类型
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(agvType), RobotMultiWaybill::getAgvType, agvType);
        //排在前面的优先显示（执行）
        wrapper.lambda().orderByDesc(RobotMultiWaybill::getCreateTime);
        return list(wrapper);
    }

    /**
     * 根据missionWorkId获取运单
     * @param missionWaybillId
     * @return
     */
    @Override
    public List<RobotMultiWaybill> getMissionWaybillByMissionWaybillId(String missionWaybillId) {
        QueryWrapper<RobotMultiWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(missionWaybillId), RobotMultiWaybill::getMissionWaybillId, missionWaybillId);
        return list(wrapper);
    }

    /**
     * 根据任务id获取运单
     *
     * @return
     */
    @Override
    public List<RobotMultiWaybill> getWaybillByTaskId(String taskId) {
        QueryWrapper<RobotMultiWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RobotMultiWaybill::getTaskNumber, taskId);
        return list(wrapper);
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
            log.info("同步运单状态到第三方系统数据内容：{}", JSON.toJSONString(waybillExecuteResult));
            if (waybillExecuteResult != null) {
                String jsonData = JSON.toJSONString(waybillExecuteResult, SerializerFeature.DisableCircularReferenceDetect);
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
                    //设置运单执行状态为失败
                    RobotMultiWaybill robotMultiWaybill = getById(waybillExecuteResult.getWaybillId());
                    //针对389厂7210Mission叉车，当同步状态失败时，需要同步取消mission叉车任务
                    if ("10.158.11.12".equals(currentServerIp)) {
                        cancelTask(robotMultiWaybill);
                    }
                    robotMultiWaybill.setWaybillStatus(RobotWaybillStatusEnum.TERMINATE.getCode());
                    saveOrUpdate(robotMultiWaybill);
                    robotTaskLogService.saveSyncMesWaybill(waybillExecuteResult, mesWaybillId, wmsWaybillId, currentServerIp, false, e.getMessage());
                    throw new Exception("同步运单状态到第三方系统失败！");
                }
            } else {
                robotTaskLogService.saveSyncMesWaybill(waybillExecuteResult, mesWaybillId, wmsWaybillId, currentServerIp, false, "不存在此运单号！");
            }
        }
    }

    /**
     * 获取运单执行结果
     *
     * @return
     */
    @Override
    public RobotWaybillResultVO getWaybillExecuteResult(String mesWaybillId, String wmsWaybillId) {
        QueryWrapper<RobotMultiWaybill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(mesWaybillId), RobotMultiWaybill::getMesWaybillId, mesWaybillId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(wmsWaybillId), RobotMultiWaybill::getWmsWaybillId, wmsWaybillId);
        RobotMultiWaybill waybillExecuteResult = list(wrapper).size() > 0 ? list(wrapper).get(0) : null;
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

}
