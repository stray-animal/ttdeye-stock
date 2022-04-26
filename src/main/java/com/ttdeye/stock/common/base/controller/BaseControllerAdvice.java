package com.ttdeye.stock.common.base.controller;


import com.ttdeye.stock.common.domain.ApiResponseCode;
import com.ttdeye.stock.common.domain.ApiResponseT;
import com.ttdeye.stock.common.exception.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基础控制器增强
 *
 * @author clayzhang
 */
@RestControllerAdvice
public class BaseControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(BaseControllerAdvice.class);

    public static final Pattern ERROR_MESSAGE_PATTERN = Pattern.compile("\\[(\\d{4})\\] (.*)");

    /**
     * 全局异常处理 TODO 兼顾老接口 先返回扁平结构
     */
    @ExceptionHandler
    public ApiResponseT globalExceptionHandler(HttpServletRequest request, Exception ex) {

        ApiResponseT apiResponse = new ApiResponseT(ApiResponseCode.SYSTEM_EXCEPTION);

        if (ex instanceof ApiException) {
            Integer code = ((ApiException) ex).getCode();

            printErrorMsg(request, ex);
            return new ApiResponseT(code, ex.getMessage());
        } else {
            logger.error("检测到未捕捉异常：IP:{} invoke url:{} Exception:{}", request.getRemoteAddr(), request.getRequestURL(),
                    ex.getClass().toGenericString(), ex);
        }


        if (StringUtils.isNotBlank(ex.getMessage())) {
            Matcher matcher = ERROR_MESSAGE_PATTERN.matcher(ex.getMessage());
            if (matcher.matches()) {
                logger.error(ex.getMessage(), ex);
                return apiResponse;
            }
        }

        return apiResponse;
    }

    private void printErrorMsg(HttpServletRequest request, Exception ex) {

        if (!(ex instanceof ApiException)) {
            return;
        }

        Integer code = ((ApiException) ex).getCode();
        ApiException.ErrorPrintLogLevelEnum errorPrintLogLevelEnum = ((ApiException) ex).getErrorPrintLogLevelEnum();

        if (errorPrintLogLevelEnum != null) {
            if (errorPrintLogLevelEnum == ApiException.ErrorPrintLogLevelEnum.TRACE) {
                logger.trace("检测到未捕捉异常：IP:{} invoke url:{} Exception:{}", request.getRemoteAddr(), request.getRequestURL(),
                        ex.getClass().toGenericString(), ex);
            } else if (errorPrintLogLevelEnum == ApiException.ErrorPrintLogLevelEnum.DEBUG) {
                logger.debug("检测到未捕捉异常：IP:{} invoke url:{} Exception:{}", request.getRemoteAddr(), request.getRequestURL(),
                        ex.getClass().toGenericString(), ex);
            } else if (errorPrintLogLevelEnum == ApiException.ErrorPrintLogLevelEnum.INFO) {
                logger.info("检测到未捕捉异常：IP:{} invoke url:{} Exception:{}", request.getRemoteAddr(), request.getRequestURL(),
                        ex.getClass().toGenericString(), ex);
            } else if (errorPrintLogLevelEnum == ApiException.ErrorPrintLogLevelEnum.WARN) {
                logger.warn("检测到未捕捉异常：IP:{} invoke url:{} Exception:{}", request.getRemoteAddr(), request.getRequestURL(),
                        ex.getClass().toGenericString(), ex);
            } else if (errorPrintLogLevelEnum == ApiException.ErrorPrintLogLevelEnum.ERROR) {
                logger.error("检测到未捕捉异常：IP:{} invoke url:{} Exception:{}", request.getRemoteAddr(), request.getRequestURL(),
                        ex.getClass().toGenericString(), ex);
            } else {

            }
        } else {
                logger.warn("检测到未捕捉异常：IP:{} invoke url:{} Exception:{}", request.getRemoteAddr(), request.getRequestURL(),
                        ex.getClass().toGenericString(), ex);
        }
    }

}
