package com.whl.wako.kernel.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * @author paul.wei
 * @Description: JSON工具
 * @date 2018/6/14
 */
public class JsonUtils {

    /**
     * 把json对象转换成java对象
     *
     * @param jsonStr
     * @param clazz
     * @return
     */
    public final static <T> T json2obj(final String jsonStr, final Class<T> clazz) {
        return JSON.parseObject(jsonStr, clazz);
    }

    /**
     * 把json数组转换成java数组
     *
     * @param jsonArrayStr
     * @param list
     * @return
     */
    public final static <T> List<T> json2list(final String jsonArrayStr, final List<T> list) {
        return JSON.parseObject(jsonArrayStr,new TypeReference<List<T>>(){});
    }

    /**
     * 把json数组转换成java数组
     *
     * @param jsonArrayStr
     * @param clazz
     * @return
     */
    public final static <T> List<T> json2list(final String jsonArrayStr, final Class<T> clazz) {
        return JSON.parseArray(jsonArrayStr, clazz);
    }

    /**
     * 把json数据对应的java对象转换成固定的key的map对象
     *
     * @param jsonStr
     * @param map
     * @return
     */
    public final static <T> Map<String, T> json2map(final String jsonStr, final Map<String,T> map) {
        return JSON.parseObject(jsonStr,new TypeReference<Map<String,T>>(){});
    }

}
