package com.ttdeye.stock.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO 待重构
 *
 * @author clayzhang
 */
public class JacksonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        configMapper();
    }

    private static void configMapper() {


        /* 序列化配置 */
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);


        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

//        // 序列化时，如果值为空，则使用 "" 空字符串替换
//        mapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
//            @Override
//            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//                jsonGenerator.writeString("");
//            }
//        });

        // 数字进行序列化时，也添加引号
        // mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
        // mapper.configure(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS, true);

        // 序列化时，属性类型是数组、列表、集合等类型时，如果size()==1 ，则不使用 [] 包裹
        /// mapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);

        /* 反序列化配置 */

        // 列表、数组接受单个元素
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * JavaBean -> JsonString
     */
    public static String toJsonString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JsonString -> JavaBean
     */
    public static <T> T readValue(String jsonString, Class<T> valueType) {

        try {
            return mapper.readValue(jsonString, valueType);
        } catch (Exception e) {
            logger.error("将 JsonString 转换成：{} 时出现异常：{}", valueType.getName(), e.getMessage(), e);
        }
        return null;
    }

    public static <T> T readValue(String jsonString, TypeReference<T> valueTypeRef) {

        try {
            return mapper.readValue(jsonString, valueTypeRef);
        } catch (Exception e) {
            logger.error("将 JsonString 转换成：{} 时出现异常：{}", valueTypeRef.getType(), e.getMessage(), e);
        }

        return null;
    }

    public static Map<String, String> beanToStringMap(Object object) {

        Map<String, String> result = Maps.newHashMap();
        try {
            Map<String, Object> tmpMap = beanToObjectMap(object);
            for (Map.Entry<String, Object> entry : tmpMap.entrySet()) {
                result.put(entry.getKey(), (entry.getValue() instanceof String ? (String) entry.getValue() : toJsonString(entry.getValue())));
            }
        } catch (Exception e) {
            logger.error("对象转换 Map 时出现异常：{}, 对象内容：{}", e.getMessage(), object.toString(), e);

        }
        return result;
    }

    public static Map<String, Object> beanToObjectMap(Object object) {
        return convertValue(object, new TypeReference<Map<String, Object>>() {
        });
    }

    public static Map<String, String> jsonStrToStringMap(String jsonString) {

        Map<String, String> result = Maps.newHashMap();
        try {

            Map<String, Object> tmpMap = readValue(jsonString, new TypeReference<HashMap<String, Object>>() {
            });

            for (Map.Entry<String, Object> entry : tmpMap.entrySet()) {
                result.put(entry.getKey(), (entry.getValue() instanceof String ? (String) entry.getValue() : toJsonString(entry.getValue())));
            }
        } catch (Exception e) {
            logger.error("字符串转换 Map 时出现异常：{}, 字符串：{}", e.getMessage(), jsonString, e);
        }
        return result;
    }

    public static <T> T convertValue(Object object, Class<T> valueType) {
        return mapper.convertValue(object, valueType);
    }

    public static <T> T convertValue(Object object, TypeReference<T> typeRef) {
        return mapper.convertValue(object, typeRef);
    }


}
