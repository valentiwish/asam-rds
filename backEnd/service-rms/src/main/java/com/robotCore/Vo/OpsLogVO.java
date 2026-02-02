package com.robotCore.Vo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Description: 描述
 * @Author: Created by lufan on 2022/4/13.
 */
@Data
public class OpsLogVO {

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

    private String category;

    public OpsLogVO(String ip, Timestamp opTime, String opUserName, String opDescription, Integer opResult, String category) {
        this.ip = ip;
        this.opTime = opTime;
        this.opUserName = opUserName;
        this.opDescription = opDescription;
        this.opResult = opResult;
        this.category = category;
    }
}