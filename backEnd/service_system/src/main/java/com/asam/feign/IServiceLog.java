package com.asam.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description: 描述
 * @Author: Created by lufan on 2022/3/24.
 */
@FeignClient(name = "service-log", fallbackFactory = IServiceLogFallBackFactory.class)
public interface IServiceLog {

    @PostMapping("/opsLog/save")
    public Object save(@RequestParam("data") String data);
}