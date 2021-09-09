package xyz.liuyou.seckill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import xyz.liuyou.seckill.pojo.Goods;
import xyz.liuyou.seckill.mapper.GoodsMapper;
import xyz.liuyou.seckill.vo.GoodsVo;
import xyz.liuyou.seckill.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-26
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
