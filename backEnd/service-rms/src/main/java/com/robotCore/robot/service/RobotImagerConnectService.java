package com.robotCore.robot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.entity.RobotConnect;
import com.robotCore.robot.entity.RobotImagerConnect;

import java.util.List;

/**
 * @Des:
 * @author: lzy
 * @date: 2023/9/25
 **/
public interface RobotImagerConnectService extends IService<RobotImagerConnect> {

    List<RobotImagerConnect> findList();
}
