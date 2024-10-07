package com.whl.wako.kernel.util;


import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtils {

    public static final int PERCENTAGE_SCALE = 4;
    public static final int CURRENCY_SCALE = 2;
    private static String CURRENCY_FORMAT = new DecimalFormatSymbols(Locale.SIMPLIFIED_CHINESE).getCurrencySymbol() + "#,##0.00";
    private static String PERCENTAGE_FORMAT = new DecimalFormatSymbols(Locale.SIMPLIFIED_CHINESE).getCurrencySymbol() + "#,##0.00%";
    private static String POINTS_FORMAT = new DecimalFormatSymbols(Locale.SIMPLIFIED_CHINESE).getCurrencySymbol() + "#,##0";
    public static final String EMPTY = "";

    public static final Long LONG_ZERO = Long.valueOf(0L);
    public static final Integer INTEGER_ZERO = Integer.valueOf(0);
    public static final Double DOUBLE_ZERO = Double.valueOf(0.00d);



    public static  <T extends Number> T nullIf(T t1,@NotNull T t2){
        if(t1==null){
            return t2;
        }
       return t1;
    }

    public static  <T extends Number> String toEmptyString(T t){
        if(t==null){
            return EMPTY;
        }
        return t.toString();
    }

    public static String formatWithoutCurrency(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == -1) {
            amount = BigDecimal.ZERO.subtract(amount);
            String result = new DecimalFormat(CURRENCY_FORMAT).format(amount);
            return "-" + result.substring(1, result.length());
        }
        String result = new DecimalFormat(CURRENCY_FORMAT).format(amount);
        return result.substring(1, result.length());
    }

    public static String formatPoints(BigDecimal amount) {
        String result = new DecimalFormat(POINTS_FORMAT).format(amount);
        return result.substring(1, result.length());
    }

    public static String formatPercentage(BigDecimal amount) {
        String result = new DecimalFormat(PERCENTAGE_FORMAT).format(amount);
        return result.substring(1, result.length());
    }

    public static BigDecimal withPercentageScale(BigDecimal value) {
        return value.setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }

    public static String formatWithFraction(BigDecimal bigDecimal){
        return bigDecimal.intValue()+":1";
    }

    public static BigDecimal divide(Long value1,Long value2){
        if(value2==null || value2==0){
            return BigDecimal.ONE;
        }
        BigDecimal v1 =new BigDecimal(value1);
        BigDecimal v2 =new BigDecimal(value2);
        return v1.divide(v2,CURRENCY_SCALE,RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(BigDecimal value1,BigDecimal value2){
        return value1.divide(value2,CURRENCY_SCALE,RoundingMode.HALF_UP);
    }

    public static String formatPercentage(Long value1,Long value2){
      return   formatPercentage(divide(value1,value2));
    }

    public static BigDecimal withCurrencyScale(BigDecimal value) {
        return value.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal withCurrencyScaleFloor(BigDecimal value) {
        return value.setScale(CURRENCY_SCALE, RoundingMode.FLOOR);
    }

    public static boolean isLessThanZero(BigDecimal value) {
        return value!=null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean isGreaterThanZero(BigDecimal value) {
    	return value!=null && value.compareTo(BigDecimal.ZERO) > 0;
    }
    public static boolean isGreaterThanZero(Double value) {
    	return value!=null && value.doubleValue() > 0;
    }

    public static boolean isGreaterThanZero(Integer value) {
    	return value!=null && value > 0;
    }

    public static boolean isGreaterThanZero(Long value) {
    	return value!=null && value > 0;
    }

    public static boolean isEqualsToZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isLessOrEqualThanZero(BigDecimal value) {
        return value!=null && value.compareTo(BigDecimal.ZERO) <= 0;
    }

    public static boolean isGreaterOrEqualThan(BigDecimal value1, BigDecimal value2) {
        if(null==value1){
            value1 = BigDecimal.ZERO;
        }
        if(null==value2){
            value2 = BigDecimal.ZERO;
        }
        return value1!=null && value1.compareTo(value2) >= 0;
    }

    public static boolean isEqualThan(Integer value1, Integer value2) {
        if(value1==null && value2==null){
            return true;
        }else if(value1==null || value2==null){
            return false;
        }
        return value1.intValue()==value2.intValue();
    }

    public static boolean isEqualThan(BigDecimal value1, BigDecimal value2) {
        if(value1==null && value2==null){
            return true;
        }else if(value1==null || value2==null){
            return false;
        }
        return value1.compareTo(value2)==0;
    }

    public static boolean isGreaterThan(BigDecimal value1, BigDecimal value2) {
        if(value1==null){
            value1 = BigDecimal.ZERO;
        }
        if(value2==null){
            value2 = BigDecimal.ZERO;
        }
        return value1.compareTo(value2) > 0;
    }

    public static <T extends Number> boolean isGreaterOrEqualThan(T  value1, T value2) {
        if(value1 == null && value2 == null){
                return true;
        }else if(value1 == null){
                return false;
        }else if(value2 == null){
            return true;
        }else{
            return value1.intValue()>=value2.intValue();
        }
    }
    public static <T extends Number> BigDecimal multi(T  value1, T value2) {
        if(value1 == null || value2 == null){
                return BigDecimal.ZERO;
        }
        return new BigDecimal(value1.toString()).multiply(new BigDecimal(value2.toString()));
    }

    public static BigDecimal add(BigDecimal ...  values){
        BigDecimal result = BigDecimal.ZERO;
    	if(values==null){
    		return result;
    	}
    	for(BigDecimal value: values){
    	    if(null==value){
                continue;
            }
            result = result.add(value);
        }
    	return result;
    }

    public static BigDecimal add(BigDecimal value1,BigDecimal value2){
    	if(value1==null){
    		value1 = BigDecimal.ZERO;
    	}
    	if(value2==null){
    		value2 = BigDecimal.ZERO;
    	}
    	return value1.add(value2);
    }

    public static BigDecimal ceil(BigDecimal value1,BigDecimal value2){
    	if(value1==null){
    		value1 = BigDecimal.ZERO;
    	}
    	if(value2==null){
    		value2 = BigDecimal.ZERO;
    	}
        if(isLessOrEqualThanZero(value1)){
            return value2;
        }
        BigDecimal floor = BigDecimal.valueOf(Math.floor(value1.doubleValue()));
    	if(isEqualThan(floor,value1)){
            return value1;
        }
        BigDecimal min = add(floor,value2);
        BigDecimal max =  (BigDecimal.valueOf(Math.ceil(value1.doubleValue())));
        return isGreaterOrEqualThan(min,value1)?min:max;
    }

    public static Double add(Double value1,Double value2){
    	if(value1==null){
    		value1 = 0.0d;
    	}
    	if(value2==null){
    		value2 = 0.0d;
    	}
    	return value1+value2;
    }

    public static Integer add(Integer value1,Integer value2){
    	if(value1==null){
    		value1 = 0;
    	}
    	if(value2==null){
    		value2 = 0;
    	}
    	return value1+value2;
    }

    public static Long add(Long value1, Long value2) {
        if (value1 == null) {
            value1 = 0L;
        }
        if (value2 == null) {
            value2 = 0L;
        }
        return value1 + value2;
    }

    public static BigDecimal subtract(BigDecimal value1,BigDecimal value2){
    	if(value1==null){
    		value1 = BigDecimal.ZERO;
    	}
    	if(value2==null){
    		value2 = BigDecimal.ZERO;
    	}
    	return value1.subtract(value2);
    }

    public static Integer subtract(Integer value1,Integer value2){
    	if(value1==null){
    		value1 = 0;
    	}
    	if(value2==null){
    		value2 = 0;
    	}
    	return value1-value2;
    }
    public static Double subtract(Double value1,Double value2){
        BigDecimal b1 = new BigDecimal(Double.toString(trimDoubleNull(value1)));
        BigDecimal b2 = new BigDecimal(Double.toString(trimDoubleNull(value2)));
        return b1.subtract(b2).doubleValue();
    }


    public static int trimIntNull(Integer t){
        return trimNull(t,Integer.class);
    }

    public static long trimLongNull(Long t){
        return trimNull(t,Long.class);
    }

    public static double trimDoubleNull(Double t){
        return trimNull(t,Double.class);
    }

    public static BigDecimal trimBigDecimalNull(BigDecimal t){
        return trimNull(t,BigDecimal.class);
    }

    public static <T extends Number> T trimNull(T t,Class<T> clazz) {
        if(null==t){
            if(Integer.class.equals(clazz)){
                return (T)Integer.valueOf(0);
            }else if(Long.class.equals(clazz)){
                return (T)Long.valueOf(0);
            }else if(BigDecimal.class.equals(clazz)){
                return (T)BigDecimal.ZERO;
            }else if(Double.class.equals(clazz)){
                return (T)Double.valueOf(0);
            }
        }
        return t;
    }

    public static BigDecimal trimToBigDecimal(String str) {
        if(BeanUtils.isEmpty(str)){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(str.trim());
    }


    public static BigDecimal trimToBigDecimal(String str, BigDecimal defaultValue) {
        if(BeanUtils.isEmpty(str)){
            return defaultValue;
        }
        return new BigDecimal(str.trim());
    }

}
