package com.robotCore.railInspection.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * @Des: 挂轨巡检信息
 * @author: zxl
 * @date: 2023/11/6
 **/
@Table(name = "rail_inspection_info") // 数据库中表名称
@Data               //能通过注解的形式自动生成构造器、getter/setter、equals、hashcode、toString等方法
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor  // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RailInspectionInfo extends BaseModelU {

    //任务名称
    @Column(name = "task_name")
    private String taskName;

    //任务类型
    @Column(name = "task_type")
    private  Integer taskType;

    //巡检对象
    @Column(name = "inspection_object")
    private String inspectionObject;

    //监测类型
    @Column(name = "inspection_type")
    private  String inspectionType;

    //巡检结果
    @Column(name = "inspection_result")
    private String inspectionResult;

    //多媒体记录
    @Column(name = "picture_id")
    private String pictureId;

}
