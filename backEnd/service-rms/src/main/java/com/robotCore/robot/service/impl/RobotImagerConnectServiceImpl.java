package com.robotCore.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robotCore.robot.entity.RobotConnect;
import com.robotCore.robot.entity.RobotImagerConnect;
import com.robotCore.robot.mapper.RobotConnectDao;
import com.robotCore.robot.mapper.RobotImagerConnectDao;
import com.robotCore.robot.service.RobotConnectService;
import com.robotCore.robot.service.RobotImagerConnectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.robotCore.common.constant.Constant.STATE_VALID;


/**
 * @Des: 热成像仪连接
 * @author: lzy
 * @date: 2023/9/25
 **/
@Slf4j
@Service
public class RobotImagerConnectServiceImpl extends ServiceImpl<RobotImagerConnectDao, RobotImagerConnect> implements RobotImagerConnectService {

    /**
     * 获取添加的热成像仪设备列表
     * @return
     */
    @Override
    public List<RobotImagerConnect> findList() {
        QueryWrapper<RobotImagerConnect> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RobotImagerConnect::getState, STATE_VALID).orderByDesc(RobotImagerConnect::getId);
        return list(wrapper);//返回list类型对象列表
    }
}


