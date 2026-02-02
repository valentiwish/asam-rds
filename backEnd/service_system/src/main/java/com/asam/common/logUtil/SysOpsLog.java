package com.asam.common.logUtil;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsAutoIncrement;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 描述
 * @Author: Created by lufan on 2020/12/28.
 */
@Data
public class SysOpsLog {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO) //mybatis-plus主键注解
    @IsKey                         //actable主键注解
    @IsAutoIncrement             //自增
    @Column
    private Long id;

    /**
     * 登录名称
     */
    @Column(name = "login_name")
    private String loginName;

    /**
     * 登录名称
     */
    @Column(name = "url")
    private String url;

    /**
     * 登录ip地址
     */
    @Column(name = "ip")
    private String ip;

    /**
     * 类型：0，登录日志，ACC；1，应用日志，APP；2，系统日志，SYS；
     */
    @Column(name="category")
    private String category = CATEGORY_APPLICATION;

    /**
     * 创建时间，即进行平台访问、应用操作、或者系统日志发生的事件
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 日志内容
     */
    @Column(name = "message")
    private String message;

    /**
     * 访问日志
     */
    public static final String CATEGORY_ACCESS = "ACC";

    /**
     * 应用日志
     */
    public static final String CATEGORY_APPLICATION = "APP";

    /**
     * 系统日志
     */
    public static final String CATEGORY_SYSTEM = "SYS";
}