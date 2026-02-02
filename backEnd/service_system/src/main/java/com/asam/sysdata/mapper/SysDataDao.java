package com.asam.sysdata.mapper;

import com.asam.sysdata.entity.SysData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface SysDataDao extends BaseMapper<SysData> {

    @Select("update sys_data r set r.data_type_code = #{dataTypeCode},r.data_type_name = #{dataTypeName} where r.data_type_id = #{dataTypeId}")
    void updateByDataTypeId(@Param("dataTypeCode") String dataTypeCode, @Param("dataTypeName") String dataTypeName,@Param("dataTypeId")  Long dataTypeId);

    @Select("delete r from sys_data r where r.data_type_id = #{dataTypeId}")
    void deleteByDataTypeId(@Param("dataTypeId") Long dataTypeId);
}
