package com.whl.wako.kernel.util.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ContextValueFilter;
import com.whl.wako.kernel.util.BeanUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author paul.wei
 * @Description: 脱敏代理
 * @date 2020/04/23
 */
public class DesensitizeAgent {

    public <T> String desensitize(final T object, final Map<String, Set<String>> config) {
        if(BeanUtils.isEmpty(object)) {
            return JSON.toJSONString(object);
        }
        DesensitizeRegistry.getInstance().setting(config);
        ContextValueFilter filter = new DesensitizeFilter(DesensitizeContext.getInstance());
        return JSON.toJSONString(object, filter);
    }

    private static class DesensitizeAgentHoler{
        private static DesensitizeAgent INSTANCE = new DesensitizeAgent();
    }

    public static DesensitizeAgent getInstance(){
        return DesensitizeAgentHoler.INSTANCE;
    }

}
