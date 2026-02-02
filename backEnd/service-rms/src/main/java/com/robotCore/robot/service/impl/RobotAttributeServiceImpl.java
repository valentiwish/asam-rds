package com.robotCore.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotAttribute;
import com.robotCore.robot.mapper.RobotAttributeDao;
import com.robotCore.robot.service.RobotAttributeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/28
 **/
@Slf4j
@Service
public class RobotAttributeServiceImpl extends ServiceImpl<RobotAttributeDao, RobotAttribute> implements RobotAttributeService {

    @Override
    public boolean save(RobotAttribute model, Long userId, String userName) {
        if(model.getId()==null){
            model.setCreateId(userId);
            model.setCreateUser(userName);
            model.setCreateTime(new Timestamp(new Date().getTime()));
        }else{
            model.setUpdateId(userId);
            model.setUpdateUser(userName);
            model.setUpdateTime(new Timestamp(new Date().getTime()));
        }
        return saveOrUpdate(model);
    }

    @Override
    public List<RobotAttribute> getRobotBindAttr(String vehicleId, String mapPoint, Integer attributeCode) {
        QueryWrapper<RobotAttribute> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(vehicleId), RobotAttribute::getVehicleId, vehicleId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(mapPoint), RobotAttribute::getPoint, mapPoint);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(attributeCode), RobotAttribute::getAttributeCode, attributeCode);
        return list(wrapper);
    }

    @Override
    public List<RobotAttribute> getRobotBindAttr(String vehicleId, Integer attributeCode) {
        QueryWrapper<RobotAttribute> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(vehicleId), RobotAttribute::getVehicleId, vehicleId);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(attributeCode), RobotAttribute::getAttributeCode, attributeCode);
        return list(wrapper);
    }

    /**
     * 获取机器人已经绑定的属性列表
     * @return
     */
    @Override
    public List<RobotAttribute> getRobotBindAttr(String vehicleId) {
        QueryWrapper<RobotAttribute> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(vehicleId), RobotAttribute::getVehicleId, vehicleId);
        //返回list类型对象列表
        return list(wrapper);
    }

    @Override
    public List<RobotAttribute> getRobotBindAttr() {
        QueryWrapper<RobotAttribute> wrapper = new QueryWrapper<>();
        //返回list类型对象列表
        return list(wrapper);
    }

    /**
     * 获取机器人已经绑定的光通信相关的属性列表
     * @return
     */
    @Override
    public List<RobotAttribute> getRobotBindDmsAttr(String vehicleId, Integer attributeCode) {
        QueryWrapper<RobotAttribute> wrapper = new QueryWrapper<>();
//        wrapper.lambda().eq(RobotAttribute::getAttributeCode,5).or().eq(RobotAttribute::getAttributeCode,6);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(attributeCode), RobotAttribute::getAttributeCode, attributeCode);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(vehicleId), RobotAttribute::getVehicleId, vehicleId);
        //返回list类型对象列表
        return list(wrapper);
    }

}
