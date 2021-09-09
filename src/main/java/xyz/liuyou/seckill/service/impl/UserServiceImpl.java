package xyz.liuyou.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import xyz.liuyou.seckill.exception.GlobalException;
import xyz.liuyou.seckill.mapper.UserMapper;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.service.IUserService;
import xyz.liuyou.seckill.utils.CookieUtil;
import xyz.liuyou.seckill.utils.MD5Util;
import xyz.liuyou.seckill.utils.UUIDUtil;
import xyz.liuyou.seckill.utils.ValidatorUtil;
import xyz.liuyou.seckill.vo.LoginVo;
import xyz.liuyou.seckill.vo.RespBean;
import xyz.liuyou.seckill.vo.RespBeanEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 秒杀用户表 服务实现类
 * </p>
 *
 * @author liuminkai
 * @since 2021-08-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 描述：登录逻辑
     * @param loginVo
     * @param request
     * @param response
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {

        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        /*使用jsr303校验，以下代码可以去掉了
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        // 验证手机号
        if (!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }*/
        // 根据手机号获取用户信息
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", mobile);
        User user = userMapper.selectOne(wrapper);
        // 用户不存在
        if (null == user){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 密码是否正确
        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 生成Cookie
        String ticket = UUIDUtil.uuid();
        //request.getSession().setAttribute(ticket, user);
        redisTemplate.opsForValue().set("user:"+ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }



    /**
     * 通过Cookie获取用户对象
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:"+userTicket);
        if (user != null){// 以防万一的操作(当userTicket在Redis中设置了有效时间时，如果在登录状态，我们是不允许userTicket失效的，以防万一每次请求都设置一下)
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

//    @Override
//    public User getUserByCookie(String userTicket) {
//        if (StringUtils.isEmpty(userTicket)) {
//            return null;
//        }
//        User user = (User) redisTemplate.opsForValue().get("user:"+userTicket);
//        return user;
//    }

    @Override
    public RespBean updatePassword(HttpServletRequest request, HttpServletResponse response, String userTicket, String password) {
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int result = userMapper.updateById(user);
        if (result == 1){
            // 删除redis中userTicket
            redisTemplate.delete("user:"+userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
