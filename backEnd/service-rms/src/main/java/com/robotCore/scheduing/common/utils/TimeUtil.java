package com.robotCore.scheduing.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/28
 **/
public class TimeUtil {

    public static long dataToTimeStamp(String timeString) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(timeString);
        return date.getTime() / 1000;
    }

}
