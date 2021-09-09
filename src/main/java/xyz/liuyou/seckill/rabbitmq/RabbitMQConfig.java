package xyz.liuyou.seckill.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static xyz.liuyou.seckill.rabbitmq.MQConstants.*;


/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/4 21:39
 * @decription rabbitmq配置类
 **/
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue seckillQueue(){
        return new Queue(SECKILL_QUEUE);
    }

    @Bean
    public TopicExchange seckillExchange(){
        return new TopicExchange(SECKILL_EXCHANGE);
    }

    @Bean
    public Binding seckillBinding(){
        return BindingBuilder.bind(seckillQueue()).to(seckillExchange()).with(SECKILL_ROUTING_KEY);
    }
}
