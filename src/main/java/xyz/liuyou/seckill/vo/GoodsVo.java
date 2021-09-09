package xyz.liuyou.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liuyou.seckill.pojo.Goods;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/26 13:47
 * @decription 商品视图对象
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends Goods {

    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

}
