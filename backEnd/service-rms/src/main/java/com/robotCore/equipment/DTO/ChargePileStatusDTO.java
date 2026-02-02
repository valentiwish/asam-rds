package com.robotCore.equipment.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Des: 自动充电桩状态
 * @author: leo
 * @date: 2025/10/22
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargePileStatusDTO {

    // 输入信号状态位（从寄存器3x13解析）
    private Boolean dryContactEnable; // Bit0: 干接点使能
    private Boolean infraredReceived; // Bit1: 红外接收
    private Boolean extendInPosition; // Bit2: 伸到位
    private Boolean shrinkInPosition; // Bit3: 缩到位
    private Boolean wifiConnected; // Bit7: wifi连接

}
