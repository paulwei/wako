package com.whl.wako.kernel.util;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BeanUtils {
    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof String) {
            return ((String) obj).trim().length() == 0;
        }

        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }

        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }

        return false;
    }

    public static boolean isEmpty(Object[] array) {
        if (array == null || array.length == 0) {
            return true;
        }

        return false;
    }

    public static  <T> T ifNull(T... ts) {
        if (BeanUtils.isEmpty(ts)) {
            return null;
        }
        for(T t:ts){
            if(BeanUtils.isNotEmpty(t)){
                return t;
            }
        }
        return null;
    }

    public static <T> List<T> intersect(List<T> ls, List<T> ls2) {
        if (ls == null) {
            return ls2;
        } else if (ls2 == null) {
            return ls;
        }
        List<T> list = new ArrayList<T>(ls.size());
        list.addAll(ls);
        list.retainAll(ls2);
        return list;
    }

    public static <T> List<T> union(List<T> ls, List<T> ls2) {
        if (ls == null) {
            return ls2;
        } else if (ls2 == null) {
            return ls;
        } else {
            ls.addAll(ls2);
            return ls;
        }
    }

    public static <T> List<T> union(List<T> ls, List<T> ls2, boolean distinct) {
        if (ls == null) {
            return ls2;
        } else if (ls2 == null) {
            return ls;
        } else {
            if (distinct) {
                ls2.removeAll(ls);
                ls.addAll(ls2);
                return ls;
            } else {
                ls.addAll(ls2);
                return ls;
            }
        }
    }

    public static <T> List<T> distinct(List<T> list) {
        HashSet<T> h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    public static <T> boolean contains(T t, T... ts) {
        if (ts == null) {
            return false;
        }
        for (T t1 : ts) {
            if (t1 == null && t == null) {
                return true;
            }
            if (t1 != null && t != null && t.equals(t1)) {
                return true;
            }
        }
        return false;
    }


    public static <T> List<T> diff(List<T> ls, List<T> ls2) {
        if (ls == null) {
            return null;
        } else if (ls2 == null) {
            return ls;
        } else {
            List<T> list = new ArrayList<T>(ls.size());
            list.addAll(ls);
            list.removeAll(ls2);
            return list;
        }
    }

    public static <T> List<T> reverse(List<T> ls, int offset) {
        LinkedList<T> list = new LinkedList<>();
        if (ls == null) {
            return null;
        }
        int len = ls.size();
        offset = (offset>=0 && offset<len)?offset:0;
        List<T> sub1 = ls.subList(0,offset);
        List<T> sub2 = ls.subList(offset,ls.size());
        list.addAll(sub2);
        list.addAll(sub1);
        return list;
    }

    public static <T> T[] join(T[] t1, T... ts) {
        return ArrayUtils.addAll(t1, ts);
    }

    public static <T> List<T> filterExclude(List<T> ls, String filterProperty, String filterValue) {
        if (CollectionUtils.isEmpty(ls)) {
            return ls;
        }
        Iterator<T> it = ls.iterator();
        while (it.hasNext()) {
            try {
                T bean = it.next();
                if (isEmpty(bean)) {
                    continue;
                }
                Object value = PropertyUtils.getNestedProperty(bean, filterProperty);
                if (value != null && filterValue.equals(value)) {
                    it.remove();
                }
            } catch (Exception e) {
                logger.error(" filterExclude exception ", e);
                throw new RuntimeException(" filterExclude exception ", e);
            }
        }
        return ls;
    }

    public static <T> Collection<T> filterMatch(Collection<T> ls, final String filterProperty, final String regexValue) {
        if (CollectionUtils.isEmpty(ls) || BeanUtils.isEmpty(regexValue)) {
            return ls;
        }
        Predicate<T> predicate = new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                try {
                    Object value = PropertyUtils.getNestedProperty(t, filterProperty);
                    return value == null ? Boolean.FALSE : value.toString().matches(regexValue);
                } catch (Exception e) {
                    logger.error(" filterMatch exception ", e);
                    throw new RuntimeException(" filterMatch exception ", e);
                }
            }
        };
        return Collections2.filter(ls, predicate);
    }

    public static <T, V> List<T> filterIn(List<T> ls, String filterProperty, V... filterValues) {
        if (CollectionUtils.isEmpty(ls)) {
            return ls;
        }
        List<T> list = new ArrayList<T>();
        Iterator<T> it = ls.iterator();
        while (it.hasNext()) {
            try {
                T bean = it.next();
                if (isEmpty(bean)) {
                    continue;
                }
                Object value = PropertyUtils.getNestedProperty(bean, filterProperty);
                for (V filterValue : filterValues) {
                    if ((isEmpty(filterValue) && isEmpty(value))
                            || !isEmpty(filterValue) && filterValue.equals(value)) {
                        list.add(bean);
                    }
                }
            } catch (Exception e) {
                logger.error(" filterIn exception ", e);
                throw new RuntimeException(" filterIn exception ", e);
            }
        }
        return list;
    }

    public static <T> List<T> filterNull2(Collection<T> ls) {
        if (CollectionUtils.isEmpty(ls)) {
            return Lists.newArrayList();
        }
        Collection<T> result = Collections2.filter(ls, Predicates.notNull());
        return Lists.newArrayList(result);
    }

    public static <T> Set<T> filterNull(Set<T> ls) {

        if (CollectionUtils.isEmpty(ls)) {
            return ls;
        }
        Collections2.filter(ls, Predicates.notNull());
        Iterator<T> it = ls.iterator();
        while (it.hasNext()) {
            T t = it.next();
            if (t == null || isEmpty(t)) {
                it.remove();
            }
        }
        return ls;
    }

    public static <T> List<T> filterNull(List<T> ls) {
        if (CollectionUtils.isEmpty(ls)) {
            return ls;
        }
        Iterator<T> it = ls.iterator();
        while (it.hasNext()) {
            T t = it.next();
            if (t == null || isEmpty(t)) {
                it.remove();
            }
        }

        return ls;
    }

    public static <T> List<T> filterNullOrZero(List<T> ls, String... filterProperties) {
        if (CollectionUtils.isEmpty(ls)) {
            return ls;
        }
        List<T> list = new CopyOnWriteArrayList(ls);
        for (T bean : list) {
            try {
                if (filterProperties == null) {
                    break;
                }
                boolean flag = true;
                for (String property : filterProperties) {
                    Object value = PropertyUtils.getNestedProperty(bean, property);
                    if (isEmpty(value) || (value instanceof BigDecimal && NumberUtils.isEqualsToZero((BigDecimal) value))) {
                        flag = flag && true;
                    } else {
                        flag = flag && false;
                    }
                }
                if (flag) {
                    list.remove(bean);
                }
            } catch (Exception e) {
                logger.error(" filter exception ", e);
                throw new RuntimeException(" filter exception ", e);
            }
        }
        return list;
    }

    public static <T> T getTopBeanPropertyList(final List<T> beanList, String propertyname, boolean asc) {
        if (CollectionUtils.isEmpty(beanList)) {
            return null;
        }
        sortBy(beanList, asc, propertyname);
        return beanList.get(0);
    }

    public static <T> List<T> sortBy(List<T> beanList, final boolean desc, final String... properties) {
        filterNull(beanList);
        if (CollectionUtils.isEmpty(beanList) || isEmpty(properties)) {
            return beanList;
        }
        Collections.sort(beanList, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                for (String property : properties) {
                    try {
                        Object value1 = PropertyUtils.getNestedProperty(o1, property);
                        Object value2 = PropertyUtils.getNestedProperty(o2, property);
                        if (isEmpty(value1) && isEmpty(value2)) {//fix timsort
                            continue;
                        }
                        if (isEmpty(value1)) {
                            return 1;
                        }
                        if (isEmpty(value2)) {
                            return -1;
                        }
                        if (value1 instanceof Comparable && value2 instanceof Comparable) {
                            int value = ((Comparable) value2).compareTo(value1);
                            if (value == 0) {
                                continue;
                            }
                            value = value > 0 ? 1 : -1;
                            return desc ? value : -value;
                        }
                    } catch (Exception e) {
                        logger.error(" sortBy exception ", e);
                        throw new RuntimeException(" sortBy exception ", e);
                    }
                }
                return 0;
            }
        });
        return beanList;
    }

    public static BigDecimal getSumBeanPropertyList(final Collection beanList, String propertyname) {
        BigDecimal sum = BigDecimal.ZERO;
        List<BigDecimal> list = getBeanPropertyList(beanList, BigDecimal.class, propertyname, false);
        if (CollectionUtils.isEmpty(list)) {
            return sum;
        }
        for (BigDecimal t : list) {
            sum = sum.add(t == null ? BigDecimal.ZERO : t);
        }
        return sum;
    }

    public static <T> List<T> getBeanPropertyList(final Collection beanList, Class<T> clazz, String propertyname, boolean unique) {
        return getBeanPropertyList(beanList, propertyname, unique);
    }

    public static <T> Map beanListToMap(final Collection<T> beanList, String keyproperty) {
        Map result = new HashMap();
        for (Object bean : beanList) {
            try {
                Object key = PropertyUtils.getNestedProperty(bean, keyproperty);
                if (key != null) {
                    result.put(key, bean);
                }
            } catch (Exception e) {
                logger.error(" getBeanPropertyList exception ", e);
                throw new RuntimeException(" getBeanPropertyList  exception ", e);

            }
        }
        return result;
    }

    public static <T> List<T> getBeanPropertyList(final Collection beanList, String propertyname, boolean unique) {
        List<T> result = new ArrayList<T>();
        if (CollectionUtils.isEmpty(beanList)) {
            return result;
        }
        for (Object bean : beanList) {
            try {
                if(BeanUtils.isEmpty(bean)){
                    continue;
                }
                T pv = (T) PropertyUtils.getProperty(bean, propertyname);
                if (pv != null && (!unique || !result.contains(pv))) {
                    result.add(pv);
                }
            } catch (Exception e) {
                logger.error(" getBeanPropertyList exception ", e);
                throw new RuntimeException(" getBeanPropertyList exception ", e);
            }
        }
        return result;
    }


    public static List<String> convert(List<Object> list) {
        List<String> result = Lists.newArrayList();
        if (BeanUtils.isEmpty(list)) {
            return result;
        }
        for (Object o : list) {
            if (o != null) {
                result.add(o.toString());
            }
        }
        return result;
    }


    /**
     * 根据property的值将beanList分组
     *
     * @param beanList
     * @param property
     * @return
     */
    public static Map groupBeanList(final Collection beanList, String property) {
        return groupBeanList(beanList, property, null);
    }

    /**
     * 根据property的值将beanList分组, null作为单独一组，key 为nullKey
     *
     * @param beanList
     * @param property
     * @param nullKey
     * @return
     */
    public static Map groupBeanList(final Collection beanList, String property, Object nullKey) {
        Map<Object, List> result = new LinkedHashMap<Object, List>();
        if (CollectionUtils.isEmpty(beanList)) {
            return result;
        }
        for (Object bean : beanList) {
            try {
                Object keyvalue = PropertyUtils.getNestedProperty(bean, property);
                if (keyvalue == null) {
                    keyvalue = nullKey;
                }
                List tmpList = result.get(keyvalue);
                if (tmpList == null) {
                    tmpList = new ArrayList();
                    result.put(keyvalue, tmpList);
                }
                tmpList.add(bean);
            } catch (Exception e) {
                logger.error(" groupBeanList exception ", e);
                throw new RuntimeException(" groupBeanList exception ", e);
            }
        }
        return result;
    }

    public static Map<String, BigDecimal> merge(final Map<String, BigDecimal> a, final Map<String, BigDecimal> b) {
        Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
        if (a != null && b != null) {
            List<String> aKeys = new ArrayList<String>(a.keySet());
            List<String> bKeys = new ArrayList<String>(b.keySet());
            List<String> keys = intersect(aKeys, bKeys);
            for (String key : keys) {
                BigDecimal aValue = a.get(key);
                BigDecimal bValue = b.get(key);
                BigDecimal value = (aValue != null ? aValue : BigDecimal.ZERO).add(bValue != null ? bValue : BigDecimal.ZERO);
                map.put(key, value);
            }
        } else if (a == null) {
            return b;
        } else {
            return a;
        }
        return map;
    }

    public static <T> Map<String, Object> bean2Map(T t) {
        Map<String, Object> map;
        try {
            map = PropertyUtils.describe(t);
        } catch (Exception e) {
            logger.error(String.format(" bean2Map Exception [t:%s]", JSON.toJSONString(t)), e);
            throw new RuntimeException(String.format(" bean2Map Exception [t:%s]", JSON.toJSONString(t)), e);
        }
        return map;
    }

    public static <T> T map2Bean(Map<String, Object> map, Class<T> tClass) {
        T t = null;
        if (map == null || map == null) {
            return t;
        }
        try {
            t = tClass.newInstance();
            org.apache.commons.beanutils.BeanUtils.populate(t, map);
        } catch (Exception e) {
            logger.error(String.format(" map2Bean Exception [map:%s]", JSON.toJSONString(map)), e);
            throw new RuntimeException(String.format(" map2Bean Exception [map:%s]", JSON.toJSONString(map)), e);
        }
        return t;
    }


    public static <T> T copyOf(T t) {
        if (t == null) {
            return null;
        }
        List<T> list = copyOf(Arrays.asList(t));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public static <T> List<T> copyOf(List<T> list) {
        List<T> copyOfList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return copyOfList;
        }
        for (T t : list) {
            try {
                T copy = (T) t.getClass().newInstance();
                copyProperties(copy, t);
                copyOfList.add(copy);
            } catch (Exception e) {
                logger.error(" copyOf exception ", e);
            }
        }
        return copyOfList;
    }

    public static void copyProperties(Object dest, Object orig) {
        try {
            PropertyUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            logger.error(" copyProperties exception ", e);
            throw new RuntimeException(String.format(" copyProperties Exception [dest:%s,orig]", JSON.toJSONString(dest), JSON.toJSONString(orig)), e);
        }
    }

    /*
    public static void copyProperties(Object dest, Object orig, boolean ignoreNull) {
        try {
            if (ignoreNull) {
                org.springframework.beans.BeanUtils.copyProperties(orig, dest, getNullPropertyNames(orig));
            } else {
                org.springframework.beans.BeanUtils.copyProperties(orig, dest);
            }
        } catch (Exception e) {
            logger.error(" copyProperties ignoreNull exception ", e);
            throw new RuntimeException(String.format(" copyProperties Exception [dest:%s,orig]", JSON.toJSONString(dest), JSON.toJSONString(orig)), e);
        }
    }

    public static void copyProperties(Object dest, Object orig, String... ignoreProperties) {
        try {
            org.springframework.beans.BeanUtils.copyProperties(orig, dest, ignoreProperties);
        } catch (Exception e) {
            logger.error(" copyProperties ignoreNull exception ", e);
            throw new RuntimeException(String.format(" copyProperties Exception [dest:%s,orig]", JSON.toJSONString(dest), JSON.toJSONString(orig)), e);
        }
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (isEmpty(srcValue)) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    */

    public static Set<String> getNestedPropertyKey(Object objectSource) {
        Set<String> keys = new HashSet<>();
        try {
            if (!isDefineWrapClass(objectSource.getClass())) {
//				keys.add(objectSource.toString());
                return keys;
            }
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(objectSource.getClass());
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                Method method = propertyDescriptor.getReadMethod();
                if (propertyDescriptor.getReadMethod() == null) {
                    continue;
                }
                if (method.getGenericParameterTypes().length > 0) {
                    continue;
                }
                String name = propertyDescriptor.getName();
                Object value = method.invoke(objectSource);
                if ("class".equalsIgnoreCase(name) || "declaringClass".equalsIgnoreCase(name)) {
                    continue;
                }
                if (value == null) {
                    continue;
                }
                if (Map.class.isAssignableFrom(value.getClass())) { // Mapped name
                    Map map = ((Map) value);
                    name = StringUtils.substringBeforeLast(name, "Map");
                    for (Object key : map.keySet()) {
                        String mappedName = name + "." + key.toString();
                        Object nestedValue = map.get(key);
                        if (nestedValue == null || !isDefineWrapClass(nestedValue.getClass())) {
                            keys.add(mappedName);
                            break;
                        }
                        Set<String> nestedNames = getNestedPropertyKey(nestedValue);
                        for (String nestedName : nestedNames) {
                            keys.add(mappedName + "." + nestedName);
                        }
                    }
                } else if (List.class.isAssignableFrom(value.getClass())) { // Indexed name
                    List list = ((List) value);
                    name = StringUtils.substringBeforeLast(name, "List");
                    for (int i = 0; i < list.size(); i++) {
                        Object nestedValue = list.get(i);
                        if (nestedValue == null || !isDefineWrapClass(nestedValue.getClass())) {
                            keys.add(name);
                            continue;
                        }
                        String indexedName = name + "[" + i + "]";
                        Set<String> nestedNames = getNestedPropertyKey(list.get(i));
                        for (String nestedName : nestedNames) {
                            keys.add(indexedName + "." + nestedName);
                        }
                    }
                } else if (Enum.class.isAssignableFrom(value.getClass())) { // Enum Value
                    keys.add(name);
                } else if (!isDefineWrapClass(value.getClass())) { // Simple Value
                    keys.add(name);
                } else { // Nested Value
                    Set<String> nestedNames = getNestedPropertyKey(value);
                    for (String nestedName : nestedNames) {
                        keys.add(name + "." + nestedName);
                    }
                }
            }
        } catch (Throwable e) {
            logger.error(" getNestedPropertyKey exception ", e);
        }
        return keys;
    }

    //基本类型与包装类判断,String不是基本类型包装类
    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            logger.error(" isWrapClass exception ", e);
            return false;
        }
    }

    //如何判断一个类型是Java本身的类型，还是用户自定义的类型
    public static boolean isDefineWrapClass(Class clz) {
        try {
            return clz == null || clz.getClassLoader() != null;
        } catch (Exception e) {
            logger.error(" isDefineWrapClass exception ", e);
            return false;
        }
    }

    public static <T> Map beanListToMap(final Collection<T> beanList, String keyproperty, String valueproperty, boolean ignoreNull) {
        Map result = new HashMap();
        if (CollectionUtils.isEmpty(beanList)) {
            return result;
        }
        for (Object bean : beanList) {
            try {
                Object key = PropertyUtils.getNestedProperty(bean, keyproperty);
                Object value = PropertyUtils.getNestedProperty(bean, valueproperty);
                if (key == null) {
                    continue;
                }
                if (value != null) {
                    result.put(key, value);
                } else if (!ignoreNull) {
                    result.put(key, value);
                }
            } catch (Exception e) {
            }
        }
        return result;
    }



    public static Method getTargetMethodByParam(Class clazz, Object[] pararm,
                                                String methodName) {
        List<Method> mList = new ArrayList<Method>();

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {//add methodName some item
                method.setAccessible(true);
                mList.add(method);
            }
        }

        if (mList.size() == 0) {
            return null;
        }

        if (mList.size() == 1) {
            return mList.get(0);
        }

        Method result = null;
        for (Method m : mList) {
            Class[] classes = m.getParameterTypes();
            if (classes.length == 0 && (pararm == null || pararm.length == 0)) {
                return m;
            }
            if (pararm == null || pararm.length == 0) {
                return null;
            }
            if (classes.length != pararm.length) {
                continue;
            }
            boolean flag = true;
            for (int i = 0; i < classes.length; i++) {
                Class clzss = classes[i];
                Class paramClzss = pararm[i].getClass();
                if (!clzss.toString().equals(paramClzss.toString())) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                result = m;
                break;
            }
        }

        return result;

    }

    /**
     * get method key
     *
     * @param clazz
     * @param pararm
     * @param methodName
     * @return
     */
    public static String getClassMethodKey(Class clazz, Object[] pararm,
                                           String methodName) {

        StringBuilder sb = new StringBuilder();
        sb.append(clazz.toString());
        sb.append(".").append(methodName);
        if (pararm != null && pararm.length > 0) {
            for (Object obj : pararm) {
                sb.append("-").append(obj.getClass().toString());
            }
        }
        return sb.toString();

    }

    public static <T> void checkNull(String message, Class<T> clazz, T t, String... properties) {
        if (t == null) {
            throw new IllegalArgumentException(String.format(message + " check [className=%s] is null", clazz.getSimpleName()));
        }
        for (String property : properties) {
            try {
                Object value = PropertyUtils.getNestedProperty(t, property);
                if (isEmpty(value)) {
                    throw new IllegalArgumentException(String.format(message + " check [className=%s,object=%s] properties contain null", clazz.getSimpleName(), JSON.toJSONString(t)));
                }
            } catch (Exception e) {
                logger.error(" checkNull getNestedProperty  exception ", e);
                throw new RuntimeException(" checkNull getNestedProperty  exception ", e);
            }
        }
    }


    public static <T> T getMin(List<T> list, String propertyValue) {
        if (StringUtils.isBlank(propertyValue) || CollectionUtils.isEmpty(list)) {
            logger.warn(" getMin propertyValue or ts is null ");
            return null;
        }
        sortBy(list, true, propertyValue);
        return list.get(0);
    }

    public static <K> List<List<K>> splitList(List<K> param, int batchSize) {
        if (BeanUtils.isEmpty(param)) {
            return Lists.newArrayList();
        }
        List<List<K>> parts = Lists.partition(param, batchSize);
        return parts;
    }

    /**
     * Check if the given type represents a "simple" value type: a primitive, a
     * String or other CharSequence, a Number, a Date, a URI, a URL, a Locale or
     * a Class.
     *
     * @param clazz the type to check
     * @return whether the given type represents a "simple" value type
     */
    public static boolean isSimpleValueType(Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
                || clazz.equals(URI.class) || clazz.equals(URL.class) || clazz.equals(Locale.class) || clazz.equals(Class.class);
    }

    public static List getBeanMapList(final Collection beanList, boolean nested) {
        List result = new ArrayList<Map>();
        for (Object bean : beanList) {
            if (bean == null || isSimpleValueType(bean.getClass())) {
                result.add(bean);
            } else {
                result.add(getBeanMap(bean, nested));
            }
        }
        return result;
    }


    public static Map getBeanMap(final Object bean, boolean nested) {
        return getBeanMap(bean, nested, false);
    }

    public static Map<String, ?> getBeanMapWithNull(final Object bean, String... properties) {
        Map<String, ?> map = getBeanMap(bean, true);
        if (isEmpty(map)) {
            return map;
        }
        List<String> nullProperties = isEmpty(properties) ? new ArrayList<String>() : Arrays.asList(properties);
        Iterator<? extends Map.Entry<String, ?>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ?> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (isEmpty(value) && !nullProperties.contains(key)) {
                it.remove();
            }
        }
        return map;
    }

    public static Map<String, Object> getBeanMapWithProps(final Object bean, String... properties) {
        Map<String, Object> map = Maps.newHashMap();
        Map<String, Object> beanMap = getBeanMap(bean, true);
        if (isEmpty(beanMap) || BeanUtils.isEmpty(properties)) {
            return beanMap;
        }
        for (String property : properties) {
            Object value = beanMap.get(property);
            if (isEmpty(value)) {
                continue;
            }
            map.put(property, value);
        }
        return map;
    }

    public static Map<String, ?> getBeanMapNestWithNull(final Object bean, String... properties) {
        Map<String, Object> map = new HashMap();
        try {
            if (isEmpty(bean)) {
                return map;
            }
            Set<String> keys = getNestedPropertyKey(bean);
            for (String key : keys) {
                map.put(key, PropertyUtils.getNestedProperty(bean, key));
            }
            if (isEmpty(properties)) {
                return map;
            }
            for (String property : properties) {
                if (!map.keySet().contains(property)) {
                    map.put(property, null);
                }
            }
        } catch (Throwable e) {
            logger.error(" getBeanMapWithNull  exception ", e);
        }
        return map;
    }

    public static Map<String, Object> getBeanMap(final Object bean, boolean nested, boolean ignoreNull) {
        Map beanMap = Maps.newHashMap();
        if (bean == null) {
            return beanMap;
        }
        if (isSimpleValueType(bean.getClass())) {
            throw new IllegalArgumentException(String.format("[class:%s]bean can't be simple!", bean.getClass().getName()));
        }
//      Assert.isTrue(!isSimpleValueType(bean.getClass()), "bean can't be simple!");
        try {
            if (bean instanceof Map) {
                beanMap = new LinkedHashMap((Map) bean);
            } else {
                beanMap = PropertyUtils.describe(bean);
            }
            Object pv = null;
            for (Object key : new ArrayList(beanMap.keySet())) {
                pv = beanMap.get(key);
                if (pv == null) {
                    if (ignoreNull) {
                        beanMap.remove(key);
                    }
                } else if (!isSimpleValueType(pv.getClass())) {
                    if (!nested) {
                        beanMap.remove(key);
                    } else {
                        if (pv instanceof Collection) {
                            beanMap.put(key, getBeanMapList((Collection) pv, false));
                        } else if (pv.getClass().isArray()) {
                            beanMap.put(key, getBeanMapList(Arrays.asList((Object[]) pv), false));
                        } else {
                            beanMap.put(key, getBeanMap(pv, false));
                        }
                    }
                }
            }
            beanMap.remove("class");
        } catch (Exception e) {
            logger.error(" getBeanMap  exception ", e);
            return null;
        }
        return beanMap;
    }

    //生成区间随机数
    public static int random(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static int shardSuff4Mod8(String key){
        if(BeanUtils.isEmpty(key)){
            return 0;
        }
        String suffix = key.substring(key.length()>=4?key.length()-4:0);
        int suffNo= org.apache.commons.lang3.StringUtils.isNumeric(suffix)?Integer.parseInt(suffix):suffix.hashCode();
        return suffNo % 8;
    }

    public static List<String> shuffleRoundRobin(Map<String,Integer> src){
        AssertUtils.notEmpty(src,"shuffleRoundRobin src must not empty");
        Map<String,Integer> map = Maps.newLinkedHashMap(src);
        LinkedList<String> list = Lists.newLinkedList();
        int size = 0 ;
        int max  = 0 ;
        for(Map.Entry<String,Integer> e:map.entrySet()){
            if(e==null || BeanUtils.isEmpty(e.getKey()) || BeanUtils.isEmpty(e.getValue())){
                    continue;
            }
            size+=e.getValue();
            max = Math.max(max,e.getValue());
        }
        Iterator<Map.Entry<String,Integer>> it = map.entrySet().iterator();
        for(int i=0;list.size()<=size && i<=max*map.size();i++){
            if(it==null || !it.hasNext()){
                it = map.entrySet().iterator();
            }
            Map.Entry<String,Integer> e = it.next();
            Integer value = map.get(e.getKey());
            if(value!=null && value>0){
                list.add(e.getKey());
                map.put(e.getKey(),e.getValue()-1);
            }
        }
        return list;
    }

    public static void main(String[] args) {
        Map<String,Integer> map =  Maps.newLinkedHashMap();
        map.put("e3",2);
        map.put("e4",1);
        map.put("e1",5);
        map.put("e2",4);
        String str = JSON.toJSONString(map);
        Map<String,Integer> mm =  JSON.parseObject(null,Map.class);
        System.out.println(JSON.toJSONString(mm));
        List<String> result = shuffleRoundRobin(map);
        System.out.println(result);
    }
}
