package xyz.liuyou.seckill.utils;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/9 20:40
 * @decription
 **/
public class ValidatorUtil {

    private static Pattern mobile_pattern = Pattern.compile("^((0\\d{2,3}-\\d{7,8})|(1[34578]\\d{9}))$");

    public static boolean isMobile(String mobile){
        if (StringUtils.isEmpty(mobile)) {
            return false;
        }
        return mobile_pattern.matcher(mobile).matches();
    }
}
