package com.ttdeye.stock.common.utils;

import java.io.Serializable;

/**
 * @Comment: $comment$
 * @Author: Zhangyongming
 * @Date: $date$ $time$
 */
public class HttpResult implements Serializable{

    private Integer statusCode;
    private String content;

    public HttpResult(Integer statusCode, String content) {
        this.statusCode = statusCode;
        this.content = content;
    }
    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "statusCode=" + statusCode +
                ", content='" + content + '\'' +
                '}';
    }



}
