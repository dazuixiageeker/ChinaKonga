package com.changgou;

import com.changgou.entity.FeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * @Author MartinMYZ
 * @description 购物车渲染启动类
 * @created at 2019/8/23
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.changgou.order.feign","com.changgou.user.feign"})

public class OrderWebApplication {

    public static void main(String[] args) {

        SpringApplication.run(OrderWebApplication.class, args);
    }

    /**
     * 创建拦截器feignInterceptor对象
     * @return
     */
    @Bean
    public FeignInterceptor feignInterceptor(){
        return new FeignInterceptor();
    }
}