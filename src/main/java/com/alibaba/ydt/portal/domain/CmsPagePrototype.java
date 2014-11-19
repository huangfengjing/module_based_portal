package com.alibaba.ydt.portal.domain;

import com.alibaba.ydt.portal.domain.common.BaseFeatureSupport;

/**
 * <p>
 * 页面原型
 * </p>
 * Time: 13-1-6 下午6:27
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class CmsPagePrototype extends BaseFeatureSupport {

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
}
