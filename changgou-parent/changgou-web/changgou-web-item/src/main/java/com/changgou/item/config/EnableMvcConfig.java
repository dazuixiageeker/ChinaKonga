package com.changgou.item.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/19
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Configuration
@ControllerAdvice
public class EnableMvcConfig implements WebMvcConfigurer {

    /**
     * 对请求资源放行
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //对资源放行
        registry.addResourceHandler("/items/**")
                .addResourceLocations("classpath:/templates/items/");
    }
}
