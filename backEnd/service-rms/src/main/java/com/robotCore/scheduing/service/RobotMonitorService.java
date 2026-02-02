package com.robotCore.scheduing.service;

import com.robotCore.common.base.vo.UserVO;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.scheduing.dto.RobotRunDTO;
import com.robotCore.scheduing.vo.RobotCountVO;
import com.robotCore.scheduing.vo.RobotMonitorVO;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/5/30
 **/
public interface RobotMonitorService {

    RobotCountVO getRobotOnlineInfo();

    Map<Integer,String> getRobotStatusList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    boolean acquireControl(List<RobotBasicInfo> robots, UserVO user);

    boolean releaseControl(List<RobotBasicInfo> robots);

    boolean getRobotLocInfo(String robotIp);

    boolean relocation(List<RobotBasicInfo> robots);

    boolean confirmLocation(List<RobotBasicInfo> robots);

    boolean continueNavigation(List<RobotBasicInfo> robots);

    boolean pauseNavigation(List<RobotBasicInfo> robots);

}
