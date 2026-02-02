package com.robotCore.scheduing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.scheduing.entity.RobotNavigation;
import com.robotCore.scheduing.mapper.RobotNavigationDao;
import com.robotCore.scheduing.service.RobotNavigationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/9/13
 **/
@Slf4j
@Service
public class RobotNavigationServiceImpl extends ServiceImpl<RobotNavigationDao, RobotNavigation> implements RobotNavigationService {

    @Override
    public List<RobotNavigation> findByRobotName(String robotName) {
        QueryWrapper<RobotNavigation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RobotNavigation::getRobotName, robotName);
        return list(wrapper);
    }

    @Override
    public RobotNavigation findByTaskId(String taskId) {
        QueryWrapper<RobotNavigation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RobotNavigation::getTaskId, taskId);
        List<RobotNavigation> list = list(wrapper);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<RobotNavigation> findByRobotNameExcludeSelf(String robotName) {
        QueryWrapper<RobotNavigation> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(RobotNavigation::getRobotName, robotName);
        return list(wrapper);
    }
}
