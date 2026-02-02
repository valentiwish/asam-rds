package com.robotCore.multiScheduling.dto;

import lombok.Data;

/**
 * @Des: 米松创建作业的请求参数DTO
 * @author: zxl
 * @date: 2025/9/11
 **/
@Data
public class MissionWorksReqDTO {

    //调度系统预设的任务id。
    private String missionId;

    //可从任务管理的编辑界面查看到该字段
    //missionId和missionCode二者必传其一
    private String missionCode;

    //指定执行任务的agv编号
    private String agvCode;

    //用户可以在调度系统预设任务时添加全局变量，并在执行作业过程中使用该全局变量。
    //外部系统创建作业时通过为全局变量赋值，以实现动态传参的目的
    //例如：外部系统创建作业时动态指定目标点。
    //"runtimeParam": "{\"endPoint\":\"1001\"}",
    private String runtimeParam;

    //作业状态变更或完成时，调度系统将发送消息给此url（例如: localhost:8080/api/callback），回调接口类型必须是POST，回调数据格式为：
    //{
    //"missionWorkId":"作业ID，可用该值查询作业",
    //"agvCode":"",
    //"status":"作业状态",
    //"errorCode":"",
    //"errorMessage":""
    //}
    private String callbackUrl;

}
