package com.robotCore.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotChargePile;
import com.robotCore.robot.mapper.RobotChargePileDao;
import com.robotCore.robot.service.RobotChargePileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/3/18
 **/
@Slf4j
@Service
public class RobotChargePileServiceImpl extends ServiceImpl<RobotChargePileDao, RobotChargePile> implements RobotChargePileService {

    @Override
    public IPage<RobotChargePile> findPageList(IPage<RobotChargePile> varPage, RobotChargePile robotChargePile) {
        QueryWrapper<RobotChargePile> wrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(robotChargePile)){
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotChargePile.getChargePileName()),RobotChargePile::getChargePileName, robotChargePile.getChargePileName());
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotChargePile.getChargePileIp()),RobotChargePile::getChargePileIp, robotChargePile.getChargePileIp());
            wrapper.lambda().eq(ObjectUtil.isNotEmpty(robotChargePile.getChargePilePoint()),RobotChargePile::getChargePilePoint, robotChargePile.getChargePilePoint());
        }
        wrapper.lambda().orderByAsc(RobotChargePile::getCreateTime);
        return this.baseMapper.selectPage(varPage,wrapper);
    }

    @Override
    public List<RobotChargePile> findListByAttribute(String id, String chargePileName, String chargePileIp, String chargePilePoint) {
        QueryWrapper<RobotChargePile> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),RobotChargePile::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(chargePileName),RobotChargePile::getChargePileName, chargePileName);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(chargePileIp),RobotChargePile::getChargePileIp, chargePileIp);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(chargePilePoint),RobotChargePile::getChargePilePoint, chargePilePoint);
        return list(wrapper);
    }

}
