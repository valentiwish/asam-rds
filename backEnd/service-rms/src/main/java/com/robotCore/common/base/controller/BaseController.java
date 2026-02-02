package com.robotCore.common.base.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.beans.exception.Constant;
import com.robotCore.common.base.model.BaseModeAuto;
import com.robotCore.common.base.model.BaseModelU;
import com.robotCore.common.base.vo.OrganizationVO;
import com.robotCore.common.base.vo.UserVO;
import com.robotCore.common.utils.JWTUtil;
import com.robotCore.common.utils.ObjectUtil;
import com.robotCore.feign.IServiceUser;
import com.utils.tools.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
public class BaseController<T> {

    @Autowired
    private IServiceUser iServiceUser;

    public UserVO getCurUser() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        String token = request.getHeader(Constant.TOKEN_KEY);
        UserVO  userVO = TokenUtils.getTByObjectId(token, UserVO.class);
        if (userVO == null || ObjectUtil.isEmpty(userVO.getId())) {
            String loginName = JWTUtil.getLoginName(token);
            userVO = iServiceUser.getUserVOByLoginName(loginName);
            Object object = iServiceUser.findRolesByLoginName(loginName);
            if(ObjectUtils.isNotEmpty(object)) {
                userVO.setUserRoleVoList(JSON.parseObject(JSON.toJSONString(object), List.class));
            }
        }
        return userVO;
    }

    public OrganizationVO getOrgInfo(String deptId) {
        OrganizationVO orgVO = iServiceUser.getDeptById(deptId);
        return orgVO;
    }

    /**
     * 根据当前登录的用户信息，填充BaseModelU中的数据
     * @param entity
     * @param <S>
     * @return
     */
    public <S extends T> S fillUserData(S entity) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        UserVO user = this.getCurUser();
        Long userId = null;
        String userName = "";
        if(Objects.nonNull(user)) {
            userId = Long.parseLong(user.getId().toString());
            userName = user.getUserName().toString();
        }
        if (entity instanceof BaseModelU) {
            BaseModelU baseModelU = (BaseModelU) entity;
            if(Objects.nonNull(user)) {
                if(Objects.isNull(baseModelU.getId())) {
                    baseModelU.setCreateId(userId);
                    baseModelU.setCreateUser(userName);
                } else {
                    baseModelU.setUpdateId(userId);
                    baseModelU.setUpdateUser(userName);
                }
            }
        }
        if (entity instanceof BaseModeAuto) {
            BaseModeAuto baseModeAuto = (BaseModeAuto) entity;
            if(Objects.nonNull(user)) {
                if(Objects.isNull(baseModeAuto.getId())) {
                    baseModeAuto.setCreateId(userId);
                    baseModeAuto.setCreateUser(userName);
                } else {
                    baseModeAuto.setUpdateId(userId);
                    baseModeAuto.setUpdateUser(userName);
                }
            }
        }
        return entity;
    }
}
