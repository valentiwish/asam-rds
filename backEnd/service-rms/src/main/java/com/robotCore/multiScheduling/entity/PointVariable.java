package com.robotCore.multiScheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.influxdb.annotation.Measurement;

import java.sql.Timestamp;

/**
 * @Description: 描述
 * @Author: Created by lufan on 2021/11/29.
 */
@Table(name = "point_variable")
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
@Measurement(name = "point_variable")
public class PointVariable extends BaseModelU {

    /**
     * 变量名称
     */
    @Column(name = "variable_name")
    @org.influxdb.annotation.Column(name = "variable_name", tag = true)
    private String variableName;

    /**
     * 变量描述
     */
    @Column(name = "variable_desp")
    @org.influxdb.annotation.Column(name = "variable_desp", tag = true)
    private String variableDesp;

    /**
     * 变量类型(1:信号量(开关量)；2：模拟量 3：报警量)
     */
    @Column(name = "data_type")
    @org.influxdb.annotation.Column(name = "data_type", tag = false)
    private Integer dataType;

    @Column(name = "value")
    @org.influxdb.annotation.Column(name = "value", tag = false)
    private String value  ;

    /**
     * 有无信号，0-无，1-有
     */
    @TableField(exist = false)
    @org.influxdb.annotation.Column(name = "singal", tag = false)
    private Integer singal = 0;

    /**
     *是否推送
     */
    @Column(name = "is_push")
    @org.influxdb.annotation.Column(name = "is_push", tag = false)
    private Integer isPush;


    @TableField(exist = false)
    @org.influxdb.annotation.Column(name = "created_at", tag = false)
    private long createdAt;

    @TableField(exist = false)
    private Timestamp getTime;

    /**
     *报警级别
     */
    @Column(name = "al_level")
    @org.influxdb.annotation.Column(name = "al_level", tag = false)
    private String alLevel;

    /**
     * 计量单位
     */
    @Column(name = "basic_unit", length = 100)
    private String basicUnit;

    /**
     * 计量单位
     */
    @Column(name = "upper_limit", length = 100)
    private String upperLimit;

    /**
     * 计量单位
     */
    @Column(name = "lower_limit", length = 100)
    private String lowerLimit;

    /**
     *展示顺序
     */
    @Column(name = "order_num")
    private Integer orderNum;

    /**
     * 设备编码
     */
    @TableField(exist = false)
    private  String eqtCode;


    /**
     * 是否展示到前端：0，false, 1，true
     */
    @TableField(exist = false)
    private Integer enable;

    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String eqtName;

    /**
     * 值更新时间
     */
    @Column(name = "update_at")
    private String updateAt;

    /**
     * 门编码
     */
    @TableField(exist = false)
    private  String doorId;

    /**
     * 门的请求状态
     */
    @TableField(exist = false)
    private  String agvRequest;


    private transient Double leftCollectTime;
}
