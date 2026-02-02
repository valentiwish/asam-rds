package com.robotCore.common.mqtt.inPlant.dto;

import lombok.Data;

import java.util.List;

// 消息体根对象
@Data
public class ReceiveMessage {
    private List<RTValue> RTValue;
}
