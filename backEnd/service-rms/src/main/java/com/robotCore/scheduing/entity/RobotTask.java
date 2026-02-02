package com.robotCore.scheduing.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 机器人任务
 * @author: zxl
 * @date: 2023/6/2
 **/
@Table(name = "robot_task") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotTask extends BaseModelU {

    /**
     * 任务序号
     */
    @Column(name = "task_Number")
    private String taskNumber;

    /**
     * 任务优先级
     */
    @Column(name = "priority")
    private Integer priority;

    /**
     * 任务名字
     */
    @Column(name = "task_name")
    private String taskName;

    /**
     * 版本，当编辑一次以后保存，则版本数+1
     */
    @Column(name = "version")
    private Integer version;

    /**
     * 版本描述
     */
    @Column(name = "version_description")
    private String versionDescription;

    /**
     * 定时任务
     */
    @Column(name = "schedule_task")
    private boolean scheduleTask;

    /**
     * 循环执行
     */
    @Column(name = "enabled_state")
    private Integer enabledState = 0;

    /**
     * 任务内容
     */
    @Column(name = "task_content", type = MySqlTypeConstant.TEXT)
    private String taskContent;

    /**
     * 任务参数
     */
    @Column(name = "task_parameter", type = MySqlTypeConstant.TEXT)
    private String taskParameter;

    /**
     * 当前运行任务参数
     */
    @Column(name = "current_task_parameter", type = MySqlTypeConstant.TEXT)
    private String currentTaskParameter;
}
