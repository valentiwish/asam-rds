package com.asam.system.service;

import com.asam.system.entity.SysAccountRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface AccountRoleService extends IService<SysAccountRole> {

    List<SysAccountRole> findList(Long accountId, Long roleId);

    List<Long> getRoleIdsByAccountId(Long accountId);

    List<Long> getAccountsByRoleId(Long roleId);

    boolean save(SysAccountRole model);

    void insertAll(List<SysAccountRole> models);

    void deleteByAccountId(Long accountId);

    void deleteByRoleId(Long roleId);
}
