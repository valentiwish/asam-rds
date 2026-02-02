package com.robotCore.robot.entityVo;

import lombok.Data;

/**
 * @Des: 热成像仪信息
 * @author: lzy
 * @date: 2023/9/22
 **/
@Data
public class ImagerByAddVO {

    private String id;   //热成像仪Id

    private String vehicleId;    //机器人名称

    private  String currentIp;  //热成像仪推送的当前热成像仪ip，展示用

    private String imagerName;	//热成像仪名称

}
