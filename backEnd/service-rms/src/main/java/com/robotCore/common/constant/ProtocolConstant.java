package com.robotCore.common.constant;

import com.entity.EntityResult;
import org.aspectj.weaver.ast.Var;

/**
 * @Des: 机器人状态的请求实例
 * @author: zxl
 * @date: 2023/4/14
 **/
public class ProtocolConstant {
    //查询机器人信息
    public static final String ROBOT_BASIC_INFO = "5A 01 00 01 00 00 00 00 03 E8 00 00 00 00 00 00";

    //查询机器人运行信息
    public static final String ROBOT_STATUS_RUN_INFO = "5A 01 00 01 00 00 00 00 03 EA 00 00 00 00 00 00";

    //查询机器人位置
    public static final String ROBOT_STATUS_LOC = "5A 01 00 01 00 00 00 00 03 EC 00 00 00 00 00 00";

    //查询机器人电池状态
    public static final String ROBOT_STATUS_BATTERY = "5A 01 00 01 00 00 00 00 03 EF 00 00 00 00 00 00";

    //配置机器人推送端口的时间间隔为2000ms  {"interval":2000}
    public static final String ROBOT_PUSH_CONFIG = "5A 01 00 01 00 00 00 11 0F FB 00 00 00 00 00 00 7B 22 69 6E 74 65 72 76 61 6C 22 3A 32 30 30 30 7D";

    //配置机器人推送端口的时间间隔为2000ms  {"interval":2000,"included_fields":["vehicle_id", "x","y","charging","battery_level","confidence"]}
    public static final String ROBOT_PUSH_CONFIG1 = "5A 01 00 01 00 00 00 3A 0F FB 00 00 00 00 00 00 7B 22 69 6E 74 65 72 76 61 6C 22 3A 32 30 30 30 2C 22 69 6E 63 6C 75 64 65 64 5F 66 69 65 6C 64 73 22 3A 5B 22 76 65 68 69 63 6C 65 5F 69 64 22 2C 22 78 22 2C 22 79 22 5D 7D";

    //释放控制权
    public static final String RELEASE_CONTROL = "5A 01 00 01 00 00 00 00 0F A6 00 00 00 00 00 00";

    //确认定位正确
    public static final String CONFIRM_LOC = "5A 01 00 01 00 00 00 00 07 D3 00 00 00 00 00 00";

    //继续导航
    public static final String CONTINUE_NAVIGATION = "5A 01 00 01 00 00 00 00 0B BA 00 00 00 00 00 00";

    //暂停导航
    public static final String PAUSE_NAVIGATION = "5A 01 00 01 00 00 00 00 0B B9 00 00 00 00 00 00";

    //取消导航
    public static final String CANCEL_NAVIGATION = "5A 01 00 01 00 00 00 00 0B BB 00 00 00 00 00 00";

    //上升
    public static final String JACK_LOAD = "5A 01 00 01 00 00 00 00 17 B6 00 00 00 00 00 00";

    //下降
    public static final String JACK_UPLOAD = "5A 01 00 01 00 00 00 00 17 B7 00 00 00 00 00 00";

    //查询机器人导航状态
    public static final String NAVIGATION_STATUS = "5A 01 00 01 00 00 00 00 03 FC 00 00 00 00 00 00";

    //查询顶升机构状态
    public static final String JACK_STATUS = "5A 01 00 01 00 00 00 00 04 03 00 00 00 00 00 00";

    //智能充电桩开始充电
//    public static final String CHARGE_PILE_START = "fe fd 00 88 18 06 e5 f4 01 24 03 84 00 00 00 00 00 00 00 26";

    public static final String CHARGE_PILE_START = "00 00 00 00 00 06 01 05 00 03 FF 00";

    public static final String SOFT_SCRAM = "5A 01 00 01 00 00 00 0F 17 74 00 00 00 00 00 00";

    //最后已完成任务和所有未完成任务状态
    public static final String ROBOT_TASK_STATUS = "5A 01 00 01 00 00 00 00 04 56 00 00 00 00 00 00";

    //查询机器人载入的地图以及存储的地图
    public static final String ROBOT_STATUS_MAP = "5A 01 00 01 00 00 00 00 05 14 00 00 00 00 00 00";

    //查询机器人当前载入地图中的站点信息
    public static final String ROBOT_STATUS_STATION = "5A 01 00 01 00 00 00 00 05 15 00 00 00 00 00 00";
}
