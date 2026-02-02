package com.robotCore.multiScheduling.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2025/6/5
 **/
@Data
public class RobotTaskRequestParam {

    //mes运单id
    private String mesWaybillId;

    //wms运单id
    private String wmsWaybillId;

    //托盘编号
    private String palletCode;

    //托盘类型
    private String palletType;

    //任务序号
    private String taskNumber;

    //任务优先级 1>2>3
    private String priority;

    //指定执行任务的agv编号
    private String agvCode;

    //机器人类型 1--叉车，2--金陵,3--西安航天
    private Integer agvType;

    //开始站点
    private String startPoint;

    //结束站点
    private String endPoint;

}
