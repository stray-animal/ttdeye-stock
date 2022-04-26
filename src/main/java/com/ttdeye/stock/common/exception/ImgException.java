package com.ttdeye.stock.common.exception;


import lombok.Data;

/**
 * @Description 自定义异常
 * @Author 张永明
 * @Date 2018/8/2
 * @Param
 * @return
 **/
@Data
public class ImgException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误编码
     */
    private Integer code;

    /**
     * 消息是否为属性文件中的Key
     */
    private boolean propertiesKey = true;

    /**
     * 构造一个基本异常.
     *
     * @param message 信息描述
     */
    public ImgException(String message) {
        super(message);
    }

    /**
     * 构造一个基本异常.
     *
     * @param code    错误编码
     * @param message 信息描述
     */
    public ImgException(Integer code, String message) {
        this(code, message, true);
    }

    /**
     * 构造一个基本异常.
     *
     * @param code    错误编码
     * @param message 信息描述
     */
    public ImgException(Integer code, String message, Throwable cause) {
        this(code, message, cause, true);
    }

    /**
     * 构造一个基本异常.
     *
     * @param code          错误编码
     * @param message       信息描述
     * @param propertiesKey 消息是否为属性文件中的Key
     */
    public ImgException(Integer code, String message, boolean propertiesKey) {
        super(message);
        this.setCode(code);
        this.setPropertiesKey(propertiesKey);
    }

    /**
     * 构造一个基本异常.
     *
     * @param code    错误编码
     * @param message 信息描述
     */
    public ImgException(Integer code, String message, Throwable cause, boolean propertiesKey) {
        super(message, cause);
        this.setCode(code);
        this.setPropertiesKey(propertiesKey);
    }

    /**
     * 构造一个基本异常.
     *
     * @param message 信息描述
     * @param cause   根异常类（可以存入任何异常）
     */
    public ImgException(String message, Throwable cause) {
        super(message, cause);
    }

    public boolean isPropertiesKey() {
        return propertiesKey;
    }

    public void setPropertiesKey(boolean propertiesKey) {
        this.propertiesKey = propertiesKey;
    }
}
