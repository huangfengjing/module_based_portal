package com.alibaba.ydt.portal.domain;

import com.google.common.base.MoreObjects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 页面模块实例
 * </p>
 * Time: 12-11-27 下午3:27
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
@Entity
@Table(name = "portal_cms_module_instance")
public class CmsModuleInstance extends BaseCmsInstance {

    public static final String TYPE_TAG = "module";

    /**
     * 布局名称
     */
    @Basic
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getInstanceTypeTag() {
        return TYPE_TAG;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", dbId).add("prototypeId", prototypeId).add("title", title).toString();
    }
}