package xyz.liuyou.seckill.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/9 21:40
 * @decription 自定义注解（手机号判断）
 **/
@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { IsMobileValidator.class })
public @interface IsMobile {

    /**
     * 参数是否必填
     */
    boolean required() default true;

    /**
     * 校验失败提示的消息
     */
    String message() default "手机号格式错误";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
