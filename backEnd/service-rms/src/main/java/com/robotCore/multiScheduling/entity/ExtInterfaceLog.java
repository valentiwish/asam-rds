package com.robotCore.multiScheduling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsAutoIncrement;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

import static com.robotCore.common.constant.Constant.STATE_INVALID;

/**
 * @Description:
 * @Author: zhangqi
 * @Create: 2022/7/31 14:57
 */
@Data
@Table(name = "ext_interface_log")
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ExtInterfaceLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO) // mybatis-plus主键注解
    @IsKey // actable主键注解
    @IsAutoIncrement // 自增
    @Column // 对应数据库字段，不配置name会直接采用属性名作为字段名
    private Integer id;

    /**
     * 接口日志唯一标识
     */
    @Column
    private String logId;

    /**
     * 新增时间
     */
    @Column(type = MySqlTypeConstant.DATETIME)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 来源应用代码
     */
    @Column
    private String fromSys;

    /**
     * 目标应用代码
     * SAP	字符串	ERP系统
     * PDM	字符串	PDM系统
     * MES	字符串	MES
     * MOM	字符串	MOM
     * OA	字符串	办公自动化系统
     * TDM	字符串	试验数据管理系统
     * PORTAL	字符串	门户系统
     * TIBCO	字符串	数据交换平台
     * WEIGHSYS002 字符串	二车间称量系统
     * 3LineMES 字符串	三车间产线MES
     * AGV_JIN 字符串  金陵AGV调度系统
     */
    @Column
    private String toSys;

    /**
     * 交换数据内容
     */
    @Column(type = MySqlTypeConstant.LONGTEXT)
    private String content;

    /**
     * 接口对接返回结果字符串
     */
    @Column(name = "return_obj", type = MySqlTypeConstant.LONGTEXT)
    private String returnObj;

    /**
     * 接口对接返回结果msg
     */
    @Column(name = "return_msg", length = 500)
    private String returnMsg;

    /**
     * 接口对接返回结果
     */
    @Column(name = "status")
    private Integer status = STATE_INVALID;

    /**
     * 查询开始时间
     */
    private transient Date startTime;

    /**
     * 查询结束时间
     */
    private transient Date endTime;
}
