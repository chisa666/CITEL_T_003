package com.example.travelstats.common;

import java.io.Serializable;

/**统一返回格式
 * 统一响应结果封装类
 *
 * @param <T> 响应数据类型
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码: 200-成功, 400-参数错误, 500-服务器错误 */
    private int code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    private Result() {
    }

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 成功响应(无数据) */
    public static <T> Result<T> ok() {
        return new Result<>(200, "success", null);
    }

    /** 成功响应(带数据) */
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    /** 成功响应(自定义消息) */
    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(200, message, data);
    }

    /** 失败响应 */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    /** 参数错误 */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }

    /** 服务器错误 */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
