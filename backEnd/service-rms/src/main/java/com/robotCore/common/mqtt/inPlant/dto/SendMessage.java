package com.robotCore.common.mqtt.inPlant.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author Lu Fan
 * @description
 * @date 2025/3/20 11:19
 */
@Data
public class SendMessage {
    @JSONField(name = "RTValue")
    List<PublishRTValue> RTValue;
}
