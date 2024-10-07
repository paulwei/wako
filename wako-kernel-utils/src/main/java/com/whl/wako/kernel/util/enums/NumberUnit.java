package com.whl.wako.kernel.util.enums;

import java.math.BigDecimal;

/**
 * @author paul.wei
 * @Description: 数字单位
 * @date 2018/8/23
 */
public enum NumberUnit {
    INTEGER {
        @Override
        public Integer convert(Number d, NumberUnit u) { return u.toInteger(d); }
    },
    LONG {
        @Override
        public Long convert(Number d, NumberUnit u) { return u.toLong(d); }
    },
    BIGDECIMAL {
        @Override
        public BigDecimal convert(Number d, NumberUnit u) { return u.toBigDecimal(d); }
    },
    ;
    static final long delta = 1024L;
    public  <T extends Number> T convert(Number sourceDuration, NumberUnit sourceUnit) {
        throw new AbstractMethodError();
    }
    public Integer toInteger(Number d)   {
        return d==null?0:d.intValue();
    }
    public Long toLong(Number d)   {
        return d==null?0L:d.longValue();
    }
    public BigDecimal toBigDecimal(Number d)   {
        return d==null?BigDecimal.ZERO:new BigDecimal(d.toString());
    }

}
