package com.whl.wako.kernel.util.enums;

public enum SplitType {
    VERTICAL("\\|", "竖线"),
    DASH("\\-", "中划线"),
    UNDERLINE("\\_", "下划线"),
    COLON("\\:", "冒号"),

    ;

    SplitType(String reg, String desc) {
        this.reg = reg;
        this.desc = desc;
    }

    private String reg;
    private String desc;

    public String getReg() {
        return reg;
    }

    public String getDesc() {
        return desc;
    }
}
