package com.asam.system.service;

import com.asam.system.entity.SysRoleMoudle;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;


public interface RoleMoudleService extends IService<SysRoleMoudle> {

    List<SysRoleMoudle> findList(Long roleId, Long systemId,Collection<Long> roleIds) ;

    boolean save(SysRoleMoudle model);

    void insertAll(List<SysRoleMoudle> models);

    void deleteByRoleId(Long roleId,Long systemId);
}
