package com.robotCore.robot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.entity.RobotChargePile;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/3/18
 **/
public interface RobotChargePileService extends IService<RobotChargePile> {

    IPage<RobotChargePile> findPageList(IPage<RobotChargePile> varPage, RobotChargePile robotChargePile);

    List<RobotChargePile> findListByAttribute(String id, String chargePileName, String chargePileIp, String chargePilePoint);
}
