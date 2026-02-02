package com.robotCore.scheduing.common.enums;

import java.util.stream.Stream;

/**
 * @Des: API错误码
 * @author: zxl
 * @date: 2023/6/8
 **/
public enum RobotApiErrorCodeEnum {

    req_unavailable(40000, "请求不可用"),

    param_missing(40001, "必要的请求参数缺失"),

    param_type_error(40002, "请求参数类型错误"),

    param_illegal(40003, "请求参数不合法"),

    mode_error(40004, "运行模式错误"),

    illegal_map_name(40005, "非法的地图名"),

    programming_dsp(40006, "正在烧写固件"),

    program_dsp_error(40007, "烧写固件错误"),

    illegal_filename(40008, "文件名非法"),

    shutdown_error(40010, "关机指令出现错误"),

    reboot_error(40011, "重启指令出现错误"),

    dispatching(40012, "调度系统控制中"),

    robod_error(40013, "robod 错误"),

    robod_warning(40014, "robod 警告"),

    manual_charging(40015, "正在手动充电，不能运动"),

    emc_status(40016, "急停状态中"),

    locked(40020, "控制权被抢占"),

    map_parse_error_(40050, "地图解析出错"),

    map_not_exists(40051, "地图不存在"),

    load_map_error(40052, "加载地图错误"),

    load_mapobj_error(40053, "重载地图错误"),

    empty_map(40054, "空地图"),

    file_not_exists(40055, "文件不存在"),

    map_convert_error(40056, "地图转换失败"),

    rawmap_not_exists(40057, "当前无可用 rawmap 文件"),

    calib_file_not_exists(40058, "当前无可用 calib 文件"),

    audio_not_exists(40060, "音频文件不存在"),

    audio_play_error(40061, "播放音频出错"),

    upload_audio_error(40062, "上传音频文件失败"),

    audio_is_playing(40063, "音频正在播放中"),

    model_save_error(40069, "保存模型文件出错"),

    model_parse_error(40070, "模型文件解析错误"),

    calibration_parse_error(40071, "标定数据解析错误"),

    calibration_save_error(40072, "保存标定文件出错"),

    calibration_clear_error(40073, "清除标定数据出错"),

    req_timeout(40100, "请求执行超时"),

    req_forbidden(40101, "请求被禁止"),

    robot_busy(40102, "机器人繁忙"),

    robot_internal_error(40199, "内部错误"),

    tasklist_parse_error(40200, "解析任务链错误"),

    illegal_tasklist_name(40201, "任务链名字非法"),

    tasklist_not_exists(40202, "任务链不存在"),

    tasklist_executing(40203, "任务链正在执行中"),

    set_param_type_error(40300, "设置参数类型错误"),

    set_param_not_exists(40301, "设置的参数不存在"),

    set_param_error(40302, "设置参数出错"),

    save_param_type_error(40310, "设置并保存参数类型错误"),

    save_param_not_exists(40311, "设置并保存参数不存在"),

    save_param_error(40312, "设置并保存参数出错"),

    reload_param_type_error(40320, "重载的参数类型错误"),

    reload_param_not_exists(40321, "重载的参数不存在"),

    reload_param_error(40322, "重载参数出错"),

    src_require_error(40400, "获取控制权错误"),

    src_release_error	(40401, "释放控制权错误"),

    init_status_error(41000, "初始化状态错误"),

    loadmap_status_error(41001, "地图载入状态错误"),

    reloc_status_error(41002, "重定位状态错误"),

    reloc_no_robot_home	(41003, "找不到重定位的 robotHome"),

    confidence_too_low(41004, "置信度过低"),

    no_start_pos(41100, "找不到起点"),

    no_ready_pos(41101, "找不到准备点"),

    no_end_pod(41102, "找不到终点"),

    no_charge_pos(41103, "充电点不存在"),

    speed_illegal(41200, "速度值非法"),

    roller_connect_error(42000, "辊筒或皮带连接错误"),

    roller_type_unknown(42001, "辊筒或皮带类型未知"),

    roller_cmd_unsupported(42002, "辊筒或皮带不支持该指令"),

    jack_connect_error(42003, "顶升机构连接错误"),

    jack_type_unknown(42004, "顶升机构类型未知"),

    jack_cmd_unsupported(42005, "顶升机构不支持该指令"),

    lift_failed(43000, "升降操作出错"),

    redis_conn_error(44000, "redis 连接错误"),

    subchannel_error(44001, "redis 订阅错误"),

    robot_error_wtype_res(60000, "错误的报文类型，若用户将某类型的报文发错了端口将得到这个响应"),

    robot_error_utype_res(60001, "未知的报文类型, 报文类型号未在上文中定义"),

    robot_error_data_res(60002, "错误的数据区，当数据区无法反序列化为 JSON 对象时将得到这个响应"),

    robot_error_version_res(60003, "协议版本错误时得到的响应"),

    robot_error_hugedata_res(60004, "数据区过大，服务器会主动断连接，限制 200 MB");


    /**
     * 成员变量：编码
     */
    private Integer code;

    /**
     * 成员变量：名称
     */
    private String name;

    /**
     * 构造方法
     * @param code
     * @param name
     */
    RobotApiErrorCodeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "code=" + this.code + ",name=" + this.name;
    }

    public static Stream<RobotApiErrorCodeEnum> stream() {
        return Stream.of(RobotApiErrorCodeEnum.values());
    }
}
