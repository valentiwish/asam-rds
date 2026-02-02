package com.robotCore.scheduing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.scheduing.entity.RobotTaskStep;
import com.robotCore.scheduing.mapper.RobotTaskStepDao;
import com.robotCore.scheduing.service.RobotTaskStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/15
 **/
@Slf4j
@Service
public class RobotTaskStepServiceImpl extends ServiceImpl<RobotTaskStepDao, RobotTaskStep> implements RobotTaskStepService {

    @Override
    public RobotTaskStep findOne(String taskId, String vehicle, String waybillId) {
        QueryWrapper<RobotTaskStep> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(taskId), RobotTaskStep::getTaskId, taskId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(vehicle), RobotTaskStep::getRobotName, vehicle);
        if (waybillId == null) {
            wrapper.lambda().isNull(RobotTaskStep::getWaybillId);
        } else {
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(vehicle), RobotTaskStep::getWaybillId, waybillId);
        }
        wrapper.lambda().orderByDesc(RobotTaskStep::getOperateTime).last("limit 1");
        List<RobotTaskStep> list = list(wrapper);
        return list.get(0);
    }
}
