package com.robotCore;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan({ "com.gitee.sunchenbin.mybatis.actable.dao.*" }) // 自动创建表扫描DAO
@ComponentScan({ "com.gitee.sunchenbin.mybatis.actable.manager.*" }) // 自动创建表扫描DAO
@EnableDiscoveryClient //Consul注册
@EnableFeignClients //服务间调用
@EnableHystrix
@SpringBootApplication(exclude = {DynamicDataSourceAutoConfiguration.class,RabbitAutoConfiguration.class})
@ComponentScan(basePackages = { "com.beans.tools","com.beans.feignconfg", "com.beans.shiro","com.beans.exception","com.beans.redis"})
@ComponentScan(basePackages = {"com.robotCore.**", "com.netsdk.**"})
@MapperScan("com.robotCore.*.mapper")
@ComponentScan(basePackages = {"com.beans.redis" })
@ComponentScan(basePackages = {"socket.handler.**" })
@EnableAsync
public class RobotCoreApplication {
	public static void main(String[] args) {
		//设置headless=false，
		SpringApplicationBuilder builder = new SpringApplicationBuilder(RobotCoreApplication.class).headless(false).web(WebApplicationType.NONE);
//		ApplicationContext context = builder.headless(false).web(WebApplicationType.NONE).run(args);
		SpringApplication.run(RobotCoreApplication.class, args);
	}
}
