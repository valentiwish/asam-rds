package com.robotCore.multiScheduling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.multiScheduling.dto.HttpResDTO;
import com.robotCore.multiScheduling.dto.MissionCancelResDTO;
import com.robotCore.multiScheduling.dto.MissionWorksResDTO;
import com.robotCore.multiScheduling.dto.RobotTaskRequestParam;
import com.robotCore.multiScheduling.entity.RobotMultiWaybill;
import com.robotCore.multiScheduling.vo.RobotMultiWaybillVO;
import com.robotCore.scheduing.vo.RobotWaybillResultVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2025/6/10
 **/
public interface RobotMultiWaybillService extends IService<RobotMultiWaybill> {

    IPage<RobotMultiWaybill> findPageList(IPage<RobotMultiWaybill> varPage, RobotMultiWaybill robotMultiWaybill);

    RobotMultiWaybill setMiMaTaskData(RobotTaskRequestParam reqDto);

    boolean createRobotMultipleWaybill(RobotTaskRequestParam reqDto);

    RobotMultiWaybill getWaybillByMesWaybillId(String mesWaybillId);

    RobotMultiWaybill getWaybillByWmsWaybillId(String wmsWaybillId);

    void runRobotMultipleWaybill();

    boolean cancelTask(RobotMultiWaybill robotMultiWaybill);

    HttpResDTO runASAMTask(RobotMultiWaybill robotMultiWaybill);

    MissionWorksResDTO runMissionTask(RobotMultiWaybill robotMultiWaybill);

    MissionCancelResDTO cancelMissionTask(RobotMultiWaybill robotMultiWaybill);

    List<RobotMultiWaybillVO> dataInit(List<RobotMultiWaybill> list);

    List<RobotMultiWaybill> getWaitExecuteWaybill();

    List<RobotMultiWaybill> getExecutingWaybill();

    List<RobotMultiWaybill> getExecutingWaybillByNotAgvType(Integer agvType);

    List<RobotMultiWaybill> getMissionWaybillByMissionWaybillId(String missionWorkId);

    List<RobotMultiWaybill> getWaybillByTaskId(String taskId);

    void completeTaskSyncToOtherSystem(String mesWaybillId, String wmsWaybillId) throws Exception;

    RobotWaybillResultVO getWaybillExecuteResult(String mesWaybillId, String wmsWaybillId);
}
