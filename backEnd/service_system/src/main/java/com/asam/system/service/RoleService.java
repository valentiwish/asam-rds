package com.asam.system.service;

import com.asam.human.entity.UserRoleVo;
import com.asam.system.entity.SysRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.page.JPAPage;

import java.util.Collection;
import java.util.List;


public interface RoleService extends IService<SysRole> {

    IPage<SysRole> findPageList(JPAPage varBasePage, String name,String loginName);

    List<SysRole> findList(Long id, String name,String roleCode,Collection<Long> ids,String loginName);

    boolean save(SysRole model, Long userId, String userName);

    List<UserRoleVo> findRoleByLoginName(String loginName);
}
