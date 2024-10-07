package com.whl.wako.kernel.util;


/**
 * @author paul.wei
 * @Description: 扩展util
 * @date 2018/6/15
 */
public class BooleanUtils {

    /**
     * @Description:判断是否
     * @params [b]
     * @return boolean
     * @author paul.wei
     * @date 2018/6/15
     */
    public static boolean isTrue(Boolean b){
        return b!=null? b.booleanValue():Boolean.FALSE;
    }

    /**
     * @Description:
     * @params [b]
     * @return boolean
     * @author paul.wei
     * @date 2018/6/19
     */
    public static boolean isFalse(Boolean b){
        return !isTrue(b);
    }

    /**
     * @param str
     * @param defaultValue
     * @return
     */
    public static Boolean toBooleanDigit(String str, Boolean defaultValue) {
        return str != null ? str.trim().equals("1") : defaultValue;
    }
    /**
     * @param defaultValue
     * @return
     */
    public static <T extends Number>  Boolean toBooleanDigit(T t, Boolean defaultValue) {
        return t != null ? t.intValue()==1 : defaultValue;
    }
    /**
     * @param str
     * @return
     */
    public static Boolean toBooleanDigit(String str) {
        return toBooleanDigit(str,Boolean.FALSE);
    }
    /**
     * @param t
     * @return
     */
    public static <T extends Number> Boolean toBooleanDigit(T t) {
        return toBooleanDigit(t,Boolean.FALSE);
    }
}
