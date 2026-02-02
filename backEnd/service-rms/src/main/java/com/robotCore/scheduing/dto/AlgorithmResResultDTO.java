package com.robotCore.scheduing.dto;

import lombok.Data;

import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/9/11
 **/
@Data
public class AlgorithmResResultDTO {

    private String robotName;

    private List<Path> pathPlanning;

    @Data
    public static class Path {

        private String startPoint;

        private String endPoint;

        private float totalCurveLength;
    }
}
