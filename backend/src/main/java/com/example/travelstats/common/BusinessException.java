package com.example.travelstats.common;

/**自定义异常
 * 业务异常类
 * 用于抛出可预见的业务逻辑异常
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
