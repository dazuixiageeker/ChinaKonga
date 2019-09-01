package com.changgou.filter;

/**
 * @Author MartinMYZ
 * @description 过滤需要用户登录的地址
 * @created at 2019/8/23
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
public class URLFilter {
    //购物车订单微服务都需要用户登录,必须携带令牌,所以所有路径都过滤,订单服务需要过滤的地址为/api/worder/**, /api/cart/**
    public static String orderFilterPath = "/api/cart/**,/api/categoryReport/**,/api/orderConfig/**,/api/order/**,/api/orderItem/**,/api/orderLog/**,/api/preferential/**,/api/returnCause/**,/api/returnOrder/**,/api/returnOrderItem/**";
    public static String userFilterPath = "/api/user/**,/api/address/**";


    /**
     * 检查请求路径是否需要进行权限校验
     * @param uri
     * @return
     */
    public static Boolean hasAuthorization(String uri){
        //处理orderFilterPath
        //替换所有的**
        String[] urls = orderFilterPath.replace("**", "").split(",");
        for (String url : urls) {
            //判断当前路径是否包含过滤器中路径
            if(uri.startsWith(url)){
                return true;
            }
        }

        String[] userUrls = userFilterPath.replace("**", "").split(",");
        for (String url : urls) {
            //判断当前路径是否包含过滤器中路径
            if(uri.startsWith(url)){
                return true;
            }
        }
        return false;
    }
}
