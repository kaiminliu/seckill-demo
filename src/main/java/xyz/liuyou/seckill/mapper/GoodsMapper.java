package xyz.liuyou.seckill.mapper;

import org.springframework.stereotype.Repository;
import xyz.liuyou.seckill.pojo.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xyz.liuyou.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-26
 */
@Repository
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 查询商品列表
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 查询商品详细信息
     * @param goodsId
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
