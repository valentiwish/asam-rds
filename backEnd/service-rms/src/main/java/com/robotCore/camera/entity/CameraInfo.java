package com.robotCore.camera.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.robotCore.common.base.model.BaseModelU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/9/1
 **/
@Table(name = "camera_info") // 数据库中表名称
@Data
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class CameraInfo extends BaseModelU {

    @Column(name = "camera_ip")
    private String cameraIp;	//摄像机IP

    @Column(name = "title_name")
    private String titleName;	//操作的模块名称

    @Column(name = "online")
    private String online;  //摄像机在线信息

    @Column(name = "disConnect_reconnect")
    private String disConnectReconnect;  //失去连接重连

}
