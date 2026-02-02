package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des: 查询机器人任务链返回信息
 * @author: zxl
 * @date: 2023/8/18
 **/
@Data
public class RobotTaskListResDTO {

    private Integer retCode; //API 错误码

    private String createOn; //API 上传时间戳

    private RobotStatus robotStatus; //机器人状态，目前只包括电量信息

    private TaskListStatus tasklistStatus; //任务链状态


    @Data
    public static class RobotStatus {

        private float batteryLevel; //机器人电量

    }

    @Data
    public static class TaskListStatus {

        private String taskListName;  //任务链名称

        private Integer taskListStatus; //任务链状态

        private boolean loop; //是否循环执行

        private Integer actionGroupId; //并行工作组动作下标

    }

}
