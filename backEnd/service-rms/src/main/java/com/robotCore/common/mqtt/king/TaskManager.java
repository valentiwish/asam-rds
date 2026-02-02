package com.robotCore.common.mqtt.king;
import com.robotCore.common.mqtt.MqttGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;


/**
 * @Author likundong
 * @create 2024-06-12
 */

@Service
@Slf4j
public class TaskManager {

    @Autowired
    private ScheduledExecutorService executor;

    @Resource
    private MqttGateway mqttGateway;

    @Value("${asam.mqtt.setTopic}")
    private String setTopic;

    @Value("${asam.mqtt.retryinterval}")
    Integer retryInterval;
    @Value("${asam.mqtt.client.outId}")
    private String clientOutId;

    @Value("${asam.mqtt.retrynum}")
    private Integer maxRetries;

    private Map<Long, MqttVariableSetter> activeSetters = new ConcurrentHashMap<>();

    public void sendMqttVariable(String variable, Object value,Long qid) {
        // 创建MqttVariableSetter实例，传递当前的topic, variable, value
        log.info("qid是:{}",qid);
        MqttVariableSetter setter = new MqttVariableSetter(mqttGateway, executor, setTopic, variable, value,clientOutId,maxRetries,qid,retryInterval);
        // 提交任务到线程池执行
        activeSetters.put(qid, setter);
        setter.setOnStopCallback(setterToRemove -> {
            removeActiveSetter(setterToRemove);
            // 可能还有其他清理逻辑
        });
        executor.execute(setter);
    }

    public void requestStopForMqttVariableSetter(Long identifier) {
        MqttVariableSetter setter = activeSetters.get(identifier);
        if (setter != null) {
            setter.onSuccessReceived();
            activeSetters.remove(identifier);
            log.info("下发任务qid：{}成功，结束任务成功",identifier);
        }
    }

    public void removeActiveSetter(MqttVariableSetter setterToRemove) {
        activeSetters.values().removeIf(setter -> setter == setterToRemove);
    }

    public MqttVariableSetter getByQid(long qid){
        return activeSetters.get(qid);
    }
}
