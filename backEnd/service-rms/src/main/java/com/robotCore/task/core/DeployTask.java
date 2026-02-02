package com.robotCore.task.core;


import com.alibaba.fastjson.JSON;
import com.beans.redis.RedisUtil;
import com.robotCore.Vo.OpsLogVO;
import com.robotCore.common.base.controller.BaseController;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.feign.IServiceLog;
import com.robotCore.scheduing.service.RobotInfoService;
import com.robotCore.scheduing.vo.RobotMonitorVO;
import com.utils.https.ServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@EnableScheduling
public class DeployTask extends BaseController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    IServiceLog iServiceLog;
    @Autowired
    private RobotInfoService robotInfoService;

    //调度系统机器人模块，每1s请求一次数据
//    @Scheduled(cron=" */1 * * * * ?")
    public void getRobotInfo() {
        Map<String, RobotMonitorVO> map = null;
        try {

        } catch (Exception e) {
            log.error("从redis中获取数据，解析robot_push_less_res数据出错:{}", e.getMessage());
        }
    }

    public void saveLog(HttpServletRequest request,String desp,boolean flag){
        UserVO curUser = getCurUser();//获取当前登录人
        String ip= ServletRequest.getRemoteIP(request);
        OpsLogVO opsLogVO=new OpsLogVO(ip,new Timestamp(new Date().getTime()),curUser.getUserName(),desp,flag?1:0,2+"");
        iServiceLog.save(JSON.toJSONString(opsLogVO));
    }
}








