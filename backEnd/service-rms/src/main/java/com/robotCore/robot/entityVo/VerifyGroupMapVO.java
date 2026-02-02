package com.robotCore.robot.entityVo;

import lombok.Data;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/29
 **/
@Data
public class VerifyGroupMapVO {

    //是否异常
    private boolean isAbnormal;

    private List<RobotByGroupVO> robotByGroupVOList;

}
