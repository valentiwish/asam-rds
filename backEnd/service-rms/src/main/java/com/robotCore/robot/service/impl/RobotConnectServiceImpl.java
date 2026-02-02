package com.robotCore.robot.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.EntityResult;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.constant.ProtocolConstant;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotConnect;
import com.robotCore.robot.mapper.RobotConnectDao;
import com.robotCore.robot.service.RobotConnectService;
import com.robotCore.scheduing.vo.RobotConnectVO;
import com.robotCore.task.tcpCilent.TcpClientThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.robotCore.common.constant.Constant.STATE_VALID;


/**
 * @Des: 机器人连接
 * @author: zxl
 * @date: 2023/4/25
 **/
@Slf4j
@Service
public class RobotConnectServiceImpl extends ServiceImpl<RobotConnectDao, RobotConnect> implements RobotConnectService {
    /**
     * 获取添加的机器人设备列表
     * @return
     */
    @Override
    public List<RobotConnect> findList() {
        QueryWrapper<RobotConnect> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RobotConnect::getState, STATE_VALID).orderByDesc(RobotConnect::getId);
        return list(wrapper);//返回list类型对象列表
    }

    @Override
    public List<RobotConnect> listByIp(String robotIp) {
        QueryWrapper<RobotConnect> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(robotIp), RobotConnect::getCurrentIp, robotIp);
        wrapper.lambda().eq(RobotConnect::getState, STATE_VALID).orderByDesc(RobotConnect::getId);
        return list(wrapper);
    }

    @Override
    public String getRobotType(String robotIp) {
        QueryWrapper<RobotConnect> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(robotIp), RobotConnect::getCurrentIp, robotIp);
        wrapper.lambda().eq(RobotConnect::getState, STATE_VALID).orderByDesc(RobotConnect::getId);
        List<RobotConnect> list = list(wrapper);
        if (list.size() > 0) {
            return list.get(0).getRobotType();
        } else {
            return null;
        }
    }

    @Override
    public List<RobotConnectVO> dataInit(List<RobotConnect> list) {
        List<RobotConnectVO> voList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RobotConnect robotConnect = list.get(i);
            RobotConnectVO vo = new RobotConnectVO();
            Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
            EntityResult result = TcpClientThread.sendHexMsg(robotConnect.getCurrentIp(), PortConstant.ROBOT_STATUS_PORT, ProtocolConstant.ROBOT_BASIC_INFO);
            vo.setOnline(map.containsKey(robotConnect.getCurrentIp() + PortConstant.ROBOT_STATUS_PORT) && result.isSuccess() && isOnline(robotConnect.getCurrentIp()));
            vo.setId(robotConnect.getId());
            vo.setVehicleId(robotConnect.getVehicleId());
            vo.setCurrentIp(robotConnect.getCurrentIp());
            vo.setRobotNote(robotConnect.getRobotNote());
            vo.setCurrentMap(robotConnect.getCurrentMap());
            vo.setVersion(robotConnect.getVersion());
            vo.setTypeName(robotConnect.getRobotType());
            voList.add(vo);
        }
        return voList;//返回list类型对象列表
    }

    /**
     * 根据ip和端口号连接机器人
     * @param robotIp
     * @param port
     * @return
     */
    @Override
    public boolean connectTcp(String robotIp, int port) {
        EntityResult result = null;
        try {
            result = TcpClientThread.start(robotIp, port);
            //防止机器人没有连接成功，就发送机器人指令
            Thread.sleep(300L);
            log.info("TCP客户端启动结果：{}", JSON.toJSON(result));
        } catch (Exception e) {
            log.error("nettyStart:{}", e.getMessage());
        }
        if (result != null) {
            return  result.isSuccess();
        } else {
            return false;
        }
    }

    @Override
    public RobotConnect findByIp(String robotIp) {
        QueryWrapper<RobotConnect> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(robotIp), RobotConnect::getCurrentIp, robotIp);
        wrapper.lambda().eq(RobotConnect::getState, STATE_VALID).orderByDesc(RobotConnect::getId);
        List<RobotConnect> list = list(wrapper);
        return list.get(0);
    }

    @Override
    public boolean save(RobotConnect model, Long userId, String userName) {
        if(model.getId()==null){
            model.setCreateId(userId);
            model.setCreateUser(userName);
            model.setCreateTime(new Timestamp(new Date().getTime()));
        }else{
            model.setUpdateId(userId);
            model.setUpdateUser(userName);
            model.setUpdateTime(new Timestamp(new Date().getTime()));
        }
        return saveOrUpdate(model);
    }

    @Override
    public boolean isOnline(String robotIp) {
        try {
            InetAddress address = InetAddress.getByName(robotIp);
            return address.isReachable(1000);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
