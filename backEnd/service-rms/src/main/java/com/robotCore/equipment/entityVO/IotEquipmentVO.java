package com.robotCore.equipment.entityVO;

import com.robotCore.equipment.entity.IotEquipment;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @Des: Modbus设备的VO类
 * @author: leo
 * @date: 2024/10/24
 **/
@Data
public class IotEquipmentVO extends IotEquipment {

    private Timestamp startTime;

    private Timestamp endTime;
}
