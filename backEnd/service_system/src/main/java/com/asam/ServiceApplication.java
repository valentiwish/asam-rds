package com.asam;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@MapperScan({"com.gitee.sunchenbin.mybatis.actable.dao.*"}) //自动创建表扫描DAO
@ComponentScan({"com.gitee.sunchenbin.mybatis.actable.manager.*"}) //自动创建表扫描DAO
@EnableDiscoveryClient //Consul注册
@EnableFeignClients //服务间调用
@EnableHystrix
@SpringBootApplication(exclude = { RabbitAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@ComponentScan(basePackages = {"com.beans.shiro", "com.beans.exception", "com.beans.tools","com.beans.feignconfg"})
@ComponentScan(value = "com.asam.config")
@MapperScan({"com.asam.*.mapper"})
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}
