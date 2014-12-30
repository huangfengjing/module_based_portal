package com.alibaba.ydt.portal.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 店铺页面模块
 * </p>
 * Time: 12-11-27 下午3:27
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
@Entity
@Table(name = "portal_cms_module_prototype")
public class CmsModulePrototype extends BaseCmsPrototype {

    // 基于 Quercus 框架处理
    public static final String SCRIPT_PHP = "PHP";

    // 基于 Java 与 Groovy 的整合
    public static final String SCRIPT_GROOVY = "GROOVY";

    /**
     * 图标链接地址
     */
    @Basic
    @Column(name = "ICON_URL")
    private String iconURL;

    /**
     * 脚本类型
     */
    @Basic
    @Column(name = "SCRIPT_TYPE")
    private String scriptType;

    /**
     * 脚本内容
     */
    @Basic
    @Column(name = "SCRIPT_CONTENT")
    private String scriptContent;

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }
}
