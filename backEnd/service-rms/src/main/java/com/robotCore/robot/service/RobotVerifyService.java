package com.robotCore.robot.service;

import com.robotCore.robot.entityVo.VerifyGroupMapVO;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/30
 **/
public interface RobotVerifyService {

    VerifyGroupMapVO verifyGroupMap(String groupName);
}
