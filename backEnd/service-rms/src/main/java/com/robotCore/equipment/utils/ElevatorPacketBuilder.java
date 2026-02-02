package com.robotCore.equipment.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.robotCore.common.utils.DataConvertUtil;

/**
 * @Des: 电梯协议报文构建工具类
 * @author: leo
 * @date: 2025/7/15
 */

public class ElevatorPacketBuilder {

    private static final byte[] STX = {(byte) 0xAB, (byte) 0x66};
    private static final byte ETX = 0x03;

    /**
     * 构建电梯协议报文
     *
     * @param bnk  组编号
     * @param nod  设备编号
     * @param data 数据内容
     * @return HEX格式的字符串报文
     */
    public static String buildPacket(byte bnk, byte nod, byte[] data) {
        ByteArrayOutputStream packet = new ByteArrayOutputStream();

        try {
            // 添加报文头
            packet.write(STX);

            // 添加组编号和设备编号
            packet.write(bnk);
            packet.write(nod);

            // 添加数据长度和数据内容
            byte len = (byte) data.length;
            packet.write(len);
            packet.write(data);

            // 计算校验和
            byte sum = calculateChecksum(bnk, nod, len, data);
            packet.write(sum);

            // 添加报文尾
            packet.write(ETX);

            return DataConvertUtil.BytesToHexString(packet.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("构建电梯协议报文失败", e);
        }
    }

    /**
     * 计算校验和: SUM = ~(BNK + NOD + LEN + DATA) + 1
     */
    private static byte calculateChecksum(byte bnk, byte nod, byte len, byte[] data) {
        int sum = (bnk & 0xFF) + (nod & 0xFF) + (len & 0xFF);

        for (byte b : data) {
            // 将字节b转换为无符号整数后累加到总和sum
            sum += (b & 0xFF);
        }
        // 取sum的低8位，按位取反后加1得到校验和
        return (byte) ((~sum + 1) & 0xFF);
    }

    // 转换满足1个字节取值范围内的16进制字符串为字节
    public static byte convertToByte(String str) {
        if (str.startsWith("0x")) {
            // 处理十六进制形式，包含2位十六进制字符时建议强制指定基数为16
            return (byte) Integer.parseInt(str.substring(2), 16);
        } else {
            // 处理普通整数字符串，自动约束byte范围
            return Byte.parseByte(str);
        }
    }
}