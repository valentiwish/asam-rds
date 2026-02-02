package com.robotCore.scheduing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beans.redis.RedisUtil;
import com.robotCore.common.constant.Constant;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.robot.entity.RobotBasicInfo;
import com.robotCore.robot.mapper.RobotBasicInfoDao;
import com.robotCore.scheduing.common.enums.*;
import com.robotCore.scheduing.common.utils.DataUtil;
import com.robotCore.scheduing.dto.AlgorithmResResultDTO;
import com.robotCore.scheduing.dto.RobotLockPathAndPoint;
import com.robotCore.scheduing.service.RobotInfoService;
import com.robotCore.scheduing.service.RobotWaybillService;
import com.robotCore.scheduing.vo.RobotInfoVO;
import com.robotCore.scheduing.vo.RobotMonitorVO;
import com.robotCore.task.tcpCilent.TcpClientThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.robotCore.common.constant.Constant.STATE_VALID;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/7
 **/
@Slf4j
@Service
public class RobotInfoServiceImpl extends ServiceImpl<RobotBasicInfoDao, RobotBasicInfo> implements RobotInfoService {
    @Autowired
    private RobotWaybillService robotWaybillService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<RobotBasicInfo> findList(Long id, String name) {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(id), RobotBasicInfo::getId, id);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(name), RobotBasicInfo::getVehicleId, name);
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByDesc(RobotBasicInfo::getId);
        return list(wrapper);
    }

    @Override
    public List<RobotBasicInfo> findListByGroupName(String groupName) {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(groupName), RobotBasicInfo::getGroupName, groupName);
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByDesc(RobotBasicInfo::getId);
        return list(wrapper);
    }

    @Override
    public IPage<RobotInfoVO> findPageList(IPage<RobotBasicInfo> varPage, RobotBasicInfo robotInfoVO) throws InterruptedException {
        QueryWrapper<RobotBasicInfo> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(robotInfoVO)) {
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotInfoVO.getVehicleId()), RobotBasicInfo::getVehicleId, robotInfoVO.getVehicleId());
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotInfoVO.getCurrentIp()), RobotBasicInfo::getCurrentIp, robotInfoVO.getCurrentIp());
            wrapper.lambda().like(ObjectUtil.isNotEmpty(robotInfoVO.getGroupName()), RobotBasicInfo::getGroupName, robotInfoVO.getGroupName());
        }
        wrapper.lambda().eq(RobotBasicInfo::getState, STATE_VALID).orderByDesc(RobotBasicInfo::getId);
        List<RobotBasicInfo> list = list(wrapper);
//        IPage<RobotBasicInfo> basicInfoIPage = this.baseMapper.selectPage(varPage,wrapper);
        IPage<RobotInfoVO> page = new Page<>();
        page.setCurrent(varPage.getCurrent());
        page.setSize(varPage.getSize());
        page.setTotal(list.size());
        page.setRecords(dataInit(list));
        return page;
    }

    /**
     * 获取机器人模块信息，并处理之后返回机器人列表数据
     *
     * @param list
     * @return
     */
    @Override
    public List<RobotInfoVO> dataInit(List<RobotBasicInfo> list) throws InterruptedException {
        List<RobotInfoVO> robotInfoVOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RobotBasicInfo robotBasicInfo = list.get(i);
            RobotInfoVO vo = new RobotInfoVO();
            //获取锁定的路径和点位信息
            RobotLockPathAndPoint robotLockAndPoint = robotWaybillService.getRobotLockAndPoint(robotBasicInfo.getVehicleId());
            if (robotLockAndPoint == null) {
                vo.setLockPoint("无");
                vo.setLockPath("无");
            } else {
                vo.setLockPoint(robotLockAndPoint.getLockedPoint());
                AlgorithmResResultDTO.Path lockedPath = robotLockAndPoint.getLockedPath();
                vo.setLockPath(lockedPath.getStartPoint() + "," + lockedPath.getEndPoint());
            }
            //由于在机器人实时推送时，设置消息推送的时间间隔为300ms，因此在这里加了一个延迟
            Map<String, RobotMonitorVO> robotInfoVOMap = (Map<String, RobotMonitorVO>) redisUtil.stringWithGet("robot_push_less_res");
            RobotMonitorVO robotMonitorVO = robotInfoVOMap.get(robotBasicInfo.getVehicleId());
            vo.setId(robotBasicInfo.getId());
            vo.setVehicleId(robotBasicInfo.getVehicleId());
            vo.setCurrentIp(robotBasicInfo.getCurrentIp());
            vo.setGroupName(robotBasicInfo.getGroupName());
            vo.setLeisureState(robotBasicInfo.getLeisure());
            vo.setBatteryLevel(robotMonitorVO.getBatteryLevel() + "%");
            vo.setConfidence(DataUtil.format(robotMonitorVO.getConfidence()));
            vo.setCurrentMap(robotBasicInfo.getCurrentMap());
            vo.setRobotType(robotBasicInfo.getRobotType());
            vo.setControlState(Objects.requireNonNull(ControlStatusEnum.getByName(robotMonitorVO.getControlStatus())).getCode());
            vo.setLocationState(Objects.requireNonNull(RobotRelocStatusEnum.getByName(robotMonitorVO.getRelocStatus())).getCode());
            vo.setNavigationState(Objects.requireNonNull(NavigationStatusEnum.getByName(robotMonitorVO.getTaskStatus())).getCode());
            if (ControlStatusEnum.SEIZED_CONTROL.getName().equals(robotMonitorVO.getControlStatus())
                    && robotMonitorVO.getBatteryLevel() > robotBasicInfo.getBatteryLevel()
                    //机器人处于空闲状态
                    && robotBasicInfo.getLeisure() == 1) {
                if (Objects.equals(robotBasicInfo.getOrderState(), RobotStatusEnum.ORDER_AVAILABLE.getCode())) {
                    Map<String, Thread> map = TcpClientThread.getIpThreadNetty();
                    //如果已经连接机器人，则获取机器人服务端返回的实时信息
                    if (map.containsKey(robotBasicInfo.getCurrentIp() + PortConstant.ROBOT_STATUS_PORT)) {
                        //机器人在电量下降到此值时，需要前往充电桩进行充电，并且在充电过程不可以接单。
                        if (robotMonitorVO.getBatteryLevel() <= robotBasicInfo.getChargeOnly()) {
                            vo.setOrderState(Constant.NOT_ORDER_AVAILABLE_NUMBER);
                        }
                        //chargeOnly值为-1，表示机器人在电量耗尽后才会前往充电，并且过程中不可以接单。
                        if (robotBasicInfo.getChargeOnly() == -1 && robotMonitorVO.getBatteryLevel() < robotBasicInfo.getChargeNeed()) {
                            vo.setOrderState(Constant.NOT_ORDER_AVAILABLE_NUMBER);
                        }
                        //机器人电量大于chargerOnly，但是小于chargeNeed,此时可以接单
                        if (robotBasicInfo.getChargeOnly() < robotMonitorVO.getBatteryLevel()
                                && robotMonitorVO.getBatteryLevel() <= robotBasicInfo.getChargeNeed()) {
                            vo.setOrderState(Constant.ORDER_AVAILABLE_NUMBER);
                        }
                        //chargeNeed值为-1，表示机器人在电量耗尽后才会前往充电，并且过程中可以接单
                        if (robotBasicInfo.getChargeNeed() == -1) {
                            vo.setOrderState(Constant.ORDER_AVAILABLE_NUMBER);
                        }
                        //chargedOk机器人在充电时接到订单，只有电量充到此值时，才可以离开充电桩去接单。
                        if (robotMonitorVO.getBatteryLevel() >= robotBasicInfo.getChargeOk()) {
                            vo.setOrderState(Constant.ORDER_AVAILABLE_NUMBER);
                        }
                        vo.setOrderState(Constant.ORDER_AVAILABLE_NUMBER);
                    } else {
                        //机器人不在线，设置机器人不可以接单
                        vo.setOrderState(Constant.NOT_ORDER_AVAILABLE_NUMBER);
                    }
                } else {
                    vo.setOrderState(Constant.NOT_ORDER_AVAILABLE_NUMBER);
                }
            } else if (ControlStatusEnum.UN_PREEMPTED_CONTROL.getName().equals(robotMonitorVO.getControlStatus())
                    && robotMonitorVO.getBatteryLevel() > robotBasicInfo.getBatteryLevel()
                    //机器人处于空闲状态
                    && robotBasicInfo.getLeisure() == 1) {
                //无人抢占控制权，DMS可以接单
                vo.setOrderState(Constant.ORDER_AVAILABLE_NUMBER);
            } else {
                //控制权被抢占，不可以接单
                vo.setOrderState(Constant.NOT_ORDER_AVAILABLE_NUMBER);
            }

            /*
             * 构造机器人当前锁定站点信息 (leo add)
             */
            String pointsToString =
                    Optional.ofNullable(list.get(i))
                            .map(RobotBasicInfo::getVehicleId)
                            .map(robotWaybillService::getLockedPointsByRobot)
                            .orElse(Collections.emptyList())
                            .stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(","));
            vo.setLockPoint(pointsToString);

            robotInfoVOList.add(vo);
        }
        return robotInfoVOList;
    }
}