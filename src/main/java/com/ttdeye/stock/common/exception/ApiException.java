package com.ttdeye.stock.common.exception;

import com.ttdeye.stock.common.domain.ApiResponseCode;
import com.ttdeye.stock.common.domain.IApiResponseCode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO 待完成
 *
 * @author clayzhang
 */
@Data
public class ApiException extends RuntimeException {

    private ApiResponseCode responseCode;

    private IApiResponseCode iApiResponseCode;

    private ErrorPrintLogLevelEnum errorPrintLogLevelEnum;

    private Integer code;

    public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ApiException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(ApiResponseCode responseCode) {
        super(responseCode.message);
        this.responseCode = responseCode;
        this.code = responseCode.getCode();
    }

    public ApiException(ApiResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
        this.code = responseCode.getCode();
    }

    public ApiException(ApiResponseCode responseCode, Throwable cause) {
        super(responseCode.message, cause);
        this.responseCode = responseCode;
        this.code = responseCode.getCode();
    }

    public ApiException(ApiResponseCode responseCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(responseCode.message, cause, enableSuppression, writableStackTrace);
        this.responseCode = responseCode;
        this.code = responseCode.getCode();
    }


    /**
     *  ==============================================================================
     *      基于IApiResponseCode接口，针对ApiResponseCode之前的方法，提供同样的方法
     *  ==============================================================================
     */
    public ApiException(IApiResponseCode iApiResponseCode) {
        super(iApiResponseCode.getMessage());
        this.iApiResponseCode = iApiResponseCode;
        this.code = iApiResponseCode.getCode();
    }

    public ApiException(IApiResponseCode iApiResponseCode, String message) {
        super(message);
        this.iApiResponseCode = iApiResponseCode;
        this.code = iApiResponseCode.getCode();
    }

    public ApiException(IApiResponseCode iApiResponseCode, String message,ErrorPrintLogLevelEnum errorPrintLogLevel) {
        super(message);
        this.iApiResponseCode = iApiResponseCode;
        this.code = iApiResponseCode.getCode();
        this.errorPrintLogLevelEnum = errorPrintLogLevel;
    }

    public ApiException(IApiResponseCode iApiResponseCode, Throwable cause) {
        super(iApiResponseCode.getMessage(), cause);
        this.iApiResponseCode = iApiResponseCode;
        this.code = iApiResponseCode.getCode();
    }

    public ApiException(IApiResponseCode iApiResponseCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(iApiResponseCode.getMessage(), cause, enableSuppression, writableStackTrace);
        this.iApiResponseCode = iApiResponseCode;
        this.code = iApiResponseCode.getCode();
    }


    /**
     * 异常打印级别
     */
    public static enum ErrorPrintLogLevelEnum {
        NONE,
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR;
    }

}
