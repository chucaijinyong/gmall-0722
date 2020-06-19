package com.atguigu.gmall.auth;

import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
	private static final String pubKeyPath = "D:\\project-0722\\rsa\\rsa.pub";

    private static final String priKeyPath = "D:\\project-0722\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    /**
     * 根据路径生成公钥和私钥，注意在生成公钥和私钥时要把before方法注销
     * 因为那是获取公钥和私钥的方法
     * 另外生成密钥对的方式也可以在命令行里实现，具体可以百度
     * @throws Exception
     */
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "11111");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token，可以设置生成的token的过期时间，默认时分钟
        String token = JwtUtils.generateToken(map, privateKey, 1);
        // 对于生成的字符串token可以通过https://base64.supfree.net/ 查看解密后的用户信息
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1OTI1Nzg1NjV9.F_obUHZFchfduH-uxDECeEna2dlX-HBCtBZTU8ciu2M5JB88xArcTHfACwn9a6MKH_gSHrmq2sZ0HsR2paC-5FdkNRziIKGOz-6MeT1cxUST3C5kYkPnB9qbH0se7JS14a1YRAIcFmW0gI5lWEseRSR-ok3ioBHlzthTjPmXJXA4omngWXEHwgEPck_rgkx4TPAhSqTK5PiSgnDtWAGIMYkR0BLhXHFsJOsoqNwkXeiftGwWfAEzeGnmrs2T5fAslHPFThSJB4gkh4lDeArTDoW04vv-BMzrIMHWwxuLLztqPd8dabFjFXU81fnsfgm4Z1g9-GBcC_AABTq26kU6EQ";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}
