package com.robotCore.robot.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des: 机器人区域编辑器类
 * @author: zxl
 * @date: 2024/6/18
 **/
@Table(name = "robot_dms_editor") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotDmsEditor extends BaseModelU {
    //通信区域的名字
    @Column(name = "area_name")
    private String areaName;

    //光通信区域的中心点（光通信区域该属性有效）
    @Column(name = "area_center_point")
    private String areaCenterPoint;

    //通信区域信息
    @Column(name = "area_info", type = MySqlTypeConstant.TEXT)
    private String areaInfo;

    //区域包含所有站点信息
    @Column(name = "area_contain_points", type = MySqlTypeConstant.TEXT)
    private String areaContainPoints;

    //区域类型,0--光通信区域，1--充电区域，2--管控区域，3--自动门区域，4--电梯区域
    @Column(name = "area_type")
    private Integer areaType;	//光通信区域类型,0--普通区域，1--充电区域，2--管控区域， 3--自动门区域， 4--电梯区域

    // 区域类型标志
    public static class AreaType {
        public static final int NORMAL = 0;
        public static final int CHARGING = 1;
        public static final int FOCUS = 2;
        public static final int AUTO_DOOR = 3;
        public static final int ELEVATOR = 4;
    }

    // 多机器人间的区域冲突关系标志
    public static class AreaConflictFlag {
        // 无区域
        public static final int NONE_AREA = 0;
        // 区域被自己占用
        public static final int OCCUPIED_BY_SELF = 1;
        // 区域被他人占用
        public static final int OCCUPIED_BY_OTHER = 2;
        // 区域空闲
        public static final int FREE_AREA = 3;
    }

    //区域占用状态,0--非占用，1--占用 （leo add）
    @Column(name = "occupied_status", isNull = false)
    private Integer occupiedStatus = 0;

    //区域内机器人ID （leo add）
    @Column(name = "robot_id", isNull = false)
    private String robotId = "";


    //是否异常取消任务标志
//    @Column(name = "cancel_task_flag")
//    private Integer cancelTaskFlag;

    //区域内占用机器人名称
    @Column(name = "occupied_robot_name")
    private String occupiedRobotName;

    //区域内占用机器人品牌,机器人类型 1--米松叉车，2--金陵,3--西安航天
    @Column(name = "occupied_robot_type")
    private Integer occupiedRobotType;

    //区域内机器人IP
    @Column(name = "robot_ip")
    private String robotIp;
}
