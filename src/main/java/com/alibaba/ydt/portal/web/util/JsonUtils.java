package com.alibaba.ydt.portal.web.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.ydt.portal.common.JsonExcludePropertyPreFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * JSON 工具类
 * </p>
 * Time: 13-1-4 下午1:37
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public abstract class JsonUtils {

    static final SerializeConfig config = new SerializeConfig();

    static {
        config.put(java.util.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
        config.put(java.sql.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
    }

    public static final SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null
    };

    public static String toCompatibleJSONString(Object object) {
        return JSON.toJSONString(object, config, features);
    }
    public static String toCompatibleJSONString(Object object, PropertyPreFilter filter) {
        return JSON.toJSONString(object, filter, features);
    }

    /**
     * 递归处理 JSON 对象为 java map
     *
     * @param valueObj JSON对象
     * @return java map
     */
    public static Map<Object, Object> processObject(JSONObject valueObj) {
        Map<Object, Object> result = new HashMap<Object, Object>();
        for (Object key : valueObj.keySet()) {
            Object value = valueObj.get(key);
            if (value instanceof JSONObject) {
                result.put(key, processObject((JSONObject) value));
            } else if (value instanceof JSONArray) {
                result.put(key, processArray((JSONArray) value));
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * 递归处理数组类型 JSON 为 java list
     *
     * @param valueObject JSON数组
     * @return java list
     */
    public static List<Object> processArray(JSONArray valueObject) {
        List<Object> list = new ArrayList<Object>();
        for (Object obj : valueObject) {
            if (obj instanceof JSONObject) {
                list.add(JSON.toJavaObject((JSONObject) obj, Map.class));
            } else if (obj instanceof JSONArray) {
                list.addAll(processArray((JSONArray) obj));
            } else {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * 创建一个新的 json config，统一处理默认过滤的字段，日期格式等
     *
     * @return json config
     */
    public static PropertyPreFilter newCommonFilter(Class cls, String... excludeProps) {
        return new JsonExcludePropertyPreFilter(cls, excludeProps);
    }
}
