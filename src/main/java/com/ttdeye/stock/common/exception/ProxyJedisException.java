package com.ttdeye.stock.common.exception;

import com.ttdeye.stock.common.base.BaseException;
import com.ttdeye.stock.common.base.ErrorCode;

/**
 * @author ZYM
 * @描述： Jedis缓存Redis时异常
 * @date 2017.06.08
 */
public class ProxyJedisException extends BaseException {

    private static final long serialVersionUID = -1241159187644741192L;


    public ProxyJedisException(Object code, String message) {
        super(code,message);
    }

    public ProxyJedisException() {
        super(ErrorCode.Access.ACCESS_ERROR_CODE, ErrorCode.Access.ACCESS_ERROR_MSG);
    }

}
