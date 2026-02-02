package com.asam.sysdata.service;

import com.asam.sysdata.entity.SysDataType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.List;


public interface SysDataTypeService extends IService<SysDataType> {

    IPage<SysDataType> findPageList(JPAPage varBasePage, String typeName, String typeCode);

    List<SysDataType> findList(Long id, String code);

    boolean save(SysDataType model, Long userId,String userName);
}
