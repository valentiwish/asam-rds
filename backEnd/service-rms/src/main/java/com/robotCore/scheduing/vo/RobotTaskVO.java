package com.robotCore.scheduing.vo;

import com.robotCore.scheduing.dto.TaskParameterDTO;
import lombok.Data;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/11/30
 **/
@Data
public class RobotTaskVO {

    //任务id
    private String taskId;

    // 任务名字
    private String taskName;

//    //任务内容
//    private RobotTaskDTO taskContent;

    //任务参数列表
    private List<TaskParameterDTO> taskParameter;
}
