package com.robotCore.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;
import java.util.Objects;

/**
 * @Description: 描述
 * @Author: Created by lufan on 2021/3/19.
 */
//@Component
public class MyMetaObjectHandler implements MetaObjectHandler{

    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();

        Object createTime = getFieldValByName("createTime", metaObject);
        if (Objects.isNull(createTime)) {
            setFieldValByName("createTime", now, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Date now = new Date();

        Object updateTime = getFieldValByName("updateTime", metaObject);
        if (Objects.isNull(updateTime)) {
            setFieldValByName("updateTime", now, metaObject);
        }
    }
}