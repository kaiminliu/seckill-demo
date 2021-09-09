package xyz.liuyou.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static xyz.liuyou.seckill.rabbitmq.MQConstants.*;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/6 21:50
 * @decription 消息发送（生产者）
 **/
@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSecillMessage(String message){
        log.info("发送消息：" + message);
        rabbitTemplate.convertAndSend(SECKILL_EXCHANGE, "seckill.message", message);
    }
}
