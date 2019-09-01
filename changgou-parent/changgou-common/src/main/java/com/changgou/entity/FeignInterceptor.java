package com.changgou.entity;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @Author MartinMYZ
 * @description 拦截feign调用并将头信息存入
 * @created at 2019/8/24
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        try {
            //使用RequestContextHolder工具获取request相关变量
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes != null){
                HttpServletRequest request = attributes.getRequest();
                //获取所有的头文件信息的key
                Enumeration<String> headerNames = request.getHeaderNames();
                if( headerNames !=null){
                    while(headerNames.hasMoreElements()){
                        //头文件的key
                        String name = headerNames.nextElement();
                        //头文件的value
                        String value = request.getHeader(name);
                        //将令牌数据添加到头文件中;
                        requestTemplate.header(name,value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
