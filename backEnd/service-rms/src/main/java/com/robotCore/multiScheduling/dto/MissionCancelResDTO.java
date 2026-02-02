package com.robotCore.multiScheduling.dto;

import lombok.Data;

/**
 * @Des: misson取消任务返回
 * @author: zxl
 * @date: 2025/10/15
 **/
@Data
public class MissionCancelResDTO {

    //作业状态,成功：SUCCESS  失败：ERROR
    private String status;

    //误码，当status=ERROR才有值, 当错误编码为 1001 时, 表
    // 示机器人当前状态为离线/断线,建议处理方式： 提示“机器人已离线, 是否强制取消？”， 后续设置强制停止后重新调用接口
    private Integer errorCode;
}
