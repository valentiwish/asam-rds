package com.asam.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/***
 * @Description: 读取自定义配置文件中的数据
 * @Author: Created by pjb on 2020/9/21 10:49
 * @Modifier: Modify by pjb on 2020/9/21 10:49
 */
@PropertySource("classpath:config/myConfig.properties")
@Data
@Component
public class MyConfig {

    //上传文件存储路径
    @Value("${file.upload.path}")
    private String uploadPath;

}
