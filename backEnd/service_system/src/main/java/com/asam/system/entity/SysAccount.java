package com.asam.system.entity;

import com.asam.common.base.model.BaseModelL;
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @Description: 用户信息
 * @Author: fyy
 * @Create: 2022-04-19
 */
@SuppressWarnings("serial")
@Data
@Table(name = "sys_account")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysAccount extends BaseModelL {

    /**
     * 登录账号;  用户状态,0:删除用户;1:正常状态;2：禁用.
     */
    @Column(name = "user_id")
    public Long userId;

    /**
     * 登录账号 默认是工号
     */
    @Unique // 全局唯一性设置
    @Column(name = "login_name", length = 20)
    private String loginName;

    /**
     * 对应用户
     */
    @Column(name = "user_name", length = 50)
    public String userName;

    /**
     * 密码
     */
    @Column(name = "password", length = 100)
    public String password;

    /**
     *微信公众号绑定的openId
     */
    @Column(name = "open_id", length = 100)
    private String openId;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    public Date lastLoginTime;

    /**
     * 最后登录ip地址
     */
    @Column(name = "ip", length = 50)
    public String ip;

    /**
     * 更新密码时间
     */
    @Column(name = "update_password_time")
    private Timestamp updatePasswordTime;

}