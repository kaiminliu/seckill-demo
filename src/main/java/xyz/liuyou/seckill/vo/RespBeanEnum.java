package xyz.liuyou.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/9 19:45
 * @decription
 **/
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {

    // 通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),
    // 登录模块
    LOGIN_ERROR(500210, "登录密码错误"),
    MOBILE_ERROR(500211, "手机号格式错误"),
    BIND_ERROR(500212, "参数校验异常"),
    MOBILE_NOT_EXIST(500213, "手机号不存在"),
    PASSWORD_UPDATE_FAIL(500214, "密码更新失败"),
    SESSION_ERROR(500215, "登录失效，请重新登录，再操作"),
    // 秒杀模块
    EMPTY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "该商品限购一件"),
    REQUEST_ILLEGAL(500502,"非法请求"),
    CAPTCHA_ERROR(500503, "输入的验证码错误"),
    ACCESS_LIMIT_REACHED(500503, "您的操作过于频繁"),
    // 订单模块
    ORDER_NOT_EXIST(500300, "订单不存在"),
    ;

    private final Integer code;

    private final String message;
}
