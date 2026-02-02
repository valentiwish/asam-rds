package com.asam.system.service;


import com.asam.system.entity.SysVariable;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.List;

public interface SysVariableService extends IService<SysVariable> {

    IPage<SysVariable> findPageList(JPAPage varBasePage, SysVariable sysVariable);

    List<SysVariable> findList(String id, String varCode);

    boolean save(SysVariable model, Long userId,String userName);
}
