package xyz.liuyou.seckill.service;

import xyz.liuyou.seckill.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.vo.GoodsVo;
import xyz.liuyou.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-26
 */
public interface IOrderService extends IService<Order> {

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);

    /**
     * 秒杀操作
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVo goods);

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId 成功>0 等待=0 失败<0
     * @return
     */
    Long getResult(User user, Long goodsId);

    /**
     * 制造秒杀path
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);


    /**
     * 检查秒杀路劲是否正确
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    boolean checkpath(User user, Long goodsId, String path);


    /**
     * 检查验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
