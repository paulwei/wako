package com.whl.wako.kernel.util;

import com.alibaba.fastjson.JSON;
import com.whl.wako.kernel.util.enums.MaskType;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author paul.wei
 * @Description: 属性工具类
 * @date 2018/7/11
 */
public class PropertiesUtils  {


    public static Properties getSysProps() {
        Properties sysProps = new Properties();
        sysProps.putAll(System.getenv());
        sysProps.putAll(System.getProperties());
        return sysProps;
    }


    public static Properties filterMatch(Properties properties, String... regexKeys) {
        Properties p = new Properties();
        if (null == properties || regexKeys==null) {
            return p;
        }

        for(String key:properties.stringPropertyNames()){
            boolean match = false;
            for(String regexKey:regexKeys){
                if(BeanUtils.isEmpty(regexKey)){
                    continue;
                }
                //忽略大小写
                if(StringUtils.isNotBlank(key) && key.toLowerCase().matches(regexKey.toLowerCase())){
                    match=true;
                }
            }
            if(match){
                p.put(key,properties.getProperty(key));
            }
        }
        return p;
    }

    public static String maskByKey(Properties properties, String... regexKeys){
        TreeMap p = new TreeMap();
        if (null == properties
                || regexKeys==null
                || BeanUtils.isEmpty(properties.stringPropertyNames())) {
            return JSON.toJSONString(properties);
        }
        List<String> propertyNames = new ArrayList<String>(properties.stringPropertyNames());
        Collections.sort(propertyNames);
        for(String key:propertyNames){
            boolean match = false;
            for(String regexKey:regexKeys){
                if(BeanUtils.isEmpty(regexKey)){
                    continue;
                }
                //忽略大小写
                if(StringUtils.isNotBlank(key) && key.toLowerCase().matches(regexKey.toLowerCase())){
                    match=true;
                }
            }
            if(match){
                p.put(key, MaskType.PASSWORD.mask(properties.getProperty(key)));
            }else {
                p.put(key, properties.getProperty(key));
            }
        }
        return JSON.toJSONString(p);
    }
}
