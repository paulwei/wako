package com.whl.wako.kernel.util.log;

import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.ContextValueFilter;
import com.whl.wako.kernel.util.BeanUtils;
import com.whl.wako.kernel.util.enums.MaskType;

/**
 * @author paul.wei
 * @Description: 序列化过滤器
 * @date 2020/04/23
 */
public class DesensitizeFilter implements ContextValueFilter {
    /**
     * 脱敏上下文
     */
    private final DesensitizeContext dc;

    public DesensitizeFilter(DesensitizeContext dc) {
        this.dc = dc;
    }


    @Override
    public Object process(BeanContext context, Object object, String name, Object value) {
        if(BeanUtils.isEmpty(name)
                || BeanUtils.isEmpty(value)
                || BeanUtils.isEmpty(dc)
                || BeanUtils.isEmpty(dc.getRegistry())
                || !(value instanceof String)) {
            return value;
        }
        MaskType maskType = dc.getRegistry().getByFiledName(name);
        if(maskType!=null){
            return maskType.mask(value.toString());
        }
        return value;
    }





}
