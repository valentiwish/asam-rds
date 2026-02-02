package com.robotCore.common.config;

import com.robotCore.multiScheduling.entity.DataInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/***
 * @Description: 存放所有异步处理接口请求队列的对象
 * @Author: zhangqi
 * @Create: 2024/10/16
 */
@Component
public class RequestQueue {
    /**
     * 处理设备数据的队列，设置缓冲容量为1000
     */
    private BlockingQueue<String> equipQueue = new LinkedBlockingQueue<>(1000);

    /**
     * Influxdb数据批量存储的队列，设置缓冲容量为1000
     */
    private BlockingQueue<List<DataInfo>> influxdbQueue = new LinkedBlockingQueue<>(1000);

    public BlockingQueue<String> getEquipQueue() {
        return equipQueue;
    }

    public BlockingQueue<List<DataInfo>> getInfluxdbQueue() {
        return influxdbQueue;
    }

}
