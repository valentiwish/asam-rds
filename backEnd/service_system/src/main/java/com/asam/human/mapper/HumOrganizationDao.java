package com.asam.human.mapper;

import com.asam.human.entity.HumOrganization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;


public interface HumOrganizationDao extends BaseMapper<HumOrganization> {

    @Select("update hum_organization r set r.state = #{state} where r.id in #{orgIds}")
    void updateStateByIds(@Param("state") Integer state,@Param("orgIds") Collection<String> orgIds);

    @Select("update hum_organization r set r.parent_code = #{parentCode},r.parent_name = #{parentName} where r.parent_id = #{parentId}")
    void updateByParentId(@Param("parentCode") String parentCode,@Param("parentName")  String parentName,@Param("parentId")  String parentId);
}
