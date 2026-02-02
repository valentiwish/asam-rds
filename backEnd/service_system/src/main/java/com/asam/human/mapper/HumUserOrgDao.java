package com.asam.human.mapper;

import com.asam.human.entity.HumUserOrg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface HumUserOrgDao extends BaseMapper<HumUserOrg> {

    @Delete("delete from hum_user_org where sys_user_id = #{sysUserId}")
    void deleteByUserId(@Param("sysUserId") Long sysUserId);

    void insertAll(@Param("list") List<HumUserOrg> list);//批量保存
}
