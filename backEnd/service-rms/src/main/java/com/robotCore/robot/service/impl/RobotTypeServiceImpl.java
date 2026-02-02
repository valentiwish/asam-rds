package com.robotCore.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotType;
import com.robotCore.robot.entityVo.RobotTypeVO;
import com.robotCore.robot.mapper.RobotTypeDao;
import com.robotCore.robot.service.RobotTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/11/1
 **/
@Slf4j
@Service
public class RobotTypeServiceImpl extends ServiceImpl<RobotTypeDao, RobotType> implements RobotTypeService {
    @Override
    public IPage<RobotTypeVO> findPageList(IPage<RobotType> varPage, RobotType robotType) {
        QueryWrapper<RobotType> wrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(robotType)){
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotType.getTypeName()),RobotType::getTypeName, robotType.getTypeName());
        }
        wrapper.lambda().orderByAsc(RobotType::getCreateTime);
        List<RobotType> list = list(wrapper);
//        IPage<RobotType> robotTypeIPage = this.baseMapper.selectPage(varPage, wrapper);
        IPage<RobotTypeVO> page =new Page<>();
        page.setCurrent(varPage.getCurrent());
        page.setSize(varPage.getSize());
        page.setTotal(list.size());
        page.setRecords(dataInit(list));
        return page;
    }

    @Override
    public List<RobotTypeVO> dataInit(List<RobotType> list) {
        List<RobotTypeVO> robotTypeVOS = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RobotTypeVO vo = new RobotTypeVO();
            RobotType robotType = list.get(i);
            vo.setSerialNumber(i + 1);
            vo.setId(robotType.getId());
            vo.setTypeName(robotType.getTypeName());
            robotTypeVOS.add(vo);
        }
        return robotTypeVOS;
    }

    @Override
    public List<RobotType> findListByTypeName(String id, String typeName) {
        QueryWrapper<RobotType> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id),RobotType::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(typeName),RobotType::getTypeName, typeName);
        return list(wrapper);
    }

    @Override
    public List<RobotType> findList() {
        QueryWrapper<RobotType> wrapper = new QueryWrapper<>();
        return list(wrapper);
    }

}
