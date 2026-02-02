package com.robotCore.scheduing.vo;


import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/4/10
 **/
@Data
public class RobotTaskLogVO {

    /**
     * id
     */
    private String id;

    /**
     * 第三方应用名称
     */
    private String mesWaybillId;

    /**
     * 第三方应用名称
     */
    private String applicationName;

    /**
     * 第三方应用请求的IP
     */
    private String requestIp;

    /**
     * 操作业务
     */
    private String operateBusiness;

    /**
     * 操作内容
     */
    private String operateContent;

    /**
     * 操作时间
     */
    private String operateTime;

    /**
     * 操作结果
     */
    private String operateSuccess;

    /**
     * 操作日志信息
     */
    private String operateMessage;
}
