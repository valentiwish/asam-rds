package com.robotCore.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @Description: 数据转换类
 * @Author: zhangqi
 * @Create: 2019-11-06 11:18
 */
public class DataConvertUtil {
    /**
     * @Description: 将byte数组转换成字符串
     * @param byteArray byte数组
     * @return 转换后的字符串
     */
    public static String BytesToString(byte[] byteArray)
    {
        String str= new String (byteArray);
        return str;
    }
    /**
     * @Description: 将字符串转换成byte数组
     * @param string 字符
     * @return 转换后的字节数组
     */
    public static byte[] StringToBytes(String string)
    {
        byte[] byteArray = string.getBytes();
        return byteArray;
    }

    /**
     * @Description: String字符串转换为10进制byte数组
     * @param str 字符
     * @return 转换后的字节数组
     */
    public static byte[] StringToBinBytes(String str)
    {
        str = str.replace(" ", "");
        if ((str.length() % 2) != 0) {
            str += " ";
        }
        byte[] returnBytes = new byte[str.length() / 2];
        for (int i = 0; i < returnBytes.length; i++) {
            returnBytes[i] = Byte.parseByte(str.substring(i * 2, (i * 2) + 2));
        }
        return returnBytes;
    }

    /**
     * @Description: 将byte数组转换成16进制字符串
     * @param byteArray byte数组
     * @return 转换后的16进制字符串
     */
    public static String BytesToHexString(byte[] byteArray)
    {
        if (byteArray == null || byteArray.length < 1) {
            throw new IllegalArgumentException("this byteArray must not be null or empty");
        }

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10) {
                //0~F前面补零
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }
    /**
     * @Description: 将16进制字符串转换成byte数组
     * @param hexString 16进制字符串
     * @return 转换后的字节数组
     */
    public static byte[] HexStringToBytes(String hexString)
    {
        if (hexString == null || hexString.length() < 1) {
            throw new IllegalArgumentException("this hexString must not be empty");
        }
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }


    /**
     * 将ArrayList转化为二进制数组
     * @param list List对象
     * @return 二进制数组
     */
    public static byte[] ListObjectToBytes(List<Byte> list) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    arrayOutputStream);
            objectOutputStream.writeObject(list);
            objectOutputStream.flush();
            byte[] data = arrayOutputStream.toByteArray();
            objectOutputStream.close();
            arrayOutputStream.close();
            return data;
        } catch (Exception e) {
            System.out.println("转化失败："+ e.getMessage());
        }
        return null;
    }

    /**
     * 从二进制数组转换List对象
     * @param bytes 二进制数组
     * @return List返回对象
     */
    public static List<Byte> BytesToListObject(byte[] bytes) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream inputStream = new ObjectInputStream(
                    arrayInputStream);
            List<Byte> list = (List<Byte>) inputStream.readObject();
            inputStream.close();
            arrayInputStream.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 数据转换，byte数组转换为时间字符串{yyyy-MM-dd HH:mm:ss}
     * @param bytes 二进制数组
     * @return String
     */
    public static String BytesToDateTime(byte[] bytes)
    {
        String dateStr = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String yearHead = df.format(new Date()).substring(0, 2);
        dateStr = yearHead + String.format("%02d",bytes[0]) + "-" + String.format("%02d",bytes[1]) + "-" + String.format("%02d",bytes[2]) + " " + String.format("%02d",bytes[3]) + ":" + String.format("%02d",bytes[4]) + ":" + String.format("%02d",bytes[5]);
        return dateStr;
    }

    /**
     * 将一个特定格式的日期时间字符串（例如 2025-07-16T18:59:02.249+0800）转换为毫秒
     * @param dateTimeStr
     * @return
     */
    public static long DateTimeToMillis(String dateTimeStr) {
        // 定义日期时间格式（ISO 8601，带时区偏移）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        // 解析字符串为 OffsetDateTime（自动处理时区）
        OffsetDateTime odt = OffsetDateTime.parse(dateTimeStr, formatter);

        // 转换为 Instant（UTC 时间）
        Instant instant = odt.toInstant();

        // 获取毫秒时间戳
        long timestampMs = instant.toEpochMilli();
        return timestampMs;
    }

    /**
     * 数据转换，byte数组转换为int
     * @param bytes 二进制数组
     * @return int
     */
    public static int BytesToInt(byte[] bytes)
    {
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
    }

    /**
     * 数据转换，byte数组转换为short
     * @param bytes 二进制数组
     * @return short
     */
    public static short BytesToShort(byte[] bytes)
    {
        return (short)((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    /**
     * Byte字节转Hex
     * @param b 字节
     * @return Hex
     */
    public static String byteToHex(byte b)
    {
        String hexString = Integer.toHexString(b & 0xFF);
        //由于十六进制是由0~9、A~F来表示1~16，所以如果Byte转换成Hex后如果是<16,就会是一个字符（比如A=10），通常是使用两个字符来表示16进制位的,
        //假如一个字符的话，遇到字符串11，这到底是1个字节，还是1和1两个字节，容易混淆，如果是补0，那么1和1补充后就是0101，11就表示纯粹的11
        if (hexString.length() < 2)
        {
            hexString = new StringBuilder(String.valueOf(0)).append(hexString).toString();
        }
        return hexString.toUpperCase();
    }

    /**
     * String转Hex
     * @param str
     * @return
     */
    public static String convertStringToHex(String str){
        char[] chars = str.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            hex.append(Integer.toHexString((int) aChar));
        }
        return hex.toString();
    }

    /**
     * hex转Ascii
     * @param hexStr
     * @return
     */
    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }
}
