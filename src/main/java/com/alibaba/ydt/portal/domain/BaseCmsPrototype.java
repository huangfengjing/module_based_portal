package com.alibaba.ydt.portal.domain;

import com.alibaba.ydt.portal.domain.common.BaseFeatureSupport;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang.StringUtils;

/**
 * 原型基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-25 上午9:55.
 */
public class BaseCmsPrototype extends BaseFeatureSupport {

    /**
     * 原型名称
     */
    private String name;

    /**
     * 原型说明
     */
    private String description;

    /**
     * 原型模板
     */
    private String template;

    /**
     * 表单模板
     */
    private String formTemplate;

    /**
     * 缓存过期时间（秒），格式：原型过期时间,渲染实例过期时间，-1 表示不过期
     */
    private String cacheExpiredSeconds = "-1,-1";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getFormTemplate() {
        return formTemplate;
    }

    public void setFormTemplate(String formTemplate) {
        this.formTemplate = formTemplate;
    }

    public String getCacheExpiredSeconds() {
        return cacheExpiredSeconds;
    }

    public void setCacheExpiredSeconds(String cacheExpiredSeconds) {
        this.cacheExpiredSeconds = cacheExpiredSeconds;
    }

    /**
     * 获取原型缓存过期时间
     * @return 原型缓存过期时间
     */
    public long getPrototypeExpiredSeconds() {
        if (StringUtils.isBlank(cacheExpiredSeconds) || !cacheExpiredSeconds.contains(",")) {
            return  -1;
        }
        String[] tmp = cacheExpiredSeconds.split(",");
        if(StringUtils.isBlank(tmp[0]) || !StringUtils.isNumeric(tmp[0])) {
            return -1;
        }
        return Long.valueOf(tmp[0]);
    }

    /**
     * 获取实例渲染结果缓存过期时间
     * @return 实例渲染结果缓存过期时间
     */
    public long getInstanceExpiredSeconds() {
        if (StringUtils.isBlank(cacheExpiredSeconds) || !cacheExpiredSeconds.contains(",")) {
            return  -1;
        }
        String[] tmp = cacheExpiredSeconds.split(",");
        if(StringUtils.isBlank(tmp[1]) || !StringUtils.isNumeric(tmp[1])) {
            return -1;
        }
        return Long.valueOf(tmp[1]);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", dbId).add("name", name).add("desc", description).toString();
    }
}