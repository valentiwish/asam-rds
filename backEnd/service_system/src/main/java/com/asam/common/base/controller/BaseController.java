package com.asam.common.base.controller;

import com.asam.common.util.ObjectUtil;
import com.asam.human.entity.UserVO;
import com.asam.human.service.HumUserService;
import com.asam.system.entity.SysAccount;
import com.asam.system.service.AccountService;
import com.beans.exception.Constant;
import com.beans.tools.ConfigSwagger;
import com.utils.tools.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description：获取当前登录人信息
 * @Author: Created by malingna on 2020-10-19
 * @Modifier: Modify by malingna on 2020-10-19
 */
public class BaseController {

    @Autowired
    private HumUserService humUserService;
    @Autowired
    private AccountService accountService;

    //当前登录名，去匹配用户表中的电话号码，身份证，邮箱，和登录账号，查询用户信息
    public String getCurLoginName() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String varToken = request.getHeader(ConfigSwagger.browserToken);
        if (null == varToken) {
            varToken = request.getHeader(ConfigSwagger.swaggerToken);// 获取用户登录凭证
        }
        String token = request.getHeader(Constant.TOKEN_KEY);
        if(token.equals("null")||token==null){
            return null;
        }else {
            String loginName = TokenUtils.getUserId(token);
            return loginName;
        }
    }

    //当前登录人员信息
    public UserVO getCurUser() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader(Constant.TOKEN_KEY);
        if(token.equals("null")||token==null){
            return null;
        }else {
            UserVO userVO = TokenUtils.getTByObjectId(token, UserVO.class);
            if (userVO == null || ObjectUtil.isEmpty(userVO.getId())) {
                String loginName = getCurLoginName();
                if (loginName != "") {
                    userVO = humUserService.getUserByLoginName(loginName);
                }
            }
            return userVO;
        }
    }

    //当前登录账户信息
    public SysAccount getCurAccount() {
        UserVO userVO  = getCurUser();
        if(userVO!=null) {
            SysAccount sysAccount = accountService.findModel(userVO.getId());
            return sysAccount;
        }else{
            return null;
        }
    }
}
