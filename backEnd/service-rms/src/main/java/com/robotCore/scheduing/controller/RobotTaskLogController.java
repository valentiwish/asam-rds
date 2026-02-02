package com.robotCore.scheduing.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entity.EntityResult;
import com.entity.R;
import com.page.BasePage;
import com.page.FormatPage;
import com.page.JPAPage;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.config.ControllerLog;
import com.robotCore.common.config.OpsLogType;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.scheduing.entity.RobotTaskLog;
import com.robotCore.scheduing.service.RobotTaskLogService;
import com.robotCore.scheduing.vo.RobotTaskLogVO;
import com.utils.tools.CopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Des: 机器人任务下发接口第三方数据输入管理
 * @author: zxl
 * @date: 2024/4/10
 **/
@Slf4j
@RestController
@Api(value = "RobotLogController", description = "机器人任务下发接口第三方数据输入管理")
@RequestMapping(value = "taskLog")
public class RobotTaskLogController extends BaseController {

    @Autowired
    private RobotTaskLogService robotTaskLogService;

    @ApiOperation(value = "获取全部对象")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data) {
        RobotTaskLog robotTaskLog = null;
        IPage<RobotTaskLog> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if(ObjectUtil.isNotEmpty(data)){
            robotTaskLog = JSON.parseObject(data,RobotTaskLog.class);
        }
        EntityResult varEntityResult = new EntityResult();
        IPage<RobotTaskLog> page = robotTaskLogService.findPageList(varPage, robotTaskLog);
        IPage<RobotTaskLogVO> newPage = new Page<>();
        newPage.setCurrent(page.getCurrent());
        newPage.setSize(page.getSize());
        newPage.setTotal(page.getTotal());
        newPage.setRecords(robotTaskLogService.dataInit(page.getRecords()));
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(newPage));
        return varEntityResult;
    }

    @ControllerLog(type = OpsLogType.DELETE, value ="删除第三方主体操作日志")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            boolean flag = robotTaskLogService.removeById(id);
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id){
        if (ObjectUtil.isNotEmpty(id)) {
            RobotTaskLog model= robotTaskLogService.getById(id);
            return R.ok(model);
        } else {
            return R.error("前端传递参数为空");
        }
    }

}
