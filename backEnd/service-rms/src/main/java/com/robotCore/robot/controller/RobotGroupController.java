package com.robotCore.robot.controller;

import com.entity.R;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.constant.Constant;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.entity.RobotGroup;
import com.robotCore.robot.service.RobotGroupService;
import com.robotCore.scheduing.service.RobotInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Des: 机器人分组管理控制类
 * @author: zxl
 * @date: 2023/4/17
 **/
@Slf4j
@RestController
@Api(value = "RobotGroupController", description = "机器人分组")
@RequestMapping(value = "robotGroup")
public class RobotGroupController extends BaseController {

    @Autowired
    RobotGroupService robotGroupService;

    @Autowired
    private RobotInfoService robotInfoService;

    @ApiOperation(value = "获取分组列表")
    @RequestMapping(value = "/list")
    public Object list(){
        List<RobotGroup> list = robotGroupService.findList();
        return R.ok(list);
    }

//    @ControllerLog(type = OpsLogType.ADDORUPDATE, value ="添加机器人分组")
    @ApiOperation(value = "新增或更新对象")
    @RequestMapping(value = "/save")
    public Object save(String groupName, String groupDes){
        if (ObjectUtil.isNotEmpty(groupName)) {
            RobotGroup model = new RobotGroup();
            model.setGroupName(groupName);
            model.setGroupDes(groupDes);
            model.setPooling(0);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean flag = robotGroupService.save(model,curUser.getId(),curUser.getUserName());
            return flag ? R.ok(): R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    //    @ControllerLog(type = OpsLogType.ADDORUPDATE, value ="更新机器人分组")
    @ApiOperation(value = "更新对象")
    @RequestMapping(value = "/update")
    public Object update(String id, String groupName, String groupDes, Integer pooling){
        if (ObjectUtil.isNotEmpty(id) && ObjectUtil.isNotEmpty(groupName) && ObjectUtil.isNotEmpty(pooling)) {
            RobotGroup model = robotGroupService.getById(id);
            String originalGroupName = model.getGroupName();

            //校验Panel中的机器人分组是否重复
            List<RobotGroup> robotGroups = robotGroupService.findList(id, groupName);
            if (robotGroups.size() > 0){
                return R.error("分组名称不能重复！");
            }

            List<RobotBasicInfo> listByGroupName = robotInfoService.findListByGroupName(originalGroupName);
            boolean b1 = true;
            for (int i = 0; i < listByGroupName.size(); i++) {
                RobotBasicInfo robotBasicInfo = listByGroupName.get(i);
                robotBasicInfo.setGroupName(groupName);
                boolean b = robotInfoService.saveOrUpdate(robotBasicInfo);
                b1 = b && b1;
            }

            model.setGroupName(groupName);
            model.setGroupDes(groupDes);

            //0--false,1--true
            model.setPooling(pooling);
            model.setState(Constant.STATE_VALID);
            UserVO curUser = getCurUser();//获取当前登录人
            if (b1){
                boolean flag = robotGroupService.save(model,curUser.getId(),curUser.getUserName());
                return flag ? R.ok(): R.error();
            }else {
                return R.error("机器人修改分组名称失败！");
            }

        } else {
            return R.error("前端传递参数为空");
        }
    }

//    @ControllerLog(type = OpsLogType.DELETE, value ="删除机器人分组")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            boolean flag = robotGroupService.removeById(id);
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id){
        if (ObjectUtil.isNotEmpty(id)) {
            RobotGroup model= robotGroupService.getById(id);
            return R.ok(model);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "校验名称")
    @RequestMapping(value = "/checkName")
    public Object check(String groupName, String id){
        if (ObjectUtil.isNotEmpty(groupName)) {
            List<RobotGroup> list = robotGroupService.findList(id,groupName);
            boolean b= Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }
}
