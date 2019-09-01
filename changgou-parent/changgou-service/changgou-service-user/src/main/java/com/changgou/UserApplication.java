package com.changgou;

import com.changgou.entity.TokenParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author MartinMYZ
 * @description 启动类
 * @created at 2019/8/20
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.changgou.user.dao")
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class);
    }


    //注入tokenparser, 通过该类获得登录人信息
    @Bean
    public TokenParser tokenParser(){
        return new TokenParser();
    }
}
