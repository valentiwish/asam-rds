package com.robotCore.scheduing.common.utils;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/12
 **/
public class TimeFormatUtil {

    public static String formatTime(Long ms) {
        int ss = 1000;
        int mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuilder sb = new StringBuilder();
        if(day > 0) {
            sb.append(day).append("天");
        }
        if(hour > 0) {
            sb.append(hour).append("小时");
        }
        if(minute > 0) {
            sb.append(minute).append("分");
        }
        if(second > 0) {
            sb.append(second).append("秒");
        }
//        if(milliSecond > 0) {
//            sb.append(milliSecond).append("毫秒");
//        }
        return sb.toString();
    }

}
