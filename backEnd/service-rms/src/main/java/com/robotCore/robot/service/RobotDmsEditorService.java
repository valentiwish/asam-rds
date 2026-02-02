package com.robotCore.robot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.robot.entity.RobotDmsEditor;

import java.util.List;
import java.util.Set;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/6/18
 **/
public interface RobotDmsEditorService extends IService<RobotDmsEditor> {

    List<RobotDmsEditor> findList();

    //根据机器人id查询单个管控区域(leo add)
    RobotDmsEditor findFocusAreaByRobotId(String robotId);

    //根据机器人id查询多个管控区域(leo add)
    List<RobotDmsEditor> findFocusAreasByRobotId(String robotId);

    List<RobotDmsEditor> findListByAreaType(Integer areaType);

    //获取目标站点所在管控区域(leo add)
    RobotDmsEditor getFocusAreaByTargetPort(String targetPort);

    //根据区域类型查询管控区域-连续导航(leo add)
    List<RobotDmsEditor> findAreasByType(Integer areaType);

    //根据区域类型查询管控区域-连续导航(leo add)
    List<RobotDmsEditor> findAreasByRobotIdAndAreaType(String robotId, Integer areaType);

    //获取目标站点所在管控区域-连续导航(leo add)
    List<RobotDmsEditor> getAssignedTypeAreasByTargetPorts(List<String> targetPorts, Integer areaType);

    //将区域内的站点Json字符串解析为站点集合(leo add)
    Set<String> parseAreaPoints(RobotDmsEditor elevatorArea);

    List<RobotDmsEditor> findListByAreaCenterPoint(String areaCenterPoint);

    List<RobotDmsEditor> findListByAreaTypeAndOccupiedAndRobotName(Integer areaType, Integer occupiedStatus, String robotName);

    List<RobotDmsEditor> findListByAreaTypeAndOccupiedAndRobotType(Integer areaType, Integer occupiedStatus, Integer robotType);

    List<RobotDmsEditor> findListByAreaTypeAndOccupiedAndNotRobotType(Integer areaType, Integer occupiedStatus, Integer robotType);

    IPage<RobotDmsEditor> findPageList(IPage<RobotDmsEditor> varPage, RobotDmsEditor robotDmsEditor);
}
