package com.robotCore.robot.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.entity.RobotConnect;
import com.robotCore.scheduing.vo.RobotConnectVO;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/4/25
 **/
public interface RobotConnectService extends IService<RobotConnect> {

    List<RobotConnect> findList();

    List<RobotConnect> listByIp(String robotIp);

    String getRobotType(String robotIp);

    List<RobotConnectVO> dataInit(List<RobotConnect> list);

    boolean connectTcp(String robotIp, int port);

    RobotConnect findByIp(String robotIp);

    boolean save(RobotConnect model, Long userId, String userName);

    boolean isOnline(String robotIp);
}
