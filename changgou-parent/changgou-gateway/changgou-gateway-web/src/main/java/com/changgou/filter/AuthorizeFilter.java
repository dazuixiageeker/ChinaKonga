package com.changgou.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author MartinMYZ
 * @description 自定义全局过滤器, 判断用户有无token, 并验证
 * @created at 2019/8/21
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    //定义常量
    private static final String AUTHORIZE_TOKEN = "Authorization";
    //登录页面
    private static final String LOGIN_URL = "http://localhost:9001/oauth/login";

    /**
     * 业务处理
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取request, response;
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //判断用户是否登录
        //获得请求路径
        String path = request.getURI().getPath();

        //1. 判断是否是登录路径,是,直接放行
        if (path.startsWith("/api/user/login") || path.startsWith("/api/brand/search")) {
            Mono<Void> filter = chain.filter(exchange);
            return filter;
        }
        //2. 其他请求,判断用户是否登录,获取token
        //2.1 从请求参数中获取token
        String token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        //2.2从请求头获取token
        if (StringUtils.isEmpty(token)) {
            token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        }
        //2.3从cookie中获得token
        if (StringUtils.isEmpty(token)) {
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (cookie != null) {
                token = cookie.getValue();
            }
        }
        // 3. 没有token ,不放行
        if (StringUtils.isEmpty(token)) {
            //设置方法不允许被访问,405错误

            //踢回到登录页面
            // 踢（重定向）回到登录页面：携带当前请求的url
            response.setStatusCode(HttpStatus.SEE_OTHER);
            String url = LOGIN_URL + "?ReturnUrl=" + request.getURI().toString();
            response.getHeaders().add("Location", url);
            return response.setComplete();
        }

        //4. token存在, 进行解析
        try {
            //解析成功,
            //Claims claims = JwtUtil.parseJWT(token);
            //调用其他微服务,需要走网关, 需要把token存放到头信息中
            request.mutate().header("Authorization","Bearer " + token);
        } catch (Exception e) {
            e.printStackTrace();
            // 踢（重定向）回到登录页面：携带当前请求的url
            response.setStatusCode(HttpStatus.SEE_OTHER);
            String url = LOGIN_URL + "?ReturnUrl=" + request.getURI().toString();
            response.getHeaders().add("Location", url);
            return response.setComplete();
        }

        //放行
        return chain.filter(exchange);
    }


    /**
     * 指定过滤器执行顺序
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
