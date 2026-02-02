package com.robotCore.equipment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.EntityResult;
import com.modbus4j.entity.ModbusTcpRead;
import com.modbus4j.util.ConstDataType;
import com.modbus4j.util.ConstFunCode;
import com.modbus4j.util.api.ModbusUtils;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.mqtt.MqttGateway;
import com.robotCore.common.mqtt.king.TaskManager;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.equipment.entity.IotEquipment;
import com.robotCore.equipment.entityVO.IotEquipmentVO;
import com.robotCore.equipment.mapper.IoTEquipmentDao;
import com.robotCore.equipment.service.IoTEquipmentService;
import com.robotCore.equipment.utils.ElevatorPacketBuilder;
import com.robotCore.equipment.utils.RobotIdToByteManager;
import com.robotCore.robot.entity.RobotDmsEditor;
import com.robotCore.robot.service.RobotBasicInfoService;
import com.robotCore.scheduing.vo.RobotPathVO;
import com.robotCore.task.tcpCilent.TcpClientThread;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description 物联网设备管理
 * @Author: leo
 * @Create: 2024/10/24
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Service
@Transactional(rollbackFor = Exception.class)
public class IoTEquipmentServiceImpl extends ServiceImpl<IoTEquipmentDao, IotEquipment> implements IoTEquipmentService {
    @Autowired
    private RobotBasicInfoService robotBasicInfoService;

    @Autowired
    private TaskManager taskManager;

    @Resource
    private MqttGateway mqttGateway;

    @Value("${asam.mqtt.setTopic}")
    private String setSubTopic;

    @Value("${asam.mqtt.client.outId}")
    private String clientOutId;

    @Override
    public IPage<IotEquipment> findPageList(IPage<IotEquipment> varPage, IotEquipmentVO iotEquipmentVO, String equipmentType) {
        QueryWrapper<IotEquipment> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(ObjectUtil.isNotEmpty(equipmentType), IotEquipment::getEquipmentType, equipmentType);
        if (ObjectUtil.isNotEmpty(iotEquipmentVO)) {
            wrapper.lambda().like(ObjectUtil.isNotEmpty(iotEquipmentVO.getEquipmentCode()), IotEquipment::getEquipmentCode, iotEquipmentVO.getEquipmentCode());
            wrapper.lambda().like(ObjectUtil.isNotEmpty(iotEquipmentVO.getEquipmentName()), IotEquipment::getEquipmentName, iotEquipmentVO.getEquipmentName());
            wrapper.lambda().like(ObjectUtil.isNotEmpty(iotEquipmentVO.getAdjacentSite()), IotEquipment::getAdjacentSite, iotEquipmentVO.getAdjacentSite());
            wrapper.lambda().like(ObjectUtil.isNotEmpty(iotEquipmentVO.getEquipmentIp()), IotEquipment::getEquipmentIp, iotEquipmentVO.getEquipmentIp());
        }
        wrapper.lambda().orderByDesc(IotEquipment::getCreateTime);
        return page(varPage, wrapper);
    }

    @Override
    public List<IotEquipment> listByEquipmentType(String equipmentType) {
        QueryWrapper<IotEquipment> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(equipmentType), IotEquipment::getEquipmentType, equipmentType);
        return list(wrapper);
    }

    @Override
    public List<IotEquipment> listByCommunicationTypeAndEquipment(String communicationType, String equipmentType) {
        QueryWrapper<IotEquipment> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(communicationType), IotEquipment::getCommunicationType, communicationType);
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(equipmentType), IotEquipment::getEquipmentType, equipmentType);
        return list(wrapper);
    }

    // 获取在线的自动门 leo add
    @Override
    public List<IotEquipment> getOnlineModbusEquipments() {
        // 初始化在线自动门列表
        List<IotEquipment> onLineAutomaticDoors = new ArrayList<>();
        // 获取场景中部署的全部自动门
        List<IotEquipment> automaticDoors = listByCommunicationTypeAndEquipment(IotEquipment.CommunicationType.MODBUS, IotEquipment.EquipmentType.AUTOMATIC_DOOR);

        for (IotEquipment automaticDoor : automaticDoors) {
            ModbusTcpRead checkOnLineModbusTcpRead = new ModbusTcpRead(automaticDoor.getSlaveId(), ConstFunCode.func03,
                    automaticDoor.getEquipStateOffset(), ConstDataType.TWO_BYTE_INT_SIGNED, automaticDoor.getEquipmentIp(), 502);
            // 读取PLC的在线状态
            Object isPlcOnline = ModbusUtils.readModbusByTcp(checkOnLineModbusTcpRead);
            if (isPlcOnline != null) {
//                    log.info("自动门{}的PLC在线状态为：{}", automaticDoor.getEquipmentName(), isPlcOnline);
                onLineAutomaticDoors.add(automaticDoor);
            }
        }
        return onLineAutomaticDoors;
    }

    // 根据IP获取设备信息
    @Override
    public IotEquipment getEquipmentByIp(String ip) {
        QueryWrapper<IotEquipment> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(ip), IotEquipment::getEquipmentIp, ip);
//        wrapper.lambda().eq(IotEquipment::getState, STATE_VALID).orderByDesc(IotEquipment::getId);
        return getOne(wrapper);
    }

    // 解析电梯绑定的EL站点为虚拟路径
    @Override
    public String parseSiteToVirtualPaths(String adjacentSite) {
        // 筛选虚拟站点
        String[] sites = Arrays.stream(adjacentSite.split(","))
                .filter(s -> s.toUpperCase().startsWith("EL"))
                .toArray(String[]::new);

        // 计算预分配大小：n*(n-1)
        List<RobotPathVO> paths = new ArrayList<>(sites.length * (sites.length - 1));

        // 双重循环生成所有可能的虚拟路径
        for (int i = 0; i < sites.length; i++) {
            final String start = sites[i];
            for (int j = 0; j < sites.length; j++) {
                if (i != j) {
                    RobotPathVO path = new RobotPathVO();
                    path.setStartPoint(start);
                    path.setEndPoint(sites[j]);
                    // 路径长度没有时默认设为1，组名可以为空
                    path.setCurveLength(1.0);
                    paths.add(path);
                }
            }
        }

        // 使用ThreadLocal缓存ObjectMapper（JSON序列化性能优化）
        return JSON.toJSONString(paths);
    }

    /**
     * 根据ip和端口号连接梯控
     *
     * @param robotIp 机器人IP
     * @param port    端口号
     * @return true：连接成功；false：连接失败
     */
    @Override
    public boolean connectTcp(String robotIp, int port) {
        EntityResult result = null;
        try {
            result = TcpClientThread.start(robotIp, port);
            //为了防止在给梯控发送指令的时候，梯控端口还未建立连接，因此给该线程加了延迟
            Thread.sleep(300L);
        } catch (Exception e) {
            log.error("nettyStart:{}", e.getMessage());
        }
        if (result != null) {
            return result.isSuccess();
        } else {
            return false;
        }
    }

    /**
     * <查询指定电梯运行状态> leo add
     *
     * @param robotId        机器人名称
     * @param targetElevator 电梯对象
     * @return 梯控tcp响应结果
     */
    @Override
    public EntityResult queryElevatorStatus(String robotId, IotEquipment targetElevator) {
        /*
         * *** 获取在线所有远程Tcp设备IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 远程Tcp设备IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
            connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
        }

        // 获取字节ID
        byte robotByteId = RobotIdToByteManager.getInstance().getRobotByteId(robotId);

        // 构建查询数据
        byte[] data = {
                // 命令字: 查询
                0x01,
                // ID号
                robotByteId,
                // 固定值
                (byte) 0xFF
        };
        // 构建BNK
        byte bnk = ElevatorPacketBuilder.convertToByte(targetElevator.getGroupCode());
        // 构建NOD
        byte nod = ElevatorPacketBuilder.convertToByte(targetElevator.getEquipmentCode());
        // 构建查询指令
        String instruction = ElevatorPacketBuilder.buildPacket(bnk, nod, data);
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, instruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info(">>>>>>机器人{}查询电梯<{}>运行状态指令发送成功，Hex指令：{}", robotId, targetElevator.getEquipmentName(), instruction);
                break;
            }
            if (retryCount >= 60) break;

            // 等待1秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                    log.info("查询机器人{}控制的电梯<{}>任务状态超时，重试次数：{}", robotId, targetElevator.getEquipmentName(), retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

        return response;
    }

    /**
     * <登记电梯楼层> leo add
     *
     * @param robotId        机器人名称
     * @param targetElevator 电梯对象
     * @param targetFloor    目标楼层
     * @param isFrontDoor    是否前门
     * @return 梯控tcp响应结果
     */
    @Override
    public void registerFloor(String robotId, IotEquipment targetElevator, int targetFloor, boolean isFrontDoor) {
        /*
         * *** 获取在线所有远程Tcp设备IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 远程Tcp设备IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
            connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
        }

        // 获取字节ID
        byte robotByteId = RobotIdToByteManager.getInstance().getRobotByteId(robotId);

        // 确保楼层在有效范围内
        if (targetFloor < 0 || targetFloor > 127) {
            throw new IllegalArgumentException("目标楼层必须在0-127范围内");
        }

        // 构建楼层数据字节 (D7:门标志, D6-D0:楼层)
        byte floorData = (byte) ((isFrontDoor ? 0x00 : 0x80) | (targetFloor & 0x7F));

        // 构建登记数据
        byte[] data = {
                // 命令字：登记楼层
                0x02,
                // ID号
                robotByteId,
                // 预留字节
                0x00,
                // 数据字节
                floorData
        };
        // 构建BNK
        byte bnk = ElevatorPacketBuilder.convertToByte(targetElevator.getGroupCode());
        // 构建NOD
        byte nod = ElevatorPacketBuilder.convertToByte(targetElevator.getEquipmentCode());
        // 构建查询指令
        String instruction = ElevatorPacketBuilder.buildPacket(bnk, nod, data);
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, instruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info(">>>>>>机器人{}登记到电梯<{}>{}层指令发送成功，Hex指令：{}", robotId, targetElevator.getEquipmentName(), targetFloor, instruction);
                break;
            }
            if (retryCount >= 60) break;

            // 等待1 秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                    log.info("发送机器人{}控制的电梯<{}>登记楼层指令失败，重试次数：{}", robotId, targetElevator.getEquipmentName(), retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

    }

    /**
     * <电梯开门请求> leo add
     *
     * @param robotId        机器人名称
     * @param targetElevator 电梯对象
     * @param isFrontDoor    是否前门
     */
    @Override
    public void openElevatorDoor(String robotId, IotEquipment targetElevator, boolean isFrontDoor) {
        /*
         * *** 获取在线所有远程Tcp设备IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 远程Tcp设备IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
            connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
        }

        // 获取字节ID
        byte robotByteId = RobotIdToByteManager.getInstance().getRobotByteId(robotId);

        // 正确构建开门数据 (前门D4位:0x10, 后门D5位:0x20)
        byte doorCommand = (byte) (isFrontDoor ? 0x10 : 0x20);

        byte[] data = {
                // 命令字: 开门请求
                0x03,
                // id号
                robotByteId,
                // 数据字节
                doorCommand
        };

        // 构建BNK
        byte bnk = ElevatorPacketBuilder.convertToByte(targetElevator.getGroupCode());
        // 构建NOD
        byte nod = ElevatorPacketBuilder.convertToByte(targetElevator.getEquipmentCode());
        // 构建查询指令
        String instruction = ElevatorPacketBuilder.buildPacket(bnk, nod, data);
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, instruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info(">>>>>>机器人{}请求电梯<{}>开门指令发送成功，Hex指令：{}", robotId, targetElevator.getEquipmentName(), instruction);
                break;
            }
            if (retryCount >= 60) break;

            // 等待1秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                    log.info("发送机器人{}控制的电梯<{}>开门指令失败，重试次数：{}", robotId, targetElevator.getEquipmentName(), retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

    }

    /**
     * <电梯关门请求> leo add
     *
     * @param robotId        机器人名称
     * @param targetElevator 电梯对象
     * @param isFrontDoor    是否前门
     * @return 梯控tcp响应结果
     */
    @Override
    public void closeElevatorDoor(String robotId, IotEquipment targetElevator, boolean isFrontDoor, boolean exitSpecialMode) {
        /*
         * *** 获取在线所有远程Tcp设备IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 远程Tcp设备IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
            connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
        }

        // 获取字节ID
        byte robotByteId = RobotIdToByteManager.getInstance().getRobotByteId(robotId);

        // 构建关门数据 (前门D4位:0x10, 后门D5位:0x20)
        byte doorCommand = (byte) (isFrontDoor ? 0x10 : 0x20);
        if (exitSpecialMode) {
            // 设置D0位
            doorCommand |= 0x01;
        }

        byte[] data = {
                // 命令字: 关门请求
                0x04,
                // ID号
                robotByteId,
                // 数据字节
                doorCommand
        };

        // 构建BNK
        byte bnk = ElevatorPacketBuilder.convertToByte(targetElevator.getGroupCode());
        // 构建NOD
        byte nod = ElevatorPacketBuilder.convertToByte(targetElevator.getEquipmentCode());
        // 构建查询指令
        String instruction = ElevatorPacketBuilder.buildPacket(bnk, nod, data);
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, instruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info(">>>>>>机器人{}请求电梯<{}>关门指令发送成功，Hex指令：{}", robotId, targetElevator.getEquipmentName(), instruction);
                break;
            }
            if (retryCount >= 60) break;

            // 等待1秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                    log.info("发送机器人{}控制的电梯<{}>关门指令失败，重试次数：{}", robotId, targetElevator.getEquipmentName(), retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

    }

    /**
     * <电梯指令取消请求> leo add
     *
     * @param robotId        机器人名称
     * @param targetElevator 电梯对象
     * @return 梯控tcp响应结果
     */
    @Override
    public EntityResult cancelElevatorRequest(String robotId, IotEquipment targetElevator) {
        /*
         * *** 获取在线所有远程Tcp设备IP地址与对应Netty客户端线程的映射关系 ***
         *
         * Key: 远程Tcp设备IP端口
         * Value: 处理该IP通信的Netty客户端线程
         */
        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
        // 若该ip端口没有tcp连接，则创建tcp连接
        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
            connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
        }

        // 获取字节ID
        byte robotByteId = RobotIdToByteManager.getInstance().getRobotByteId(robotId);

        // 构建取消数据
        byte[] data = {
                // 命令字: 取消
                0x05,
                robotByteId,
                // 预留字节
                0x00
        };

        // 构建BNK
        byte bnk = ElevatorPacketBuilder.convertToByte(targetElevator.getGroupCode());
        // 构建NOD
        byte nod = ElevatorPacketBuilder.convertToByte(targetElevator.getEquipmentCode());
        // 构建查询指令
        String instruction = ElevatorPacketBuilder.buildPacket(bnk, nod, data);
        // 向该ip端口发送tcp指令
        EntityResult response = null;
        int retryCount = 0;
        do {
            // 发送TCP指令
            response = TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, instruction);

            // 成功时或已达最大重试次数时退出循环
            if (response.isSuccess()) {
                log.info(">>>>>>机器人{}取消电梯<{}>指令：{}", robotId, targetElevator.getEquipmentName(), instruction);
                break;
            }
            if (retryCount >= 60) break;

            // 等待1秒后重试
            CountDownLatch latch = new CountDownLatch(1);
            try {
                if (!latch.await(1000, TimeUnit.MILLISECONDS)) {
                    log.info("发送机器人{}控制的电梯<{}>取消指令失败，重试次数：{}", robotId, targetElevator.getEquipmentName(), retryCount + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 处理中断逻辑
            }
            retryCount++;
        } while (!Thread.currentThread().isInterrupted());

        return response;
    }

    /**
     * <筛选指定区域内的物联网设备> leo add
     *
     * @param iotAreas      机器人名称
     * @param iotEquipments 电梯对象
     * @return 梯控tcp响应结果
     */
    public Set<IotEquipment> filterEquipmentByArea(List<RobotDmsEditor> iotAreas, List<IotEquipment> iotEquipments) {
        // 筛选符合条件的自动门设备（即待锁站点覆盖区域的自动门集合）
        return iotEquipments.stream()
                .filter(automaticDoor -> {
                    // 将自动门绑定的相邻站点拆分为列表
                    List<String> adjacentSites = Arrays.asList(
                            automaticDoor.getAdjacentSite().split(",")
                    );
                    // 检查当前自动门是否关联到目标区域集合中的任意一个区域
                    return iotAreas.stream().map(doorArea ->
                                    // 将区域配置的JSON字符串转换为站点列表
                                    JSON.parseArray(doorArea.getAreaContainPoints(), String.class)
                            )
                            // 短路：找到第一个符合条件的区域即返回
                            .anyMatch(doorAreaPoints ->
                                    // 判断该区域是否包含自动门的绑定点
                                    new HashSet<>(doorAreaPoints).containsAll(adjacentSites)
                            );
                })
                // 获取首个匹配的iot（满足条件立即终止流）
//                    .findFirst()
                // 获取匹配的全部iot设备
                .collect(Collectors.toSet());
    }

    /**
     * 获取自动门通过设备区域
     * @param equipmentRegion
     * @return
     */
    @Override
    public List<IotEquipment> getAutomaticDoorByEquipmentRegion(String equipmentRegion) {
        QueryWrapper<IotEquipment> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(IotEquipment::getEquipmentType, "automaticDoor");
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(equipmentRegion), IotEquipment::getEquipmentRegion, equipmentRegion);
        return list(wrapper);
    }

    /**
     * 获取烘箱设备通过设备区域
     * @param equipmentRegion
     * @return
     */
    @Override
    public List<IotEquipment> getOvenByEquipmentRegion(String equipmentRegion) {
        QueryWrapper<IotEquipment> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(IotEquipment::getEquipmentType, "oven");
        wrapper.lambda().eq(ObjectUtil.isNotEmpty(equipmentRegion), IotEquipment::getEquipmentRegion, equipmentRegion);
        return list(wrapper);
    }

    /**
     * 控制烘箱门开关
     * @param variable
     * @param value
     */
    @Override
    public void controlOvenDoor(String variable, boolean value) {
        log.info("请求开烘箱门的接口数据:变量variable：{};下发value:{}", variable, value);
        Long qid = System.nanoTime() % 1000000000;
        taskManager.sendMqttVariable(variable, value, qid);
    }

    /**
     * 异步任务：下发卷帘门开门指令
     */
    @Override
    @Async("asyncServiceExecutor")
    public void open359DoorAsync(String variable, boolean value) {
        log.info("进入异步开359门任务时间{}", new Date());
        //根据卷帘门Id获取开门变量
        String sendData = commandSendPackage(variable, value, 0);
        log.info("开卷帘门{}指令发送,数据：{}", variable, sendData);
        mqttGateway.sendToMqtt(setSubTopic, sendData);
    }

    /**
     * IOServer下发控制指令封装，参数：变量名，设置值，下发次数
     * @param variable
     * @param value
     * @param sendNum
     * @return
     */
    @Override
    public String commandSendPackage(String variable, Object value, Integer sendNum){
        Long qid= System.nanoTime()%1000000000;
        Map<String,Object> map=new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSS+0800");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        // 获取当前时间并格式化为UTC时间字符串
        Date now = new Date();
        String utcTime = sdf.format(now);
        map.put("Writer",clientOutId);
        map.put("WriteTime",utcTime);
        map.put("Qid",qid);
        map.put("Username","admin");
        map.put("Password","e10adc3949ba59abbe56e057f20f883e");
        Map<String,Object> pnsmap1=new HashMap<>();
        pnsmap1.put("1","V");
        pnsmap1.put("2","T");
        pnsmap1.put("3","Q");
        map.put("PNs",pnsmap1);
        Map<String,Object> pvsmap1=new HashMap<>();
        pvsmap1.put("1",0);
        pvsmap1.put("2",utcTime);
        pvsmap1.put("3",192);
        map.put("PVs",pvsmap1);
        List<Map<String,Object>> maps=new ArrayList<>();
        Map<String,Object> Objs=new HashMap<>();
        Objs.put("N",variable);
        Objs.put("1",value);
        Objs.put("2",now.getTime());
        Objs.put("3",192);
        maps.add(Objs);
        map.put("Objs",maps);
        return JSONObject.toJSONString(map);
    }

}
