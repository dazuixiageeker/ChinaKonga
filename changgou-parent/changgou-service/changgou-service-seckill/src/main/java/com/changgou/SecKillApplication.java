package com.changgou;

import com.changgou.entity.IdWorker;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author MartinMYZ
 * @description 
 * @created at 2019/8/28
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.changgou.seckill.dao")
@EnableScheduling //开启定时任务
@EnableAsync            //开启异步执行
public class SecKillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecKillApplication.class, args);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker();
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
    /*创建秒杀队列
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
    }*/

    /**
     * 到期数据队列
     * @return
     *//*
    @Bean
    public Queue seckillOrderTimerQueue() {
        return new Queue(env.getProperty("mq.pay.queue.seckillordertimer"), true);
    }

    *//**
     * 超时数据队列
     * @return
     *//*
    @Bean
    public Queue delaySeckillOrderTimerQueue() {
        return QueueBuilder.durable(env.getProperty("mq.pay.queue.seckillordertimerdelay"))
                .withArgument("x-dead-letter-exchange", env.getProperty("mq.pay.exchange.order"))        // 消息超时进入死信队列，绑定死信队列交换机
                .withArgument("x-dead-letter-routing-key", env.getProperty("mq.pay.queue.seckillordertimer"))   // 绑定指定的routing-key
                .build();
    }

    *//***
     * 交换机与队列绑定
     * @return
     *//*
    @Bean
    public Binding basicBinding() {
        return BindingBuilder.bind(seckillOrderTimerQueue())
                .to(basicExchange())
                .with(env.getProperty("mq.pay.queue.seckillordertimer"));
    }
*/
}