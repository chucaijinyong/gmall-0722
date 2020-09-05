package com.atguigu.gmall.ums.annotation;

import java.lang.annotation.*;

/**
 * @author chubaodong
 * @version v1.0.0
 * @Package : com.atguigu.gmall.ums.annotation
 * @Description : TODO
 * @Create on : 2020/9/5 21:58
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface PrintlnLog {

    /**
     * 自定义日志描述信息文案
     *
     * @return
     */
    String description() default "";
}
