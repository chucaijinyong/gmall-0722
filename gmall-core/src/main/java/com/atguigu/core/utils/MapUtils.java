package com.atguigu.core.utils;
import java.util.HashMap;

/**
 * @author chucai
 * @Description 生成链式map方便存储
 * @CreateTime 2020/8/30 19:11
 **/
public class MapUtils extends HashMap<String, Object> {

    @Override
    public MapUtils put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
