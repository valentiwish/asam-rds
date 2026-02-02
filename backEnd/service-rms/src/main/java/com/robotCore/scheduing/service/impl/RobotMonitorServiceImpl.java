package com.robotCore.scheduing.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beans.redis.RedisUtil;
import com.entity.EntityResult;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.constant.ProtocolConstant;
import com.robotCore.common.utils.DataConvertUtil;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.service.RobotBasicInfoService;
import com.robotCore.robot.service.RobotConnectService;
import com.robotCore.scheduing.common.enums.RobotStatusEnum;
import com.robotCore.scheduing.dto.RobotLocDTO;
import com.robotCore.scheduing.service.RobotMonitorService;
import com.robotCore.scheduing.vo.RobotCountVO;
import com.robotCore.task.tcpCilent.TcpClientThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/5/30
 **/
@Slf4j
@Service
public class RobotMonitorServiceImpl implements RobotMonitorService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private RobotConnectService robotConnectService;

    @Value("${data.serverIP}")
    private String currentServerIp;


    /**
     * 获取机器人全部数量和在线数量
     *
     * @return
     */
    @Override
    public RobotCountVO getRobotOnlineInfo() {
        List<RobotBasicInfo> basicInfoServiceList = robotBasicInfoService.findListAll();
        Integer onlineCount = 0;
        RobotCountVO robotCountVO = new RobotCountVO();
        robotCountVO.setAllRobots(basicInfoServiceList.size());
        for (int i = 0; i < basicInfoServiceList.size(); i++) {
            Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
            if (basicInfoServiceList.size() > 0) {
                String currentIp = basicInfoServiceList.get(i).getCurrentIp();
                //如果机器人在线
                if (map.containsKey(currentIp + PortConstant.ROBOT_STATUS_PORT) && robotConnectService.isOnline(currentIp)) {
                    onlineCount++;
                }
            }
        }
        robotCountVO.setOnlineRobots(onlineCount);
        return robotCountVO;
    }

    /**
     * 获取机器人状态枚举的列表
     *
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    public Map<Integer, String> getRobotStatusList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<RobotStatusEnum> enumClass = RobotStatusEnum.class;
        Object[] objects = enumClass.getEnumConstants();
        Method getCode = enumClass.getMethod("getCode");
        Method getName = enumClass.getMethod("getName");
        Map<Integer, String> map = new HashMap<>();
        for (Object object : objects) {
            map.put(Integer.valueOf(getCode.invoke(object).toString()), getName.invoke(object).toString());
        }
        return map;
    }

    /**
     * 抢占机器人控制权
     *
     * @param robots
     * @return
     */
    @Override
    public boolean acquireControl(List<RobotBasicInfo> robots, UserVO user) {
        String convertString = "{\"nick_name\":\"" + user.getLoginName() + "\"}";
        String data = DataConvertUtil.convertStringToHex(convertString);
        String dataLength = Integer.toHexString(data.length() / 2);
        String instruction = "5A010001000000" + dataLength + "0FA5000000000000" + data;
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        String robotIp = robots.get(0).getCurrentIp();
        if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_CONFIGURATION_PORT)) {
            //连接机器人配置端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, instruction);
        return result.isSuccess();
    }

    /**
     * 释放机器人控制权
     *
     * @param robots
     * @return
     */
    @Override
    public boolean releaseControl(List<RobotBasicInfo> robots) {
        String robotIp = robots.get(0).getCurrentIp();
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_CONFIGURATION_PORT)) {
            //连接机器人配置端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_CONFIGURATION_PORT, ProtocolConstant.RELEASE_CONTROL);
        return result.isSuccess();
    }

    /**
     * 获取机器人位置信息，用于重定位
     *
     * @param robotIp
     */
    @Override
    public boolean getRobotLocInfo(String robotIp) {
        //查询机器人的置信度
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_STATUS_PORT, ProtocolConstant.ROBOT_STATUS_LOC);
        return result.isSuccess();
    }

    /**
     * 机器人重定位
     *
     * @param robots
     * @return
     */
    @Override
    public boolean relocation(List<RobotBasicInfo> robots) {
        String robotIp = robots.get(0).getCurrentIp();
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_CONTROL_PORT)) {
            //连接机器人配置端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_CONTROL_PORT);
        }
        if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_STATUS_PORT)) {
            //连接机器人状态端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_STATUS_PORT);
        }
        if (getRobotLocInfo(robotIp)) {
            String locInfoRes = (String) redisUtil.stringWithGet("robot_status_loc_res");
            RobotLocDTO robotLoc = JSON.parseObject(locInfoRes, RobotLocDTO.class);
            String robotLocStr = JSONObject.toJSONString(robotLoc);
            String data = DataConvertUtil.convertStringToHex(robotLocStr);
            String dataLength = Integer.toHexString(data.length() / 2);
            String instruction = "5A010001000000" + dataLength + "07D2000000000000" + data;
            EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_CONTROL_PORT, instruction);
            return result.isSuccess();
        }
        return false;
    }

    /**
     * 确认定位正确
     *
     * @param robots
     * @return
     */
    @Override
    public boolean confirmLocation(List<RobotBasicInfo> robots) {
        String robotIp = robots.get(0).getCurrentIp();
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_CONTROL_PORT)) {
            //连接机器人配置端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_CONTROL_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_CONTROL_PORT, ProtocolConstant.CONFIRM_LOC);
        return result.isSuccess();
    }

    /**
     * 继续当前导航
     *
     * @param robots
     * @return
     */
    @Override
    public boolean continueNavigation(List<RobotBasicInfo> robots) {
        String robotIp = robots.get(0).getCurrentIp();
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人导航端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, ProtocolConstant.CONTINUE_NAVIGATION);
        return result.isSuccess();
    }

    /**
     * 暂停当前导航
     *
     * @param robots
     * @return
     */
    @Override
    public boolean pauseNavigation(List<RobotBasicInfo> robots) {
        String robotIp = robots.get(0).getCurrentIp();
        Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
        //如果当前ip和端口的的机器人已经连接
        if (!map.containsKey(robots.get(0).getCurrentIp() + PortConstant.ROBOT_NAVIGATION_PORT)) {
            //连接机器人导航端口
            robotBasicInfoService.connectTcp(robotIp, PortConstant.ROBOT_NAVIGATION_PORT);
        }
        EntityResult result = TcpClientThread.sendHexMsg(robotIp, PortConstant.ROBOT_NAVIGATION_PORT, ProtocolConstant.PAUSE_NAVIGATION);
        return result.isSuccess();
    }

}
