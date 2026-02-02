package com.robotCore.robot.service.impl;

import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.entityVo.RobotByGroupVO;
import com.robotCore.robot.entityVo.VerifyGroupMapVO;
import com.robotCore.robot.service.RobotBasicInfoService;
import com.robotCore.robot.service.RobotVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/30
 **/
@Slf4j
@Service
public class RobotVerifyServiceImpl implements RobotVerifyService {

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    /**
     * 校验分组中的地图是否一致
     * @param groupName
     * @return
     */
    @Override
    public VerifyGroupMapVO verifyGroupMap(String groupName) {
        List<RobotBasicInfo> robotBasicInfoList = robotBasicInfoService.findListByGroupName(groupName);
        VerifyGroupMapVO verifyGroupMapVO = new VerifyGroupMapVO();
        List<RobotByGroupVO> robotByGroupVOList = new ArrayList<>();
        Set<String> mapNames = new HashSet<>();
        for (RobotBasicInfo robotBasicInfo : robotBasicInfoList) {
            RobotByGroupVO vo = new RobotByGroupVO();
            vo.setId(robotBasicInfo.getId());
            vo.setCurrentIp(robotBasicInfo.getCurrentIp());
            vo.setCurrentMap(robotBasicInfo.getCurrentMap());
            vo.setVehicleId(robotBasicInfo.getVehicleId());
            robotByGroupVOList.add(vo);
            mapNames.add(robotBasicInfo.getCurrentMap());
        }
        //分组中所有机器人地图是否一致
        if (robotBasicInfoList.size() == 0) {
            verifyGroupMapVO.setAbnormal(true);
        } else {
            verifyGroupMapVO.setAbnormal(robotBasicInfoList.size() != mapNames.size());
        }
        verifyGroupMapVO.setRobotByGroupVOList(robotByGroupVOList);
        return verifyGroupMapVO;
    }
}
