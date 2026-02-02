package com.robotCore.scheduing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.dto.RobotPathPlanDTO;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.scheduing.dto.AlgorithmRequestResDTO;
import com.robotCore.scheduing.dto.RobotTaskListDTO;
import com.robotCore.scheduing.dto.RobotTaskRequestDTO;
import com.robotCore.scheduing.dto.TaskParameterDTO;
import com.robotCore.scheduing.entity.RobotTask;
import com.robotCore.scheduing.vo.RobotPathVO;
import com.robotCore.scheduing.vo.RobotTaskVO;

import java.util.List;

import java.util.Map;
import java.util.Set;


/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/5
 **/
public interface RobotTaskService extends IService<RobotTask> {

    IPage<RobotTask> findPageList(IPage<RobotTask> varPage,RobotTask robotTask);

    List<RobotTaskVO> getTaskInfo();

    RobotTask getNewlyCreated();

    RobotTask getTaskByNumber(String taskNumber);

    List<TaskParameterDTO> taskParameterDataConvert(List<RobotTaskRequestDTO.SubParameter> reqParameterList);

    boolean save(RobotTask model, Long userId, String userName);

    List<RobotTask> getCheckList(String id, String name);

    String getTaskParameterList(String taskId);

    RobotPathPlanDTO.RobotPath encapsulateData(String vehicle, String groupName, String startSite, String endSite);

    AlgorithmRequestResDTO startPathPlanningTask(List<RobotPathVO> pathVOList, List<RobotPathPlanDTO.RobotPath> robotPathList, List<RobotPathPlanDTO.LockInfo> lockInfoList) throws Exception;

    AlgorithmRequestResDTO executePathPlanningTask(List<RobotPathVO> pathVOList, Set<String> pointSet, List<RobotPathPlanDTO.RobotPath> robotPathList);

    String getRobotName(String id) throws Exception;

    boolean jackLoad(List<RobotBasicInfo> robots);

    boolean jackUnload(List<RobotBasicInfo> robots);

    String refactorJson(String customizedActionsRawJson, Map<String, String> dragMap);

    RobotTaskListDTO reconstructRobotTaskSeer(String name, String stringValue);

    RobotTaskListDTO reconstructRobotTask(String name, String stringValue);
}
