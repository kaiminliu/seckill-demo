package xyz.liuyou.seckill.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.liuyou.seckill.service.IUserService;
import xyz.liuyou.seckill.vo.LoginVo;
import xyz.liuyou.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.ws.RequestWrapper;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/9 19:24
 * @decription 登录
 **/
@Controller
@Slf4j
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IUserService userService;

    @RequestMapping("toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        log.info("loginVo: {}", loginVo);
        return userService.doLogin(loginVo, request, response);
    }

    @RequestMapping("updatePassword")
    @ResponseBody
    public RespBean updatePassword(@CookieValue("userTicket") String userTicket, HttpServletRequest request, HttpServletResponse response, String password) {
        return userService.updatePassword(request, response, userTicket, password);
    }
}
