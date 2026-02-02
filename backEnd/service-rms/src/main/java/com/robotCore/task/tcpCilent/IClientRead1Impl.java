package com.robotCore.task.tcpCilent;

import com.alibaba.fastjson.JSON;
import com.beans.redis.RedisUtil;
import com.robotCore.common.base.service.PusherLiteService;
import com.robotCore.common.base.vo.BusinessDataVO;
import com.robotCore.common.utils.DataConvertUtil;
import com.robotCore.robot.dto.*;
import com.robotCore.scheduing.common.enums.ControlStatusEnum;
import com.robotCore.scheduing.common.enums.JackStatusEnum;
import com.robotCore.scheduing.common.enums.RobotRelocStatusEnum;
import com.robotCore.scheduing.common.enums.NavigationStatusEnum;
import com.robotCore.scheduing.common.utils.DataUtil;
import com.robotCore.scheduing.common.utils.TimeFormatUtil;
import com.robotCore.scheduing.dto.*;
import com.robotCore.scheduing.vo.RobotMonitorVO;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author zxl
 * @create 2023/4/21 16:59
 */
//@Qualifier("IClientRead1")
@Service
@Slf4j
public class IClientRead1Impl implements IClientRead1 {
    @Autowired
    private PusherLiteService pusherLiteService;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${data.serverIP}")
    private String currentServerIp;

    @Override
    public void parseData(ChannelHandlerContext ctx, String msg) {

        //在界面展示的全部机器人的实时信息,每次从数据库中
        Map<String, RobotMonitorVO> map;
        if (redisUtil.stringWithGet("robot_push_less_res") == null) {
            map = new HashMap<>();
        } else {
            map = (Map<String, RobotMonitorVO>) redisUtil.stringWithGet("robot_push_less_res");
        }

        //获取redis数据库中存储的地图信息
        Map<String, String> maps;
        if (redisUtil.stringWithGet("robot_config_downloadmap_res") == null) {
            maps = new HashMap<>();
        } else {
            maps = (Map<String, String>) redisUtil.stringWithGet("robot_config_downloadmap_res");
        }

        //获取redis数据库中存储的机器人任务链信息
        Map<String, String> taskListMaps;
        if (redisUtil.stringWithGet("robot_tasklist_status_res") == null) {
            taskListMaps = new HashMap<>();
        } else {
            taskListMaps = (Map<String, String>) redisUtil.stringWithGet("robot_tasklist_status_res");
        }

        //将机器人返回的hex字符串转换成byte数组
        byte[] bytes = DataConvertUtil.HexStringToBytes(msg);
        //推送机器人信息
        if (DataConvertUtil.byteToHex(bytes[0]).equals("2A") && DataConvertUtil.byteToHex(bytes[1]).equals("F8")) {
            String message = DataConvertUtil.BytesToString(bytes);
//            log.info("根据协议和端口号返回得到的机器人信息是：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_status_info_res", data);
        }

        if (DataConvertUtil.byteToHex(bytes[0]).equals("33") && DataConvertUtil.byteToHex(bytes[1]).equals("0A")) {
            String message = DataConvertUtil.BytesToString(bytes);
//            log.info("根据协议和端口号返回得到的路径导航的响应：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_task_gotargetlist_req", data);
        }

        //执行任务链
        if (DataConvertUtil.byteToHex(bytes[0]).equals("51") && DataConvertUtil.byteToHex(bytes[1]).equals("44")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号返回得到的机器人执行任务链信息是：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
        }

        //执行任务链
//        if (true) {
//            String message = DataConvertUtil.BytesToString(bytes);
//            log.info("根据协议和端口号返回得到的机器人执行任务链信息是：{}", message);
//            int beginIndex = message.indexOf("{");
//            String data = message.substring(beginIndex);
//        }

        //推送机器人运行信息
        if (DataConvertUtil.byteToHex(bytes[0]).equals("2A") && DataConvertUtil.byteToHex(bytes[1]).equals("FA")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号返回得到的机器人运行信息是：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_status_run_res", data);
        }

        //推送机器人位置信息
        if (DataConvertUtil.byteToHex(bytes[0]).equals("2A") && DataConvertUtil.byteToHex(bytes[1]).equals("FC")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号返回得到的机器人位置信息是：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_status_loc_res", data);
        }

        //推送机器人电池状态信息
        if (DataConvertUtil.byteToHex(bytes[0]).equals("2A") && DataConvertUtil.byteToHex(bytes[1]).equals("FF")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号返回得到的机器人电池状态是：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_status_battery_res", data);
        }

        //配置机器人推送端口
        if (DataConvertUtil.byteToHex(bytes[0]).equals("37") && DataConvertUtil.byteToHex(bytes[1]).equals("0B")) {
            String message = DataConvertUtil.BytesToString(bytes);
//            log.info("根据协议和端口号配置机器人推送端口：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            RobotPushConfigResDTO dto = JSON.parseObject(data, RobotPushConfigResDTO.class);
            if (dto.getRetCode() != 0) {
                log.info("配置机器人推送端口失败,retCode：{}，errMsg:{}", dto.getRetCode(), dto.getErrMsg());
            } else {
                log.info("配置机器人推送端口成功");
            }
        }

        //推送机器人状态信息（机器人推送API推送的数据）
        if (DataConvertUtil.byteToHex(bytes[0]).equals("4B") && DataConvertUtil.byteToHex(bytes[1]).equals("65")) {
            String message = DataConvertUtil.BytesToString(bytes);
//            log.info("根据协议和端口号返回得到的机器人状态信息是：{}", message);
            BusinessDataVO businessDataVO = new BusinessDataVO();
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            RobotStatusInfoResDTO dto = JSON.parseObject(data, RobotStatusInfoResDTO.class);
            //推送所有机器人的x，y,以及电池和置信度信息
            RobotMonitorVO robotMonitorVO = new RobotMonitorVO();
            robotMonitorVO.setVehicleId(dto.getVehicleId());
            Object[] diArr = dto.getDI();
            ArrayList<String> dis = new ArrayList<>();
            Map<String, String> disMap = new HashMap<>();
            for (int i = 0; i < dto.getDI().length; i++) {
                DiOrDoDTO diDto = JSON.parseObject(diArr[i].toString(), DiOrDoDTO.class);
                String str = diDto.getId() + ":" + diDto.getStatus();
                dis.add(str);
                disMap.put(Integer.toString(diDto.getId()), Integer.toString(diDto.getStatus()));
            }
            robotMonitorVO.setDI(dis.toString());
            Object[] doArr = dto.getDO();
            ArrayList<String> dos = new ArrayList<>();
            Map<String, String> dosMap = new HashMap<>();
            for (int i = 0; i < dto.getDO().length; i++) {
                DiOrDoDTO doDto = JSON.parseObject(doArr[i].toString(), DiOrDoDTO.class);
                String str = doDto.getId() + ":" + doDto.getStatus();
                dos.add(str);
                dosMap.put(Integer.toString(doDto.getId()), Integer.toString(doDto.getStatus()));
            }
            robotMonitorVO.setDO(dos.toString());

            //设置di、do数组
            robotMonitorVO.setDisMap(disMap.toString());
            robotMonitorVO.setDosMap(dosMap.toString());
            robotMonitorVO.setRobotNote(dto.getRobotNote());
            robotMonitorVO.setCurrentMap(dto.getCurrentMap());
            robotMonitorVO.setCurrentMapMd5(dto.getCurrentMapMd5());
            robotMonitorVO.setX(dto.getX());
            robotMonitorVO.setY(dto.getY());
            robotMonitorVO.setAngle(DataUtil.format(Float.parseFloat(dto.getAngle()) / Math.PI * 180) + "°");
            robotMonitorVO.setOdo(DataUtil.format(dto.getOdo() / 1000) + "km");
            robotMonitorVO.setTodayOdo(DataUtil.format(dto.getTodayOdo()) + "m");
            robotMonitorVO.setTotalTime(TimeFormatUtil.formatTime((long) dto.getTotalTime()));
            robotMonitorVO.setTime(TimeFormatUtil.formatTime((long) dto.getTime()));
            robotMonitorVO.setBlocked(dto.isBlocked());
            robotMonitorVO.setVx(DataUtil.format(dto.getVx()) + "m/s");
            robotMonitorVO.setVy(DataUtil.format(dto.getVy()) + "m/s");
            robotMonitorVO.setW(DataUtil.format(dto.getW()) + "°/s");
            robotMonitorVO.setCurrentStation(dto.getCurrentStation());
            robotMonitorVO.setBrake(dto.isBrake());
            robotMonitorVO.setVoltage(DataUtil.format(dto.getVoltage()) + "V");
            robotMonitorVO.setCurrent(DataUtil.format(dto.getCurrent()) + "A");
            if (dto.getRelocStatus() != null) {
                robotMonitorVO.setRelocStatus(Objects.requireNonNull(RobotRelocStatusEnum.getByCode(dto.getRelocStatus())).getName());
            }
            robotMonitorVO.setSoftEmc(dto.isSoftEmc());
            robotMonitorVO.setConfidence(dto.getConfidence());
            robotMonitorVO.setBatteryLevel(Float.parseFloat(DataUtil.format(dto.getBatteryLevel() * 100)));
            robotMonitorVO.setCharging(dto.isCharging());
            robotMonitorVO.setTaskStatus(Objects.requireNonNull(NavigationStatusEnum.getByCode(dto.getTaskStatus())).getName());
            robotMonitorVO.setJackStatus(Objects.requireNonNull(JackStatusEnum.getByCode(dto.getJack().getJackState())).getName());
            if (dto.getCurrentLock().isLocked()) {
                //调度系统登录的用户名是admin
                if (dto.getCurrentLock().getIp().equals(currentServerIp) && dto.getCurrentLock().getNickName().equals("admin")) {
                    robotMonitorVO.setControlStatus(ControlStatusEnum.SEIZED_CONTROL.getName());
                } else {
                    robotMonitorVO.setControlStatus(ControlStatusEnum.LOSS_OF_CONTROL.getName());
                }
            } else {
                robotMonitorVO.setControlStatus(ControlStatusEnum.UN_PREEMPTED_CONTROL.getName());
            }
            //控制器湿度、温度、电压
            robotMonitorVO.setControllerTemp(dto.getControllerTemp());
            robotMonitorVO.setControllerHumi(dto.getControllerHumi());
            robotMonitorVO.setControllerVoltage(dto.getControllerVoltage());

            //机器人控制权详情
            RobotControlResDTO currentLock = dto.getCurrentLock();
            robotMonitorVO.setControlDesc(currentLock.getDesc());
            robotMonitorVO.setControlIp(currentLock.getIp());
            robotMonitorVO.setControlLocked(currentLock.isLocked());
            robotMonitorVO.setControlNickName(currentLock.getNickName());
            robotMonitorVO.setControlPort(currentLock.getPort());
            robotMonitorVO.setControlTimeT(currentLock.getTimeT());
            robotMonitorVO.setControlType(currentLock.getType());

            //机器人叉车详情
            RobotForkResDTO fork = dto.getFork();
            if (fork != null) {
                robotMonitorVO.setForkHeight(fork.getForkHeight());
                robotMonitorVO.setForkHeightInPlace(fork.isForkHeightInPlace());
                robotMonitorVO.setForkAutoFlag(fork.isForkAutoFlag());
                robotMonitorVO.setForwardVal(fork.getForwardVal());
                robotMonitorVO.setForwardInPlace(fork.isForwardInPlace());
                robotMonitorVO.setForkPressureActual(fork.getForkPressureActual());
            }

            //任务链信息
            RobotTaskListStatusDTO tasklistStatus = dto.getTasklistStatus();
            robotMonitorVO.setTaskListName(tasklistStatus.getTaskListName());
            robotMonitorVO.setTaskListStatus(tasklistStatus.getTaskListStatus());

            //急停按钮处于激活状态
            robotMonitorVO.setEmergency(dto.isEmergency());
            //单舵轮机器人当前的舵轮角度, 单位 rad
            robotMonitorVO.setSteer(dto.getSteer());
            //机器人严重错误+机器人普通错误
            List<String> fatalsAndErrors = new ArrayList<>();
            //机器人严重错误
            List<RobotWarningInfoDTO> fatals = new ArrayList<>();
            for (int i = 0; i < dto.getFatals().length; i++) {
                RobotWarningInfoDTO robotWarningInfoDTO = new RobotWarningInfoDTO();
                robotWarningInfoDTO.setVehicleId(dto.getVehicleId());
                robotWarningInfoDTO.setMessage(dto.getFatals()[i].toString());
                fatals.add(robotWarningInfoDTO);
                fatalsAndErrors.add(dto.getFatals()[i].toString());
            }
            robotMonitorVO.setFatals(fatals);

            //机器人普通错误
            List<RobotWarningInfoDTO> errors = new ArrayList<>();
            for (int i = 0; i < dto.getErrors().length; i++) {
                RobotWarningInfoDTO robotWarningInfoDTO = new RobotWarningInfoDTO();
                robotWarningInfoDTO.setVehicleId(dto.getVehicleId());
                robotWarningInfoDTO.setMessage(dto.getErrors()[i].toString());
                errors.add(robotWarningInfoDTO);
                fatalsAndErrors.add(dto.getErrors()[i].toString());
            }
            robotMonitorVO.setErrors(errors);

            //机器人警告
            List<RobotWarningInfoDTO> warnings = new ArrayList<>();
            for (int i = 0; i < dto.getWarnings().length; i++) {
                RobotWarningInfoDTO robotWarningInfoDTO = new RobotWarningInfoDTO();
                robotWarningInfoDTO.setVehicleId(dto.getVehicleId());
                robotWarningInfoDTO.setMessage(dto.getWarnings()[i].toString());
                warnings.add(robotWarningInfoDTO);
            }
            robotMonitorVO.setWarnings(warnings);

            //机器人通知
            List<RobotWarningInfoDTO> notices = new ArrayList<>();
            for (int i = 0; i < dto.getNotices().length; i++) {
                RobotWarningInfoDTO infoDTO = new RobotWarningInfoDTO();
                infoDTO.setVehicleId(dto.getVehicleId());
                infoDTO.setMessage(dto.getNotices()[i].toString());
                notices.add(infoDTO);
            }
            robotMonitorVO.setNotices(notices);

            //当前导航路径上最后已经经过的站点
            String[] finishedPath = dto.getFinishedPath();
            if (finishedPath.length > 0) {
                robotMonitorVO.setEndFinishedPath(finishedPath[finishedPath.length - 1]);
            }
            //获取redis存储的当前在分组中存在的机器人名字
            List<String> robots = (List<String>) redisUtil.stringWithGet("robots");
            //获取在分组中存在的机器人信息
            Map<String, RobotMonitorVO> monitorVOMap = new HashMap<>();
            for (String robot : robots) {
                Set<String> robotNames = map.keySet();
                if (robotNames.contains(robot)) {
                    RobotMonitorVO monitorVO = map.get(robot);
                    monitorVOMap.put(robot, monitorVO);
                }
                //如果推送的数据中，机器人的名字相同，即key相同，则新的value之前的value
                map.put(dto.getVehicleId(), robotMonitorVO);
                redisUtil.stringWithSet("robot_push_less_res", map);
            }
            //只推送在分组中存在的机器人数据
            businessDataVO.setData(monitorVOMap.values());
            businessDataVO.setCode("robot_push_less_res");
            pusherLiteService.sendBusinessMessage(businessDataVO);
        }

        //保存机器人当前载入的地图以及储存的地图 leo update
        if (DataConvertUtil.byteToHex(bytes[0]).equals("2C") && DataConvertUtil.byteToHex(bytes[1]).equals("24")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号返回得到的机器人载入的地图以及储存的地图：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_status_map_res", data);
        }

        //从机器人下载地图
        if (DataConvertUtil.byteToHex(bytes[0]).equals("36") && DataConvertUtil.byteToHex(bytes[1]).equals("BB")) {
            String message = DataConvertUtil.BytesToString(bytes);
//            log.info("根据协议和端口号从机器人下载地图：{}", message);
            BusinessDataVO businessDataVO = new BusinessDataVO();
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            RobotMapDTO dto = JSON.parseObject(data, RobotMapDTO.class);

            //获取redis存储的当前在分组中存在的机器人名字
            List<String> robots = (List<String>) redisUtil.stringWithGet("robots");
            //获取在分组中存在的机器人，并且获取对应的地图数据
            Map<String, String> mapTextMap = new HashMap<>();
            for (String robot : robots) {
                RobotMonitorVO robotMonitorVO = map.get(robot);
                mapTextMap.put(robotMonitorVO.getCurrentMap(), maps.get(robotMonitorVO.getCurrentMap()));
            }
            maps.put(dto.getHeader().getMapName(), data);
            redisUtil.stringWithSet("robot_config_downloadmap_res", maps);
            businessDataVO.setData(mapTextMap.values());
            businessDataVO.setCode("robot_config_downloadmap_res");
            pusherLiteService.sendBusinessMessage(businessDataVO);
        }

        //确认定位正确
        if (DataConvertUtil.byteToHex(bytes[0]).equals("2E") && DataConvertUtil.byteToHex(bytes[1]).equals("E3")) {
            String message = DataConvertUtil.BytesToString(bytes);
//            log.info("根据协议和端口号确认机器人定位正确：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_control_comfirmloc_res", data);
        }

        //抢占控制权
        if (DataConvertUtil.byteToHex(bytes[0]).equals("36") && DataConvertUtil.byteToHex(bytes[1]).equals("B5")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号抢占控制权：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_config_lock_res", data);
        }

        //释放控制权
        if (DataConvertUtil.byteToHex(bytes[0]).equals("36") && DataConvertUtil.byteToHex(bytes[1]).equals("B6")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号释放控制权：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_config_unlock_res", data);
        }

        //暂停当前导航
        if (DataConvertUtil.byteToHex(bytes[0]).equals("32") && DataConvertUtil.byteToHex(bytes[1]).equals("C9")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号暂停当前导航：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_task_pause_res", data);
        }

        //继续当前导航
        if (DataConvertUtil.byteToHex(bytes[0]).equals("32") && DataConvertUtil.byteToHex(bytes[1]).equals("CA")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号继续当前导航：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_task_resume_res", data);
        }

        //取消当前导航
        if (DataConvertUtil.byteToHex(bytes[0]).equals("32") && DataConvertUtil.byteToHex(bytes[1]).equals("CB")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号取消当前导航：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_task_cancel_res", data);
        }

        //路径导航
        if (DataConvertUtil.byteToHex(bytes[0]).equals("32") && DataConvertUtil.byteToHex(bytes[1]).equals("FB")) {
            String message = DataConvertUtil.BytesToString(bytes);
//            log.info("根据协议和端口号路径导航：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_task_gotarget_res", data);
        }

        //顶升机构上升
        if (DataConvertUtil.byteToHex(bytes[0]).equals("3E") && DataConvertUtil.byteToHex(bytes[1]).equals("C6")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号顶升机构上升：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_other_jack_load_res", data);
        }

        //顶升机构下降
        if (DataConvertUtil.byteToHex(bytes[0]).equals("3E") && DataConvertUtil.byteToHex(bytes[1]).equals("C7")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号顶升机构下降：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_other_jack_unload_res", data);
        }

        //查询机器人导航状态
        if (DataConvertUtil.byteToHex(bytes[0]).equals("2B") && DataConvertUtil.byteToHex(bytes[1]).equals("0C")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号返回得到的机器人导航状态是：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_status_task_res", data);
        }

        //查询机器人顶升机构状态
        if (DataConvertUtil.byteToHex(bytes[0]).equals("2B") && DataConvertUtil.byteToHex(bytes[1]).equals("13")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号返回得到的机器人顶升机构状态是：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            redisUtil.stringWithSet("robot_status_jack_res", data);
        }

        //查询机器人任务链
        if (DataConvertUtil.byteToHex(bytes[0]).equals("33") && DataConvertUtil.byteToHex(bytes[1]).equals("2D")) {
            String message = DataConvertUtil.BytesToString(bytes);
            log.info("根据协议和端口号返回得到的查询机器人任务链的响应是：{}", message);
            int beginIndex = message.indexOf("{");
            String data = message.substring(beginIndex);
            RobotTaskListResDTO model = JSON.parseObject(data, RobotTaskListResDTO.class);
            String taskListName = model.getTasklistStatus().getTaskListName();
            int commaIndex = taskListName.lastIndexOf('_');
            String robotName;
            if (commaIndex != -1) {
                robotName = taskListName.substring(0, commaIndex);
            } else {
                //没有“-”，返回整个字符串
                robotName = taskListName;
            }
            taskListMaps.put(robotName, data);
            redisUtil.stringWithSet("robot_tasklist_status_res", taskListMaps);
        }

//        log.info("客户端收到服务端消息: " + msg);
//        System.out.println("ctx:"+ ctx);
        ctx.fireChannelRead(msg);// 手动触发,其中context中，方法以fire开头的都是inbound事件，也就是输入事件

    }
}
