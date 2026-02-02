package com.robotCore.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Administrator on 2022/5/20.
 */
@FeignClient(name = "mes-service-38901", fallbackFactory = IServiceMesFallBackFactory.class)
public interface IServiceMes {

    /**
     * 根据门的反馈变量更新门的信息
     */
    @PostMapping("/workDoor/updateDoorStatus")
    void updateDoorStatus(@RequestParam("data")String data);

}
