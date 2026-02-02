package com.robotCore.robot.dto;

import lombok.Data;

/**
 * @Des: 机器人基本信息返回的实体
 * @author: zxl
 * @date: 2023/5/18
 **/
@Data
public class RobotBasicInfoResDTO {
    /**
     * 机器人ID
     */
    private String id;

    /**
     * 机器人名称
     * transient 表明此处是机器人推送过来的，无需存储在数据库
     */
    private String vehicleId;

    /**
     * 机器人备注
     */
    private  String robotNote;

    /**
     * Robokit版本号
     */
    private String version;

    /**
     * 固件版本号
     */
    private String dspVersion;

    /**
     * 地图版本号
     */
    private String mapVersion;

    /**
     * 当前IP
     */
    private String currentIp;

    /**
     * 当前地图名
     */
    private String currentMap;

    /**
     * 当前地图 MD5 值
     */
    private String currentMapMd5;

    /**
     * 机器人模型
     */
    private String model;

    /**
     * API 错误码
     */
    private Integer retCode;

    /**
     * API 上传时间戳
     */
    private String createOn;

    /**
     * 错误信息
     */
    private String errMsg;
}
