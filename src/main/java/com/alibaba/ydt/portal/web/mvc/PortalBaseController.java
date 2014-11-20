package com.alibaba.ydt.portal.web.mvc;

import com.alibaba.ydt.portal.util.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.util.*;

/**
 * Base multiple action, provide some utilities
 *
 * @author <a href="huangfengjing@gmail.com">Ivan</a>
 */
public abstract class PortalBaseController {

    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Join error message
     *
     * @param errorResult error result map
     * @return message joined
     */
    public String joinErrorMsg(Map<String, String> errorResult) {
        if (!errorResult.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean isFirst = true;
            for (String msg : errorResult.values()) {
                if (isFirst) {
                    sb.append(",");
                    isFirst = false;
                }
                sb.append(msg);
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Copy bean properties without some common properties such as ID, version, createdOn, lastModifiedOn
     *
     * @param source      source object
     * @param ignoreProps properties will be ignored
     * @param dest        destination object
     */
    public void copyWithoutCommonProps(Object source, Object dest, String... ignoreProps) {
        if (null == ignoreProps) {
            ignoreProps = new String[0];
        }
        List<String> common = new ArrayList<String>();
        common.add("id");
        common.add("dbId");
        common.add("version");
        common.add("createdOn");
        common.add("features");
        common.add("featureMap");
        common.add("serialVersionUID");
        String[] ignores = new String[ignoreProps.length + common.size()];
        System.arraycopy(ignoreProps, 0, ignores, 0, ignoreProps.length);
        int index = ignoreProps.length;
        for (String p : common) {
            ignores[index++] = p;
        }
        BeanUtils.copyProperties(source, dest, ignores);
    }

    /**
     * Convert object properties to a map with out very common and ignored properties
     *
     * @param o           object to convert
     * @param ignoreProps properties will be ignored when converting
     * @return object properties map
     */
    public Map<String, Object> convertObjectWithoutCommon2Map(Object o, String[] ignoreProps) {
        List<String> common = new ArrayList<String>();
        common.add("version");
        common.add("features");
        common.add("featureMap");
        common.add("serialVersionUID");
        common.addAll(Arrays.asList(ignoreProps));
        return convertObject2Map(o, common.toArray(new String[common.size()]));
    }

    /**
     * Convert object properties to a map with out ignored properties
     *
     * @param o           object to convert
     * @param ignoreProps properties will be ignored when converting
     * @return object properties map
     */
    public Map<String, Object> convertObject2Map(Object o, String[] ignoreProps) {
        Map<String, Object> data = new HashMap<String, Object>();
        String propName = null;
        try {
            for (Field f : ReflectionUtils.getAllFields(o.getClass())) {
                if (Modifier.isTransient(f.getModifiers()) || Modifier.isVolatile(f.getModifiers())
                        || Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                propName = f.getName();
                boolean skip = false;
                for (String p : ignoreProps) {
                    if (propName.equals(p)) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }
                ReflectionUtils.makeAccessible(f);
                Object propValue = ReflectionUtils.getProperty(o, propName);
                // 兼容
                if ("dbId".equals(propName)) {
                    data.put("id", propValue);
                }
                data.put(propName, propValue);
            }
        } catch (Exception e) {
            logger.error("Error occurs when retrieving property, object: " + o + ", property: " + propName, e);
            // ignore
        }
        return data;
    }

    /**
     * convert the parameter data to the map
     *
     * @param data the parameter data such as foo=a&example=true
     * @return converted map such as {foo: a, exmpale: true}
     */
    public Map<String, String> convertParams2Map(String data) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            for (String pair : data.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    map.put(kv[0], URLDecoder.decode(kv[1], "utf-8"));
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return map;
    }
}