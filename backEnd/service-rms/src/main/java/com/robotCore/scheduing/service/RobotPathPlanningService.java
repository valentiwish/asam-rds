package com.robotCore.scheduing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.scheduing.entity.RobotPathPlanning;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/9/8
 **/
public interface RobotPathPlanningService extends IService<RobotPathPlanning> {

    List<RobotPathPlanning> getListByTaskId(String taskId);

}
