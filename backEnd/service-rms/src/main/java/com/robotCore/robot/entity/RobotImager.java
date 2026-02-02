package com.robotCore.robot.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des:
 * @author: lzy
 * @date: 2023/9/13
 **/
@Table(name = "robot_imager") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotImager extends BaseModelU{

//    @Column(name = "vehicle_id")
//    private String vehicleId;    //机器人名称

    @Column(name = "robot_name")
    private String robotName;	//此处为热成像仪相对应的机器人的名称

    @Column(name = "current_ip")
    private String currentIp;	//热成像仪推送的当前热成像仪ip，展示用

    @Column(name = "imager_name")
    private String imagerName;	//热成像仪名称
}
