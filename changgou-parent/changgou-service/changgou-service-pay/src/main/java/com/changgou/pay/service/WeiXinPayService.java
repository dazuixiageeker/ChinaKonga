package com.changgou.pay.service;

import java.util.Map;

/**
 *
 */
public interface WeiXinPayService {

    /**
     * 生成支付二维码
     * @param map 其他附加信息
     * @param
     * @return
     */
    Map<String, String> createNative(Map<String, String> map);

    /**
     * 查询订单支付状态
     * @param out_trade_no
     * @return
     */
    Map<String, String> queryStatus(String out_trade_no);


    /**
     * 关闭支付
     * @param orderId
     * @return
     * @throws Exception
     */
    Map<String, String> closePay(Long orderId) throws Exception;
}
