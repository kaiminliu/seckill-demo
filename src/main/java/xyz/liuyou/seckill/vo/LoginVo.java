package xyz.liuyou.seckill.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import xyz.liuyou.seckill.validator.IsMobile;

import javax.validation.constraints.NotNull;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/9 20:09
 * @decription 登录对象
 **/
@Data
public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;

    @Length(min = 32)
    @NotNull
    private String password;
}
