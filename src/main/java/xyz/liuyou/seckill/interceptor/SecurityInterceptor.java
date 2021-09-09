package xyz.liuyou.seckill.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.liuyou.seckill.config.UserContext;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/9 0:21
 * @decription
 **/
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        HttpSession session = request.getSession();

        // User user = (User) session.getAttribute("user:"+userTicket);
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user == null){
            request.getRequestDispatcher("/login").forward(request, response);
            return false;
        }
        // UserContext 是 ThreadLocal<User>
        UserContext.setUser(user);
        return true;
    }
}
