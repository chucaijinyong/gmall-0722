package com.atguigu.gmall.order.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author chucai
 * @Description : 阿里支付配置
 * @Create on : 2020/9/15 16:46
 **/
@Data
@Slf4j
@ConfigurationProperties(prefix = "alipay")
public class AliPayConfig {
    //在支付宝创建的应用的id
    private String app_id;
    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key;
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key;

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl;

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url;

    // 签名方式
    private String sign_type;

    // 字符编码格式
    private String charset;
}
