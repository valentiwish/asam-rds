package com.robotCore.railInspection.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.railInspection.entity.RailInspectionInfo;
import com.robotCore.railInspection.entityVo.RailInspectionInfoVo;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/7/21
 **/
public interface RailInspectionInfoService extends IService<RailInspectionInfo> {

    IPage<RailInspectionInfoVo> findPageList(IPage<RailInspectionInfo> varPage, RailInspectionInfo railInspectionInfo);

    List<RailInspectionInfoVo> dataInit(List<RailInspectionInfo> list);
}
