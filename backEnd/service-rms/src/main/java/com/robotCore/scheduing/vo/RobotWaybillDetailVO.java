package com.robotCore.scheduing.vo;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/8/16
 **/
@Data
public class RobotWaybillDetailVO {

    private Integer blockId;

    private String storageLoc;

    private String operation;

    private String waybillStatus;
}
