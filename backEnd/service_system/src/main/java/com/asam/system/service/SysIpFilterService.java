package com.asam.system.service;

import com.asam.system.entity.SysIpFilter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.List;


public interface SysIpFilterService extends IService<SysIpFilter> {

    IPage<SysIpFilter> findPageList(JPAPage varBasePage, SysIpFilter sysIpFilter);

    List<SysIpFilter> findList(String id, String ip);

    boolean save(SysIpFilter model, Long userId,String userName);
}
