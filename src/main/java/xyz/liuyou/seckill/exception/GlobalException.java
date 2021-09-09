package xyz.liuyou.seckill.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liuyou.seckill.vo.RespBeanEnum;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/9 22:46
 * @decription 全局异常
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;
}
