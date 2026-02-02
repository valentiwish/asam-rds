package com.robotCore.common.mqtt.inPlant.dto;

import lombok.Data;
import java.util.List;

@Data
public class HistoryDataRequest {
    private String method = "HistoryData";         // 请求方法
    private String topic;          // 主题
    private List<String> names;   // 数据项名称列表
    private int seq;               // 请求序列号
    private int mode;              // 数据模式
    private long begintime;        // 开始时间（Unix 毫秒时间戳）
    private long endtime;          // 结束时间（Unix 毫秒时间戳）
    private int count;             // 数据点数量
    private int interval;          // 数据间隔（毫秒）
    private int timeout;          // 超时时间（毫秒）
}