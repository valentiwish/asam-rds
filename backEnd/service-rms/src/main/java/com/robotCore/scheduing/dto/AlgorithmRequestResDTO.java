package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des: 算法请求返回
 * @author: zxl
 * @date: 2023/9/8
 **/
@Data
public class AlgorithmRequestResDTO {

    //任务状态
    private String status;

    //唯一任务ID
    private String taskId;

    //请求状态信息
    private String info;
}
