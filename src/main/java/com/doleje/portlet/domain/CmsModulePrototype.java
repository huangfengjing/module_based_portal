package com.doleje.portlet.domain;

import java.util.List;

/**
 * <p>
 * 店铺页面模块
 * </p>
 * Time: 12-11-27 下午3:27
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class CmsModulePrototype extends BaseFeatureSupport {

    /**
     * 模块名称
     */
    private String name;

    /**
     * 图标链接地址
     */
    private String iconLink;

    /**
     * 模块标题
     */
    private String title;

    /**
     * 模块 vm 内容
     */
    private String template;

    /**
     * 模块表单 vm 内容
     */
    private String formTemplate;

    /**
     * 模块说明
     */
    private String description;

    /**
     * 缓存过期时间（秒），格式：原型过期时间,渲染实例过期时间
     */
    private String cacheExpiredSeconds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconLink() {
        return iconLink;
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCacheExpiredSeconds() {
        return cacheExpiredSeconds;
    }

    public void setCacheExpiredSeconds(String cacheExpiredSeconds) {
        this.cacheExpiredSeconds = cacheExpiredSeconds;
    }
}
