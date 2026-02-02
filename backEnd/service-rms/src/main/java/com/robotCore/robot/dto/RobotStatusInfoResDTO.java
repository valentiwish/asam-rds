package com.robotCore.robot.dto;

import com.robotCore.scheduing.dto.RobotJackDTO;
import com.robotCore.scheduing.dto.RobotTaskListStatusDTO;
import lombok.Data;

/**
 * @Des: 机器人数据推送返回
 * @author: zxl
 * @date: 2023/4/25
 **/
@Data
public class RobotStatusInfoResDTO {

    private float controllerTemp; //控制器温度, 单位 ℃

    private float controllerHumi; //控制器湿度, 单位 %

    private float controllerVoltage; //控制器电压, 单位 V

    private float x; //机器人的 x 坐标, 单位 m

    private float y; //机器人的 y 坐标, 单位 m

    private String angle; //机器人的 angle 坐标, 单位 rad

    private String currentStation; //离机器人最近站点的 id

    private float vx; //机器人在机器人坐标系的 x 方向实际的速度, 单位 m/s

    private float vy; //机器人在机器人坐标系的 y 方向实际的速度, 单位 m/s

    private float w; //机器人在机器人坐标系的实际的角速度(即顺时针转为负, 逆时针转为正), 单位 rad/s

    private float steer; //单舵轮机器人当前的舵轮角度, 单位 rad

    private boolean blocked; //机器人是否被阻挡

    private float batteryLevel; //机器人电池电量, 范围 [0, 1]

    private boolean charging; //电池是否正在充电

    private boolean emergency; //true 表示急停按钮处于激活状态(按下), false 表示急停按钮处于非激活状态(拔起)

    /**
     * id: 对应的 id 号
     * source: 来源
     * normal = 普通 DI
     * virtual = 虚拟 DI
     * modbus = modbus DI
     * status: 表示高低电平 true = 高电平false = 低电平
     * valid: 对应 DI 是否启用 true = 启用 false = 禁用
     */
    private Object[] DI; //DI

    /**
     * id: 对应的 id 号
     * source: 来源
     * normal = 普通 DO
     * modbus = modbus DO
     * status: 表示高低电平 true = 高电平 false = 低电平
     */
    private Object[] DO; //DO

    private String[] finishedPath;

    private Object[] fatals; //报警码 Fatal的数组, 所有出现的 Fatal 报警都会出现在数据中

    private Object[] errors; //报警码 Error的数组, 所有出现的 Error 报警都会出现在数据中

    private Object[] warnings; //报警码 Warning的数组, 所有出现的 Warning 报警都会出现在数据中

    private Object[] notices; //Notice的数组, 所有出现的 Notice 报警都会出现在数据中

    private String currentMap; //当前地图名

    private String vehicleId; //机器人名称

    private String createOn; //时间戳（2020-10-28T14:29:32.153Z）

    private float requestVoltage; //请求充电的电压。这两个变量是驼峰命名的，先不要改，为了兼容旧版程序

    private float requestCurrent; //请求充电的电流

    private boolean brake; //是否抱闸

    private boolean softEmc; //是否处于软急停状态

    private Object taskStatusPackage; //返回机器人上个已完成任务和所有未完成任务(包括失败任务)的列表

    private Object map; //内部(seed)使用，详见地图 userData

    private Object chargeSpot; //叉车自动充电桩状态数据

    private Integer relocStatus; //0 = FAILED(定位失败),1 = SUCCESS(定位正确),2 = RELOCING(正在重定位),3=COMPLETED(定位完成)

    private Integer loadmapStatus; //0 = FAILED(载入地图失败),1 = SUCCESS(载入地图成功),2 = LOADING(正在载入地图)

    private Integer slamStatus; //0 = 没有扫图,1 = 正在扫图(离线),2 = 正在实时扫图(SLAM),3 = 正在3D扫图（离线）,4 = 正在实时3D扫图

    private String robotNote; //机器人备注

    private String currentMapMd5; //当前地图 MD5 值

    private RobotControlResDTO currentLock; //查询当前控制权所有者

    private RobotForkResDTO fork; //叉车状态

    private float confidence; //机器人的定位置信度, 范围 [0, 1]

    private float batteryTemp; //机器人电池温度, 单位 ℃

    private String batteryUserData; //电池用户自定义数据

    private Integer batteryCycle; //电池循环次数(从电池 BMS 获取，不保证正确)

    private float voltage; //电压, 单位 V

    private float current; //电流, 单位 A

    private float rVx; //机器人在机器人坐标系的 x 方向接收到的速度, 单位 m/s

    private float rVy; //机器人在机器人坐标系的 y 方向收到的速度, 单位 m/s

    private float rW; //机器人在机器人坐标系的收到的角速度(即顺时针转为负, 逆时针转为正), 单位 rad/s

    private float rSteer; //单舵轮机器人收到的舵轮角度, 单位 rad

    private float rSpin; //托盘机器人的收到托盘（如果存在）转动速度, 单位 rad/s

    private float odo; //累计行驶里程, 单位 m

    private float todayOdo; //今日累计行驶里程, 单位 m

    private float time; //本次运行时间(开机后到当前的时间), 单位 ms

    private float totalTime; //累计运行时间, 单位 ms

    private float spin; //机器人的托盘（如果存在）角度, 单位 rad

    private Integer taskStatus; //导航任务状态

    private RobotJackDTO jack;//顶升相关状态

    private RobotTaskListStatusDTO tasklistStatus; //任务链状态

}
