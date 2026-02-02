package com.robotCore.robot.dto;

import com.aspose.cad.internal.N.a;
import com.robotCore.scheduing.vo.RobotPathVO;
import lombok.Data;

import java.lang.management.LockInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/9/1
 **/
@Data
public class RobotPathPlanDTO {

    // 机器人<起点，终点>路径列表
    private List<RobotPath> agents;

    // 多机器人融合地图的路径信息
    private List<RobotPathVO> pathList;

    // 管控区域信息，格式为：[ [x1,y1,...],[x2,y2,...],...,[xn,yn,...]]，可为空。leo add
    private List<List<String>> conAreas = new ArrayList<>();

    private List<LockInfo> lock;

    @Data
    public static class RobotPath {

        private String robotName;

        private String groupName;

        private String startPoint;

        private String endPoint;
    }

    @Data
    public static class LockInfo {

        private String robotName;

        private String startPoint;

        private String endPoint;

        private String lockPoint;

        private float distance;
    }
}
