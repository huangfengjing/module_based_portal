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

    public CmsLayoutPrototype(long dbId, String template) {
        this.dbId = dbId;
        this.template = template;
    }

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

    /**
     * 布局原型
     */
    public static final List<CmsLayoutPrototype> LAYOUT_PROTOTYPE_POOL = new ArrayList<CmsLayoutPrototype>() {
        {
            new CmsLayoutPrototype(1L, "<div class=\"layout_box\"#{if}($env.mode == 'design') data-prototype-id=\"0\" data-inst-id=\"0\"#{end}>\n" +
                    CONTENT_PLACEHOLDER +
                    "</div>");
        }
    };
}
