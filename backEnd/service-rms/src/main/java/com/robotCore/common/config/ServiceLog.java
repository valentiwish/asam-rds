package com.robotCore.common.config;

import java.lang.annotation.*;

/**
 * @Description:
 * @Author: Created by crystal on 2020/4/27.
 * @Modifier: Modify by crystal on 2020/4/27.
 */
//作用在参数和方法上
@Target({ElementType.PARAMETER, ElementType.METHOD})
// 表示运行时注释
@Retention(RetentionPolicy.RUNTIME)
// 表明这个注释应该做javadoc工具记录
@Documented
public @interface ServiceLog {

    String value() default "";

    OpsLogType type() default OpsLogType.SELECT;

    String category() default "SYS";

}
