package com.robotCore.common.utils;

/**
 * @Description: 物料批次生成工具类
 * @Author: zhangqi
 * @Create: 2021/4/24 11:30
 */
public class MatBatchUtil {

    /**
     * 根据批次和上个序列号生产新的产品编码
     */
    public static String getBySerialCode(String bathcNo, String lastBatchCode) {
        String code = "";
        if(!"".equals(lastBatchCode)) {
            //上个生成的产品编码是当天的，需要取后五位进行累加
            Integer lastNumber = Integer.parseInt(lastBatchCode.substring(lastBatchCode.length() - 5));
            code = lastBatchCode.substring(0, lastBatchCode.length() - 5) + String.format("%05d", lastNumber + 1);
        }else{
            code = bathcNo + "00001";
        }
        return code;
    }

    /**
     * 根据炉批号和上个序列号生产新的毛坯编码 (毛坯料)
     */
    public static String getByroughCode(String bathcNo, String lastBatchCode) {
        String code = "";
        if(!"".equals(lastBatchCode)) {
            //上个生成的产品编码是当天的，需要取后三位进行累加
            Integer lastNumber = Integer.parseInt(lastBatchCode.substring(lastBatchCode.length() - 3));
            code = lastBatchCode.substring(0, lastBatchCode.length() - 3) + String.format("%03d", lastNumber + 1);
        }else{
            code = bathcNo + "001";
        }
        return code;
    }
}
