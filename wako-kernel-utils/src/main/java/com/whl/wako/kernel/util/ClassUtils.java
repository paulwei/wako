package com.whl.wako.kernel.util;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * @author paul.wei
 * @Description: 类相关工具
 * @date 2018/7/30
 */
public class ClassUtils  {
    private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    private static final Class[] BASE_TYPE_CLASS = new Class[]{String.class, Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class, Object.class, Class.class};


    public static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray();
    }

    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    public static boolean isIterable(Class<?> clazz) {
        return Iterable.class.isAssignableFrom(clazz);
    }

    public static boolean isEnum(Class<?> clazz) {
        return clazz!=null && clazz.isEnum();
    }

    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public static boolean isJavaBean(Class<?> clazz) {
        return null != clazz && !clazz.isInterface() && !isAbstract(clazz) && !clazz.isEnum() && !clazz.isArray() && !clazz.isAnnotation() && !clazz.isSynthetic() && !isPrimitiveOrWrapper(clazz) && !isIterable(clazz) && !isMap(clazz);
    }

    public static boolean isPrimitiveOrWrapper(final Class clazz) {
        if(null==clazz){
            return false;
        }
        if (clazz.isPrimitive()) {
            return true;
        } else {
            Class[] var1 = BASE_TYPE_CLASS;
            int var2 = var1.length;
            for(int var3 = 0; var3 < var2; ++var3) {
                Class baseClazz = var1[var3];
                if (baseClazz.equals(clazz)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * @Description:编译类文件版本信息
     * JDK 1.6 — major version 50 and minor version 0
     * JDK 1.7 — major version 51 and minor version 0
     * JDK 1.8 — major version 52 and minor version 0
     * @params [filename]
     * @return java.lang.String
     * @author paul.wei
     * @date 2018/7/30
     */
    public static String getClassVersion(String filename) {
        String version = "";
        DataInputStream ds = null;
        FileInputStream fi = null;
        try {
            fi=new FileInputStream(filename);
            ds = new DataInputStream(fi);
            int magicBytes = ds.readInt();
            if (magicBytes != 0xcafebabe) {
                logger.error(filename + " is not a valid java file!");
            } else {
                int minorVersion = ds.readUnsignedShort();
                int majorVersion = ds.readUnsignedShort();
                version = majorVersion + "." + minorVersion;
            }
            return version;
        } catch (Exception e) {
            logger.error(String.format("getVersionDetails exception[fileName:%s]",filename),e);
        }finally {
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e) {
                    logger.debug("getVersionDetails FileInputStream close IOException:" + e.getMessage());
                }
            }
            if (ds != null) {
                try {
                    ds.close();
                } catch (IOException e) {
                    logger.debug("getVersionDetails DataInputStream close IOException:" + e.getMessage());
                }
            }
        }
        return version;
    }

    /**
     * @Description:编译类文件版本信息
     * JDK 1.6 — major version 50 and minor version 0
     * JDK 1.7 — major version 51 and minor version 0
     * JDK 1.8 — major version 52 and minor version 0
     * @params [clazz]
     * @return java.lang.String
     * @author paul.wei
     * @date 2018/7/30
     */
    public static String getClassVersion(Class<?> clazz) {
        Validate.notNull(clazz, "clazz must not be null");
        try {
            String classFilePath = clazz.getResource("").getPath() + clazz.getSimpleName() + ".class";
            return getClassVersion(classFilePath);
        }catch (Exception e){
            logger.error(String.format("getClassVersion exception[clazz:%s]",clazz.getName()),e);
            return "";
        }
    }


}
