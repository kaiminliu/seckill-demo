package xyz.liuyou.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import xyz.liuyou.seckill.mapper.OrderMapper;
import xyz.liuyou.seckill.mapper.SeckillGoodsMapper;
import xyz.liuyou.seckill.pojo.*;
import xyz.liuyou.seckill.mapper.SeckillOrderMapper;
import xyz.liuyou.seckill.service.ISeckillOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.liuyou.seckill.vo.GoodsVo;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-26
 */
@Service
@Transactional
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {


}
