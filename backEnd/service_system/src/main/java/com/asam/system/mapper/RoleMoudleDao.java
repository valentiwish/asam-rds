package com.asam.system.mapper;

import com.asam.system.entity.SysRoleMoudle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface RoleMoudleDao extends BaseMapper<SysRoleMoudle> {

    @Delete("delete from sys_role_moudle where sys_role_id = #{roleId} and system_id = #{systemId}")
    void deleteByRoleIdAndSystemId(@Param("roleId") Long roleId,@Param("systemId")  Long systemId);

    @Delete("delete from sys_role_moudle where sys_role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    void insertAll(@Param("list") List<SysRoleMoudle> list);//批量保存
}
