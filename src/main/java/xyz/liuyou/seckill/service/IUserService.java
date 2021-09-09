package xyz.liuyou.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.vo.LoginVo;
import xyz.liuyou.seckill.vo.RespBean;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 秒杀用户表 服务类
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-09
 */
public interface IUserService extends IService<User> {

    /**
     * 描述：登录逻辑
     * @param loginVo
     * @param request
     * @param response
     */
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 通过Cookie获取用户对象
     * @param cookie
     * @param request
     * @param response
     */
    User getUserByCookie(String cookie, HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新密码
     * @param request
     * @param response
     * @param userTicket
     * @param password
     */
    RespBean updatePassword(HttpServletRequest request, HttpServletResponse response, String userTicket, String password);
}
