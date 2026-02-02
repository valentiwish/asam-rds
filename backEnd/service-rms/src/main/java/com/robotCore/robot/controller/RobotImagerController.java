package com.robotCore.robot.controller;

import com.alibaba.fastjson.JSON;
import com.beans.redis.RedisUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.entity.R;
import com.page.JPAPage;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.config.ControllerLog;
import com.robotCore.common.config.OpsLogType;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotImager;
import com.robotCore.robot.entityVo.ImagerByAddVO;
import com.robotCore.robot.entityVo.ImagersAddVO;
import com.robotCore.robot.service.RobotImagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Des: 机器人热成像仪控制器
 * @author: lzy
 * @date: 2023/9/13
 **/

@Slf4j
@RestController
@Api(value = "RobotImagerController", description = "机器人热成像仪控制器")
@RequestMapping(value = "robotImager")
public class RobotImagerController extends BaseController{

    public static final int STATE_VALID = 1;
    public static final int STATE_INVALID = 0;

    @Autowired
    private RobotImagerService robotImagerService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "获取热成像仪列表")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String robotName, String currentIp, String imagerName) {
        IPage<RobotImager> page = robotImagerService.findPageList(varBasePage,robotName, currentIp,imagerName);
        return R.ok(page);
    }

    @ControllerLog(type = OpsLogType.ADDORUPDATE, value = "热成像仪")
    @ApiOperation(value = "新增或修改")
    @RequestMapping(value = "/save")
    public Object save(String data) {
        if (ObjectUtil.isNotEmpty(data)) {
            RobotImager model = JSON.parseObject(data,RobotImager.class);
            model.setState(STATE_VALID);
            UserVO curUser = getCurUser();//当前创建人
            boolean b= robotImagerService.save(model,curUser.getId(),curUser.getUserName());
            return b ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "根据Id获取对象")
    @RequestMapping(value = "/findById")
    public Object findById(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotImager data = robotImagerService.getById(id);
            return R.ok(data);
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ControllerLog(type = OpsLogType.DELETE, value = "根据id删除热成像仪")
    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            RobotImager model = robotImagerService.getById(id);
            model.setState(STATE_INVALID);
            UserVO curUser = getCurUser();//获取当前登录人
            boolean flag = robotImagerService.save(model, curUser.getId(), curUser.getUserName());
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取需要添加的真实热成像仪列表", notes = "当点击添加热成像仪按钮的摄像机按钮")
    @PostMapping(value = "/getAddImager")
    public Object getAddImager(){
        List<ImagerByAddVO> addList = robotImagerService.getAddImager();
        return R.ok(addList);
    }

    //    @ControllerLog(value = "添加真实热成像仪")
    @ApiOperation(value = "添加真实热成像仪", notes = "添加真实热成像仪")
    @PostMapping(value = "/saveRealImager")
    public Object addRealImagers(@RequestBody String data){
        if (ObjectUtil.isNotEmpty(data)) {
            ImagersAddVO imagersAddVO = JSON.parseObject(data, ImagersAddVO.class);
            boolean flag = true;
            for (int i = 0; i < imagersAddVO.getData().size(); i++) {
                ImagerByAddVO imagerByAddVO = imagersAddVO.getData().get(i);
                RobotImager model = robotImagerService.setImagerValue(imagerByAddVO, imagersAddVO);
                UserVO curUser = getCurUser();//当前创建人
                boolean b = robotImagerService.save(model,curUser.getId(),curUser.getUserName());
                flag = flag && b;
                if (flag) {
                    List<String> imagers = (List<String>)redisUtil.stringWithGet("imagers");
                    imagers.add(imagerByAddVO.getVehicleId());
                    redisUtil.stringWithSet("imagers", imagers);
                }
            }
            return flag ? R.ok() : R.error("获取添加的真实热成像仪信息失败");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "热成像仪名称校验")
    @PostMapping(value = "/checkImagerName")
    public Object checkCode(String id, String imagerName) {
        if (ObjectUtil.isNotEmpty(imagerName)) {
            List<RobotImager> list = robotImagerService.findList(id, imagerName);
            boolean b = Objects.isNull(list) || 0 == list.size();
            return R.ok(b);
        } else {
            return R.error("前端传递参数为空");
        }
    }
}
