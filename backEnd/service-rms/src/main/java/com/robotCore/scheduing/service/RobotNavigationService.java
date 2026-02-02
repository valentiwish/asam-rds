package com.robotCore.scheduing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.scheduing.entity.RobotNavigation;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/5
 **/
public interface RobotNavigationService extends IService<RobotNavigation> {

    List<RobotNavigation> findByRobotName(String robotName);

    RobotNavigation findByTaskId(String taskId);

    List<RobotNavigation> findByRobotNameExcludeSelf(String robotName);
}
