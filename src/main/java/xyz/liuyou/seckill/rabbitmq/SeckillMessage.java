package xyz.liuyou.seckill.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liuyou.seckill.pojo.User;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/6 21:49
 * @decription 秒杀消息
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {
    private User user;
    private Long goodsId;
}
