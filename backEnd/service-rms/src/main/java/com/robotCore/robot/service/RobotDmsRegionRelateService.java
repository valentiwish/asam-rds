package com.robotCore.robot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.entity.RobotDmsRegionRelate;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/6/18
 **/
public interface RobotDmsRegionRelateService extends IService<RobotDmsRegionRelate> {

    List<RobotDmsRegionRelate> getRelateRegionsByCurrentRegionId(String currentRegionId);

    List<RobotDmsRegionRelate> getRelateRegions();
}
