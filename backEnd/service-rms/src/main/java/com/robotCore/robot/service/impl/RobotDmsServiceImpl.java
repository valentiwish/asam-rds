package com.robotCore.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotDms;
import com.robotCore.robot.mapper.RobotDmsDao;
import com.robotCore.robot.service.RobotDmsService;
import com.robotCore.scheduing.common.enums.RobotDmsAttributeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/7/21
 **/
@Slf4j
@Service
public class RobotDmsServiceImpl extends ServiceImpl<RobotDmsDao, RobotDms> implements RobotDmsService {

    @Override
    public IPage<RobotDms> findPageList(IPage<RobotDms> varPage, RobotDms robotDms) {
        QueryWrapper<RobotDms> wrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(robotDms)){
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotDms.getDmsName()),RobotDms::getDmsName, robotDms.getDmsName());
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotDms.getDmsIp()),RobotDms::getDmsIp, robotDms.getDmsIp());
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotDms.getDmsPoint()),RobotDms::getDmsPoint, robotDms.getDmsPoint());
        }
        wrapper.lambda().orderByAsc(RobotDms::getCreateTime);
        return this.baseMapper.selectPage(varPage,wrapper);
    }

    @Override
    public List<RobotDms> findListByDmsName(String id, String dmsName) {
        QueryWrapper<RobotDms> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),RobotDms::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(dmsName),RobotDms::getDmsName, dmsName);
        return list(wrapper);
    }

    @Override
    public List<RobotDms> findListByDmsIp(String id, String dmsIp) {
        QueryWrapper<RobotDms> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),RobotDms::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(dmsIp),RobotDms::getDmsIp, dmsIp);
        return list(wrapper);
    }

    @Override
    public List<RobotDms> findListByDmsPoint(String id, String dmsPoint) {
        QueryWrapper<RobotDms> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),RobotDms::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(dmsPoint),RobotDms::getDmsPoint, dmsPoint);
        return list(wrapper);
    }

    @Override
    public List<RobotDms> findList(String dmsPoint, Integer attributeCode) {
        QueryWrapper<RobotDms> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(dmsPoint),RobotDms::getDmsPoint, dmsPoint);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(attributeCode),RobotDms::getDmsType, Objects.requireNonNull(RobotDmsAttributeTypeEnum.getByCode(attributeCode)).getName());
        return list(wrapper);
    }

    /**
     * 根据光通信站点位名称得到光通信站
     * @param dmsPoint
     * @return
     */
    @Override
    public RobotDms getDmsByDmsPoint(String dmsPoint) {
        QueryWrapper<RobotDms> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(dmsPoint),RobotDms::getDmsPoint, dmsPoint);
        return list(wrapper).size() > 0 ? list(wrapper).get(0) : null;
    }

}
