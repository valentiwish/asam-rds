package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2024/7/2
 **/
@Data
public class RobotTaskListStatusDTO {

    private String taskListName;  //任务链名称

    private Integer taskListStatus; //任务链状态
}
