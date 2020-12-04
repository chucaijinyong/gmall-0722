package com.atguigu.gmall.order.interceptors;

import com.atguigu.core.bean.UserInfo;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.order.config.JwtProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@EnableConfigurationProperties(JwtProperties.class)
@Component
//public class LoginInterceptor implements HandlerInterceptor  也可以直接实现HandlerInterceptor来实现拦截器
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties properties;

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 统一获取登陆状态。保证访问订单的所有用户都是登陆后的，没登陆的直接返回登录页
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfo userInfo = new UserInfo();
        // 获取cookie中的token信息（jwt）及userKey信息
        String token = CookieUtils.getCookieValue(request, this.properties.getCookieName());

        // 判断有没有token
        if (StringUtils.isNotBlank(token)) {
            // 解析token信息
            Map<String, Object> infoFromToken = JwtUtils.getInfoFromToken(token, this.properties.getPublicKey());
            userInfo.setId(new Long(infoFromToken.get("id").toString()));
        }

        THREAD_LOCAL.set(userInfo);

        return super.preHandle(request, response, handler);

        // 该方法需要改进的地方是当没有获取到用户信息时，不让其进行支付,可以让其跳转到登录页，并基于用户合适的提示信息
        // 另外获取用户的信息除了根据请求中的cookie来获取然后用jwt进行解析之外，还能使用springDataSession来存储和获取用户信息
        // 默认springDataSession可以指定存储session的信息在哪里，你可以使用redis作为存储库，这样对代码的侵入性很小，只需要引入依赖
        // 添加配置文件即可。不好的地方就是如果不是同系统即是域名都不同就不容易处理了。具体代码如下

//        UserInfo attribute = (UserInfo)request.getSession().getAttribute("你指定的值");
//        if (attribute == null) {
//            // 没登录就去登录
//            request.getSession().setAttribute("msg","请先进行登录");
//            response.sendRedirect("http://auth.gulimall.com/login.html");
//            return false;
//        }else{
//            //将获取到的值放到threadLocal里便于用户往下传递
//            THREAD_LOCAL.set(attribute);
//            return true;
//        }

    }

    public static UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 必须手动清除threadLocal中线程变量，因为使用的是tomcat线程池
        THREAD_LOCAL.remove();
    }
}
