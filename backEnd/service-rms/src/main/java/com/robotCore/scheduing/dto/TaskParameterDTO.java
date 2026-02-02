package com.robotCore.scheduing.dto;

import lombok.Data;


/**
 * @Des:
 * @author: zxl
 * @date: 2023/12/19
 **/
@Data
public class TaskParameterDTO {

    //数据类型
    private String dataType;

    //默认值
    private String defaultValue;

    //是否必填
    private String isRequired;

    //标签
    private String label;

    //变量名
    private String variableName;
}
