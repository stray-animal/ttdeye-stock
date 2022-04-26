package com.ttdeye.stock.common.domain;

import lombok.Data;

import java.io.Serializable;


/**
 * 通用返回实体
 * @param <T>
 */
@Data
public class ApiResponseT<T> implements Serializable {

    /**
     * 状态码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    public ApiResponseT() {
    }

    public ApiResponseT(Integer status) {
        this.code = status;
    }

    public ApiResponseT(Integer status, String message) {
        this.code = status;
        this.message = message;
    }

    public ApiResponseT(T data) {
        this.code = 0;
        this.data = data;
    }

    public ApiResponseT(Integer status, String message, T data) {
        this.code = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseT<T> ok(T body) {
        return new ApiResponseT<>(body);
    }

    public static <T> ApiResponseT<T> ok() {
        return new ApiResponseT<>(0,"success");
    }

    public static <T> ApiResponseT<T> ok(Integer status, T body) {
        return new ApiResponseT<>(status, null, body);
    }


    public static <T> ApiResponseT<T> ok(Integer status, String msg, T body) {
        return new ApiResponseT<>(status, msg, body);
    }

    public static <T> ApiResponseT<T> failed(Integer status) {
        return new ApiResponseT<>(status);
    }

    public static <T> ApiResponseT<T> failed() {
        return new ApiResponseT<>(ApiResponseCode.COMMON_FAILED_CODE.code);
    }


    public static <T> ApiResponseT<T> failed(String msg) {
        return new ApiResponseT<>(ApiResponseCode.COMMON_FAILED_CODE.code,msg);
    }

    public static <T> ApiResponseT<T> failed(Integer status, String msg) {
        return new ApiResponseT<>(status, msg);
    }

    public static <T> ApiResponseT<T> failed(Integer status, String msg, T body) {
        return new ApiResponseT<>(status, msg, body);
    }

}
