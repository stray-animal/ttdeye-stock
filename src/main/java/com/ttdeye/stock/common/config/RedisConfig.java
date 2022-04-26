package com.ttdeye.stock.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

/**
 * @Comment: $
 * @Author: Zhangyongming
 * @Date: $ $
 */
@Configuration
public class RedisConfig {


    @Autowired
    private RedisTemplate redisTemplate;

    @Bean
    public RedisTemplate<Serializable, Object> stringSerializerRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        //把对象转化为json保存
        RedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jsonSerializer);
        return redisTemplate;
    }

}
