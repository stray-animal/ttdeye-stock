package com.ttdeye.stock.common.domain;

import org.springframework.stereotype.Component;

@Component
public interface RedisMsg {
    /**
     * 接受信息
     * @param message
     */
    public void receiveMessage(String message);
}