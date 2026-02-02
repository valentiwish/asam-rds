package com.robotCore.railInspection.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 机器人巡检地图
 * @author: zxl
 * @date: 2023/12/14
 **/
@Table(name = "rail_inspection_map") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RailInspectionMap extends BaseModelU {

    //站点名称
    @Column(name = "point_name")
    private String pointName;

    //站点距离原点的绝对长度
    @Column(name = "point_length")
    private float pointLength;

    //站点的检测状态
    @Column(name = "inspection_status")
    private Integer inspectionStatus;
}
