package xyz.liuyou.seckill.exception;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.liuyou.seckill.vo.RespBean;
import xyz.liuyou.seckill.vo.RespBeanEnum;


/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/9 22:42
 * @decription 全局异常处理
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean exception(Exception e){
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        } else if (e instanceof BindException){
            BindException ex = (BindException) e;
            RespBean error = RespBean.error(RespBeanEnum.BIND_ERROR);
            error.setMessage("参数校验异常：" + ex.getAllErrors().get(0).getDefaultMessage());
            return error;
        } else {
            log.error("异常处理器捕获到其他异常：", e);
            return RespBean.error(RespBeanEnum.ERROR);
        }
    }
}
