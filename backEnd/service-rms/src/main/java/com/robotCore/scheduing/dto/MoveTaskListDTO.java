package com.robotCore.scheduing.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Des: 指定路径导航指令
 * @author: leo
 * @date: 2025/3/21
 */
@Data
@Builder
public class MoveTaskListDTO {
    @JSONField(name = "move_task_list")
    private List<MoveTaskItem> moveTaskList;

    @Data
    @Builder
    public static class MoveTaskItem {
        // 目标站点
        @JSONField(name = "id")
        private String id;

        // 当前站点
        @JSONField(name = "source_id")
        private String sourceId;

        // 任务ID
        @JSONField(name = "task_id")
        private String taskId;

        @JSONField(name = "operation")
        private String operation;

        @JSONField(name = "jack_height")
        private Double jackHeight;

    }
}
