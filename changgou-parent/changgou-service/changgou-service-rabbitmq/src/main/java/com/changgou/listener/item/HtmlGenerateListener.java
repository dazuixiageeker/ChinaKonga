package com.changgou.listener.item;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Message;
import com.changgou.item.feign.PageFeign;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/19
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Component
@RabbitListener(queues = "topic.queue.spu")
public class HtmlGenerateListener {

    @Autowired
    private PageFeign pageFeign;

    /***
     * 生成静态页/删除静态页
     * @param msg
     */
    @RabbitHandler
    public void getInfo(String msg){
        //将数据转成Message
        Message message = JSON.parseObject(msg,Message.class);
        if(message.getCode()==2){
            //审核，生成静态页
            pageFeign.createHtml(Long.parseLong(message.getContent().toString()));
        }
    }
}
