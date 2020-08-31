//package com.atguigu.gmall.pms.config;
//
//import com.atguigu.gmall.pms.json.JsonReturnHandler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class GmallWebMvcConfig implements WebMvcConfigurer {
//
//    @Autowired
//    private JsonReturnHandler jsonReturnHandler;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor((HandlerInterceptor) jsonReturnHandler).addPathPatterns("/**");
//    }
//}
