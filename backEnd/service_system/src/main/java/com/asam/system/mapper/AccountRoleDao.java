package com.asam.system.mapper;

import com.asam.system.entity.SysAccountRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface AccountRoleDao extends BaseMapper<SysAccountRole> {

    @Delete("delete a from sys_account_role a where a.sys_account_id=#{accountId}")
    void deleteByAId(@Param("accountId") Long accountId);

    @Delete("delete a from sys_account_role a where a.sys_role_id=#{sysRoleId}")
    void deleteByRoleId(@Param("sysRoleId") Long sysRoleId);

    void insertAll(@Param("list") List<SysAccountRole> list);//批量保存
}
