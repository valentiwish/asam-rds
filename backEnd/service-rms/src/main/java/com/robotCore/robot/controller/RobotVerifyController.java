package com.robotCore.robot.controller;

import com.entity.R;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entityVo.VerifyGroupMapVO;
import com.robotCore.robot.service.RobotVerifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Des: 机器人异常校验控制器
 * @author: zxl
 * @date: 2023/6/29
 **/

@Slf4j
@RestController
@Api(value = "RobotVerifyController", description = "机器人异常校验控制器")
@RequestMapping(value = "verify")
public class RobotVerifyController extends BaseController {

    @Autowired
    private RobotVerifyService robotVerifyService;

    @ApiOperation(value = "校验分组中的地图是否一致", notes = "同一个分组中机器人使用的地图必须一致")
    @PostMapping(value = "/groupMap")
    public Object verifyGroupMap(String groupName) {
        if (ObjectUtil.isNotEmpty(groupName)) {
            VerifyGroupMapVO verifyGroupMapVO = robotVerifyService.verifyGroupMap(groupName);
            return R.ok(verifyGroupMapVO);
        } else {
            return R.error("前端传递参数为空");
        }
    }

}
