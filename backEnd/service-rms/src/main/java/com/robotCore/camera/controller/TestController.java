package com.robotCore.camera.controller;

import com.beans.redis.RedisUtil;
import com.entity.R;
import com.netsdk.demo.frame.CapturePictureFrame;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/8/30
 **/
@Slf4j
@RestController
@Api(value = "TestController", description = "测试控制类管理")
@RequestMapping(value = "test1")
public class TestController {
    @Autowired
    private RedisUtil redisUtil;
    @PostMapping(value = "/test1")
    public Object test() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CapturePictureFrame demo = new CapturePictureFrame();
                demo.setVisible(true);
            }
        });
        return R.ok();
    }
    @ApiOperation(value = "获取全部对象")
    @PostMapping(value = "/test2")
    public void test2() throws InterruptedException{
        Thread thread = new Thread(() -> {
            // 处理子列表的代码
            for (int i = 0; i < 100; i++) {
                redisUtil.stringWithSet("111",1);
            }
        });

        thread.start();
        thread.interrupt();

    }
}
