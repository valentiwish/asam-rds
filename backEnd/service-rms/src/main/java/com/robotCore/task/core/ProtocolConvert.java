package com.robotCore.task.core;

import com.alibaba.fastjson.JSON;
import com.entity.EntityResult;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.constant.ProtocolConstant;
import com.robotCore.robot.service.RobotConnectService;
import com.robotCore.task.tcpCilent.TcpClientThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/11/1
 **/
@Slf4j
@Component
public class ProtocolConvert {

    public static EntityResult sendHexMsg(String robotType, String robotIp, Integer robotPort, String instruction) {
        if (robotType.equals("仙工机器人") || robotType.equals("西安航天机器人")) {
            Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
            if (!map.containsKey(robotIp + robotPort)) {
                connect(robotType, robotIp, robotPort);
                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //如果发送指令成功，则连接成功，然后推送机器人的基本信息给前端处理
            return TcpClientThread.sendHexMsg(robotIp, robotPort, instruction);
        } else {
            return null;
        }
    }

    public static boolean connect(String robotType, String robotIp, Integer robotPort) {
        if (robotType.equals("仙工机器人") || robotType.equals("西安航天机器人")){
            EntityResult connectResult = null;
            try {
                connectResult = TcpClientThread.start(robotIp, robotPort);
                //防止机器人没有连接成功，就发送机器人指令
                Thread.sleep(100L);
//                log.info("TCP客户端启动结果：{}", JSON.toJSON(connectResult));
                return connectResult.isSuccess();
            } catch (Exception e) {
                log.error("nettyStart:{}", e.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean disConnect(String robotType, String robotIp) {
        if (robotType.equals("仙工机器人") || robotType.equals("西安航天机器人")){
            EntityResult result1 = TcpClientThread.interruptThread(robotIp, PortConstant.ROBOT_STATUS_PORT);
            EntityResult result2 = TcpClientThread.interruptThread(robotIp, PortConstant.ROBOT_OTHER_PORT);
            EntityResult result3 = TcpClientThread.interruptThread(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
            EntityResult result4 = TcpClientThread.interruptThread(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT);
            EntityResult result5 = TcpClientThread.interruptThread(robotIp, PortConstant.ROBOT_PUSH_PORT);
            EntityResult result6 = TcpClientThread.interruptThread(robotIp, PortConstant.ROBOT_CONTROL_PORT);
            return result1.isSuccess() && result2.isSuccess() && result3.isSuccess() && result4.isSuccess() && result5.isSuccess() && result6.isSuccess();
        } else {
            return false;
        }
    }
}
