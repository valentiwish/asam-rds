package com.robotCore.scheduing.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 机器人运单详情表
 * @author: zxl
 * @date: 2023/8/15
 **/
@Table(name = "robot_waybill_detail") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotWaybillDetail extends BaseModelU {

    /**
     * 运单ID
     */
    @Column(name = "waybill_id")
    private String waybillId;

    /**
     * 块ID
     */
    @Column(name = "block_id")
    private Integer blockId;

    /**
     * 库位
     */
    @Column(name = "storage_loc")
    private String storageLoc;

    /**
     * 操作
     */
    @Column(name = "operation")
    private String operation;

    /**
     * 运单状态 0--等待，1--正在执行，2--完成，3--终止
     */
    @Column(name = "waybill_status")
    private String waybillStatus;
}
