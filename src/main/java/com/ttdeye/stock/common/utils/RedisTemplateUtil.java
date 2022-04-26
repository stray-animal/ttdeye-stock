
package com.ttdeye.stock.common.utils;


import com.ttdeye.stock.common.base.ErrorCode;
import com.ttdeye.stock.common.exception.ProxyJedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis cache 工具类
 */
@Component
public final class RedisTemplateUtil {

    private static Logger logger = LoggerFactory.getLogger(RedisTemplateUtil.class);

    /**
     * @描述:redis session 模板
     */
    @Resource
    private RedisTemplate<Serializable, Object> redisTemplate;




    /**
     * @param keys
     * @
     * @描述:批量删除key 的value
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除 正则表达式相应的 key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0){
            redisTemplate.delete(keys);
        }

    }


    /**
     * @param key
     * @描述:删除对应的value
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * @param key
     * @return
     * @描述:判断缓存中是否有对应的value
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * @param key
     * @return
     * @描述:读取相应的key的缓存
     */
    public Object get(final String key) {

        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get( key);
        return result;
    }


    /**
     * 通配获取rediskey
     *
     * @title keys
     * @author yongming.zhang
     * @date 2017-05-17
     */
    public List<String> keys(String key) {
        try {
            Set<Serializable> keys = redisTemplate.keys(key);

            List<String> ks = new ArrayList<String>();

            for (Serializable k : keys) {
                String kStr = k.toString();
                ks.add(kStr);
            }
            return ks;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("#获取keys的过程中发生错误", e);
            return null;
        }

    }


    /**
     * @param key
     * @param value
     * @return
     * @描述:永久写入缓存
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @param value
     * @return
     * @描述:时间限制写入缓存
     */
    public boolean set(final String key, Object value, Long expireTime) throws ProxyJedisException {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate
                    .opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            throw new ProxyJedisException(ErrorCode.Redis.REDIS_ERROR_CODE,"缓存" + key + "异常！");
            }
        return result;
    }

    /**
     * 执行SETNX命令
     * @param key
     * @param key
     * @param expireTime
     * @return
     */
    public boolean setNx(String key, String value, int expireTime) {
        boolean success = (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.set(key.getBytes(), value.getBytes(), Expiration.from(expireTime, TimeUnit.SECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT));
        return success;
    }

    /**
     * 
     * incr:自获取增
     * @author wind
     * @param key
     * @return Object
     * @throws 
     * @since  CodingExample　Ver 1.1
     */
    public long incr(final String key) {
        
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        Long num = null;
        num = operations.increment(key, 1L);
        if(num.compareTo(9999L)==0){
            redisTemplate.delete( key);
        }
        return num;
    }



    /**
     *
     * incr:自获取增
     * @author wind
     * @param key
     * @return Object
     * @throws
     * @since  CodingExample　Ver 1.1
     */
    public long incrByValue(final String key,Long value) {

        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        Long num = null;
        num = operations.increment(key, value);
        if(num.compareTo(9999L)==0){
            redisTemplate.delete( key);
        }
        return num;
    }






    public boolean hmsetAll(String key, Map<Object, Object> map) {
        boolean result = false;
        try {
            HashOperations<Serializable, Object, Object> operations = redisTemplate.opsForHash();
            operations.putAll(key, map);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public boolean hmset(String key, String hk, Object hv) {
        boolean result = false;
        try {
            HashOperations<Serializable, Serializable, Object> operations = redisTemplate.opsForHash();
            operations.put(key, hk, hv);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Object hmget(String key, String hk) {
        try {
            HashOperations<Serializable, Serializable, Object> operations = redisTemplate.opsForHash();
            return operations.get(key, hk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Object, Object> hmget(String key) {
        try {
            HashOperations<Serializable, Object, Object> operations = redisTemplate.opsForHash();
            return operations.entries(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 加锁
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public Boolean getLock(String key,String value,Long seconds){
        Boolean lockStatus = this.redisTemplate.opsForValue().setIfAbsent(key,value, Duration.ofSeconds(seconds));
        return lockStatus;
    }


    /**
     * 释放锁
     * @param key
     * @param value
     * @return
     */
    public Long unLock(String key,String value){
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript,Long.class);
        Long releaseStatus = (Long)this.redisTemplate.execute(redisScript, Collections.singletonList(key),value);
        return releaseStatus;
    }


}