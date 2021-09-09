package xyz.liuyou.seckill.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/9/6 22:01
 * @decription 更多http://bcxw.net/article/301.html
 **/
public class JSONUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象转换为json字符串
     * @param obj
     * @return
     */
    public static String object2JsonStr(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将json字符串转为对象
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T> T jsonStr2Object(String jsonStr, Class<T> clazz){
        try {
            return objectMapper.readValue(jsonStr.getBytes("UTF-8"), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json字符串转为list
     * @param jsonStr
     * @param clazz
     * @return
     */
    public static <T> List<T> jsonStr2List(String jsonStr, Class<T> clazz){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            List<T> list = objectMapper.readValue(jsonStr, javaType);
            return list;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
