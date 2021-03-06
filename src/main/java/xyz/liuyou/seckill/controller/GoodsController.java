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

    /*   δΌεε
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




//    // δΌεε QPS 231.3
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
//    // δΌεε QPSοΌ149.51
//    @RequestMapping("/toDetail/{goodsId}")
//    public String toDetail(Model model, User user, @PathVariable Long goodsId){
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        Date startDate = goods.getStartDate();
//        Date endDate = goods.getEndDate();
//        Date nowDate = new Date();
//
//        // η§ζηΆζ
//        int seckillStatus = 0;
//        // η§ζεθ?‘ζΆζΆι΄
//        int remainSeconds = 0;
//
//        if (nowDate.before(startDate)){
//            // η§ζζͺεΌε§
//            seckillStatus = 0;
//            remainSeconds = ((int) (startDate.getTime() - nowDate.getTime()) / 1000);
//        } else if (nowDate.after(endDate)) {
//            // η§ζε·²η»ζ
//            seckillStatus = 2;
//            remainSeconds = -1;
//        } else {
//            // η§ζθΏθ‘δΈ­
//            seckillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("seckillStatus", seckillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        model.addAttribute("user", user);
//        model.addAttribute("goods",goods);
//        return "goods_detail";
//    }


    // δΌεε QPS 231.3
    // ι‘΅ι’ηΌε­δΌεε QPS: 356.5
    // δΌεοΌι‘΅ι’ηΌε­εURLηΌε­
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, User user){
        if (Objects.isNull(user)){
            return "login";
        }
        // δ»redisδΈ­θ·ει‘΅ι’
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goods_list");
        if (!StringUtils.isEmpty(html)) {
            // ε¦ζζι‘΅ι’
            return html;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
//        return "goods_list";
        // ζε¨ζΈ²ζι‘΅ι’
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);
        if (!StringUtils.isEmpty(html)){
            // ε°ζ°ι‘΅ι’ε­ε₯Redis
            valueOperations.set("goods_list", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    // δΌεε QPSοΌ149.51
    // URLηΌε­δΌεε QPS: 261.7
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable Long goodsId){
        // δ»redisδΈ­θ·εηΌε­
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goods_detail:" + goodsId);
        if (!StringUtils.isEmpty(html)) {
            // ε¦ζζι‘΅ι’
            return html;
        }
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();

        // η§ζηΆζ
        int seckillStatus = 0;
        // η§ζεθ?‘ζΆζΆι΄
        int remainSeconds = 0;

        if (nowDate.before(startDate)){
            // η§ζζͺεΌε§
            seckillStatus = 0;
            remainSeconds = ((int) (startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            // η§ζε·²η»ζ
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            // η§ζθΏθ‘δΈ­
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("user", user);
        model.addAttribute("goods",goods);

        // ζε¨ζΈ²ζι‘΅ι’
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", webContext);
        if (!StringUtils.isEmpty(html)) {
            // ε°ι‘΅ι’ε­ε₯Redis
            valueOperations.set("goods_detail:"+goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    // δΌεε QPSοΌ149.51
    // URLηΌε­δΌεε QPS: 261.7
    // goods_detailι‘΅ι’ιζεδΌεε QPS:461.1
    @RequestMapping("/toDetail2/{goodsId}")
    @ResponseBody
    public RespBean toDetail2(User user, @PathVariable Long goodsId){
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();

        // η§ζηΆζ
        int seckillStatus = 0;
        // η§ζεθ?‘ζΆζΆι΄
        int remainSeconds = 0;

        if (nowDate.before(startDate)){
            // η§ζζͺεΌε§
            seckillStatus = 0;
            remainSeconds = ((int) (startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            // η§ζε·²η»ζ
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            // η§ζθΏθ‘δΈ­
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
