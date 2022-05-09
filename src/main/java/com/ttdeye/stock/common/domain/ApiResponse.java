package com.ttdeye.stock.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.ttdeye.stock.common.utils.JacksonUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * API 响应 TODO 待完成
 *
 * @author clayzhang
 */
@Data
@ToString
public class ApiResponse implements Serializable {

    private int code;

    private String message;

    private Map<String, Object> data = new HashMap<>();


    public static ApiResponse builder() {
        return new ApiResponse();
    }

    public ApiResponse() {
        this.code = ApiResponseCode.SUCCESS.code;
        this.message = ApiResponseCode.SUCCESS.message;
    }

    public ApiResponse(ApiResponseCode responseCode) {
        this.code = responseCode.code;
        this.message = responseCode.message;
    }

    public ApiResponse(ApiResponseCode responseCode, String message) {
        this.code = responseCode.code;
        this.message = message;
    }

    public ApiResponse(IApiResponseCode responseCode, String message) {
        this.code = responseCode.getCode();
        this.message = message;
    }

    public ApiResponse(IApiResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse setResponseCodeAndMessage(ApiResponseCode responseCode) {
        this.code = responseCode.code;
        this.message = responseCode.message;
        return this;
    }

    public ApiResponse setResponseCodeAndMessage(ApiResponseCode responseCode, String message) {
        this.code = responseCode.code;
        this.message = message;
        return this;
    }

    public ApiResponse setResponseCodeAndMessage(IApiResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        return this;
    }

    public ApiResponse setResponseCodeAndMessage(IApiResponseCode responseCode, String message) {
        this.code = responseCode.getCode();
        this.message = message;
        return this;
    }

    public ApiResponse setResponseCodeAndMessage(int code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }

    public ApiResponse set(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public ApiResponse setData(Map<String, Object> data) {
        return this.setData(data, true);
    }

    public ApiResponse setData(Map<String, Object> data, boolean append) {

        if (data == null) {
            return this;
        }

        if (append) {
            this.data.putAll(data);
        } else {
            this.data = data;
        }

        return this;
    }

    public ApiResponse setPojo(Object entity) {
        return this.setPojo(entity, true);
    }

    public ApiResponse setPojo(Object object, boolean append) {

        if (object == null) {
            return this;
        }

        if (object instanceof Boolean
                || object instanceof Number
                || object instanceof CharSequence
                || object instanceof List
                || object.getClass().isArray()
                || object.getClass().isPrimitive()) {
            throw new RuntimeException("Boolean Number CharSequence List Array Primitive 类型的数据，请使用 set(key, value) 方法");
        }

        if (object instanceof Map && ((Map) object).isEmpty()) {
            return this;
        }


        String json = JacksonUtil.toJsonString(object);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(json) && json.length() >= 5) {
            return setData( JacksonUtil.beanToObjectMap(object) , append);
        } else {
            throw new RuntimeException("无法将值转为Map类型，请使用 set(key, value) 方法");
        }
    }

    public FlatApiResponse toFlat() {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(data.size() + 2);
        map.put("errcode", code);
        map.put("errmsg", message);

        if (!CollectionUtils.isEmpty(data)) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }

        return new FlatApiResponse(map);
    }

    /**
     * 这是一个扁平的数据结构类。将data层级里的key-value，放到外面一层。
     */
    class FlatApiResponse extends ApiResponse implements Map<String, Object> {

        public FlatApiResponse(Map<String, Object> map) {
            getData().putAll(map);
        }

        @JsonIgnore
        @Override
        public Map<String, Object> getData() {
            return super.getData();
        }

        @Override
        public int size() {
            return getData().size();
        }

        @Override
        public boolean isEmpty() {
            return getData().isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return getData().containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return getData().containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return getData().get(key);
        }

        @Override
        public Object put(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<String> keySet() {
            return getData().keySet();
        }

        @Override
        public Collection<Object> values() {
            return getData().values();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return getData().entrySet();
        }
    }
}
