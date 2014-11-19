package com.doleje.portlet.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 列原型
 */
public class CmsColumnPrototype {

    public static final String CONTENT_PLACEHOLDER = "${column_content}";

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

    /**
     * 参数
     */
    private String params4Store;

    public CmsColumnPrototype(long dbId, String template) {
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
     * 当前已有的列原型池
     */
    public static final List<CmsColumnPrototype> COLUMN_PROTOTYPE_POOL = new ArrayList<CmsColumnPrototype>(){
        {
            add(new CmsColumnPrototype(1L, "<div class=\"column_box width25p\"#{if}($env.mode == 'design') data-inst-id=\"0\"#{end}>" + CONTENT_PLACEHOLDER + "</div>"));
        }
    };
}
