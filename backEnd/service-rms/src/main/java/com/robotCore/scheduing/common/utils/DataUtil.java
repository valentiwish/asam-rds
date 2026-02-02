package com.robotCore.scheduing.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/12
 **/
public class DataUtil {
    public static String format(double value){
        BigDecimal bd = new BigDecimal(value);//创建一个bd对象，将要转换的值value传入构造函数
        bd = bd.setScale(2, RoundingMode.HALF_UP);//调用setScale方法进行数据格式化，保留两位小数，采用四舍五入规则
        return bd.toString(); //返回bd对象的值（转化为string形式）
    }
}
