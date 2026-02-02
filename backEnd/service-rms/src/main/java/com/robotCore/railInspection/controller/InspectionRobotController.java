package com.robotCore.railInspection.controller;

import com.entity.EntityResult;
import com.page.JPAPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Des: 巡检机器人控制器类
 * @author: zxl
 * @date: 2024/3/8
 **/
@Slf4j
@RestController
@Api(value = "InspectionRobotController", description = "巡检机器人控制器类")
@RequestMapping(value = "inspectionRobot")
public class InspectionRobotController {

    @ApiOperation(value = "获取巡检机器人列表")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data) {
        EntityResult varEntityResult = new EntityResult();
        return varEntityResult;
    }

}
