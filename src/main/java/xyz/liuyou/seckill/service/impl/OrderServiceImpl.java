package xyz.liuyou.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.thymeleaf.util.StringUtils;
import xyz.liuyou.seckill.exception.GlobalException;
import xyz.liuyou.seckill.mapper.GoodsMapper;
import xyz.liuyou.seckill.mapper.SeckillGoodsMapper;
import xyz.liuyou.seckill.mapper.SeckillOrderMapper;
import xyz.liuyou.seckill.pojo.Order;
import xyz.liuyou.seckill.mapper.OrderMapper;
import xyz.liuyou.seckill.pojo.SeckillGoods;
import xyz.liuyou.seckill.pojo.SeckillOrder;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.service.IGoodsService;
import xyz.liuyou.seckill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.liuyou.seckill.utils.UUIDUtil;
import xyz.liuyou.seckill.vo.GoodsVo;
import xyz.liuyou.seckill.vo.OrderDetailVo;
import xyz.liuyou.seckill.vo.RespBeanEnum;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-26
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {


    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goods = goodsMapper.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo(goods, order);
        return orderDetailVo;
    }


    @Override
    public Order seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 库存减一 seckill_goods
        SeckillGoods seckillGoods = seckillGoodsMapper.selectOne(
                new QueryWrapper<SeckillGoods>()
                        .eq("goods_id", goods.getId())
        );
/*        多个线程进入，回到值库存超卖，我们应该更改减库存的方式，采用sql语句判断库存余额，有库存才减
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        seckillGoodsMapper.updateById(seckillGoods);*/
/*        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1); //这句有问题，多线程下会有多个线程同时在这里
        int result = seckillGoodsMapper.update(seckillGoods, new UpdateWrapper<SeckillGoods>()
                .eq("id", goods.getId())
                .gt("stock_count", 0));*/
        int result = seckillGoodsMapper.update(null, new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = stock_count - 1")
                .eq("id", goods.getId())
                .gt("stock_count", 0));
        if (seckillGoods.getStockCount() < 1) { // 没有减库存，即库存不足，我们就直接返回null，不再生成订单
            valueOperations.set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }
        // 生成订单  order
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);// 创建未支付
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        // 生成秒杀订单 seckill_order
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderMapper.insert(seckillOrder);
        // 解决同一用户同时秒杀多件商品：使用user_id+goods_id建立唯一索引接口，优化：再将秒杀订单信息存入redis
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);
        return order;
    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId 成功>0 等待=0 失败<0
     * @return
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId())
                .eq("goods_id", goodsId));
        if (seckillOrder != null){
            return seckillOrder.getId();
        } else {
            if (redisTemplate.hasKey("isStockEmpty:" + goodsId)){
                return -1L;
            } else {
                return 0L;
            }
        }
    }

    @Override
    public String createPath(User user, Long goodsId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String path = UUIDUtil.uuid();
        valueOperations.set("seckillpath:"+user.getId()+":"+goodsId, path, 60, TimeUnit.SECONDS);
        return path;
    }

    @Override
    public boolean checkpath(User user, Long goodsId, String path) {
        if (user == null || StringUtils.isEmpty(path)){
            return false;
        }
        String p  =(String)redisTemplate.opsForValue().get("seckillpath:" + user.getId() + ":" + goodsId);
        return path.equals(p);
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(captcha)){
            return false;
        }
        String c = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(c);
    }
}
