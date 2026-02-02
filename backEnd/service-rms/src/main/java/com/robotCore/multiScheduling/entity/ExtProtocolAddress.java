package com.robotCore.multiScheduling.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsAutoIncrement;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: WebService通信协议信息
 * @Author: crystal
 * @Create: 2022/10/28 14:57
 */
@Data
@TableName(value = "ext_protocol_address")
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
@Accessors(chain = true)
public class ExtProtocolAddress implements Serializable {

    @TableId(type = IdType.AUTO) // mybatis-plus主键注解
    @IsKey // actable主键注解
    @IsAutoIncrement // 自增
    @Column // 对应数据库字段，不配置name会直接采用属性名作为字段名
    private Long id;

    @TableField("system_id")
    private Long systemId;

    /**
     * HTTP/HTTPS URL
     */
    @TableField("url")
    private String url;

    /**
     * 请求方式：GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE，以上针对HttpMethod请求
     */
    @TableField("request_mode")
    private String requestMode;

    /**
     * 来源单位代码
     */
    @TableField("from_unit")
    private String fromUnit;

    @TableField("from_unit_id")
    private String fromUnitId;

    @TableField("from_unit_name")
    private String fromUnitName;

    /**
     * 目标单位代码
     */
    @TableField("to_unit")
    private String toUnit;

    @TableField("to_unit_id")
    private String toUnitId;

    @TableField("to_unit_name")
    private String toUnitName;

    /**
     * 源头系统
     */
    @TableField("from_sys")
    private String fromSys;

    /**
     * 目标系统
     */
    @TableField("to_sys")
    private String toSys;

    /**
     * 业务操作名
     */
    @TableField("biz_opn")
    private String bizOpn;

    /**
     * 接口名称
     */
    @TableField("biz_opn_from")
    private String bizOpnFrom;

    /**
     * 协议类型：包括XML、XSD、SOAP、WSDL、RPC、JSON
     */
    @TableField("protocol_name")
    private String protocolName;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 数据状态：0表示逻辑删除；1表示数据有效
     */
    @TableField("state")
    private Integer state;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    private transient String content;

    private transient ReturnObj returnObj;

}
