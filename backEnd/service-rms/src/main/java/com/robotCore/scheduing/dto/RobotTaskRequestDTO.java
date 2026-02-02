package com.robotCore.scheduing.dto;

import lombok.Data;

import java.util.List;

/**
 * @Des: 机器人任务请求参数
 * @author: zxl
 * @date: 2024/4/10
 **/
@Data
public class RobotTaskRequestDTO {

    //mes运单id
    private String mesWaybillId;

    //wms运单id
    private String wmsWaybillId;

    //托盘编号
    private String palletCode;

    //任务序号
    private String taskNumber;

    //任务参数列表
    private List<SubParameter> taskParameter;

    @Data
    public static class SubParameter {
        private String name;
        private String value;
        private String type;
    }

}
