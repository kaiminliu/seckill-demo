package xyz.liuyou.seckill.config;

import xyz.liuyou.seckill.pojo.User;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/8 13:34
 * @decription
 **/
public class UserContext {

    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }
}
