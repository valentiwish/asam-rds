package com.robotCore.scheduing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.scheduing.entity.RobotTaskStep;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/15
 **/
public interface RobotTaskStepService extends IService<RobotTaskStep> {

    RobotTaskStep findOne(String taskId, String vehicle, String waybillId);
}
