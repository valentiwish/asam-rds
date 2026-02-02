package com.robotCore.scheduing.dto;

import lombok.Data;

import java.util.List;

/**
 * @Des: 机器人任务链DTO
 * @author: zxl
 * @date: 2023/8/7
 **/
@Data
public class RobotTaskListDTO {

    private String name;

    private List<TaskDTO> tasks;

    private boolean loop;

}
