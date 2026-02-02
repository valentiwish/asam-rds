package com.robotCore.common.base.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 描述
 * @Author: Created by lufan on 2021/7/21.
 */
@Data
public class AccountVo {

    private Long id;

    /**
     * 登录账号;  用户状态,0:删除用户;1:正常状态;2：禁用.
     */
    private String loginName;

    /**
     * 密码
     */
    private String password;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 最后登录ip地址
     */
    private String ip;

    /**
     * 对应用户
     */
    private String userName;

}