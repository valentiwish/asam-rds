package com.robotCore.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotDmsRegionRelate;
import com.robotCore.robot.mapper.RobotDmsRegionRelateDao;
import com.robotCore.robot.service.RobotDmsRegionRelateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/6/18
 **/
@Slf4j
@Service
public class RobotDmsRegionRelateServiceImpl extends ServiceImpl<RobotDmsRegionRelateDao, RobotDmsRegionRelate> implements RobotDmsRegionRelateService {

    /**
     * 根据当前区域id获取绑定的区域
     * @param currentRegionId
     * @return
     */
    @Override
    public List<RobotDmsRegionRelate> getRelateRegionsByCurrentRegionId(String currentRegionId) {
        QueryWrapper<RobotDmsRegionRelate> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(currentRegionId), RobotDmsRegionRelate::getCurrentRegionId, currentRegionId);
        wrapper.lambda().orderByDesc(RobotDmsRegionRelate::getId);
        return list(wrapper);//返回list类型对象列表
    }

    @Override
    public List<RobotDmsRegionRelate> getRelateRegions() {
        QueryWrapper<RobotDmsRegionRelate> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByDesc(RobotDmsRegionRelate::getId);
        return list(wrapper);//返回list类型对象列表
    }

}
