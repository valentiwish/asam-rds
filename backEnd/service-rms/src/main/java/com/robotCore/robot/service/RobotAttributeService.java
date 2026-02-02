package com.robotCore.robot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.entity.RobotAttribute;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/4/17
 **/
public interface RobotAttributeService extends IService<RobotAttribute>{

    boolean save(RobotAttribute model, Long userId, String userName);

    List<RobotAttribute> getRobotBindAttr(String vehicleId, String mapPoint, Integer attributeCode);

    List<RobotAttribute> getRobotBindAttr(String vehicleId, Integer attributeCode);

    List<RobotAttribute> getRobotBindAttr(String vehicleId);

    List<RobotAttribute> getRobotBindAttr();

    List<RobotAttribute> getRobotBindDmsAttr(String vehicleId, Integer attributeCode);
}
