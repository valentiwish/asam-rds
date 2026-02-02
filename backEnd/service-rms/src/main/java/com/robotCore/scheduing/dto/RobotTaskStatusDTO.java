package com.robotCore.scheduing.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @Des: 机器人任务状态（指定路径导航）
 * @author: leo
 * @date: 2025/3/21
 */
@Data
public class RobotTaskStatusDTO {

    @JSONField(name = "create_on")
    private String createOn;

    @JSONField(name = "ret_code")
    private Integer retCode;

    @JSONField(name = "task_status_package")
    private TaskStatusPackage taskStatusPackage;

    // 静态内部类：映射 task_status_package 对象
    @Data
    public static class TaskStatusPackage {
        @JSONField(name = "closest_target")
        private String closestTarget;

        private Double distance;

        private String info;

        private Double percentage;

        @JSONField(name = "source_name")
        private String sourceName;

        @JSONField(name = "target_name")
        private String targetName;

        // 嵌套数组 task_status_list
        @JSONField(name = "task_status_list")
        private List<TaskStatus> taskStatusList;
    }

    // 静态内部类：映射 task_status_list 数组中的对象
    @Data
    public static class TaskStatus {

        // 任务状态：Waiting=1, Running=2, Suspended=3, Completed=4, Failed=5, Canceled=6
        private Integer status;

        @JSONField(name = "task_id")
        private String taskId;

        private Integer type;
    }

}
