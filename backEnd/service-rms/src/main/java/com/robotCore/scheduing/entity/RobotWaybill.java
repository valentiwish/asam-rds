package com.robotCore.scheduing.entity;

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
 * @Des: 机器人运单
 * @author: zxl
 * @date: 2023/8/14
 **/
@Table(name = "robot_waybill") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotWaybill extends BaseModelU {

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
     * 托盘ID
     */
    @Column(name = "pallet_code")
    private String palletCode;

    /**
     * 任务ID
     */
    @Column(name = "task_id")
    private String taskId;

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
     * 运单状态 0--路径规划中，1--正在执行，2--完成，3--终止，4--取消，5--等待放行，6--取货完成，7--放货完成
     */
    @Column(name = "waybill_status")
    private Integer waybillStatus;

    /**
     * 机器人分组
     */
    @Column(name = "robot_group_name")
    private String robotGroupName;

    /**
     * 通信方式
     */
    @Column(name = "communication_type")
    private String communicationType;

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
     * 错误信息
     */
    @Column(name = "error_message")
    private String errorMessage;

    /**
     * 错误时间
     */
    @Column(type = MySqlTypeConstant.DATETIME)
    private Timestamp errorTime;

    /**
     * 待执行任务列表
     */
    @Column(name = "pending_task", type = MySqlTypeConstant.TEXT)
    private String pendingTask;

    /**
     * 当前任务任务参数
     */
    @Column(name = "task_parameter", type = MySqlTypeConstant.TEXT)
    private String taskParameter;

    /**
     * 任务列表
     */
    @Column(name = "task_list", type = MySqlTypeConstant.TEXT)
    private String taskList;

    /**
     * 子任务列表
     */
    @Column(name = "sub_task_list", type = MySqlTypeConstant.TEXT)
    private String subTaskList;

    /**
     * 路径规划算法结果
     */
    @Column(name = "path_planning_result", type = MySqlTypeConstant.TEXT)
    private String pathPlanningResult;

}
