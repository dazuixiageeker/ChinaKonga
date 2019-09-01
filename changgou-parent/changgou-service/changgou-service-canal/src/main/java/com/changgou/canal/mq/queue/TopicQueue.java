package com.changgou.canal.mq.queue;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/19
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
@Configuration
public class TopicQueue {
    public static final String TOPIC_QUEUE_SPU = "topic.queue.spu";
    public static final String TOPIC_EXCHANGE_SPU = "topic.exchange.spu";

    /**
     * Topic模式 SPU变更队列
     * @return
     */
    @Bean(name=TOPIC_QUEUE_SPU)
    public Queue topicQueueSpu() {
        return new Queue(TOPIC_QUEUE_SPU);
    }

    /***
     * SPU队列交换机
     * @return
     */
    @Bean(name=TOPIC_EXCHANGE_SPU)
    public TopicExchange topicSpuExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_SPU);
    }

    /***
     * 队列绑定交换机
     * @return
     */
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueueSpu()).to(topicSpuExchange()).with(TOPIC_QUEUE_SPU);
    }

}
