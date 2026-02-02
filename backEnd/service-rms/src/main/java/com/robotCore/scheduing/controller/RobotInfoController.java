package com.robotCore.scheduing.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.entity.EntityResult;
import com.page.BasePage;
import com.page.FormatPage;
import com.page.JPAPage;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.scheduing.service.RobotInfoService;
import com.robotCore.scheduing.service.RobotWaybillService;
import com.robotCore.scheduing.vo.RobotInfoVO;
import com.utils.tools.CopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Des: 调度系统中机器人模块的控制器类
 * @author: zxl
 * @date: 2023/6/7
 **/
@Slf4j
@RestController
@Api(value = "RobotInfoController", description = "调度系统中机器人模块的控制器类")
@RequestMapping(value = "robotInfo")
public class RobotInfoController extends BaseController {

    @Autowired
    private RobotInfoService robotInfoService;

    @Autowired
    private RobotWaybillService robotWaybillService;

    @ApiOperation(value = "获取全部对象")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data) throws InterruptedException {
        RobotBasicInfo robotBasicInfo=null;
        IPage<RobotBasicInfo> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if(ObjectUtil.isNotEmpty(data)){
            robotBasicInfo= JSON.parseObject(data,RobotBasicInfo.class);
        }
        EntityResult varEntityResult = new EntityResult();
        IPage<RobotInfoVO> page = robotInfoService.findPageList(varPage, robotBasicInfo);
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(page));
        return varEntityResult;
    }

    @ApiOperation(value = "释放机器人占用的站点和区域")
    @PostMapping(value = "/releasePointsAndAreas")
    public Object releasePointsAndAreas(String robotId) throws InterruptedException {
        EntityResult varEntityResult = new EntityResult();
        List<String> lockedPointsByRobot = robotWaybillService.unlockAllPointsAndAreas(robotId);
        String points = String.join(",", lockedPointsByRobot);
        varEntityResult.setData(points);
        return varEntityResult;
    }

    @ApiOperation(value = "获取机器人全部锁定的站点信息")
    @PostMapping(value = "/getLockedPoints")
    public Object getLockedPoints(String robotId) throws InterruptedException {
        EntityResult varEntityResult = new EntityResult();
        List<String> lockedPointsByRobot = robotWaybillService.getLockedPointsByRobot(robotId);
        String points = String.join(",", lockedPointsByRobot);
        varEntityResult.setData(points);
        return varEntityResult;
    }
}
