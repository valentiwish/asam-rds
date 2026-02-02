package com.robotCore.common.config;

import com.utils.tools.TokenUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.beans.exception.Constant;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.utils.IpUtil;
import com.robotCore.feign.IServiceLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @Description: 日志记录切面
 * @Author: Created by crystal on 2020/4/27.
 * @Modifier: Modify by crystal on 2020/4/27.
 */
@Aspect
@Component
@Slf4j
@SuppressWarnings("all")
public class LogAspect {

    /**
     * 本地异常日志记录对象
     */
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LogAspect.class);

    private static LogAspect logAspect;

    /**
     * 注入Service用于把日志保存到数据库，实际项目入库采用队列做异步
     */
    @Autowired
    private IServiceLog iServiceLog;


    @PostConstruct
    public void init() {
        logAspect = this;
        logAspect.iServiceLog = this.iServiceLog;
    }

    /**
     * @Description: 记录日志，登陆日志，操作日志等
     * @param loginName
     * @param url
     * @param ip
     * @param opsLogTypeName
     * @param args
     */
    public void saveLog(UserVO user, String url, String ip, String category, OpsLogType type, String desp, Map<String, String[]> args, Integer opResult){
        // 保存日志
        OpsLogVo sysOpsLog = new OpsLogVo();
        sysOpsLog.setOpUserName(user.getUserName());
        sysOpsLog.setIp(ip);
        sysOpsLog.setCategory(OpsLogVo.CATEGORY_APPLICATION);
        sysOpsLog.setOpTime(new Timestamp(System.currentTimeMillis()));
        sysOpsLog.setOpResult(opResult);
        StringBuilder sb = new StringBuilder();
//        sb.append(loginName).append("|");
//        sb.append(url);
//        if(null != type) {
//            sb.append("|").append(type.getName());
//        }
        sb.append(desp);
        // 将参数所在的数组转化为JSON
        String params = null;
        try {
//            params = JSONObject.toJSONString(args);
//            if(null != params) {
//                sb.append("|").append(params);
//            }
            sysOpsLog.setOpDescription(sb.toString());
            String data = JSONObject.toJSONString(sysOpsLog);
            logAspect.iServiceLog.save(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning(value = "@annotation(com.robotCore.common.config.ControllerLog)", returning = "ret")
    public void doControllerAfter(JoinPoint joinPoint, Object ret){
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader(Constant.TOKEN_KEY);
            // 获取用户名等信息
            UserVO userVO = new UserVO();
            userVO = TokenUtils.getTByObjectId(token, UserVO.class);

            // 获取URL地址
            String url = request.getRequestURI().toString();
        /*String url0 = request.getScheme() + "://"+ request.getServerName() + request.getRequestURI() + "?" + request.getQueryString();
        String url1 = request.getScheme() + "_" + request.getServerName() + "_" + request.getRequestURI() + "_" + request.getQueryString();
        System.out.println("获取全路径（协议类型：//域名/项目名/命名空间/action名称?其他参数）url0 = " + url0);
        System.out.println("获取全路径（协议类型：//域名/项目名/命名空间/action名称?其他参数）url1 = " + url1);*/

            // 获取用户IP地址
            String ip = IpUtil.getIpAddress(request);

            // 获取访问参数
            Map<String, String[]> params = request.getParameterMap();

            // 从切面织入处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature)joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            ControllerLog operation = method.getAnnotation(ControllerLog.class);
            String opsLogTypeName = null;

            // 获取请求的controller类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            Integer opResult = 0;
            JSONObject jsonObject = (JSONObject)JSONObject.toJSON(ret);
            Map<String, Object> result  = jsonObject;
            if(ObjectUtils.isNotEmpty(result)) {
                if("200".equals(result.get("code"))) {
                    opResult = 1;
                }
            }
            String desp = operation.value();
            if(operation.type().equals(OpsLogType.ADDORUPDATE)) {
                Map<String, Object> result2  = JSON.parseObject(params.get("data")[0]);
                if(ObjectUtils.isNotEmpty(result2.get("id"))) {
                    desp = "更新" + operation.value();
                } else {
                    desp = "新增" + operation.value();
                }
            }
            this.saveLog(userVO, url, ip, operation.category(), operation.type(), desp, params, opResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @AfterReturning(value = "@annotation(com.mes.common.config.ServiceLog)")
//    public void doServiceAfter(JoinPoint joinPoint) {
//
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String token = request.getHeader(Constant.TOKEN_KEY);
//        // 获取用户名等信息
//        String userName = TokenUtils.getUserId(token);
//
//        // 获取URL地址
//        String url = request.getRequestURI().toString();
//
//        // 获取用户IP地址
//        String ip = IpUtil.getIpFromRequest(request);
//
//        // 获取访问参数
//        Map<String, String[]> params = request.getParameterMap();
//
//        // 从切面织入处通过反射机制获取织入点处的方法
//        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
//        // 获取切入点所在的方法
//        Method method = signature.getMethod();
//        // 获取操作
//        ServiceLog operation = method.getAnnotation(ServiceLog.class);
//
//        // 获取请求的controller类名
//        String className = joinPoint.getTarget().getClass().getName();
//        // 获取请求的方法名
//        String methodName = method.getName();
//
//        this.saveLog(userName, url, ip, operation.category(), operation.type(), operation.value(), params);
//    }

}
