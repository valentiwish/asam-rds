package com.robotCore.multiScheduling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsAutoIncrement;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/***
 * @Description: 动态采集变量
 * @Author: zhangqi
 * @Create: 2024/7/3
 */
@Data
@TableName(value = "pem_eqt_dynamic_param")
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class PemEqtDynamicParam {

    @TableId(type = IdType.AUTO) // mybatis-plus主键注解
    @IsKey // actable主键注解
    @IsAutoIncrement // 自增
    @Column // 对应数据库字段，不配置name会直接采用属性名作为字段名
    private Long id;

    /**
     * 设备ID
     */
    @Column(name = "eqt_code")
    private String eqtCode;

    /**
     * 点变量TagName；从别的厂商数据库过来的数据，PemEquipmentInfo.eqtCode == machineId
     */
    @Column(value = "variable_name")
    private String variableName;

    /**
     * 变量描述
     */
    @Column(name = "variable_desp")
    private String variableDesp;

    /**
     * 设备名称
     */
    @Column(name = "eqt_name")
    private String eqtName;

    /**
     * 工房Id
     */
    @Column(name = "plant_id")
    private String plantId;

    /**
     * 工房名称
     */
    @Column(name = "plant_name")
    private String plantName;

    /**
     * 产品编号
     */
    @Column(name = "product_serial")
    private String productSerial;

    /**
     * 产品编号
     */
    @Column(name = "collect_condition_realtime")
    private String collectConditionRealtime;

    /**
     * 产品编码
     */
    @Column(name = "mat_code")
    private String matCode;

    /**
     * 产品名称
     */
    @Column(name = "mat_name")
    private String matName;

    /**
     * 产品代号
     */
    @Column(name = "product_code")
    private String productCode;

    /**
     * 产品图号
     */
    @Column(name = "product_figure_no")
    private String productFigureNo;

    /**
     * 批次
     */
    @Column(name = "batch_no")
    private String batchNo;

    /**
     * 工序ID
     */
    @Column(name = "process_id")
    private String processId;

    /**
     * 数据最大值
     */
    @Column(name = "upper_limit")
    private String upperLimit;

    /**
     * 采集条件
     */
    @Column(name = "collect_condition_scheduled")
    private String collectConditionScheduled;

    /**
     * 数据最小值
     */
    @Column(name = "lower_limit")
    private String lowerLimit;

    /**
     * 工序号
     */
    @Column(name = "process_no")
    private String processNo;

    /**
     *  是否报警 1 是  0否
     */
    @Column(name = "is_alarm")
    private Integer isAlarm = 0;

    /**
     * 工序名称
     */
    @Column(name = "process_name")
    private String processName;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private String updateTime;

    /**
     * 是否开启采集，0-停止，1-开启，默认停止
     */
    @Column(name = "collect_flag")
    private Integer collectFlag = 0;

    /**
     * 瞬时采集是否已采集0-否，1-是
     */
    @Column(name = "collect_flag_realtime")
    private String collectFlagRealtime ;

    /**
     * 是否正在采集，0-否，1-是
     */
    @Column(name = "collecting_flag")
    private Integer collectingFlag = 0;

    /**
     * 采集周期，分钟，默认30
     */
    @Column(name = "collect_cycle")
    private Integer collectCycle;

    /**
     * 密级
     */
    @Column(name = "secret_level_no")
    private Integer secretLevelNo;

    /**
     *剩余采集时间(分钟)
     */
    @Column(name = "left_collect_time",decimalLength = 1)
    private Double leftCollectTime;

    /**
     *采集时长
     */
    @Column(name = "collect_time")
    private Integer collectTime;

    /**
     *采集时长
     */
    @Column(name = "job_id")
    private Integer jobId;

    private transient String processCode;

}
