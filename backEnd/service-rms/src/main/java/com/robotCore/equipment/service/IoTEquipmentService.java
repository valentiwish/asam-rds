package com.robotCore.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.EntityResult;
import com.robotCore.equipment.entity.IotEquipment;
import com.robotCore.equipment.entityVO.IotEquipmentVO;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface IoTEquipmentService extends IService<IotEquipment> {
    IPage<IotEquipment> findPageList(IPage<IotEquipment> varPage, IotEquipmentVO modbusEquipmentVO, String equipmentType);

    List<IotEquipment> listByEquipmentType(String equipmentType);

    List<IotEquipment> listByCommunicationTypeAndEquipment(String communicationType, String equipmentType);

    // 获取在线的自动门 leo add
    List<IotEquipment> getOnlineModbusEquipments();

    // 根据设备IP获取设备对象
    IotEquipment getEquipmentByIp(String ip);

    boolean connectTcp(String robotIp, int port);

    // 解析电梯绑定的站点为虚拟路径
    String parseSiteToVirtualPaths(String adjacentSite);

    // 查询指定电梯运行状态
    EntityResult queryElevatorStatus(String robotId, IotEquipment targetElevator);
    // 登记电梯楼层
    void registerFloor(String robotId, IotEquipment targetElevator, int targetFloor, boolean isFrontDoor);
    // 电梯开门请求
    void openElevatorDoor(String robotId, IotEquipment targetElevator, boolean isFrontDoor);
    // 电梯关门请求
    void closeElevatorDoor(String robotId, IotEquipment targetElevator, boolean isFrontDoor, boolean exitSpecialMode);
    // 电梯指令取消请求
    EntityResult cancelElevatorRequest(String robotId, IotEquipment targetElevator);

    List<IotEquipment> getAutomaticDoorByEquipmentRegion(String equipmentRegion);

    List<IotEquipment> getOvenByEquipmentRegion(String equipmentRegion);

    void controlOvenDoor(String variable, boolean value);

    @Async("asyncServiceExecutor")
    void open359DoorAsync(String variable, boolean value);

    String commandSendPackage(String variable, Object value, Integer sendNum);

}
