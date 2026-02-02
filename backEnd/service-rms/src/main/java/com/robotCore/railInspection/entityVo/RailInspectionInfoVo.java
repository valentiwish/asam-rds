package com.robotCore.railInspection.entityVo;

import lombok.Data;

/**
 * @Des: 挂轨巡检信息
 * @author: zxl
 * @date: 2023/11/6
 **/
@Data
public class RailInspectionInfoVo {

    private String id;

    private Integer serialNumber;

    private String inspectionTime;  //巡检时间

    private String taskName;

    private  String taskType;

    private String inspectionObject;

    private  String inspectionType;

    private Integer resultType;

    private String inspectionResult; //巡检结果

    private String pictureId;	//巡检图片ID




}
