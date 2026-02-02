package com.asam.human.mapper;

import com.asam.human.entity.HumUser;
import com.asam.human.entity.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface HumUserDao extends BaseMapper<HumUser> {

    @Select("select * from hum_user r where r.state = 1 and (r.login_name = #{loginName} or r.job_number = #{loginName} " +
            " or r.user_phone = #{loginName} or r.card_id=#{loginName} or r.email=#{loginName} or r.unique_code=#{loginName})")
    UserVO findByLoginName(@Param("loginName") String loginName);

    @Select("update hum_user r set r.org_code = #{orgCode},r.org_name = #{orgName} where r.org_id = #{orgId}")
    void updateByOrgId(@Param("orgCode") String orgCode,@Param("orgName")  String orgName,@Param("orgId")  String orgId);
}
