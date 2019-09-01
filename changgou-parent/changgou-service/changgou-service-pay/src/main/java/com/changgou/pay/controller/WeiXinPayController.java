package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.pay.service.WeiXinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/27
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */

@RestController
@RequestMapping("/weixin/pay")
public class WeiXinPayController {

    @Autowired
    private WeiXinPayService weiXinPayService;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;


    /**
     * 支付二维码查询成功
     * @param
     * @param parameter
     * @return
     */
    @RequestMapping("/create/native")
    public Result createNative(@RequestParam Map<String, String> parameter){
        Map<String, String> map = weiXinPayService.createNative(parameter);
        return new Result(true, StatusCode.OK, "支付二维码创建成功",map);
    }


    /**
     * 查询订单支付状态
     * @param outtradeno
     * @return
     */
    @RequestMapping("/query/status")
    public Result queryStatus(String outtradeno){
        Map<String, String> map = weiXinPayService.queryStatus(outtradeno);
        return new Result(true, StatusCode.OK, "订单支付状态查询成功",map);
    }

    /**
     * 回调通知
     * @return
     */
    @RequestMapping("/notify/url")
    public String notifyurl(HttpServletRequest request) throws Exception {
        //获取回调的微信通知数据
        ServletInputStream inputStream = request.getInputStream();//网络流对象
        //将数据写出流, 写到内存
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //定义缓冲区
        byte[] buffer = new byte[1024];
        int len = -1;
        while((len = inputStream.read(buffer))!=-1){
            os.write(buffer,0,len);
        }
        os.flush();
        os.close();
        inputStream.close();
        //回调的数据
        String strXML = new String(os.toByteArray(),"UTF-8");
        Map<String, String> map = WXPayUtil.xmlToMap(strXML);
        //坑: 发送的map, 消费者解析不了, 转成json串
        Map<String,String> attach = JSON.parseObject(map.get("attach"), Map.class);
        System.out.println(attach);
        String exchange = env.getProperty(attach.get("exchange"));
        String routingkey = env.getProperty(attach.get("routingkey"));
        //将回调数据发送给mq
        rabbitTemplate.convertAndSend(exchange,routingkey, JSON.toJSONString(map));
        // System.out.println("回调数据为"+ strXML);
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }
}
