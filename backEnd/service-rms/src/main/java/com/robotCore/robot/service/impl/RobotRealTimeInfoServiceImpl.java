package com.robotCore.robot.service.impl;

import com.alibaba.fastjson.JSON;
import com.beans.redis.RedisUtil;
import com.entity.EntityResult;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.utils.DataConvertUtil;
import com.robotCore.robot.service.RobotConnectService;
import com.robotCore.robot.service.RobotRealTimeInfoService;
import com.robotCore.task.core.ProtocolConvert;
import com.robotCore.task.tcpCilent.TcpClientThread;
import com.utils.https.ServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Des: 机器人状态信息（状态信息、JSON、树形结构）
 * @author: zxl
 * @date: 2023/4/26
 **/
@Slf4j
@Service
public class RobotRealTimeInfoServiceImpl implements RobotRealTimeInfoService {

    @Autowired
    private RobotConnectService robotConnectService;

    /**
     * 根据ip获取机器人实时状态信息
     * @param robotIp
     */
    @Override
    public boolean getRealTimeStatusInfo(String robotIp) {
//        robotConnectService.connectTcp(robotIp, PortConstant.ROBOT_PUSH_PORT);
        String robotType = robotConnectService.getRobotType(robotIp);
        return ProtocolConvert.connect(robotType, robotIp, PortConstant.ROBOT_PUSH_PORT);
    }

    /**
     * 根据ip配置机器人推送端口（设置消息推送的时间间隔）
     * @param robotIp
     */
    @Override
    public boolean pushMsgConfig(String robotIp) {
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        String convertString = "{\"interval\":2500}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        String instruction = "5A010001000000" + dataLength + "0FFB000000000000" + data;
        if(!map.containsKey(robotIp + PortConstant.ROBOT_CONFIGURATION_PORT)){
            //连接机器人配置端口
            robotConnectService.connectTcp(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, instruction);
        return result.isSuccess();
    }

    /**
     * 根据ip断开机器人实时状态信息
     * @param robotIp
     */
    @Override
    public void disRealTimeStatusInfo(String robotIp) {
        try {
            Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
            if (map.containsKey(robotIp + PortConstant.ROBOT_CONFIGURATION_PORT)) {
                //配置完机器人推送端口，则关闭连接
                EntityResult result = TcpClientThread.interruptThread(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT);
                log.info("关闭配置机器人推送端口，TCP客户端断开结果：{}", JSON.toJSON(result));
            }
            if (map.containsKey(robotIp + PortConstant.ROBOT_PUSH_PORT)) {
                EntityResult result = TcpClientThread.interruptThread(robotIp, PortConstant.ROBOT_PUSH_PORT);
                log.info("关闭获取机器人实时状态信息端口，TCP客户端断开结果：{}", JSON.toJSON(result));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
