package xyz.liuyou.seckill.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;
import xyz.liuyou.seckill.annotation.AccessLimit;
import xyz.liuyou.seckill.config.UserContext;
import xyz.liuyou.seckill.pojo.User;
import xyz.liuyou.seckill.service.IUserService;
import xyz.liuyou.seckill.utils.CookieUtil;
import xyz.liuyou.seckill.utils.JSONUtil;
import xyz.liuyou.seckill.vo.RespBean;
import xyz.liuyou.seckill.vo.RespBeanEnum;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/8 13:30
 * @decription 访问拦截器（接口限流）
 **/
@Component
public class AccessInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null){
                int seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                boolean needLogin = accessLimit.needLogin();
                String redisKey = "limit:"+user.getId()+":"+request.getRequestURI();
                if (needLogin){
                    redisKey += ":"+user.getId();
                }
                ValueOperations valueOperations = redisTemplate.opsForValue();
                Integer count = (Integer) valueOperations.get(redisKey);

                if (count == null){
                    valueOperations.set(redisKey, 0, seconds, TimeUnit.SECONDS);
                } else if (count < maxCount){
                    valueOperations.increment(redisKey);
                } else {
                    render(response, RespBeanEnum.ACCESS_LIMIT_REACHED);
                    return false;
                }
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, RespBeanEnum accessLimitReached) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            ServletOutputStream sos = response.getOutputStream();
            RespBean error = RespBean.error(accessLimitReached);
            sos.write(JSONUtil.object2JsonStr(error).getBytes());
            sos.flush();
            sos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private User getUser(HttpServletRequest request, HttpServletResponse response){
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if (StringUtils.isEmpty(ticket)){
            return null;
        }
        return userService.getUserByCookie(ticket, request,response);
    }
}
