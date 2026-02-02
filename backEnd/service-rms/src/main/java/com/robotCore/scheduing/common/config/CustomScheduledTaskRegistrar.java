package com.robotCore.scheduing.common.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/13
 **/
@Slf4j
@AllArgsConstructor
@Configuration
public class CustomScheduledTaskRegistrar implements DisposableBean {

    private TaskScheduler taskScheduler;

    private final Map<String,CustomScheduledTask> scheduledTasks = new ConcurrentHashMap(16);

    public void addTriggerTask(String key,Runnable task, Trigger trigger) {
        if (task == null || trigger == null || StringUtils.isBlank(key)){
            log.error("任务key和任务线程以及触发器不能为空");
            return;
        }
        CustomScheduledTask customScheduledTask1 = scheduledTasks.get(key);
        if (customScheduledTask1 != null){
            log.error("{}---对应的任务已存在，请勿重复创建，如需重复创建，请先执行删除后在尝试新建任务",key);
            return;
        }
        // addTriggerTask(new TriggerTask(task, trigger));
        CustomScheduledTask customScheduledTask = scheduleTriggerTask(new TriggerTask(task, trigger));
        customScheduledTask.future = this.taskScheduler.schedule(task, trigger);
        scheduledTasks.put(key,customScheduledTask);
    }

    public void removeTriggerTask(String key) {
        if (StringUtils.isBlank(key)){
            log.error("key不能为空");
            return;
        }
        CustomScheduledTask scheduledTask = scheduledTasks.get(key);
        if (scheduledTask == null){
//            log.error("{}对应的任务不存在，请勿重复删除",key);
        }else {
            scheduledTask.cancel();
            scheduledTasks.remove(key);
        }
    }

    private CustomScheduledTask scheduleTriggerTask(TriggerTask task) {

        return  new CustomScheduledTask(task);
    }

    @Override
    public void destroy() throws Exception {
        for (CustomScheduledTask task : this.scheduledTasks.values()) {
            task.cancel();
        }
    }
}
