package xyz.liuyou.seckill.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.rabbitmq.MQSender;
import xyz.liuyou.seckill.vo.RespBean;

/**
 * <p>
 * 秒杀用户表 前端控制器
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-09
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    @RequestMapping("info")
    public RespBean info(User user){
        return RespBean.success(user);
    }
}
