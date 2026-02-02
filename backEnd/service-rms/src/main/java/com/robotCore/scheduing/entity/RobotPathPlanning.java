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
 * @Des:
 * @author: zxl
 * @date: 2023/9/8
 **/
@Table(name = "robot_path_planning") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotPathPlanning extends BaseModelU {

    @Column(name = "status")
    private String status; //执行的状态

    @Column(name = "task_id")
    private String taskId;  //任务id

    @Column(name = "processing_time")
    private String processingTime; //算法执行的时间

    @Column(name = "result", type = MySqlTypeConstant.TEXT)
    private String result; //路径规划结果

}
