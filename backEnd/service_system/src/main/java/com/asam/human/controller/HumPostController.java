package com.asam.human.controller;

import com.alibaba.fastjson.JSON;
import com.asam.common.base.controller.BaseController;
import com.asam.common.constant.Constant;
import com.asam.common.logUtil.ControllerLog;
import com.asam.common.logUtil.OpsLogType;
import com.asam.common.util.ObjectUtil;
import com.entity.R;
import com.asam.human.entity.HumPost;
import com.asam.human.entity.UserVO;
import com.asam.human.service.HumPostService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.page.JPAPage;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/***
 * @Description: 岗位
 * @Author: fyy
 * @Create: 2022-07-26
 */
@RestController
@RequestMapping(value = "/post")
@Slf4j
public class HumPostController extends BaseController {

    @Autowired
    private HumPostService humPostService;

    @ApiOperation(value = "获取分页列表")
    @RequestMapping("/list")
    public Object list(JPAPage varBasePage, String name) {
        IPage<HumPost> page = humPostService.findPageList(varBasePage, name);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "职务管理")
    @ApiOperation(value = "新增或更新对象")
    @RequestMapping("/save")
    public Object save(String data) {
        if (ObjectUtil.isNotEmpty(data)) {
            HumPost model = JSON.parseObject(data, HumPost.class);
            model.setState(Constant.STATE_VALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean flag = humPostService.save(model, curUser.getId(), curUser.getUserName());
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
            HumPost model = humPostService.getById(id);
            model.setState(Constant.STATE_INVALID);
            UserVO curUser=getCurUser();//当前创建人
            boolean flag = humPostService.save(model, curUser.getId(), curUser.getUserName());
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id) {
        HumPost model=humPostService.getById(id);
        return R.ok(model);
    }

    @ApiOperation(value = "校验对象")
    @RequestMapping(value = "/checkName")
    public Object check(String name,Long id) {
        if (ObjectUtil.isNotEmpty(name)) {
            List<HumPost> list = humPostService.findList(id, name);
            boolean b = Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取下拉列表数据")
    @RequestMapping(value = "/selectList")
    public Object selectList(){
        List<HumPost> list = humPostService.findList(null,null);
        return R.ok(list);
    }
}
