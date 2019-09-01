package com.changgou;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @Author MartinMYZ
 * @description 
 * @created at 2019/8/27
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
public class WeiXinPayApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeiXinPayApplication.class, args);
    }


    @Autowired
    private Environment env;

    //创建队列
    @Bean
    public Queue queueOrder(){
        return new Queue(env.getProperty("mq.pay.queue.order"),true);
    }
    //创建交换机
    @Bean
    public Exchange exchangeOrder(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"),true,false);
    }


    //将队列绑定到交换机
    @Bean
    public Binding bindingQueue2Exchange(){
        return BindingBuilder
                .bind(queueOrder())
                .to(exchangeOrder())
                .with(env.getProperty("mq.pay.routing.key"))
                .noargs();
    }
    //***************************秒杀订单队列*************************************************//
    //创建秒杀队列
    @Bean
    public Queue queueSeckillOrder(){
        return new Queue(env.getProperty("mq.pay.queue.seckillorder"),true);
    }

    //创建秒杀交换机
    @Bean
    public Exchange seckillorderExchange(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.seckillorder"),true,false);
    }

    //将秒杀队列绑定到交换机
    @Bean
    public Binding bindingQueue2ExchangeSeckillOrder(Queue queueSeckillOrder, Exchange seckillorderExchange){
        return BindingBuilder
                .bind(queueSeckillOrder)
                .to(seckillorderExchange)
                .with(env.getProperty("mq.pay.routing.seckillorderkey"))
                .noargs();
    }

}


