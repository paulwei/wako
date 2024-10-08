package com.whl.wako.kernel.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author paul.wei
 * @Description: 断言工具类
 * @date 2019/3/20
 */
public class AssertUtils {
    private static final Logger logger = LoggerFactory.getLogger(AssertUtils.class);


    public static <T extends Number> void isEnumerable(List<T> ts,T t, String message) {
        if(BeanUtils.isEmpty(ts)){
            return;
        }
        if (!ts.contains(t)) {
            throw new IllegalArgumentException(message);
        }
    }
    public static <T extends Number> void isEnumerable(List<T> ts,T t) {
         isEnumerable(ts,t,String.format("[Assertion failed] - this ts:%s not contain t:%s ", JSON.toJSONString(ts),t));
    }

    public static void isDigit(String str, String message) {
        if (!NumberUtils.isNumber(str)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isDigit(String str) {
         isDigit(str,"[Assertion failed] - this expression must digit");
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNull(Object object) {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void limitLength(String text,int limit,String message) {
        if(BeanUtils.isEmpty(text)){
            return;
        }
        if(text.length()>limit){
            throw new IllegalArgumentException(message);
        }
    }

    public static void limitLength(String text,int limit) {
        if(BeanUtils.isEmpty(text)){
            return;
        }
        limitLength(text,limit,"[Assertion failed] - this String argument length than max:"+limit+";");
    }

    public static void hasLength(String text, String message) {
        if (!BeanUtils.isNotEmpty(text)) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void hasLength(String text) {
        hasLength(text,
                "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }


    public static void hasText(String text, String message) {
        if (BeanUtils.isEmpty(text)) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void hasText(String text) {
        hasText(text,
                "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }


    public static void doesNotContain(String textToSearch, String substring, String message) {
        if (BeanUtils.isNotEmpty(textToSearch) && BeanUtils.isNotEmpty(substring) &&
                textToSearch.contains(substring)) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void doesNotContain(String textToSearch, String substring) {
        doesNotContain(textToSearch, substring,
                "[Assertion failed] - this String argument must not contain the substring [" + substring + "]");
    }



    public static void notEmpty(Object[] array, String message) {
        if (BeanUtils.isEmpty(array)) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void notEmpty(Object[] array) {
        notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }


    public static void noNullElements(Object[] array, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }


    public static void noNullElements(Object[] array) {
        noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
    }


    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void notEmpty(Collection<?> collection) {
        notEmpty(collection,
                "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    public static void notEmpty(Map<?, ?> map, String message) {
        if (BeanUtils.isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void notEmpty(Map<?, ?> map) {
        notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }



    public static void isInstanceOf(Class<?> clazz, Object obj) {
        isInstanceOf(clazz, obj, "");
    }


    public static void isInstanceOf(Class<?> type, Object obj, String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(
                    (BeanUtils.isNotEmpty(message) ? message + " " : "") +
                            "Object of class [" + (obj != null ? obj.getClass().getName() : "null") +
                            "] must be an instance of " + type);
        }
    }


    public static void isAssignable(Class<?> superType, Class<?> subType) {
        isAssignable(superType, subType, "");
    }


    public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
        }
    }


    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void state(boolean expression) {
        state(expression, "[Assertion failed] - this state invariant must be true");
    }



}
