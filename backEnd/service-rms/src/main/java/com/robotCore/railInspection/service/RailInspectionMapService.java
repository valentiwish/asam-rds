package com.robotCore.railInspection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.robotCore.railInspection.entity.RailInspectionMap;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/7/21
 **/
public interface RailInspectionMapService extends IService<RailInspectionMap> {

    List<RailInspectionMap> findList();
}
