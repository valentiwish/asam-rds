package com.asam.system.mapper;

import com.asam.human.entity.UserRoleVo;
import com.asam.system.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface RoleDao  extends BaseMapper<SysRole> {


    @Select("SELECT a.* FROM sys_role a JOIN sys_account_role b ON a.id = b.sys_role_id JOIN sys_account c ON b.sys_account_id= c.id  WHERE c.state <> 0 AND c.login_name =#{loginName}")
    List<UserRoleVo> findRoleByLoginName(String loginName);

}
