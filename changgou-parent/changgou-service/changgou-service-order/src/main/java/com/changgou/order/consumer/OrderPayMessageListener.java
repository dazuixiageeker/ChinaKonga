package com.changgou.order.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author MartinMYZ
 * @description 监听订单支付的消息
 * @created at 2019/8/27
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */



@Component
@RabbitListener(queues = {"${mq.pay.queue.order}"})
public class OrderPayMessageListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void readMessage(String text){
        //消息: 支付的消息
        Map<String, String> map = JSON.parseObject(text,Map.class);
        //业务实现,根据回调数据
        String return_code = map.get("return_code");
        if("SUCCESS".equals(return_code)){
            //判断业务结果
            String result_code = map.get("result_code");
            if("SUCCESS".equals(result_code)){
                //成功
                orderService.updatePayStatus(map.get("out_trade_no"),map.get("transaction_id"));
            }else{
                //失败
                orderService.deleteOrderwhenFailed(map.get("out_trade_no"));
            }
        }
    }
}
