package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.HttpClient;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/27
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {


    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.partner}")
    private String partner;
    @Value("${weixin.partnerkey}")
    private String partnerkey;
    @Value("${weixin.notifyurl}")
    private String notifyurl;

    /**
     * 生成支付二维码
     *
     * @param
     * @return
     */
    @Override
    public Map<String, String> createNative(Map<String, String> parameter) {
        try {
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            //封装接口参数(map)
            Map<String, String> data = new HashMap<>();
            data.put("appid", appid);  //开放平台id
            data.put("mch_id", partner); //商户id
            data.put("body", "小马超市"); //商户id
            data.put("nonce_str", WXPayUtil.generateNonceStr());  //产生随机字符串
            //data.put("sign",) //指定签名
            data.put("out_trade_no", parameter.get("outtradeno") );  //订单号
            data.put("total_fee", parameter.get("money"));            //支付金额
            data.put("spbill_create_ip", "127.0.0.1"); //终端ip地址
            data.put("notify_url", notifyurl);   //通知结果的url地址
            data.put("trade_type", "NATIVE");     //支付类型

            String username = parameter.get("username");
            String exchange = parameter.get("exchange");
            String routingkey = parameter.get("routingkey");
            Map<String, String> attachMap = new HashMap<>();
            attachMap.put("username",username);
            attachMap.put("exchange",exchange);
            attachMap.put("routingkey",routingkey);
            data.put("attach", JSON.toJSONString(attachMap));  //消息需要发送的队列名称

            //将参数转成xml
            String signedXml = WXPayUtil.generateSignedXml(data,partnerkey);
            //创建httpClient进行调用,发送请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();
            //调用统一下单的api,(接口地址已提供)
            //获取响应数据
            String strXml = httpClient.getContent();
            //将响应数据转成map,
            Map<String, String> map = WXPayUtil.xmlToMap(strXml);
            map.put("out_trade_no", parameter.get("outtradeno"));
            map.put("total_fee", parameter.get("money"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询订单
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, String> queryStatus(String out_trade_no) {
        try {
             //指定接口地址, 查询订单的api
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            //构建接口需要的数据
            //封装接口参数(map)
            Map<String, String> data = new HashMap<>();
            data.put("appid", appid);  //开放平   台id
            data.put("mch_id", partner); //商户id
            data.put("nonce_str", WXPayUtil.generateNonceStr());  //产生随机字符串
            //data.put("sign",) //指定签名
            data.put("out_trade_no", out_trade_no);  //订单号
            //将参数转成xml
            String signedXml = WXPayUtil.generateSignedXml(data, partnerkey);
            //创建httpClient进行调用,发送请求

            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();
            //调用统一下单的api,(接口地址已提供)
            //获取响应数据
            String strXml = httpClient.getContent();
            //将响应数据转成map,
            Map<String, String> map = WXPayUtil.xmlToMap(strXml);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 关闭微信支付
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, String> closePay(Long orderId) throws Exception {
        //参数设置
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("appid",appid); //应用ID
        paramMap.put("mch_id",partner);    //商户编号
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符
        paramMap.put("out_trade_no",String.valueOf(orderId));   //商家的唯一编号

        //将Map数据转成XML字符
        String xmlParam = WXPayUtil.generateSignedXml(paramMap,partnerkey);

        //确定url
        String url = "https://api.mch.weixin.qq.com/pay/closeorder";

        //发送请求
        HttpClient httpClient = new HttpClient(url);
        //https
        httpClient.setHttps(true);
        //提交参数
        httpClient.setXmlParam(xmlParam);

        //提交
        httpClient.post();

        //获取返回数据
        String content = httpClient.getContent();

        //将返回数据解析成Map
        return  WXPayUtil.xmlToMap(content);
    }
}
