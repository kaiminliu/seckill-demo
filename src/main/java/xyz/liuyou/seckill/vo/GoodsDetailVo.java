package xyz.liuyou.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liuyou.seckill.pojo.User;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/2 23:35
 * @decription 商品详情视图对象
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDetailVo {
    private User user;
    private GoodsVo goodsVo;
    private int seckillStatus;
    private int remainSeconds;
}
