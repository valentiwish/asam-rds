package com.robotCore.scheduing.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 机器人任务下发接口第三方数据输入日志实体类
 * @author: zxl
 * @date: 2024/4/10
 **/
@Table(name = "robot_task_log") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotTaskLog extends BaseModelU {

    /**
     * MES运单
     */
    @Column(name = "mes_waybill_id")
    private String mesWaybillId;

    /**
     * WMS运单
     */
    @Column(name = "wms_waybill_id")
    private String wmsWaybillId;

    /**
     * 第三方应用名称
     */
    @Column(name = "application_name")
    private String applicationName;

    /**
     * 第三方应用请求的IP
     */
    @Column(name = "request_ip")
    private String requestIp;

    /**
     * 操作业务
     */
    @Column(name = "operate_business")
    private String operateBusiness;

    /**
     * 操作内容
     */
    @Column(name = "operate_content", type = MySqlTypeConstant.TEXT)
    private String operateContent;

    /**
     * 操作结果
     */
    @Column(name = "operate_success")
    private Boolean operateSuccess;

    /**
     * 操作日志信息
     */
    @Column(name = "operate_message", type = MySqlTypeConstant.TEXT)
    private String operateMessage;

}
