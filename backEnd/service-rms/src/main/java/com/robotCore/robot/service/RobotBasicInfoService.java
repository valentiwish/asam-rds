package com.robotCore.robot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.robotCore.robot.dto.RobotStatusMapResDTO;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.entityVo.RobotBasicInfoVO;
import com.robotCore.robot.entityVo.RobotByAddVO;
import com.robotCore.robot.entityVo.RobotsAddVO;
import com.robotCore.scheduing.dto.GroupAndRobotDTO;
import com.robotCore.scheduing.vo.RobotMonitorVO;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;


/**
 * @Description: 描述
 * @Author: Created by zxl on 2023/4/19.
 */
public interface RobotBasicInfoService extends IService<RobotBasicInfo> {

    List<RobotBasicInfo> findListByGroupName(String groupName);

    List<RobotBasicInfo> findListAll();

    List<RobotBasicInfo> findListByMapName(String robotName, String mapName);

    List<RobotBasicInfo> findListByMapNames(List<String> mapNames);

    List<GroupAndRobotDTO> getGroupAndRobot();

    boolean isExistAbnormal(RobotMonitorVO robotMonitorVO, RobotBasicInfo robotBasicInfo);

    List<RobotBasicInfo> getEffectiveRobots(String vehicleId, String groupName);

    List<RobotBasicInfo> findByName(String vehicleId);

    RobotBasicInfo findByVehicleId(String vehicleId);

    RobotBasicInfo findByIp(String robotIp);

    List<RobotByAddVO> getAddRobotInfo();

    void getRealTimeBaseInfo(String robotIp);

    RobotBasicInfoVO realTimeDataInit();

    RobotBasicInfoVO getOffLineBaseInfo(String robotIp);

    RobotBasicInfo setRobotValue(RobotByAddVO robotByAddVO, RobotsAddVO robotsAddVO);

    RobotStatusMapResDTO getRobotMapInfo(String robotIp);

    boolean downloadRobotMap(String robotIp);

    boolean downloadAllRobotMap();

    boolean downloadCurrentRobotMap(String robotIp);

    List<String> getAllMapsData() throws JsonProcessingException;

    boolean connectTcp(String robotIp, int port);

    boolean save(RobotBasicInfo model, Long userId, String userName);

    boolean runRobotTask(String robotName, String beginPort, String endPort);

    void autoCharge(String vehicleId) throws Exception;

    void autoDmsCharge(String vehicleId, String dmsParkPoint) throws InterruptedException, IOException;

    boolean getPathList(String vehicle) throws Exception;

    boolean autoConnect(String robotIp) throws Exception;

    boolean acquireControl(RobotBasicInfo robot);

    boolean releaseControl(RobotBasicInfo robot);
}
