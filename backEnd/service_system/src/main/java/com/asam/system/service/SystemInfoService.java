package com.asam.system.service;

import com.asam.system.entity.SysSubsystem;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.Collection;
import java.util.List;


public interface SystemInfoService extends IService<SysSubsystem> {

    IPage<SysSubsystem> findPageList(JPAPage varBasePage, String name, String appid);

    List<SysSubsystem> findList(String appid, Integer enable, Collection<String> ids);

    boolean save(SysSubsystem model, Long userId,String userName);

    SysSubsystem checkAppid(Long id, String appid);
}