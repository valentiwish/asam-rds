package com.robotCore.feign;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Description: 描述
 * @Author: Created by lufan on 2022/3/24.
 */
@Component
public class IServiceLogFallBackFactory implements FallbackFactory<IServiceLog> {

    @Override
    public IServiceLog create(Throwable throwable) {
        return new IServiceLog() {
            @Override
            public Object save(String data) {
                return null;
            }
        };
    }
}