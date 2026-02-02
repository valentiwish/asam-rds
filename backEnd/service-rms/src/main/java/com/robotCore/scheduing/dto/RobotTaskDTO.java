package com.robotCore.scheduing.dto;

import lombok.Data;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/7/13
 **/
@Data
public class RobotTaskDTO {

    private String id;

    private String name;

    private SubParameter[] parameters;

    private RobotTaskDTO[] children;

    @Data
    public static class SubParameter {
        private String id;
        private String name;
        private String value;
    }
}
