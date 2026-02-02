package com.robotCore.common.mqtt.inPlant.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RealAlarmData {
    private String method;         // 方法名称 ("RealAlarm")
    private List<Alarm> alarms;   // 报警信息列表

    @Data
    public static class Alarm {
        @NotNull
        private String name;       // 报警点名称 (如 "MEM1")
        @Min(0)
        private int state;         // 报警状态 (0=未确认, 1=已确认)
        private double trigger;    // 触发值 (如 51)
        private double limit;      // 报警限值 (如 50)
        private String operate;    // 操作人 (如 "admin")
        private String results;    // 处理结果 (如 "确认报警")
        private long newtime;      // 报警产生时间 (Unix 时间戳)
        private long acktime;     // 确认时间 (0=未确认)
        private long canceltime;  // 消警时间 (0=未消警)
        private int level;        // 报警级别 (1=一般, 2=严重)
        private String type;      // 报警类型 (如 "L"=低限, "H"=高限)
        private String almdesc;   // 报警描述 (如 "低限报警")
        private String desc;     // 位号描述
    }
}