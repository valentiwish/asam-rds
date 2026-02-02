package com.robotCore.robot.entity;

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
 * @date: 2023/4/19
 **/
@Table(name = "robot_basic_info") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class RobotBasicInfo extends BaseModelU{
    @Column(name = "group_name")
    private  String groupName;	//string	页面配置 机器人分组

    @Column(name = "robot_type")
    private  String robotType;	//机器人类型

    @Column(name = "charge_only")
    private  Integer chargeOnly;	//Integer	机器人在电量下降到此值时，需要前往充电桩进行充电，并且在充电过程不可以接单。

    @Column(name = "charge_need")
    private  Integer chargeNeed;	//Integer	机器人在电量下降到此值时，需要前往充电桩进行充电，但是在充电过程中可以接单。

    @Column(name = "charge_ok")
    private  Integer chargeOk;	//Integer	机器人在充电时接到订单，只有电量充到此值时，才可以离开充电桩去接单。

    @Column(name = "charge_full")
    private  Integer chargeFull;	//Integer	机器人在充电时，电量达到此值后可以离开充电点。

    /**
     * 机器人接单状态，0--可接单，1--不接单且占用资源，2--接单不占用资源
     */
    @Column(name = "order_state")
    private  Integer orderState;

    /**
     * 机器人名称
     * transient 表明此处是机器人推送过来的，无需存储在数据库
     */

    @Column(name = "vehicle_id")
    private String vehicleId;

    /**
     * 机器人备注
     */
    @Column(name = "robot_note")
    private String robotNote;
    /**
     * Robokit版本号
     */
    @Column(name = "version")
    private String version;
    /**
     * 机器人模型名
     */
    @Column(name = "model")
    private String model;
    /**
     * 固件版本号
     */
    @Column(name = "dsp_version")
    private String dspVersion;
    /**
     * 陀螺仪版本号
     */
    @Column(name = "gyro_version")
    private String gyroVersion;
    /**
     * 地图版本号
     */
    @Column(name = "map_version")
    private String mapVersion;

    @Column(name = "model_version")
    private String modelVersion;  //string	模型版本号	是

    @Column(name = "netprotocol_version")
    private String netprotocolVersion;	//string	网络协议版本号	是

    @Column(name = "modbus_version")
    private String modbusVersion;	//string	ModbusTCP 协议版本号	是

    @Column(name = "current_map")
    private String currentMap;	//string	当前地图名	是

    @Column(name = "current_map_md5")
    private String currentMapMd5;	//string	当前地图 MD5 值	是

    @Column(name = "model_md5")
    private String modelMd5;	//string	当前模型 MD5 值	是

    @Column(name = "ssid")
    private String ssid	;//string;	//当前连接 Wifi 的 SSID（机器人需要支持连接 Wifi 并且已经连接，否则为空）	是

    @Column(name = "rssi")
    private Integer rssi;	//number	//当前连接 Wifi 的信号强度，0-100 百分比（机器人需要支持连接 Wifi 并且已经连接，否则为0）	是

    @Column(name = "ap_addr")
    private String apAddr;	///string	当前连接的AP的mac地址	是

    @Column(name = "current_ip")
    private String currentIp;	//string	机器人推送的当前机器人ip，展示用

    @Column(name = "m_a_c")
    private String MAC;	//string	//机器人无线客户端专用以太网接口的 MAC 地址（仅对 SRC -2000 有效），格式为 XXXXXXXXXXXX，中间没有分隔符	是

    @Column(name = "echoid_type")
    private String echoidType;	//string	机器人机器码类型 (用于激活，通常为 0x1800)	是

    @Column(name = "echoid")
    private String echoid;	//string	机器人机器码	是

    @Column(name = "features")
    private String features;	//array	机器人功能模块name = 功能模块名称 active = 是否已激活 expiry_date = 到期日期(可缺省)	是

    @Column(name = "battery_level")
    private float batteryLevel; //机器人电池电量, 范围 [0, 1]

    @Column(name = "confidence")
    private float confidence; //机器人的定位置信度, 范围 [0, 1]

    /**
     * 机器人当前地图对应的站点列表
     */
    @Column(name = "points", type = MySqlTypeConstant.MEDIUMTEXT)
    private String points;

    /**
     * 机器人地图对应的路径列表
     */
    @Column(name = "paths", type = MySqlTypeConstant.MEDIUMTEXT)
    private String paths;

    /**
     * 机器人 <地图名称-站点列表> Map
     */
    @Column(name = "map_name_to_points", type = MySqlTypeConstant.MEDIUMTEXT)
    private String mapNameToPoints;

    /**
     * 机器人是否空闲，0--不空闲，1--空闲
     */
    @Column(name = "leisure")
    private Integer leisure;

    /**
     * 是否充电，0--不·充电，1--充电
     */
    @Column(name = "charge")
    private Integer charge;
}
