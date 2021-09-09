package xyz.liuyou.seckill.controller;


import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.liuyou.seckill.exception.GlobalException;
import xyz.liuyou.seckill.pojo.Order;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.service.IGoodsService;
import xyz.liuyou.seckill.service.IOrderService;
import xyz.liuyou.seckill.vo.GoodsVo;
import xyz.liuyou.seckill.vo.OrderDetailVo;
import xyz.liuyou.seckill.vo.RespBean;
import xyz.liuyou.seckill.vo.RespBeanEnum;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-26
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IGoodsService goodsService;

    @RequestMapping("/detail")
    public RespBean detail(User user, Long orderId){
        if (user == null) {
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo orderDetailVo = orderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }
}
