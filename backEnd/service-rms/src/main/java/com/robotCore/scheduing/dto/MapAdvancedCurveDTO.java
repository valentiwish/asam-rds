package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des: 地图
 * @author: zxl
 * @date: 2023/8/25
 **/
@Data
public class MapAdvancedCurveDTO {

    private String className;

    private String instanceName;

    private PointPos startPos;

    private PointPos endPos;

    private Pos controlPos1;

    private Pos controlPos2;

    @Data
    public static class PointPos {

        private String instanceName;

        private Pos pos;

    }

    @Data
    public static class Pos {

        private float x;

        private float y;
    }
}
