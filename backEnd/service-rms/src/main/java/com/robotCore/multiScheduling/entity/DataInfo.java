package com.robotCore.multiScheduling.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

/*
* 自动采集存储
 */
@Accessors(chain = true) // 链表的访问
@Data
@Measurement(name = "dataInfo")
public class DataInfo {
    // Column中的name为measurement中的列名
    // 注解中添加tag = true,表示当前字段内容为tag内容
    @Column(name = "hostname", tag = true)
    private String hostname;

    @Column(name = "value")
    private Double value;

    // 有无信号，0-无，1-有
    @Column(name = "singal")
    private float singal;

    // 变量类型(1:信号量(开关量)；2：模拟量 3：报警量)
    @Column(name = "type")
    private float type;

    @Column(name = "time")
    private long time;

    @Column(name = "ctime")
    private String ctime;
}
