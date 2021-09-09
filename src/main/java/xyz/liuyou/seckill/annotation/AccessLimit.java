package xyz.liuyou.seckill.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/8 13:48
 * @decription
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

    int seconds();

    int maxCount();

    boolean needLogin() default true;
}
