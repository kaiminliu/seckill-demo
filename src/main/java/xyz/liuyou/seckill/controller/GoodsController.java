package xyz.liuyou.seckill.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.service.IGoodsService;
import xyz.liuyou.seckill.service.IUserService;
import xyz.liuyou.seckill.vo.GoodsDetailVo;
import xyz.liuyou.seckill.vo.GoodsVo;
import xyz.liuyou.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/9 23:36
 * @decription
 **/
@Controller
@Slf4j
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /*   优化前
    @RequestMapping("/toList")
    public String toList(HttpSession session, Model model,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         @CookieValue("userTicket") String ticket){
        if (StringUtils.isEmpty(ticket)){
            return "login";
        }
        // User user = (User) session.getAttribute(ticket);
        User user = userService.getUserByCookie(ticket, request, response);
        if (user == null){
            return "login";
        }
        model.addAttribute("user", user);
        return "goods_list";
    }*/




//    // 优化前 QPS 231.3
//    @RequestMapping("/toList")
//    public String toList(Model model, User user){
//        if (Objects.isNull(user)){
//            return "login";
//        }
//        model.addAttribute("user", user);
//        model.addAttribute("goodsList", goodsService.findGoodsVo());
//        return "goods_list";
//    }
//
//    // 优化前 QPS：149.51
//    @RequestMapping("/toDetail/{goodsId}")
//    public String toDetail(Model model, User user, @PathVariable Long goodsId){
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        Date startDate = goods.getStartDate();
//        Date endDate = goods.getEndDate();
//        Date nowDate = new Date();
//
//        // 秒杀状态
//        int seckillStatus = 0;
//        // 秒杀倒计时时间
//        int remainSeconds = 0;
//
//        if (nowDate.before(startDate)){
//            // 秒杀未开始
//            seckillStatus = 0;
//            remainSeconds = ((int) (startDate.getTime() - nowDate.getTime()) / 1000);
//        } else if (nowDate.after(endDate)) {
//            // 秒杀已结束
//            seckillStatus = 2;
//            remainSeconds = -1;
//        } else {
//            // 秒杀进行中
//            seckillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("seckillStatus", seckillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        model.addAttribute("user", user);
//        model.addAttribute("goods",goods);
//        return "goods_detail";
//    }


    // 优化前 QPS 231.3
    // 页面缓存优化后 QPS: 356.5
    // 优化：页面缓存和URL缓存
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, User user){
        if (Objects.isNull(user)){
            return "login";
        }
        // 从redis中获取页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goods_list");
        if (!StringUtils.isEmpty(html)) {
            // 如果有页面
            return html;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
//        return "goods_list";
        // 手动渲染页面
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);
        if (!StringUtils.isEmpty(html)){
            // 将新页面存入Redis
            valueOperations.set("goods_list", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    // 优化前 QPS：149.51
    // URL缓存优化后 QPS: 261.7
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable Long goodsId){
        // 从redis中获取缓存
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goods_detail:" + goodsId);
        if (!StringUtils.isEmpty(html)) {
            // 如果有页面
            return html;
        }
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();

        // 秒杀状态
        int seckillStatus = 0;
        // 秒杀倒计时时间
        int remainSeconds = 0;

        if (nowDate.before(startDate)){
            // 秒杀未开始
            seckillStatus = 0;
            remainSeconds = ((int) (startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            // 秒杀已结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            // 秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("user", user);
        model.addAttribute("goods",goods);

        // 手动渲染页面
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", webContext);
        if (!StringUtils.isEmpty(html)) {
            // 将页面存入Redis
            valueOperations.set("goods_detail:"+goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    // 优化前 QPS：149.51
    // URL缓存优化后 QPS: 261.7
    // goods_detail页面静态化优化后 QPS:461.1
    @RequestMapping("/toDetail2/{goodsId}")
    @ResponseBody
    public RespBean toDetail2(User user, @PathVariable Long goodsId){
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();

        // 秒杀状态
        int seckillStatus = 0;
        // 秒杀倒计时时间
        int remainSeconds = 0;

        if (nowDate.before(startDate)){
            // 秒杀未开始
            seckillStatus = 0;
            remainSeconds = ((int) (startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            // 秒杀已结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            // 秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setUser(user);
        goodsDetailVo.setGoodsVo(goods);
        goodsDetailVo.setSeckillStatus(seckillStatus);
        goodsDetailVo.setRemainSeconds(remainSeconds);

        return RespBean.success(goodsDetailVo);
    }

}
