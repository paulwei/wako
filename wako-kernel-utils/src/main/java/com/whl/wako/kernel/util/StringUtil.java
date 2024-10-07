package com.whl.wako.kernel.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.whl.wako.kernel.util.ann.SplitAnn;
import com.whl.wako.kernel.util.ann.SplitBean;
import com.whl.wako.kernel.util.ann.SplitSeg;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by weihonglin on 18/10/27.
 */
public  class StringUtil {
    private final static  Logger logger = LoggerFactory.getLogger(StringUtil.class);
    private static final Map<Class<?>, Map<Field,Integer>> classMap =new ConcurrentHashMap<>();
    public static final String EMPTY = "";
    public static final String DASH = "-";
    public static final String DOT = ".";
    public static final String UNDERLINE = "_";

    public static String appendIfNumPlus(String str){
        if(str==null){
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        int index = str.lastIndexOf(DASH);
        if(index<0){
            return str.trim()+DASH+1;
        }
        if(index==(str.length()-1)){
            return str.substring(0,index).trim()+DASH+1;
        }
        String suffix = str.substring(index+1).trim();
        if(NumberUtils.isNumber(suffix)){
            int digit  = (Integer.valueOf(suffix));
            return str.substring(0,index).trim()+DASH+(++digit);
        }
        return str;
    }

    public static String appendIfNoPresentNumPlus(String str,String suffix){
        if(str==null){
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        int index = str.lastIndexOf(suffix);
        if(index<0){
            return str.trim()+suffix;
        }
        return str;
    }

    public static String trimNull(String str){
        if(str==null){
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        return str.trim();
    }

    public static String trim(String str,String defaultValue){
        if(BeanUtils.isEmpty(str)){
            return trimNull(defaultValue);
        }
        return str.trim();
    }

    public static <T extends Number> String trimNull(T t){
        if(t==null){
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        return t.toString();
    }

    public static String reverse(Class clazz){
        if(null==clazz){
            return "";
        }
        return reverse(clazz.getCanonicalName(),DOT);
    }

    public static String reverse(String str,String separator){
        List<String> list = split(str,separator);
        Collections.reverse(list);
        return join(separator,list);

    }


    public static String getMD5String(String data) {
        if(BeanUtils.isEmpty(data)){
            return "";
        }
        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(data.getBytes("UTF8"));
            return new String(Hex.encodeHex(messageDigest.digest()));
        } catch (Exception e) {
            throw new RuntimeException("getMD5String digest eception data="+data,e);
        }
    }


    public static   String join(String separator,List beanList,String propertyname){
           List<String>  list = BeanUtils.getBeanPropertyList(beanList, String.class, propertyname, true);
           return join(separator,list);

    }

    public static String join(String separator,List<String> args){
        if(StringUtils.isBlank(separator)){
            throw new RuntimeException("separator must be not null");
        }
        if(CollectionUtils.isEmpty(args)){
           return "";
        }
        return  Joiner.on(separator).skipNulls().join(args);
    }

    public static String join(String separator,String... args){
        if(StringUtils.isBlank(separator)){
            throw new RuntimeException("separator must be not null");
        }
        if(args==null || args.length==0){
            return "";
        }
        return  Joiner.on(separator).skipNulls().join(args);
    }

    public static String joinPath(String... args){
        return join(File.separator,args);
    }

    public static String filterNull(String separator,String str){
        List<String> list = StringUtil.split(separator,str);
        return  join(separator,BeanUtils.filterNull(list));
    }

    public static <T extends SplitBean> T split(String str, String separator, Class<T> clazz){
        try {
            T t = clazz.newInstance();
            if(BeanUtils.isEmpty(str) || BeanUtils.isEmpty(separator)){
                logger.info("StringUtil split illegal params ignore str:{},separator:{},clazz:{}",str,separator,clazz);
                return t;
            }
            SplitAnn splitAnn =  clazz.getAnnotation(SplitAnn.class);
            if(splitAnn==null){
                return t;
            }
            String[] arr = str.split(separator);
            if(BeanUtils.isEmpty(arr)){
                return t;
            }

            Map<Field,Integer> fieldMap = classMap.computeIfAbsent(clazz,k->fieldMap(clazz));
            for (Map.Entry<Field,Integer> entry: fieldMap.entrySet()) {
                Field field = entry.getKey();
                int order = entry.getValue();
                if(order>=arr.length){
                    logger.warn("StringUtil order must less then arr.length str:{},separator:{},clazz:{}",str,separator,clazz);
                    continue;
                }
                String value = arr[order];
                if(value==null){
                    continue;
                }
                value = value.trim();
                if(field.getType()== String.class){
                    field.set(t,TypeUtils.castToString(value));
                }else if(field.getType()== Integer.class){
                    field.set(t,TypeUtils.castToInt(value));
                }else if(field.getType()== Long.class){
                    field.set(t,TypeUtils.castToLong(value));
                }else if(field.getType()== BigDecimal.class){
                    field.set(t,TypeUtils.castToBigDecimal(value));
                }else if(field.getType()== Double.class){
                    field.set(t,TypeUtils.castToDouble(value));
                }else if(field.getType()== Date.class){
                    field.set(t,TypeUtils.castToDate(value));
                }
            }
            if(logger.isDebugEnabled()){
                logger.debug("StringUtil split t:{},str:{},separator:{},clazz:{}", JSON.toJSONString(t),str,separator,clazz);
            }
            return t;
        } catch (InstantiationException e) {
            logger.error("StringUtil split InstantiationException str:"+str+",separator:"+separator+",clazz:"+clazz,e);
            return null;
        } catch (IllegalAccessException e) {
            logger.error("StringUtil split IllegalAccessException str:"+str+",separator:"+separator+",clazz:"+clazz,e);
            return null;
        }
    }

    public static  <T extends SplitBean>  Map<Field,Integer> fieldMap(Class<T> clazz){
        Map<Field,Integer> fieldMap = new LinkedHashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            SplitSeg seg = field.getAnnotation(SplitSeg.class);
            if(seg==null){
                continue;
            }
            int order = seg.order();
            if(seg.order()<0){
                throw new RuntimeException(String.format("SplitSeg注解order必须大于等于0[%s]",field.getName()));
            }
            field.setAccessible(true);
            fieldMap.put(field,order);
        }
        logger.info("StringUtil fieldMap:{},clazz:{}",JSON.toJSONString(fieldMap),clazz);
        System.out.println("StringUtil fieldMap:"+JSON.toJSONString(fieldMap)+",clazz:"+clazz);
        return fieldMap;
    }

    public static List<String> split(String str,String separator){
        List<String> list  =  new ArrayList<>();
        if(StringUtils.isBlank(separator)){
            throw new RuntimeException("separator must be not null");
        }
        if(StringUtils.isBlank(str)){
            return list;
        }
        Splitter splitter = Splitter.on(separator).omitEmptyStrings().trimResults();
        List<String> unmodifiableList =  splitter.splitToList(str);
        list.addAll(unmodifiableList);
        return list;
    }

    public static String[] split(String str,String separator,int pivot){
        int begin = 0;
        String[] arr = new String[pivot];
        int len = str.length();
        for(int i = 0; i < pivot; i++){
            if(BeanUtils.isEmpty(str) || BeanUtils.isEmpty(separator) || begin>len || pivot<1){
                throw new RuntimeException(String.format("split illegal params str:%s,separator:%s,pivot:%s,i:%s",str,separator,pivot,i));
            }
            int end =  str.indexOf(separator,begin);
            if(i<pivot-1 && end>begin){
                arr[i]=str.substring(begin,end);
            }else{
                arr[i]=str.substring(begin);
            }
            if(end<0){
                break;
            }
            begin = end+1;
        }
        return arr;
    }

    public  static  String contactHttp(String domain,String url){
        if(StringUtils.isBlank(url)){
            return domain;
        }
        if((url.trim().startsWith("http"))){
              return url;
        }
        return contactBackSlash(domain,url);
    }

    public  static String escape(String s){
        if (s == null || "".equals(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '>':
                    sb.append('＞');//全角大于号
                    break;
                case '<':
                    sb.append('＜');//全角小于号
                    break;
                case '\'':
                    sb.append(' ');//全角小于号
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    public  static  String contactBackSlash(String a,String b){
        return contact("/",a,b);
    }

    public  static  String contact(String sperator,String a,String b){
        if(StringUtils.isBlank(sperator)){
            return  a+b;
        }
        if(a==null){
            return b;
        }
        if(b==null){
            return a;
        }
        a = StringUtils.removeEnd(a,sperator);
        b = StringUtils.removeStart(b, sperator);
        return a+sperator+b;
    }

    public  static  String trunc(String prefix,String original){
        if(StringUtils.isBlank(original) || StringUtils.isBlank(prefix)){
            return  original;
        }
        return original.replaceFirst(prefix,"");
    }

    public  static  String truncChar(String str,int length){
        if(StringUtils.isBlank(str)){
            return  str;
        }
        return org.apache.commons.lang3.StringUtils.truncate(str,length);
    }

    public  static  String trunc(String original,int length){
        if(StringUtils.isBlank(original)){
            return  original;
        }
        int ln = getUnicodeLength(original);
        length = length>ln?ln:length;
        return getUnicode(original).substring(0,length);
    }

    public  static  String truncWith(String original,String witch){
        if(StringUtils.isBlank(original)){
            return  original;
        }
        String[] strArray = original.split(witch);
        if(strArray==null || strArray.length<=0){
            return "";
        }
        return strArray[0];
    }

    public  static  String truncBlank(String original,int length){
        if(StringUtils.isBlank(original)){
            return  original;
        }
        original=original.replaceAll("[\u3000|\\s|\\n|\\r]+"," ").trim();
        int ln = original.length();
        length = length>ln?ln:length;
        return original.substring(0,length);
    }

    public static String toString(Object obj){
        return obj!=null?ReflectionToStringBuilder.toString(obj):"";
    }

    /**
     * 获取unicode编码长度
     * @param original
     * @return
     */
    public  static  int getUnicodeLength(String original){
        String unicodeStr = getUnicode(original);
        if(unicodeStr==null){
            return  0;
        }
        return  unicodeStr.length();
    }

    /**
     * 获取unicode编码
     * @param original
     * @return
     */
    public static String getUnicode(String original) {
        if (original == null) {
            return null;
        }
        String result = "";
        for (int i = 0, length = original.length(); i < length; i++) {
            if (original.charAt(i) > 0 && original.charAt(i) < 256) {
                result += original.charAt(i);
            } else {
                result += "\\u" + Integer.toHexString(original.charAt(i)).toUpperCase();
            }
        }
        return result;
    }

    public static String byte2Str(Byte bt) {
        if(bt==null){
            return "";
        }
        return bt.toString().trim();
    }

}
