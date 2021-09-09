package xyz.liuyou.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liuyou.seckill.pojo.Order;
import xyz.liuyou.seckill.pojo.User;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/3 12:46
 * @decription
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {
    private GoodsVo goodsVo;
    private Order order;
}
