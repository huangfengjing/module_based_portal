package com.alibaba.ydt.portal.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 布局原型
 */
public class CmsLayoutPrototype {

    public static final String CONTENT_PLACEHOLDER = "${layout_content}";

    /**
     * ID
     */
    private long dbId;

    /**
     * 模板内容
     */
    private String template;

    /**
     * 表单模板
     */
    private String formTemplate;

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
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
}
