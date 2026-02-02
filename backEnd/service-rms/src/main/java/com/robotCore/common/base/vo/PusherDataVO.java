package com.robotCore.common.base.vo;

import lombok.Data;

/***
 * @Description: 推送数据对象
 * @Author: zhangqi
 * @Create: 2024/10/30
 */
@Data
public class PusherDataVO {

    /***
     * appid
     */
    public String appid;

    /***
     * appsecret
     */
    public String appsecret;

    /***
     * roomKey：分组主题
     */
    public String roomKey;

    /***
     * 数据主题
     */
    public String pusherKey;

    /***
     * 是否需要回传结果
     */
    public Integer needResult = 0;

    /***
     * 推送数据
     */
    public Object pusherData;

}
