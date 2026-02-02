package com.robotCore.camera.controller;

import com.entity.R;
import com.netsdk.lib.NetSDKLib;
import com.robotCore.camera.utils.LoginUtil;
import com.robotCore.camera.utils.PTZControlUtil;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.utils.ObjectUtil;
import com.sun.jna.ptr.IntByReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Des: 云台操作控制器类
 * @author: zxl
 * @date: 2023/10/27
 **/

@Slf4j
@RestController
@Api(value = "PTZController", description = "云台操作控制器类")
@RequestMapping(value = "ptz")
public class PTZController extends BaseController {

    @ApiOperation(value = "控制云台")
    @RequestMapping(value = "/control")
    public Object upControlPtz(int flag){
        if (ObjectUtil.isNotEmpty(flag)) {
            if (LoginUtil.m_hLoginHandle.longValue() == 0) {
                boolean login = LoginUtil.login("192.168.13.117", 37777, "admin", "admin123");
                if (!login) {
                    return R.error("登录失败！");
                }
            }
            //向上移动
            if (flag == 0) {
                boolean b = PTZControlUtil.upControlPtz(0, 0, 2);
                return b ? R.ok("向上移动") : R.error("没有登录，请先登录！");
            }

            //向下移动
            if (flag == 1 || flag == 2) {
                boolean b = PTZControlUtil.downControlPtz(0, 0, 2);
                return b ? R.ok("向下移动") : R.error("没有登录，请先登录！");
            }

            //向左移动
            if (flag == 3 || flag == 4) {
                boolean b = PTZControlUtil.leftControlPtz(0, 0, 2);
                return b ? R.ok("向左移动") : R.error("没有登录，请先登录！");
            }

            //向右移动
            if (flag == 5) {
                boolean b = PTZControlUtil.rightControlPtz(0, 0, 2);
                return b ? R.ok("向右移动") : R.error("没有登录，请先登录！");
            }

            //停止移动
            if (flag == 13) {
                boolean b = PTZControlUtil.stopUpControl(0);
                return b ? R.ok("停止移动") : R.error("没有登录，请先登录！");
            }

            //没有手势
            if (flag == 18) {
                return R.ok("没有手势！");
            }

            return R.error("识别手势错误!");
        } else {
            return R.error("前端传递参数为空");
        }
    }

    @ApiOperation(value = "获取云台精确位置信息")
    @RequestMapping(value = "/getControlPtz")
    public Object getControlPtz(){
        if (LoginUtil.m_hLoginHandle.longValue() == 0) {
            boolean login = LoginUtil.login("192.168.13.117", 37777, "admin", "admin123");
            if (!login) {
                return R.error("登录失败！");
            }
        }
        boolean b = PTZControlUtil.ptzQueryPreciseControlStatus();
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }

    @ApiOperation(value = "设置云台精确绝对移动")
    @RequestMapping(value = "/setControlPtz")
    public Object setControlPtz(int nChannelID, int xParam, int yParam, int zoomParam){
        if (LoginUtil.m_hLoginHandle.longValue() == 0) {
            boolean login = LoginUtil.login("192.168.13.117", 37777, "admin", "admin123");
            if (!login) {
                return R.error("登录失败！");
            }
        }
        boolean b = PTZControlUtil.ptzControlBaseMoveAbsolutely(LoginUtil.m_hLoginHandle, nChannelID, xParam, yParam, zoomParam);
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }
}
