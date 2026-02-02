package com.robotCore.multiScheduling.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * @Des: 多品牌调度系统的机器人运单
 * @author: zxl
 * @date: 2025/6/10
 **/

@Table(name = "robot_multi_waybill") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotMultiWaybill extends BaseModelU {
    /**
     * MES运单ID
     */
    @Column(name = "mes_waybill_id")
    private String mesWaybillId;

    /**
     * WMS运单ID
     */
    @Column(name = "wms_waybill_id")
    private String wmsWaybillId;

    /**
     * Mission任务ID
     */
    @Column(name = "mission_work_id")
    private String missionWorkId;

    /**
     * Mission运单ID
     */
    @Column(name = "mission_waybill_id")
    private String missionWaybillId;

    /**
     * 托盘ID
     */
    @Column(name = "pallet_code")
    private String palletCode;

    /**
     * 托盘类型
     */
    @Column(name = "pallet_type")
    private String palletType;

    /**
     * 任务ID或者任务序号
     */
    @Column(name = "task_number")
    private String taskNumber;

    /**
     * 任务名称
     */
    @Column(name = "task_name")
    private String taskName;

    /**
     * 任务优先级
     */
    @Column(name = "task_priority")
    private Integer taskPriority;

    /**
     * 机器人
     */
    @Column(name = "robot_name")
    private String robotName;

    /**
     * 0-CANCEL: 取消
     * 1-CREATE：创建
     * 2-ASSIGNED：已分配
     * 3-WAIT：等待执行
     * 4-RUNNING：执行中
     * 5-SUCCESS：执行成功
     * 6-FAULT：执行错误
     * 7-PAUSE：暂停
     * 8-SHUTDOWN：已停止
     * 9-BEING_PAUSE：暂停中
     * 10-BEING_RESUME：恢复中
     * 11-BEING_SHUTDOWN：停止中
     */
    @Column(name = "waybill_status")
    private Integer waybillStatus;

    /**
     * 机器人分组
     */
    @Column(name = "robot_group_name")
    private String robotGroupName;

    /**
     * 机器人类型 1--叉车，2--金陵,3--西安航天
     */
    @Column(name = "agv_type")
    private Integer agvType;

    /**
     *开始站点
     */
    @Column(name = "start_point")
    private String startPoint;

    /**
     *结束站点
     */
    @Column(name = "end_point")
    private String endPoint;

    /**
     * 接单时间戳
     */
    @Column(type = MySqlTypeConstant.DATETIME)
    private Timestamp orderTime;

    /**
     * 完成时间戳
     */
    @Column(type = MySqlTypeConstant.DATETIME)
    private Timestamp completeTime;

    /**
     * 执行耗时
     */
    @Column(name = "execution_time")
    private Long executionTime;

    /**
     * 错误编码
     */
    @Column(name = "error_code")
    private String errorCode;

    /**
     * 错误信息
     */
    @Column(name = "error_message")
    private String errorMessage;

    /**
     * 错误时间
     */
    @Column(type = MySqlTypeConstant.DATETIME)
    private Timestamp errorTime;

}
