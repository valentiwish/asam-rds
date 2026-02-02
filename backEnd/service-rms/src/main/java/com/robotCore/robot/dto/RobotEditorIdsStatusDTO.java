package com.robotCore.robot.dto;

import lombok.Data;

import java.util.List;

@Data
public class RobotEditorIdsStatusDTO {

    private List<RobotEditorIdStatus> editorIdStatuses;

    @Data
    public static final class RobotEditorIdStatus {

        //区域编号
        private String editorId;

        //0--非占用， 1--占用
        private Integer isOccupied;

        //机器人类型 1-米松叉车2-金陵,3-西安航天
        private Integer occupiedRobotType;

    }

}
