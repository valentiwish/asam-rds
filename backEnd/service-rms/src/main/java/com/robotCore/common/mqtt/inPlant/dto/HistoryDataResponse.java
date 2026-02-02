package com.robotCore.common.mqtt.inPlant.dto;

import lombok.Data;
import java.util.List;

@Data
public class HistoryDataResponse {
    private String method;       // 请求方法
    private int code;            // 响应状态码
    private String msg;          // 响应消息
    private int seq;             // 请求序列号
    private Result result;       // 响应数据主体

    @lombok.Data
    public static class Result {
        private List<Data> data; // 数据集合
    }

    @lombok.Data
    public static class Data {
        private String name;     // 数据项名称 (如 MEM1)
        private List<DataItem> datalist; // 具体数据列表
    }

    @lombok.Data
    public static class DataItem {
        private int q;           // 数据质量标识
        private long time;       // 时间戳 (Unix 毫秒时间戳)
        private int type;        // 数据类型标识
        private Object val;      // 数据值 (根据实际情况可改为具体类型)
    }
}