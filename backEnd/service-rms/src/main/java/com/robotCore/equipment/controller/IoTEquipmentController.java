package com.robotCore.equipment.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.entity.EntityResult;
import com.entity.R;
import com.modbus4j.entity.ModbusTcpRead;
import com.modbus4j.util.ConstDataType;
import com.modbus4j.util.ConstFunCode;
import com.modbus4j.util.api.ModbusUtils;
import com.page.BasePage;
import com.page.FormatPage;
import com.page.JPAPage;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.base.service.AsynTaskService;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.constant.PortConstant;
import com.robotCore.common.mqtt.king.TaskManager;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.equipment.entity.IotEquipment;
import com.robotCore.equipment.entityVO.IotEquipmentVO;
import com.robotCore.equipment.service.IoTEquipmentService;
import com.robotCore.task.tcpCilent.TcpClientThread;
import com.utils.tools.CopyUtils;
import com.utils.tools.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Des: Moudbus设备管理控制器
 * @author: leo
 * @date: 2024/10/24
 **/
@Slf4j
@RestController
@Api(value = "IoTEquipmentController", description = "IoT设备管理控制器")
@RequestMapping(value = "/iotEquipment")
public class IoTEquipmentController extends BaseController {

    @Autowired
    private IoTEquipmentService ioTEquipmentService;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private AsynTaskService asynTaskService;


    @ApiOperation(value = "IoT设备列表")
    @PostMapping(value = "/list")
    public Object list(JPAPage varBasePage, String data, String equipmentType) {
        IotEquipmentVO iotEquipmentVO = null;
        IPage<IotEquipment> varPage = BasePage.getMybaitsPage(varBasePage);
        CopyUtils.convert(varBasePage, varPage, CopyUtils.methodType.Define);
        if (ObjectUtil.isNotEmpty(data)) {
            iotEquipmentVO = JSON.parseObject(data, IotEquipmentVO.class);
        }
        EntityResult varEntityResult = new EntityResult();

        IPage<IotEquipment> page = ioTEquipmentService.findPageList(varPage, iotEquipmentVO, equipmentType);
        varEntityResult.setData(FormatPage.getFormatMybaitsPage(page));
        return varEntityResult;
    }

    @ApiOperation(value = "检查modbus设备在线状态")
    @PostMapping(value = "/checkConnect")
    public Object checkConnect(String equipmentType) throws IOException {
        ModbusTcpRead modbusTcpRead;
        boolean isUpdate = false;
        //modbus相关的设备检查在线状态
        List<IotEquipment> modbusEquipments = ioTEquipmentService.listByCommunicationTypeAndEquipment("Modbus", equipmentType);
        for (IotEquipment modbusEquipment : modbusEquipments) {
            modbusTcpRead = new ModbusTcpRead(modbusEquipment.getSlaveId(), ConstFunCode.func03, modbusEquipment.getEquipStateOffset(), ConstDataType.TWO_BYTE_INT_SIGNED, modbusEquipment.getEquipmentIp(), modbusEquipment.getEquipmentPort());
            Object modbusRes = ModbusUtils.readModbusByTcp(modbusTcpRead);
            boolean isUpdateRow = false;
            if (modbusRes != null) {
                if (modbusEquipment.getEquipmentStatus() == 0) {
                    modbusEquipment.setEquipmentStatus(1);
                    isUpdateRow = true;
                    log.info("设备{}连接成功", modbusEquipment.getEquipmentName());
                }
            } else {
                if (modbusEquipment.getEquipmentStatus() == 1) {
                    modbusEquipment.setEquipmentStatus(0);
                    isUpdateRow = true;
                    log.info("设备{}断开连接", modbusEquipment.getEquipmentName());
                }
            }
            ioTEquipmentService.saveOrUpdate(modbusEquipment);
            isUpdate = isUpdate || isUpdateRow;
        }
        //mqtt相关的设备检查在线状态
        List<IotEquipment> mqttEquipments = ioTEquipmentService.listByCommunicationTypeAndEquipment("MQTT", equipmentType);
        for (IotEquipment mqttEquipment : mqttEquipments) {
            if (StringUtils.isNotEmpty(mqttEquipment.getEquipmentIp())) {
                InetAddress geek = InetAddress.getByName(mqttEquipment.getEquipmentIp());
                if (geek.isReachable(1000)) {
                    mqttEquipment.setEquipmentStatus(1);
                } else {
                    mqttEquipment.setEquipmentStatus(0);
                }
            } else {
                mqttEquipment.setEquipmentStatus(0);
            }
            ioTEquipmentService.saveOrUpdate(mqttEquipment);
        }
        return isUpdate;
    }

    @ApiOperation(value = "保存", notes = "保存IoT设备")
    @PostMapping(value = "/save")
    public Object save(String data) {
        UserVO user = this.getCurUser();
        EntityResult varEntityResult = new EntityResult();
        if (Objects.nonNull(data)) {
            IotEquipment iotEquipment = JSON.parseObject(data, IotEquipment.class);

            // 若设备类型为电梯，则生成虚拟路径
            if (iotEquipment.getEquipmentType().equals("elevator")) {
                // 解析电梯绑定的EL站点为虚拟路径
                String virtualPathsJsonStr = ioTEquipmentService.parseSiteToVirtualPaths(iotEquipment.getAdjacentSite());
                iotEquipment.setVirtualPaths(virtualPathsJsonStr);
            }

            if (ObjectUtils.isEmpty(iotEquipment.getId())) {
                iotEquipment.setCreateUser(user.getUserName());
                iotEquipment.setCreateId(user.getId());
            } else {
                iotEquipment.setUpdateUser(user.getUserName());
                iotEquipment.setUpdateId(user.getId());
            }
            ioTEquipmentService.saveOrUpdate(iotEquipment);
        } else {
            varEntityResult.setSuccess(false);
        }
        return varEntityResult;
    }

    @ApiOperation(value = "根据ID查询", notes = "IoT设备详情")
    @PostMapping(value = "/findById")
    public Object findById(String id) {
        EntityResult entityResult = new EntityResult();
        if (Objects.nonNull(id)) {
            IotEquipment iotEquipment = ioTEquipmentService.getById(id);
            entityResult.setData(iotEquipment);
        } else {
            entityResult.setSuccess(false);
            entityResult.setMsg("前台传递信息为空，请检查！");
        }
        return entityResult;
    }

    @ApiOperation(value = "根据Id删除对象")
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (ObjectUtil.isNotEmpty(id)) {
            boolean flag = ioTEquipmentService.removeById(id);
            return flag ? R.ok() : R.error();
        } else {
            return R.error("前端传递参数为空");
        }
    }

    /* ----------------------- 测试电梯相关接口 ----------------------- */

    @ApiOperation("查询指定电梯运行状态")
    @PostMapping("/queryElevatorStatus")
    public EntityResult queryElevatorStatus(String robotId, IotEquipment targetElevator) {
        EntityResult result = null;
        result = ioTEquipmentService.queryElevatorStatus(robotId, targetElevator);
//        Map<String, Thread> ipThreadNettyMap = TcpClientThread.getIpThreadNetty();
//        // 若该ip端口没有tcp连接，则创建tcp连接
//        if (!ipThreadNettyMap.containsKey(targetElevator.getEquipmentIp() + PortConstant.ELEVATOR_CONTROL_PORT)) {
//            ioTEquipmentService.connectTcp(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT);
//        }
//        TcpClientThread.sendHexMsg(targetElevator.getEquipmentIp(), PortConstant.ELEVATOR_CONTROL_PORT, "AB 66 00 00 03 01 00 FF FD 03");
        return result;
    }

    @ApiOperation("注册电梯到指定楼层")
    @PostMapping("/registerFloor")
    public EntityResult registerFloor(String robotId, IotEquipment targetElevator, int targetFloor, boolean isFrontDoor) {
        ioTEquipmentService.registerFloor(robotId, targetElevator, targetFloor, isFrontDoor);
        return new EntityResult();
    }

    @ApiOperation("打开电梯门")
    @PostMapping("/openElevatorDoor")
    public EntityResult openElevatorDoor(String robotId, IotEquipment targetElevator, boolean isFrontDoor) {
        ioTEquipmentService.openElevatorDoor(robotId, targetElevator, isFrontDoor);
        return new EntityResult();
    }

    @ApiOperation("关闭电梯门")
    @PostMapping("/closeElevatorDoor")
    public EntityResult closeElevatorDoor(String robotId, IotEquipment targetElevator, boolean isFrontDoor, boolean exitSpecialMode) {
        ioTEquipmentService.closeElevatorDoor(robotId, targetElevator, isFrontDoor, exitSpecialMode);
        return new EntityResult();
    }

    @ApiOperation("取消电梯请求")
    @PostMapping("/cancelElevatorRequest")
    public EntityResult cancelElevatorRequest(String robotId, IotEquipment targetElevator) {
        EntityResult response = null;
        response = ioTEquipmentService.cancelElevatorRequest(robotId, targetElevator);
        return response;
    }

    @ApiOperation(value = "请求控制烘箱门", notes = "转运任务")
    @PostMapping(value = "/controlDoor")
    public Object openDoor(String variable, boolean value) {
        log.info("请求开烘箱门的接口数据:变量variable：{};下发value:{}", variable, value);
        Long qid = System.nanoTime() % 1000000000;
        taskManager.sendMqttVariable(variable, value, qid);
        return R.ok();
    }

    @ApiOperation(value = "请求控制烘箱门", notes = "转运任务")
    @PostMapping(value = "/openDoor")
    public Object open359Door(String variable, boolean value) {
        log.info("请求开359三台重载自动门的接口数据:变量variable：{};下发value:{}", variable, value);
        Long qid = System.nanoTime() % 1000000000;
        ioTEquipmentService.open359DoorAsync(variable, value);
        return R.ok();
    }
}
