package com.robotCore.scheduing.vo;

import lombok.Data;

/**
 * @Des: 机器人运单执行结果
 * @author: zxl
 * @date: 2023/8/14
 **/
@Data
public class RobotWaybillResultVO {

    /**
     * 运单id
     */
    private String waybillId;

    /**
     * mes运单id
     */
    private String mesWaybillId;

    /**
     * wms运单id
     */
    private String wmsWaybillId;

    /**
     * 托盘编号
     */
    private String palletCode;

    /**
     * 运单状态 0--等待，1--正在执行，2--完成，3--终止, 4--取消, 5--等待do,6--取货完成
     */
    private Integer waybillStatus;

    /**
     * 接单时间戳
     */
    private String orderTime;

    /**
     * 完成时间戳
     */
    private String completeTime;

    /**
     * 执行耗时
     */
    private String executionTime;

}
