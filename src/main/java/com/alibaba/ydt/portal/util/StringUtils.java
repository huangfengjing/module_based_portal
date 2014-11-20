/*
 * Copyright 2007-2008 Inc OF CCNU OF HUBEI.CHINA.PR.
 * 
 * Licensed under the Inc License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://inc.ccnu.edu.cn/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.ydt.portal.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * String related utility
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

    public static final Log log = LogFactory.getLog(StringUtils.class);

    /**
     * Counter
     */
    private static int countNumber;

    private static final byte[] lock = new byte[0];

    /**
     * Convert ISO encoded string to a utf-8 encoded string
     *
     * @param str ISO encoded string
     * @return utf-8 encoded string
     */
    public static String iso2utf8(String str) {
        try {
            return new String(str.getBytes("iso-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    /**
     * Convert utf-8 encoded string to a ISO encoded string
     *
     * @param str utf-8 encoded string
     * @return ISO encoded string
     */
    public static String utf82iso(String str) {
        try {
            return new String(str.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    /**
     * 获取“是”“否”表示的布尔值
     *
     * @param value 布尔值
     * @return “是”或者“否”
     */
    public static String getShiFouBooleanValue(boolean value) {
        return value ? "是" : "否";
    }

    /**
     * 随机获取UUID字符串(无中划线)
     *
     * @return UUID字符串
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24);
    }

    /**
     * 随机获取UUID字符串(无中划线)
     *
     * @return UUID字符串
     */
    public static String getUUID(int length) {
        String uuid = UUID.randomUUID().toString();
        if(length >= uuid.length()) {
            return uuid.toString();
        }
        int seg = length / 2;
        int left = length - seg;
        return uuid.substring(0, seg) + uuid.substring(uuid.length() - left);
    }

    /**
     * 随机获取字符串
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String getRandomString(int length) {
        if (length <= 0) {
            return "";
        }
        char[] randomChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd',
                'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm'};
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(randomChar[Math.abs(random.nextInt()) % randomChar.length]);
        }
        return stringBuilder.toString();
    }

    /**
     * 根据分隔符将字符串分隔成 List
     *
     * @param str    需要处理的字符串
     * @param sep 分隔符
     * @return 字符串集合
     */
    public static List<Long> splitToIdList(String str, String sep) {
        if(StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        List<Long> list = new ArrayList<Long>();
        for(String tmp : str.split(sep)) {
            if(StringUtils.isBlank(tmp) || !StringUtils.isNumeric(tmp)) {
                continue;
            }
            list.add(Long.valueOf(tmp));
        }
        return list;
    }

    /**
     * 将对象转换成 JSON 字符串
     * @param o 待转换的对象
     * @return JSON字符串
     */
    public static String toJsonString(Object o) {
        return JsonUtils.toCompatibleJSONString(o);
    }

    /**
     * 将原字符串包装后再串接
     * @param collection 待串接的字符串
     * @param sep 分隔符
     * @param prefix 包装前缀
     * @param suffix 包装后缀
     * @return 串接后的字符串
     */
    public String joinWithWrapper(Collection<String> collection, String sep, String prefix, String suffix) {
        List<String> wrapped = new ArrayList<String>();
        for(String str : collection) {
            wrapped.add(prefix + str + suffix);
        }
        return join(wrapped, sep);
    }

    /**
     * 将参数串接到 url 后面
     * @param url url
     * @param params 参数
     * @return 新的 url
     */
    public static String joinUrlWithParam(String url, Map<String, String> params) {
        if(StringUtils.isBlank(url)) {
            return null;
        }
        if(null == params || params.isEmpty()) {
            return url;
        }
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();
        for(String key : params.keySet()) {
            if(!isFirst) {
                sb.append("&");
            }
            sb.append(key).append("=").append(params.get(key));
            isFirst = false;
        }
        if(url.contains("?")) {
            return url += "&" + sb.toString();
        }
        return url += "?" + sb.toString();
    }

    /**
     * 将参数格式的字符串转换为 map
     * @param query 参数格式的字符串
     * @return map
     */
    public static Map<String, String> queryToMap(String query) {
        if(StringUtils.isBlank(query)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<String, String>();
        for(String pair : query.split("&")) {
            String[] keyValue = pair.split("=");
            if(keyValue.length != 2) {
                continue;
            }
            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }

    /**
     * 将字符串中间部分字符用 * 号代替
     * @param original 原始字符串
     * @param prefixCharCount 前面所保留的字符串长度
     * @param suffixCharCount 后面所保留的字符串长度
     * @return 代替后的字符串
     */
    public static String enMidCharWithStar(String original, int prefixCharCount, int suffixCharCount) {
        if(StringUtils.isBlank(original) || prefixCharCount > original.length() || suffixCharCount > original.length()
                || prefixCharCount + suffixCharCount > original.length()) {
            return original;
        }
        String prefix = original.substring(0, prefixCharCount);
        String suffix = original.substring(original.length() - suffixCharCount);
        String mid = StringUtils.repeat("*", original.length() - prefixCharCount - suffixCharCount);
        return prefix + mid + suffix;
    }

    public static void main(String[] args) {
        System.out.println(getUUID(10));
        System.out.println(getUUID(13));
        System.out.println(getUUID(7));
        System.out.println(enMidCharWithStar("le8jglkgd", 2, 2));
        System.out.println(enMidCharWithStar("lllfhowiethn", 8, 2));
        System.out.println(enMidCharWithStar("lllfhowiethn", 8, 20));
    }
}
