package com.asam.system.service;

import com.asam.system.entity.SysAccount;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.Collection;
import java.util.List;


public interface AccountService extends IService<SysAccount> {

    IPage<SysAccount> findPageList(JPAPage varBasePage, String userName,String loginName,String loginName1);

    SysAccount findModel(Long userId);

    boolean save(SysAccount model, Long userId,String userName);

    List<SysAccount> findList(Collection<Long> ids,String loginName,String loginName1);
}
