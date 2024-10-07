package com.whl.wako.kernel.util.enums;

/**
 * @author paul.wei
 * @Description: 字节单位
 * @date 2018/7/19
 */
public enum ByteUnit {
    BYTE {
        @Override
        public long toByte(long d)   { return d; }
        @Override
        public long toKb(long d)   { return d/delta; }
        @Override
        public long toMb(long d)   { return d/delta/delta; }
        @Override
        public long toGb(long d)   { return d/delta/delta/delta; }
        @Override
        public long toTb(long d)   { return d/delta/delta/delta/delta; }
        @Override
        public long convert(long d, ByteUnit u) { return u.toByte(d); }
    },
    KB {
        @Override
        public long toByte(long d)   { return d*delta; }
        @Override
        public long toKb(long d)   { return d; }
        @Override
        public long toMb(long d)   { return d/delta; }
        @Override
        public long toGb(long d)   { return d/delta/delta; }
        @Override
        public long toTb(long d)   { return d/delta/delta/delta; }
//        public long convert(long d, ByteUnit u) { return u.toKb(d); }
    },
    MB {
        @Override
        public long toByte(long d)   { return d*delta*delta; }
        @Override
        public long toKb(long d)   { return d*delta; }
        @Override
        public long toMb(long d)   { return d; }
        @Override
        public long toGb(long d)   { return d/delta; }
        @Override
        public long toTb(long d)   { return d/delta/delta; }
        @Override
        public long convert(long d, ByteUnit u) { return u.toByte(d); }
    },
    GB {
        @Override
        public long toByte(long d)   { return d*delta*delta*delta; }
        @Override
        public long toKb(long d)   { return d*delta*delta; }
        @Override
        public long toMb(long d)   { return d*delta; }
        @Override
        public long toGb(long d)   { return d; }
        @Override
        public long toTb(long d)   { return d/delta; }
        @Override
        public long convert(long d, ByteUnit u) { return u.toByte(d); }
    },
    TB {
        @Override
        public long toByte(long d)   { return d*delta*delta*delta*delta; }
        @Override
        public long toKb(long d)   { return d*delta*delta*delta; }
        @Override
        public long toMb(long d)   { return d*delta*delta; }
        @Override
        public long toGb(long d)   { return d*delta; }
        @Override
        public long toTb(long d)   { return d; }
        @Override
        public long convert(long d, ByteUnit u) { return u.toTb(d); }
    }
    ;
    static final long delta = 1024L;
    public long convert(long sourceDuration, ByteUnit sourceUnit) {
        throw new AbstractMethodError();
    }
    public long toByte(long d)   {
        throw new AbstractMethodError();
    }
    public long toKb(long d)   {
        throw new AbstractMethodError();
    }
    public long toMb(long d)   {
        throw new AbstractMethodError();
    }
    public long toGb(long d)   {
        throw new AbstractMethodError();
    }
    public long toTb(long d)   {
        throw new AbstractMethodError();
    }

}
