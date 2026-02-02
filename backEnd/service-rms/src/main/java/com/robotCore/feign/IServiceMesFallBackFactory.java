package com.robotCore.feign;


import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2022/5/20.
 */
@Component
public class IServiceMesFallBackFactory implements FallbackFactory<IServiceMes> {

    @Override
    public IServiceMes create(Throwable cause) {

        return new IServiceMes() {

            @Override
            public void updateDoorStatus(String data) {
                cause.getMessage();
            }


        };
    }
}
