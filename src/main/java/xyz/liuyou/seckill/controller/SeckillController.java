package xyz.liuyou.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.RequestHandledEvent;
import org.thymeleaf.util.StringUtils;
import xyz.liuyou.seckill.annotation.AccessLimit;
import xyz.liuyou.seckill.exception.GlobalException;
import xyz.liuyou.seckill.pojo.Order;
import xyz.liuyou.seckill.pojo.SeckillOrder;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.rabbitmq.MQSender;
import xyz.liuyou.seckill.rabbitmq.SeckillMessage;
import xyz.liuyou.seckill.service.IGoodsService;
import xyz.liuyou.seckill.service.IOrderService;
import xyz.liuyou.seckill.service.ISeckillGoodsService;
import xyz.liuyou.seckill.service.ISeckillOrderService;
import xyz.liuyou.seckill.utils.JSONUtil;
import xyz.liuyou.seckill.vo.GoodsVo;
import xyz.liuyou.seckill.vo.RespBean;
import xyz.liuyou.seckill.vo.RespBeanEnum;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/27 0:09
 * @decription
 **/
@Slf4j
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    /**
     * ????????????????????????redis??????
     **/
    private Map<Long, Boolean> emptyStockMap  = new HashMap();

//    // ????????? QPS: 213.4
//    @RequestMapping("/doSeckill")
//    public String doSeckill(Model model, User user, Long goodsId){
//        if (user == null) {
//            return "login";
//        }
//        model.addAttribute("user", user);
//        // ???????????????????????????????????????
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        // ????????????????????????
//        if (goods.getStockCount() <= 0) {
//            model.addAttribute("errmsg",RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "seckill_fail";
//        }
//        // ????????????????????????
//        SeckillOrder seckillOrder = seckillOrderService.getOne(
//                new QueryWrapper<SeckillOrder>()
//                        .eq("user_id", user.getId())
//                        .eq("goods_id", goodsId)
//        );
//        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "seckill_fail";
//        }
//        // ????????????
//        Order order = orderService.seckill(user, goods);
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goods);
//        return "order_detail";
//    }

//    // ????????? QPS: 213.4
//    // ???????????????????????? QPS: 388.1
//    @RequestMapping("/doSeckill2")
//    @ResponseBody
//    public RespBean doSeckill2(User user, Long goodsId){
//        if (user == null) {
//            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
//        }
//        // ???????????????????????????????????????
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        // ????????????????????????
//        if (goods.getStockCount() <= 0) {
//            throw new GlobalException(RespBeanEnum.EMPTY_STOCK);
//        }
//        // ????????????????????????
///*        SeckillOrder seckillOrder = seckillOrderService.getOne(
//                new QueryWrapper<SeckillOrder>()
//                        .eq("user_id", user.getId())
//                        .eq("goods_id", goodsId)*/
//        /*????????????*/
//        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());
//        if (seckillOrder != null) {
//            throw new GlobalException(RespBeanEnum.REPEAT_ERROR);
//        }
//        // ????????????
//        Order order = seckillOrderService.seckill(user, goods);
//        if (order == null) {
//            return RespBean.error(RespBeanEnum.ERROR);
//        }
//        return RespBean.success(order);
//    }


    // ????????? QPS: 213.4
    // ???????????????????????? QPS: 388.1
    // redis???????????? QPS: 305.5
    @RequestMapping("/{path}/doSeckill2")
    @ResponseBody
    public RespBean doSeckill2(@PathVariable("path") String path, User user, Long goodsId){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        if (user == null) {
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }
        if (StringUtils.isEmpty(path)){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        boolean check = orderService.checkpath(user, goodsId, path);
        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // ????????????????????????
        /*????????????*/
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            throw new GlobalException(RespBeanEnum.REPEAT_ERROR);
        }
        //????????????,??????Redis??????
        if (emptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // redis????????????
        Long stock = valueOperations.decrement("goods:" + goodsId + "stockcount");
        if (stock < 0){
            emptyStockMap.put(goodsId, true);
            valueOperations.increment("goods:" + goodsId + "stockcount");
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // ?????????????????????rabbitmq ???????????????
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSecillMessage(JSONUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0); // 0?????????
    }

    /**
     * ????????????????????????????????????????????????Redis???
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goods->{
            valueOperations.set("goods:"+ goods.getId()+"stockcount", goods.getStockCount());
            emptyStockMap.put(goods.getId(), false);
        });
    }

    /**
     * ??????????????????
     * @param user
     * @param goodsId ??????>0 ??????=0 ??????<0
     * @return
     */
    @RequestMapping("result")
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = orderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }


    /**
     * ??????????????????
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @RequestMapping("path")
    @ResponseBody
    @AccessLimit(seconds = 5, maxCount = 5)
    public RespBean getPath(User user, Long goodsId, String captcha){
        if (user == null) {
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check){
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    /**
     * ???????????????
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping("captcha")
    public void captcha(User user, Long goodsId, HttpServletResponse response) {
        if (user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // ????????????????????????????????????
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // ???????????????????????????????????????
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);

        // ????????????
        captcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // ????????????????????????????????????
        // ?????????????????????
        captcha.setCharType(Captcha.TYPE_ONLY_NUMBER);

        // ???????????????session
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId, captcha.text(), 300, TimeUnit.SECONDS);

        // ???????????????
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("?????????????????????", e.getMessage());
        }


    }
}
