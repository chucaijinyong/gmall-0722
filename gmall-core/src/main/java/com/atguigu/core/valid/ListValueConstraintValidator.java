package com.atguigu.core.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author chubaodong
 * @version v1.0.0
 * @Package : com.atguigu.core.valid
 * @Description : TODO
 * @Create on : 2020/7/21 23:24
 **/
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    Set set = new HashSet<Integer>();
    @Override
    public void initialize(ListValue constraintAnnotation) {

        int[] values = constraintAnnotation.values();
        for (int value : values) {
            set.add(value);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
