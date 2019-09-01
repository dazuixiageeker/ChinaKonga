package com.changgou;

import com.changgou.entity.FeignInterceptor;
import com.changgou.entity.IdWorker;
import com.changgou.entity.TokenParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author MartinMYZ
 * @description 购物车启动类
 * @created at 2019/8/23
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.changgou.order.dao")
@EnableFeignClients(basePackages = {"com.changgou.goods.feign","com.changgou.user.feign"})
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }


    @Bean
    public TokenParser tokenParser(){
        return new TokenParser();
    }

    /**
     * 生成唯一ID,
     * @return
     */
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1,1);
    }


    @Bean
    public FeignInterceptor feignInterceptor(){
        return new FeignInterceptor();
    }
}