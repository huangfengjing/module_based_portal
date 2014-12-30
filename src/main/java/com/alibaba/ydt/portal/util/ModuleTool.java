package com.alibaba.ydt.portal.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * CMS 工具类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-12-30 下午4:00.
 */
abstract public class ModuleTool {


    /**
     * 判断一个对象是否为 true： boolean 对象直接返回其值，其它返回 toString = true
     *
     * @param obj 待判断的对象
     * @return 是否为 true 的判断结果
     */
    public static boolean isTrue(Object obj) {
        if (null == obj) {
            return false;
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        return "true".equals(obj.toString().toLowerCase());
    }

    /**
     * 判断一个对象是否不为 true： boolean 对象直接返回其值，其它返回 toString = true
     *
     * @param obj 待判断的对象
     * @return 是否为 true 的判断结果
     */
    public static boolean isNotTrue(Object obj) {
        return !isTrue(obj);
    }

    /**
     * 判断对象是否为空：null , 或者 空字符串，或者空的集合将返回 true，否则返回 false
     *
     * @param obj 要判断的对象
     * @return 是否为空的判断结果
     */
    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj instanceof String) {
            return StringUtils.isEmpty(obj.toString());
        }
        return obj instanceof Collection && ((Collection) obj).isEmpty();
    }

    /**
     * 判断对象是否不为空：null , 或者 空字符串，或者空的集合将返回 false，否则返回 true
     *
     * @param obj 要判断的对象
     * @return 是否为空的判断结果
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断某对象为 NULL
     * @param obj 待判断的对象
     * @return 是否为空的标识
     */
    public static boolean isNull(Object obj) {
        return null == obj;
    }

    /**
     * 判断某对象不为 NULL
     * @param obj 待判断的对象
     * @return 是否为空的标识
     */
    public static boolean isNotNull(Object obj) {
        return null != obj;
    }

    /**
     * 判断容器中是否含有某对象（对象将通过 toString 转换为选定字符串），如果容器为字符串，则返回容器字符串是否包含选定字符串的结果
     * 如果容器为 MAP，则返回容器 KEY SET 是否含有选定字符串
     * 如果容器为 LIST，则返回容器中是否包含选定字符串
     *
     * @param container 容器
     * @param key       字符串
     * @return 含有则返回 true，否则返回 false
     */
    public static boolean contains(Object container, Object key) {
        if (null == container || null == key) {
            return false;
        }
        String strKey = key.toString();
        if (container instanceof String) {
            return ((String) container).contains(strKey);
        } else if (container instanceof List) {
            for (Object obj : (List) container) {
                if (strKey.equals(obj.toString())) {
                    return true;
                }
            }
        } else if (container instanceof Map) {
            for (Object obj : ((Map) container).keySet()) {
                if (strKey.equals(obj.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
}
