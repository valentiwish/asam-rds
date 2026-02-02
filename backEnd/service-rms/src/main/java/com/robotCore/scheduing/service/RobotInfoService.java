package com.robotCore.scheduing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.scheduing.vo.RobotInfoVO;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/7
 **/
public interface RobotInfoService extends IService<RobotBasicInfo> {

    List<RobotBasicInfo> findList(Long id, String name);

    List<RobotBasicInfo> findListByGroupName(String groupName);

    IPage<RobotInfoVO> findPageList(IPage<RobotBasicInfo> varPage, RobotBasicInfo robotBasicInfo) throws InterruptedException;

    List<RobotInfoVO> dataInit(List<RobotBasicInfo> list) throws InterruptedException;

}
