package com.robotCore.scheduing.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.robotCore.common.base.model.BaseModelU;
import com.robotCore.scheduing.dto.RobotRunDTO;
import com.robotCore.scheduing.vo.RobotMonitorVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * @Des: 任务分步步骤操作记录
 * @author: zxl
 * @date: 2023/6/15
 **/
@Table(name = "robot_task_step") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotTaskStep extends BaseModelU {

    /**
     * 任务ID
     */
    @Column(name = "task_id")
    private String taskId;

    /**
     * 运行任务的机器人名字
     */
    @Column(name = "robot_name")
    private String robotName;

    /**
     * 运行任务的运单ID
     */
    @Column(name = "waybill_id")
    private String waybillId;

    /**
     * 任务
     */
    @Column(name = "task_step", type = MySqlTypeConstant.TEXT)
    private String taskStep;

    /**
     * 操作任务的时间戳
     */
    @Column(name = "operate_time")
    private Long operateTime;
}
