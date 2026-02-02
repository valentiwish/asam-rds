package com.robotCore.common.config;

import com.robotCore.task.EquipDataTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/***
 * @Description: 队列监听器，初始化启动所有监听任务
 * @Author: zhangqi
 * @Create: 2024/10/16
 */
@Component
@Slf4j
public class QueueListener {

    @Autowired
    private EquipDataTask equipDataTask;

    /**
     * 初始化时启动监听请求队列
     */
    @PostConstruct
    public void init(){
        log.info("queue Consumer启动");
        equipDataTask.start();
    }


    /**
     * 销毁容器时停止监听任务
     */
    @PreDestroy
    public void destory() {
        equipDataTask.setRunning(false);
    }
}
