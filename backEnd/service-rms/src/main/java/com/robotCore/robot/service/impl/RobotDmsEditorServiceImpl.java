package com.robotCore.robot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotDmsEditor;
import com.robotCore.robot.mapper.RobotDmsEditorDao;
import com.robotCore.robot.service.RobotDmsEditorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/6/18
 **/
@Slf4j
@Service
public class RobotDmsEditorServiceImpl extends ServiceImpl<RobotDmsEditorDao, RobotDmsEditor> implements RobotDmsEditorService {

    @Override
    public List<RobotDmsEditor> findList() {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByDesc(RobotDmsEditor::getId);
        return list(wrapper);//返回list类型对象列表
    }

    //根据机器人id查询单个管控区域(leo add)
    @Override
    public RobotDmsEditor findFocusAreaByRobotId(String robotId) {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotId), RobotDmsEditor::getRobotId, robotId);
        List<RobotDmsEditor> list = list(wrapper);
        //返回list中的第一个元素，如果没有则返回null
        return list.size() > 0 ? list.get(0) : null;
    }

    //根据机器人id查询多个管控区域(leo add)
    @Override
    public List<RobotDmsEditor> findFocusAreasByRobotId(String robotId) {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotId), RobotDmsEditor::getRobotId, robotId);
        return list(wrapper);
    }

    //根据区域类型查询管控区域(leo add)
    @Override
    public List<RobotDmsEditor> findListByAreaType(Integer areaType) {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(areaType), RobotDmsEditor::getAreaType, areaType);
        // 筛选 occupiedStatus 为 1 的区域
//        wrapper.lambda().eq(RobotDmsEditor::getOccupiedStatus, 1);

        List<RobotDmsEditor> list = list(wrapper);
        return list.isEmpty() ? null : list;
    }

    /**
     * <获取目标站点所在管控区域> (leo add)
     *
     * @param targetPort 步进目标站点
     * @return 返回目标站点所在管控区域对象
     */
    @Override
    public RobotDmsEditor getFocusAreaByTargetPort(String targetPort) {
        // 获取所有管控区域
        List<RobotDmsEditor> focusAreaList = findListByAreaType(2);
        // 如果当前地图不存在管控区域，则直接返回null
        if (focusAreaList == null) {
            return null;
        }
        // 遍历所有管控区域
        for (RobotDmsEditor focusArea : focusAreaList) {
            String areaContainPointsJson = focusArea.getAreaContainPoints();
            List<String> areaPoints = JSON.parseArray(areaContainPointsJson, String.class);
//            Set<String> areaPointSet = JSON.parseObject(areaContainPointsJson, new TypeReference<Set<String>>() {}.getType());

            // 遍历当前管控区域内所有站点
            for (String areaPoint : areaPoints) {
                if (targetPort.equals(areaPoint)) {
                    // 返回目标点所在管控区域
                    return focusArea;
                }
            }
        }
        return null;
    }

    // 根据区域类型查询管控区域-连续导航(leo add)
    @Override
    public List<RobotDmsEditor> findAreasByType(Integer areaType) {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(areaType), RobotDmsEditor::getAreaType, areaType);
        // 筛选 occupiedStatus 为 1 的区域
//        wrapper.lambda().eq(RobotDmsEditor::getOccupiedStatus, 1);
        return list(wrapper);
    }

    // 根据机器人Id和区域类型查询管控区域-连续导航(leo add)
    @Override
    public List<RobotDmsEditor> findAreasByRobotIdAndAreaType(String robotId,Integer areaType) {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotId),RobotDmsEditor::getRobotId, robotId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(areaType), RobotDmsEditor::getAreaType, areaType);
        return list(wrapper);
    }

    /**
     * <获取目标站点所覆盖的指定类型管控区域-连续导航> (leo add)
     *
     * @param targetPorts 待锁定站点列表
     * @param areaType    区域类型
     * @return 返回目标站点所覆盖的管控区域列表
     */
    @Override
    public List<RobotDmsEditor> getAssignedTypeAreasByTargetPorts(List<String> targetPorts, Integer areaType) {
        // 将目标站点列表转为HashSet提升查询效率（O(1)时间复杂度）
        Set<String> targetPortSet = new HashSet<>(targetPorts);
        // 获取所有指定类型区域
        List<RobotDmsEditor> assignedTypeAreas = findAreasByType(areaType);
        // 如果当前地图不存在指定类型区域，则直接返回空列表
        if (assignedTypeAreas.isEmpty()) {
            return assignedTypeAreas;
        }
        // 返回目标点覆盖到的指定类型区域列表
        return assignedTypeAreas.stream()
                .filter(assignedTypeArea -> {
                    List<String> areaPoints = JSON.parseArray(assignedTypeArea.getAreaContainPoints(), String.class);
                    return !Collections.disjoint(areaPoints, targetPortSet);
                })
                .collect(Collectors.toList());
    }

    // 将区域内的站点Json字符串解析为站点集合(leo add)
    @Override
    public Set<String> parseAreaPoints(RobotDmsEditor area) {
        return JSON.parseObject(area.getAreaContainPoints(), new TypeReference<Set<String>>() {
        });
    }

    @Override
    public List<RobotDmsEditor> findListByAreaCenterPoint(String areaCenterPoint) {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(areaCenterPoint), RobotDmsEditor::getAreaCenterPoint, areaCenterPoint);
        List<RobotDmsEditor> list = list(wrapper);
        return list.isEmpty() ? null : list;
    }

    /**
     *  根据区域类型和占用状态获取区域列表
     * @param areaType
     * @param occupiedStatus
     * @param robotName
     * @return
     */
    @Override
    public List<RobotDmsEditor> findListByAreaTypeAndOccupiedAndRobotName(Integer areaType, Integer occupiedStatus, String robotName) {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(areaType), RobotDmsEditor::getAreaType, areaType);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(occupiedStatus), RobotDmsEditor::getOccupiedStatus, occupiedStatus);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotName), RobotDmsEditor::getOccupiedRobotName, robotName);
        List<RobotDmsEditor> list = list(wrapper);
        return list.isEmpty() ? null : list;
    }

    /**
     * 根据占用状态和机器人品牌查询相应区域
     * @param areaType
     * @param occupiedStatus
     * @param robotType
     * @return
     */
    @Override
    public List<RobotDmsEditor> findListByAreaTypeAndOccupiedAndRobotType(Integer areaType, Integer occupiedStatus, Integer robotType) {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(areaType), RobotDmsEditor::getAreaType, areaType);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(occupiedStatus), RobotDmsEditor::getOccupiedStatus, occupiedStatus);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotType), RobotDmsEditor::getOccupiedRobotType, robotType);
        List<RobotDmsEditor> list = list(wrapper);
        return list.isEmpty() ? null : list;
    }

    /**
     * 查询不是被当前机器人品牌所占用的管控区域
     * @param areaType
     * @param occupiedStatus
     * @param robotType
     * @return
     */
    @Override
    public List<RobotDmsEditor> findListByAreaTypeAndOccupiedAndNotRobotType(Integer areaType, Integer occupiedStatus, Integer robotType) {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(areaType), RobotDmsEditor::getAreaType, areaType);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(occupiedStatus), RobotDmsEditor::getOccupiedStatus, occupiedStatus);
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(robotType), RobotDmsEditor::getOccupiedRobotType, robotType);
        List<RobotDmsEditor> list = list(wrapper);
        return list.isEmpty() ? null : list;
    }

    /**
     * 获取区域列表
     * @param varPage
     * @param robotDmsEditor
     * @return
     */
    @Override
    public IPage<RobotDmsEditor> findPageList(IPage<RobotDmsEditor> varPage, RobotDmsEditor robotDmsEditor) {
        QueryWrapper<RobotDmsEditor> wrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(robotDmsEditor)){
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotDmsEditor.getAreaName()),RobotDmsEditor::getAreaName, robotDmsEditor.getAreaName());
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotDmsEditor.getAreaCenterPoint()),RobotDmsEditor::getAreaCenterPoint, robotDmsEditor.getAreaCenterPoint());
            //区域类型
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotDmsEditor.getAreaType()), RobotDmsEditor::getAreaType, robotDmsEditor.getAreaType());
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotDmsEditor.getOccupiedStatus()),RobotDmsEditor::getOccupiedStatus, robotDmsEditor.getOccupiedStatus());
        }
        wrapper.lambda().orderByAsc(RobotDmsEditor::getCreateTime);
        return this.baseMapper.selectPage(varPage,wrapper);
    }
}
