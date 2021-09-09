package xyz.liuyou.seckill.validator;

import org.thymeleaf.util.StringUtils;
import xyz.liuyou.seckill.utils.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author liuminkai
 * @version 1.0
 * @datetime 2021/8/9 22:16
 * @decription
 **/
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!required && StringUtils.isEmpty(value)) {
            return true;
        } else {
            return ValidatorUtil.isMobile(value);
        }
    }
}
