package com.robotCore.robot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.entity.RobotType;
import com.robotCore.robot.entityVo.RobotTypeVO;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/11/1
 **/
public interface RobotTypeService extends IService<RobotType> {

    IPage<RobotTypeVO> findPageList(IPage<RobotType> varPage, RobotType robotType);

    List<RobotTypeVO> dataInit(List<RobotType> list);

    List<RobotType> findListByTypeName(String id, String typeName);

    List<RobotType> findList();

}
