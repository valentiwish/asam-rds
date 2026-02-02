package com.robotCore.task.tcpCilent;

import com.beans.tools.PropertiesUtil;
import com.entity.EntityResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @Description: 描述
 * @Author: Created by lkd on 2022/9/15.
 */
@Component
@EnableScheduling
@Slf4j
public class ReconnectTask {

//    @Scheduled(cron = "*/10 * * * * ?")//每10查看机器人是否处于链接状态，如果不在的话，1.可以推送提示机器人已断开链接 2。或者尝试自动链接机器人
    public void reconnectTask() {
        try {
            String ip1 = PropertiesUtil.getString("socket.clientIp1");
            int port1 = PropertiesUtil.getIntByKey("socket.clientport1");
            Map<Integer, Thread> map = TcpClientThread.getThreadNetty();
            if(!map.containsKey(port1)){
                EntityResult varReuslt = TcpClientThread.start(ip1,port1);
            }
        } catch (Exception e) {
            log.error("nettyStart:{}", e.getMessage());
        }
    }

}