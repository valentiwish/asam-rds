package com.robotCore.camera.controller;

import com.entity.R;
import com.netsdk.lib.NetSDKLib;
import com.robotCore.camera.utils.CapturePictureUtil;
import com.robotCore.camera.utils.LoginUtil;
import com.robotCore.camera.utils.PTZControlUtil;
import com.robotCore.camera.utils.RealPlayUtil;
import com.robotCore.common.base.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

/**
 * @Des: 热成像摄像机管理控制器类
 * @author: zxl
 * @date: 2023/9/1
 **/
@Slf4j
@RestController
@Api(value = "CameraController", description = "热成像摄像机管理控制器类")
@RequestMapping(value = "camera")
public class CameraController extends BaseController {

    @ApiOperation(value = "登录功能")
    @RequestMapping(value = "/login")
    public Object login(String ip, int port, String user, String password){
        boolean login = LoginUtil.login(ip, port, user, password);
        return login ? R.ok() : R.error("登录失败，请重新登陆！");
    }

    @ApiOperation(value = "退出登录功能")
    @RequestMapping(value = "/logout")
    public Object logout(){
        boolean logout = LoginUtil.logout();
        return logout ? R.ok() : R.error("退出登录失败！");
    }

    /**
     * 封装抓图方法
     * @param channelId 通道id
     * @param mode 请求一帧 默认 0
     * @param interval 时间单位秒：默认 0
     * @return
     */
    @ApiOperation(value = "抓图功能")
    @RequestMapping(value = "/capturePicture")
    public Object capturePicture(int channelId, int mode, int interval){
        boolean b = CapturePictureUtil.capturePicture(channelId, mode, interval);
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }

    /**
     *云台控制--向上移动
     * @return
     */
    @ApiOperation(value = "控制云台--向上移动")
    @RequestMapping(value = "/upControlPtz")
    public Object upControlPtz(int channelId, int lParam1, int lParam2){
        boolean b = PTZControlUtil.upControlPtz(channelId, lParam1, lParam2);
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }

    @ApiOperation(value = "控制云台--向下移动")
    @RequestMapping(value = "/downControlPtz")
    public Object downControlPtz(int channelId, int lParam1, int lParam2){
        boolean b = PTZControlUtil.downControlPtz(channelId, lParam1, lParam2);
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }

    @ApiOperation(value = "控制云台--向左上移动")
    @RequestMapping(value = "/ptzControlLeftUpStart")
    public Object ptzControlLeftUpStart(int channelId, int lParam1, int lParam2){
        boolean b = PTZControlUtil.ptzControlLeftUpStart(channelId, lParam1, lParam2);
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }

    @ApiOperation(value = "控制云台--停止移动")
    @RequestMapping(value = "/stopUpControl")
    public Object stopUpControl(int channelId){
        boolean b = PTZControlUtil.stopUpControl(channelId);
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }

    /**
     * 控制云台--转至预置点
     * @param channelId 通道id 默认为0
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 预置点的编号值
     * @return
     */
    @ApiOperation(value = "控制云台--转至预置点")
    @RequestMapping(value = "/toPresetPointControl")
    public Object toPresetPointControl(int channelId, int lParam1, int lParam2){
        boolean b = PTZControlUtil.presetPointControlPtz(channelId, lParam1, lParam2);
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }

    /**
     * 控制云台--转至预置点
     * @param channelId 通道id 默认为0
     * @param lParam1 默认 0，当有左上或左下等操作时才会传值 (1-8)
     * @param lParam2 预置点的编号值
     * @return
     */
    @ApiOperation(value = "控制云台--转至预置点并抓图")
    @RequestMapping(value = "/toPresetPointAndCpControl")
    public Object toPresetPointAndCpControl(int channelId, int lParam1, int lParam2, int lParam3){
        boolean b = PTZControlUtil.toPresetPointAndCpControl(channelId, lParam1, lParam2, lParam3);
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }

    @ApiOperation(value = "普通视频实时预览")
    @RequestMapping(value = "/realPlay")
    public Object realPlay(int channelId) {
        Frame frame=new Frame("实时预览");
        Panel panel=new Panel();
        //设置布局；
        frame.setLayout(null);
        //坐标
        frame.setBounds(300,300,500,500);
        panel.setBounds(50,50,300,300);
        //背景色
        panel.setBackground(new Color(100,100,100));
        //添加panel面板到Frame窗口
        frame.add(panel);
        //设置可见性
        frame.setVisible(true);
        //监听事件，监听窗口关闭事件 System.exit(0)；
        //适配器模式
//        frame.addWindowListener(new WindowAdapter(){
//            @Override
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });
        boolean b = RealPlayUtil.startRealPlayOne(channelId, 0, panel);
        return b ? R.ok() : R.error("没有登录，请先登录！");
    }

    @ApiOperation(value = "停止预览")
    @RequestMapping(value = "/stopRealPlay")
    public Object stopRealPlay(){
        boolean flag = RealPlayUtil.stopRealPlayOne();
        return flag ? R.ok() : R.error("停止预览失败！");
    }

    @ApiOperation(value = "保存预览")
    @RequestMapping(value = "/saveRealPlay")
    public Object saveRealPlay(){
        boolean flag = RealPlayUtil.saveRealPlayOne();
        return flag ? R.ok() : R.error("保存预览失败！");
    }


    @ApiOperation(value = "暂停保存预览")
    @RequestMapping(value = "/stopSaveRealPlay")
    public Object stopSaveRealPlay(){
        boolean flag = RealPlayUtil.stopSaveRealPlayOne();
        return flag ? R.ok() : R.error("暂停保存预览失败！");
    }

}
