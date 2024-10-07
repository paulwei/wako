package com.whl.wako.kernel.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.whl.wako.kernel.util.Constants.SEMICOLON;


/**
 * @author paul.wei
 */
public class ReflectionUtils  {
    private final static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    public static LinkedHashMap<String,LinkedHashMap<String,Type>> getInvokeMethod(Class<?> serviceClazz){
        LinkedHashMap<String,LinkedHashMap<String,Type>> invokeMethod = new LinkedHashMap<>();
        AssertUtils.notNull(serviceClazz,"serviceClazz must not null");
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        for(Method m:serviceClazz.getDeclaredMethods()){
            String[] argNames = discoverer.getParameterNames(m);
            LinkedHashMap<String,Type> args = new LinkedHashMap<>();
            Type[] argTypes = m.getGenericParameterTypes();
            for(int i= 0;i<argTypes.length;i++){
                String argName = argNames[i];
                args.put(argName,argTypes[i]);
            }
            invokeMethod.put(m.getName(),args);
        }
        return invokeMethod;
    }



    public static String[] getFieldNames(Class<?> clazz) {
        Validate.notNull(clazz, "clazz can't be null");
        Field[] fields = clazz.getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }



}
