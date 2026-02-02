package com.asam.system.mapper;

import com.asam.system.entity.SysMoudle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;


public interface MoudleDao extends BaseMapper<SysMoudle> {

    void updateByIds(@Param("state") Integer state,@Param("enable")  Integer enable,@Param("list")  Collection<Long> list);

    @Select("delete r from sys_moudle r where r.system_id = #{systemId}")
    void deleteBySystemId(@Param("systemId") Long systemId);
}
