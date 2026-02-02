package com.robotCore.multiScheduling.vo;

import lombok.Data;

/***
 * @Description: 升降平台状态信息
 * @Author: zhangqi
 * @Create: 2024/6/23
 */
@Data
public class LiftInfoVO {

    /**
     * 升降平台远程状态，1-远程，非1，0-本地
     */
    private Integer remotStatus = 1;

    /**
     * 被请求开始楼层
     */
    private String startFloor;

    /**
     * 被请求到达楼层
     */
    private String endFloor;

    /**
     * 升降平台故障状态位：
     * 10-急停被按下，11-超高保护，12-热继保护1，13-热继保护2，
     * 14-电动推杆异常，15-压力信号异常，16-升降中光幕异常，17-升降中门被打开
     */
    private Integer liftFault = 0;

    /**
     * 升降平台一层门状态，1已开2已关3正开4正关5故障
     */
    private Integer floorOneStatus = 0;

    /**
     * 升降平台二层门状态，1已开2已关3正开4正关5故障
     */
    private Integer floorTwoStatus = 0;

    /**
     * 升降平台总状态，0不在楼层1正升2正降3在二层4在一层
     */
    private Integer liftStatus = 0;

    /**
     * 升降平台是否占用，0-空闲，1-占用
     */
    private Integer useStatus = 0;

    /**
     * 升降平台允许AGV进出，1一层允许，2二层允许，0-无
     */
    private Integer allowEntry = 0;

    /**
     * 给调度系统发送允许进入，0-未发送，1-已发送
     */
    private Integer sendAllowEntry = 0;

    /**
     * 给调度系统发送允许出，0-未发送，1-已发送
     */
    private Integer sendAllowOut = 0;
}
