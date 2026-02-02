package com.robotCore.robot.entityVo;

import lombok.Data;

import java.util.List;

/**
 * @Des:
 * @author: lzy
 * @date: 2023/9/26
 **/
@Data
public class ImagersAddVO {

    private String vehicleId;    //机器人名称

    private List<ImagerByAddVO> data;
}
