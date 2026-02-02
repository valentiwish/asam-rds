package com.robotCore.scheduing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.scheduing.entity.RobotWaybillDetail;
import com.robotCore.scheduing.mapper.RobotWaybillDetailDao;
import com.robotCore.scheduing.service.RobotWaybillDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/8/14
 **/
@Slf4j
@Service
public class RobotWaybillDetailServiceImpl extends ServiceImpl<RobotWaybillDetailDao, RobotWaybillDetail> implements RobotWaybillDetailService {
    @Override
    public IPage<RobotWaybillDetail> findPageList(IPage<RobotWaybillDetail> varPage, RobotWaybillDetail robotWaybillDetail) {
       return null;
    }

    @Override
    public List<RobotWaybillDetail> getListByWaybillId(String waybillId) {
        QueryWrapper<RobotWaybillDetail> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(waybillId),RobotWaybillDetail::getWaybillId, waybillId);
        wrapper.lambda().orderByAsc(RobotWaybillDetail::getCreateTime);
        return list(wrapper);
    }

    @Override
    public RobotWaybillDetail findOne(String waybillId) {
        QueryWrapper<RobotWaybillDetail> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(waybillId),RobotWaybillDetail::getWaybillId,waybillId);
        wrapper.lambda().orderByDesc(RobotWaybillDetail::getCreateTime).last("limit 1");
        List<RobotWaybillDetail> list = list(wrapper);
        return list.size() > 0 ? list.get(0) : null;
    }
}
