package xyz.liuyou.seckill.vo;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/9 19:44
 * @decription
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {

    private long code;

    private String message;

    private Object obj;

    /**
     * 描述：简单成功返回结果
     * @return xyz.liuyou.seckill.vo.RespBean
     **/
    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), null);
    }


    /**
     * 描述：成功返回结果
     * @return xyz.liuyou.seckill.vo.RespBean
     **/
    public static RespBean success(Object obj){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), obj);
    }


    /**
     * 描述：失败返回结果
     * @return xyz.liuyou.seckill.vo.RespBean
     **/
    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), null);
    }

    /**
     * 描述：失败返回结果
     * @return xyz.liuyou.seckill.vo.RespBean
     **/
    public static RespBean error(RespBeanEnum respBeanEnum, Object obj){
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), obj);
    }
}
