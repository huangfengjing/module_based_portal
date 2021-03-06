package com.alibaba.ydt.portal.common;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

import java.util.HashSet;
import java.util.Set;

/**
 * 排队除法过滤器
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-9-23 下午2:11.
 */
public class JsonExcludePropertyPreFilter implements PropertyPreFilter {

    private final Class<?>    clazz;
    private final Set<String> includes = new HashSet<String>();
    private final Set<String> excludes = new HashSet<String>();

    public JsonExcludePropertyPreFilter(String... properties){
        this(null, properties);
    }

    public JsonExcludePropertyPreFilter(Class<?> clazz, String... properties){
        super();
        this.clazz = clazz;
        for (String item : properties) {
            if (item != null) {
                this.excludes.add(item);
            }
        }
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Set<String> getIncludes() {
        return includes;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public boolean apply(JSONSerializer serializer, Object source, String name) {
        if (source == null) {
            return true;
        }

        if (clazz != null && !clazz.isInstance(source)) {
            return true;
        }

        if (this.excludes.contains(name)) {
            return false;
        }

        if (includes.size() == 0 || includes.contains(name)) {
            return true;
        }

        return false;
    }

}