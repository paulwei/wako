package com.whl.wako.kernel.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


public class ClassLoaderUtil {
   private static Logger logger = LoggerFactory.getLogger(ClassLoaderUtil.class);

    public static ClassLoader getClassLoader(String url) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            URLClassLoader classLoader = new URLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());
            method.invoke(classLoader, new URL(url));
            return classLoader;
        } catch (Exception e) {
            logger.error("ClassLoaderUtil getClassLoader exception url:"+url, e);
            return null;
        }
    }
}
