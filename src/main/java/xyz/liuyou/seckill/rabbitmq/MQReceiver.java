package xyz.liuyou.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.omg.IOP.IOR;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import xyz.liuyou.seckill.pojo.Order;
import xyz.liuyou.seckill.pojo.SeckillGoods;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.service.IGoodsService;
import xyz.liuyou.seckill.service.IOrderService;
import xyz.liuyou.seckill.service.ISeckillOrderService;
import xyz.liuyou.seckill.utils.JSONUtil;
import xyz.liuyou.seckill.vo.GoodsVo;

import static xyz.liuyou.seckill.rabbitmq.MQConstants.SECKILL_QUEUE;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/6 21:51
 * @decription 消息接收（消费者）
 **/
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = SECKILL_QUEUE)
    public void receiveSeckillMessage(String message){
        log.info("接收消息：" + message);
        SeckillMessage seckillMessage = JSONUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        // 判断库存（这里是mysql库存判断，之前是redis）
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods.getStockCount() < 1){
            return;
        }
        // 查看是否重复秒杀
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillGoods seckillGoods = (SeckillGoods) valueOperations.get("goods:" + user.getId() + ":" + goodsId);
        if (seckillGoods != null) {
            return;
        }
        orderService.seckill(user, goods);
    }
}
