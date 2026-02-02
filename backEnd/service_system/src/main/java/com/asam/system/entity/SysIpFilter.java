package com.asam.system.entity;

import com.asam.common.base.model.BaseModelU;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 针对三员管理的三个账户，设置IP白名单，只有白名单内的才能登录系统
 * @Author: fyy
 * @Create: 2022-07-26
 */
@Data
@Table(name = "sys_ip_filter")
public class SysIpFilter extends BaseModelU {

    @Column
    private String ip;

    private transient Date startTime;

    private transient Date endTime;
}