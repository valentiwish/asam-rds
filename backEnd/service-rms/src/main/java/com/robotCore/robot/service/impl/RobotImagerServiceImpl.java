package com.robotCore.robot.service.impl;

import com.robotCore.common.constant.Constant;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.entity.RobotImager;
import com.robotCore.robot.entity.RobotImagerConnect;
import com.robotCore.robot.entityVo.ImagerByAddVO;
import com.robotCore.robot.entityVo.ImagersAddVO;
import com.robotCore.robot.mapper.RobotImagerDao;
import com.robotCore.robot.service.RobotConnectService;
import com.robotCore.robot.service.RobotImagerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.page.BasePage;
import com.page.JPAPage;
import com.robotCore.robot.service.RobotImagerConnectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: Aven
 * @Create: 2023/9/13
 */
@Slf4j
@Service
public class RobotImagerServiceImpl extends ServiceImpl<RobotImagerDao, RobotImager> implements RobotImagerService {
    public static final int STATE_VALID = 1;

    @Autowired
    private RobotConnectService robotConnectService;

    @Autowired
    private RobotImagerConnectService robotImagerConnectService;

    @Override
    public IPage<RobotImager> findPageList(JPAPage varBasePage,String robotName, String currentIp, String imagerName) {
        QueryWrapper<RobotImager> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(robotName), RobotImager::getRobotName, robotName);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(currentIp), RobotImager::getCurrentIp, currentIp);
        wrapper.lambda().like(ObjectUtil.isNotEmpty(imagerName), RobotImager::getImagerName, imagerName);
        wrapper.lambda().eq(RobotImager::getState, STATE_VALID);
        wrapper.lambda().orderByDesc(RobotImager::getRobotName);
        IPage<RobotImager> varPage = BasePage.getMybaitsPage(varBasePage);
        return page(varPage, wrapper);
    }

    @Override
    public boolean save(RobotImager model, Long userId, String userName) {
        if (model.getId() == null) {
            model.setCreateId(userId);
            model.setCreateUser(userName);
            model.setCreateTime(new Timestamp(new Date().getTime()));
        } else {
            model.setUpdateId(userId);
            model.setUpdateUser(userName);
            model.setUpdateTime(new Timestamp(new Date().getTime()));
        }
        return saveOrUpdate(model);
    }

    @Override
    public List<RobotImager> findListAll() {
        QueryWrapper<RobotImager> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RobotImager::getState, STATE_VALID).orderByDesc(RobotImager::getId);
        //返回list类型对象列表
        return list(wrapper);
    }

    @Override
    public List<ImagerByAddVO> getAddImager() {
        List<RobotImagerConnect> robotImagerConnects = robotImagerConnectService.findList();//获取已经连接的热成像仪列表
        List<RobotImager> robotImagers = findListAll();
        List<String> imagerIpList = new ArrayList<>();
        for (int j = 0; j < robotImagers.size(); j++) {
            RobotImager robotImager = robotImagers.get(j);
            imagerIpList.add(robotImager.getCurrentIp());
        }
        //需要添加的热成像仪列表
        List<ImagerByAddVO> addList = new ArrayList<>();
        for (RobotImagerConnect robotImagerConnect : robotImagerConnects) {
            //获取已连接的热成像仪的IP
            String connectIp = robotImagerConnect.getCurrentIp();
            //如果热成像仪列表中不存在已经添加的设备，则获取该（当前）已连接的热成像仪设备信息
            if (!imagerIpList.contains(connectIp)) {
                ImagerByAddVO vo = new ImagerByAddVO();
                vo.setCurrentIp(robotImagerConnect.getCurrentIp());
                vo.setId(robotImagerConnect.getId());
                vo.setImagerName(robotImagerConnect.getImagerName());
                addList.add(vo);
            }
        }
        return addList;
    }

    @Override
    public RobotImager setImagerValue(ImagerByAddVO imagerByAddVO, ImagersAddVO imagersAddVO) {
        RobotImager model = new RobotImager();
        model.setId(imagerByAddVO.getId());
        model.setCurrentIp(imagerByAddVO.getCurrentIp());
        model.setImagerName(imagerByAddVO.getImagerName());
        model.setState(Constant.STATE_VALID);
        //设置热成像仪属于哪个机器人
//        model.setVehicleId(imagersAddVO.getVehicleId());
        return model;
    }

    @Override
    public List<RobotImager> findList(String id, String imagerName) {
        QueryWrapper<RobotImager> wrapper = new QueryWrapper<>();
        wrapper.lambda().ne(ObjectUtil.isNotEmpty(id), RobotImager::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(imagerName), RobotImager::getImagerName, imagerName);
        wrapper.lambda().eq(RobotImager::getState, STATE_VALID).orderByDesc(RobotImager::getId);

        return list(wrapper);
    }
}


