package com.robotCore.multiScheduling.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Des:
 * @author: zxl
 * @date: 2025/9/11
 **/
@Data
public class MissionWorksResDTO {

    //创建作业时调度系统自动生成的唯一id，可通过此值执行查询作业、暂停作业等操作
    private String id;

    //上游指定的机器人编号
    private String agvCode;

    //任务ID
    private String missionId;

    //调度计划id
    private String schedulePlanId;

    //回调url
    private String callbackUrl;

    //作业名称
    private String name;

    //优先级
    //配置在作业模板中的优先级（默认为2）；
    //1-低；
    //2-普通
    //3-高
    //最高
    private int sequence;

    //作业描述
    private String description;

    //作业状态
    //总共有如下作业状态
    //-CREATE：创建
    //-ASSIGNED：已分配
    //-WAIT：等待执行
    //-RUNNING：执行中
    //-SUCCESS：执行成功
    //-FAULT：执行错误
    //-PAUSE：暂停
    //-SHUTDOWN：已停止
    //-BEING_PAUSE：暂停中
    //-BEING_RESUME：恢复中
    //BEING_SHUTDOWN：停止中
    private String status;

    //消息
    private String message;

    //运行参数
    private String runtimeParam;

    //开始时间
    private Timestamp startTime;

    //结束时间
    private Timestamp endTime;

    //机器人组id
    private String agvGroupId;

    //机器人类型
    private String agvType;

    //创建时间
    private Timestamp createTime;

    //更新时间
    private Timestamp updateTime;

    //当前动作序号
    private int currentActionSequence;

    //异常错误代码
    private int errorCode;

    //机器人名称
    private String agvName;

    //任务组ID
    private String missionGroupId;

    //任务呼叫id
    private String missionCallId;

}
