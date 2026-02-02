package com.asam.common.logUtil;

import java.lang.annotation.*;

/**
 * @Description: 定义一个拦截controller的注释
 * @Author: Created by crystal on 2020/4/27.
 * @Modifier: Modify by crystal on 2020/4/27.
 */
//作用在参数和方法上
@Target({ElementType.PARAMETER, ElementType.METHOD})
// 表示运行时注释
@Retention(RetentionPolicy.RUNTIME)
// 表明这个注释应该做javadoc工具记录
@Documented
public @interface ControllerLog {

    String value() default "";

    OpsLogType type() default OpsLogType.SELECT;

    String category() default SysOpsLog.CATEGORY_APPLICATION;
}
