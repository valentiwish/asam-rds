package com.robotCore.railInspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.railInspection.entity.RailInspectionMap;
import com.robotCore.railInspection.mapper.RailInspectionMapDao;
import com.robotCore.railInspection.service.RailInspectionMapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @Des:
 * @author: zxl
 * @date: 2023/11/6
 **/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RailInspectionMapServiceImpl extends ServiceImpl<RailInspectionMapDao, RailInspectionMap> implements RailInspectionMapService {

    @Override
    public List<RailInspectionMap> findList() {
        QueryWrapper<RailInspectionMap> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByAsc(RailInspectionMap::getPointLength);
        return list(wrapper);
    }

}
