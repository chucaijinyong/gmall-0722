package com.atguigu.core.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author chubaodong
 * @version v1.0.0
 * @Package : com.atguigu.core.valid
 * @Description : TODO
 * @Create on : 2020/7/21 23:11
 **/

@Documented
@Constraint(validatedBy = {ListValueConstraintValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ListValue {
    String message() default "必须提交指定的值";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
    int[] values() default { };
}
