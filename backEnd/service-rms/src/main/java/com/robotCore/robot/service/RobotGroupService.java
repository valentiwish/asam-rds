package com.robotCore.robot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.entity.RobotGroup;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/4/17
 **/
public interface RobotGroupService extends IService<RobotGroup>{

    List<RobotGroup> findList();

    List<RobotGroup> findList(String id, String name);

    boolean save(RobotGroup model, Long userId, String userName);
}
