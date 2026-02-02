package com.robotCore.common.mqtt.inPlant.dto;

import lombok.Data;


// RTValue 对象定义（单条标签数据）
@Data
public class RTValue {
    private String name;     // 标签名称（如 tag1）
    private int quality;     // 数据质量标识（1 表示有效）
    private long timestamp;  // 时间戳（Unix 毫秒）
    private int type;        // 数据类型标识（如 16 表示浮点数）
    private int value;    // 数据值（字符串格式）
}