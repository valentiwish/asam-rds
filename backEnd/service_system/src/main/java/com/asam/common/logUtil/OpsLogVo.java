package com.asam.common.logUtil;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Description: 描述
 * @Author: Created by lufan on 2022/3/24.
 */
@Data
public class OpsLogVo {

    /**
     * IP地址
     */
    private String ip;

    /**
     * 操作时间
     */
    private Timestamp opTime;

    /**
     * 操作人
     */
    private String opUserName;

    /**
     * 操作描述
     */
    private String opDescription;

    /**
     * 操作结果(0:失败，1：成功)
     */
    private Integer opResult;

    private Integer category;

    /**
     * 访问日志
     */
    public static final int CATEGORY_USER = 1;

    /**
     * 应用日志
     */
    public static final int CATEGORY_APPLICATION = 2;

    /**
     * 系统日志
     */
    public static final int CATEGORY_SYSTEM = 3;
}