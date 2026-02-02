package com.robotCore.scheduing.dto;

import com.robotCore.robot.entityVo.RobotBasicInfoVO;
import lombok.Data;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/27
 **/
@Data
public class GroupAndRobotDTO {

    //分组的id
    private String groupId;

    //分组的名字
    private String groupName;

    //属性值是否pooing
    private Integer pooling;

    //分组的描述
    private String groupDes;

    //分组中校验地图中所有机器人用的数据是否一致
    private boolean isMapAbnormal;

    private List<RobotBasicInfoVO> robots;
}
