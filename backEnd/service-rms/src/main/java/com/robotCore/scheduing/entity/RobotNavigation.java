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
 * @Des: 机器人导航
 * @author: zxl
 * @date: 2023/9/13
 **/
@Table(name = "robot_navigation") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotNavigation extends BaseModelU {

    /**
     * 机器人名称
     */
    @Column(name = "robot_name")
    private String robotName;

    /**
     * 导航起点
     */
    @Column(name = "begin_port")
    private String beginPort;

    /**
     * 导航起点的下一个点
     */
    @Column(name = "next_begin_port")
    private String nextBeginPort;

    /**
     * 导航终点
     */
    @Column(name = "end_port")
    private String endPort;

    /**
     * 任务ID
     */
    @Column(name = "task_id")
    private String taskId;

    /**
     * 子任务列表
     */
    @Column(name = "task_list", type = MySqlTypeConstant.TEXT)
    private String taskList;

}
