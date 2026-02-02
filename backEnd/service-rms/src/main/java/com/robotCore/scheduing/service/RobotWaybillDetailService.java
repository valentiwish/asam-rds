package com.robotCore.scheduing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.scheduing.entity.RobotWaybillDetail;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/8/14
 **/
public interface RobotWaybillDetailService extends IService<RobotWaybillDetail> {

    IPage<RobotWaybillDetail> findPageList(IPage<RobotWaybillDetail> varPage, RobotWaybillDetail robotWaybillDetail);

    List<RobotWaybillDetail> getListByWaybillId(String waybillId);

    RobotWaybillDetail findOne(String waybillId);
}
