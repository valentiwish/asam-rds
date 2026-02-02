package com.robotCore.common.mqtt.inPlant.dto;

import lombok.Data;

/**
 * @author Lu Fan
 * @description 订阅报警消息
 * @date 2025/3/21 10:03
 */
@Data
public class SubAlarmMessage {

    private String method = "RealAlarm";

    private Integer state;

    private String topic;
}
