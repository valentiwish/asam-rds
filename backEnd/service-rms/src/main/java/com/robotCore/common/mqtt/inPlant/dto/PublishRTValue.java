package com.robotCore.common.mqtt.inPlant.dto;

import lombok.Data;

/**
 * @author Lu Fan
 * @description
 * @date 2025/3/19 20:55
 */
@Data
public class PublishRTValue {

    private String name;     // 标签名称（如 tag1）
    private int type = 16;        // 数据类型标识（如 16 表示浮点数）
    private String value;    // 数据值（字符串格式）
}
