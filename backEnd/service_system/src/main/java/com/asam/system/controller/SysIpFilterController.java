package com.asam.system.controller;

import com.alibaba.fastjson.JSON;
import com.asam.common.base.controller.BaseController;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.entity.R;
import com.asam.human.entity.UserVO;
import com.asam.system.entity.SysIpFilter;
import com.asam.system.service.SysIpFilterService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.page.JPAPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 针对三员管理的三个账户，设置IP白名单，只有白名单内的才能登录系统
 * @Author: fyy
 * @Create: 2022-07-26
 */
@RestController
@Api(value = "SysIpFilterController", description = "白名单管理")
@RequestMapping(value = "sysIpFilter")
public class SysIpFilterController extends BaseController {

    @Autowired
    private SysIpFilterService sysIpFilterService;


    @ApiOperation(value = "获取分页列表")
    @RequestMapping("/list")
    public Object list(JPAPage varBasePage, String data) {
        SysIpFilter sysIpFilter = JSON.parseObject(data, SysIpFilter.class);
        IPage<SysIpFilter> page = sysIpFilterService.findPageList(varBasePage, sysIpFilter);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "职务管理")
    @ApiOperation(value = "新增或更新对象")
    @RequestMapping("/save")
    public Object save(String data) {
        if (ObjectUtil.isNotEmpty(data)) {
            SysIpFilter model = JSON.parseObject(data, SysIpFilter.class);
            model.setState(Constant.STATE_VALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean flag = sysIpFilterService.save(model, curUser.getId(), curUser.getUserName());
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "职务管理")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(Long id){
        if (ObjectUtil.isNotEmpty(id)) {
            SysIpFilter model = sysIpFilterService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean flag = sysIpFilterService.save(model, curUser.getId(), curUser.getUserName());
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id) {
        SysIpFilter model=sysIpFilterService.getById(id);
        return R.ok(model);
    }

    @ApiOperation(value = "IP唯一校验", notes = "白名单管理")
    @RequestMapping(value = "/checkIpUnique")
    public Object checkIpUnique(String ip,String id) {
        if (ObjectUtil.isNotEmpty(ip)) {
            List<SysIpFilter> list = sysIpFilterService.findList(id, ip);
            boolean b = Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }
}