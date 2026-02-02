package com.robotCore.task.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.robotCore.common.base.model.BaseModelU;

import java.sql.Timestamp;

/**
 * @Author lkd
 * @create 2023/5/22 8:33
 */
public class RobotTaskTemplate extends BaseModelU { //调度任务实体类



    @Column
    private String taskName;      // 任务名称：任务的名称，便于用户识别。
    @Column
    private Integer taskState;     // 任务的状态，包括待执行、执行中、已完成等。
    @Column
    private Integer taskType;     //任务类型：任务的类型，包括单次任务、循环任务、定时任务等。

    @Column
    private Integer taskPriority;  //任务优先级：任务的优先级，用于确定任务的执行顺序。

    @Column
    private String cycle;     // 任务执行周期 ，此处可以选cron表达式，任务调度框架可选quarz

    @Column(type = MySqlTypeConstant.DATETIME)
    private Timestamp taskStart;     // 任务开始时间


    @Column(type = MySqlTypeConstant.DATETIME)
    private Timestamp taskEnd;     // 任务结束时间

    @Column
    private String taskDesp;      //任务描述：对任务的描述，包括任务的目的、执行步骤等。

    @Column
    private String parentId;      //关联父级任务

    @Column
    private Integer result;     //任务执行结果：任务执行的结果，包括成功、失败、异常等。

    @Column
    private String logId;     //任务执行日志：记录任务执行的详细日志，便于排查问题。

    @Column
    private String robotIds;     //任务关联机器人：任务关联的机器人，用于确定任务的执行对象。

    @Column
    private String eqpIds;     // 任务关联设备：任务关联的设备，用于确定任务的执行位置。


    private int timeLimit; // 任务时限
    private int startLocation; // 任务起始位置
    private int targetLocation; // 任务目标位置

    private String scheduleAlgorithm; // 任务调度算法code,适配不同的算法来进行分配任务，如果不需要算法，此处可选为NO
    /*
        任务创建时间：任务的创建时间，用于记录任务的创建时间。   createTime
        任务更新时间：任务的更新时间，用于记录任务的最后更新时间。  updateTime
    */

}
