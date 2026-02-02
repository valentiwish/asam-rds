package com.asam.sysdata.service;

import com.asam.sysdata.entity.SysData;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.List;


public interface SysDataService extends IService<SysData> {

    IPage<SysData> findPageList(JPAPage varBasePage, String textName, Long dataTypeId);

    List<SysData> findList(Long id, String textName, String dataTypeCode, Long dataTypeId);

    boolean save(SysData model, Long userId,String userName);

    void updateByDataTypeId(String dataTypeCode, String dataTypeName, Long dataTypeId);

    void deleteByDataTypeId(Long dataTypeId);
}
