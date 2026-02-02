package com.robotCore.robot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.entity.RobotDms;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/7/21
 **/
public interface RobotDmsService  extends IService<RobotDms> {

    IPage<RobotDms> findPageList(IPage<RobotDms> varPage, RobotDms robotDms);

    List<RobotDms> findListByDmsName(String id, String dmsName);

    List<RobotDms> findListByDmsIp(String id, String dmsIp);

    List<RobotDms> findListByDmsPoint(String id, String dmsPoint);

    List<RobotDms> findList(String dmsPoint, Integer attributeCode);

    RobotDms getDmsByDmsPoint(String dmsPoint);
}
