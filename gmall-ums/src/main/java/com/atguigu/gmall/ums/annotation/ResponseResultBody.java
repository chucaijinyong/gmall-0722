package com.atguigu.gmall.ums.annotation;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * @author chubaodong
 * @version v1.0.0
 * @Package : com.atguigu.gmall.ums.annotation
 * @Description : TODO
 * @Create on : 2020/9/5 21:42
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ResponseBody
public @interface ResponseResultBody {

}
