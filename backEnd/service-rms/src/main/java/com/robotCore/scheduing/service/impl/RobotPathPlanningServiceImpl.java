package com.robotCore.scheduing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.scheduing.entity.RobotPathPlanning;
import com.robotCore.scheduing.mapper.RobotPathPlanningDao;
import com.robotCore.scheduing.service.RobotPathPlanningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.robotCore.common.constant.Constant.STATE_VALID;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/9/8
 **/
@Slf4j
@Service
public class RobotPathPlanningServiceImpl extends ServiceImpl<RobotPathPlanningDao, RobotPathPlanning> implements RobotPathPlanningService {

    /**
     * 根据任务ID获取算法执行返回的结果
     * @param taskId
     * @return
     */
    @Override
    public List<RobotPathPlanning> getListByTaskId(String taskId) {
        QueryWrapper<RobotPathPlanning> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(taskId),RobotPathPlanning::getTaskId, taskId);
        wrapper.lambda().eq(RobotPathPlanning::getState, STATE_VALID).orderByAsc(RobotPathPlanning::getCreateTime);
        return list(wrapper);
    }
}
