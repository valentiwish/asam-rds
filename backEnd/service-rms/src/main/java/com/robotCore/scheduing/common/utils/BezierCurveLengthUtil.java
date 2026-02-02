package com.robotCore.scheduing.common.utils;

import com.robotCore.scheduing.dto.BezierCurvePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @Des: 计算三阶贝塞尔曲线的长度
 * @author: zxl
 * @date: 2023/8/25
 **/
public class BezierCurveLengthUtil {

    public static float getBezierCurveLength(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {

        List<BezierCurvePoint> points = new ArrayList<>();

        for (float i = 0; i <= 1; i = (float) (i + 0.2)) {
            BezierCurvePoint point = new BezierCurvePoint();
            point.setX((float) (Math.pow(1 - i,3) * x1 + 3 * Math.pow(1 - i,2) * i * x2 +
                    3 * (1 - i) * Math.pow(i,2) * x3 + Math.pow(i,3) * x4));
            point.setY((float) (Math.pow(1 - i,3) * y1 + 3 * Math.pow(1 - i,2) * i * y2 +
                    3 * (1 - i) * Math.pow(i,2) * y3 + Math.pow(i,3) * y4));
            points.add(point);
        }

        float length = 0;
        for (int j = 1; j < points.size(); j++) {
           float x =  points.get(j).getX() - points.get(j-1).getX();
           float y = points.get(j).getY() - points.get(j-1).getY();
           length = (float) (length + Math.sqrt(x * x + y * y));
        }
        return length;
    }
}
