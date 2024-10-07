package com.whl.wako.kernel.util.exception;

public class ClockBackException extends RuntimeException {
    private String code;

    public ClockBackException(String code) {
        this.code = code;
    }

    public ClockBackException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ClockBackException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
