package com.changgou.seckill.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author MartinMYZ
 * @description 监听消息
 * @created at 2019/8/29
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Component
@RabbitListener(queues = "${mq.pay.queue.seckillorder}")
public class SeckillOrderPayMessageListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 监听消费消息
     * @param message
     */
    @RabbitHandler
    public void consumeMessage(@Payload String message){
        System.out.println(message);
        //将消息转换为map对象
        Map<String, String> resultMap = JSON.parseObject(message, Map.class);
        System.out.println("监听的消息为:" + resultMap);

        String return_code = resultMap.get("return_code");
        String result_code = resultMap.get("result_code");
        if (return_code.equalsIgnoreCase("success")){
            //获取outtradeno
            String outTradeNo = resultMap.get("out_trade_no");
            //获取附加消息
            Map<String, String> attachMap = JSON.parseObject(resultMap.get("attach"), Map.class);
            //支付成功
            if(result_code.equalsIgnoreCase("success")){
                seckillOrderService.updatePayStatus(outTradeNo,resultMap.get("transaction_id"),attachMap.get("username"));
            }else{
                //支付失败,删除订单
                seckillOrderService.closeOrder(attachMap.get("username"));
            }
        }

        System.out.println("");

    }




}
