package com.robotCore.robot.service;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/4/26
 **/
public interface RobotRealTimeInfoService {

    boolean getRealTimeStatusInfo(String robotIp);

    boolean pushMsgConfig(String robotIp);

    void disRealTimeStatusInfo(String robotIp);
}
