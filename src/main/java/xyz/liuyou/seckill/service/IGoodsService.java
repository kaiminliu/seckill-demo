package xyz.liuyou.seckill.service;

import xyz.liuyou.seckill.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.liuyou.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-26
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 查询商品列表
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 查询详细商品信息
     * @param goodsId
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
