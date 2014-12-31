package com.alibaba.ydt.portal.domain;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * CMS 页面列对象
 * </p>
 * Time: 13-1-4 下午2:44
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
@Entity
@Table(name = "portal_cms_column_instance")
public class CmsColumnInstance extends BaseCmsInstance {

    public static final String TYPE_TAG = "column";

    /**
     * 布局名称
     */
    @Basic
    private String title;

    /**
     * 布局栏目列表
     */
    @Transient
    private List<CmsModuleInstance> modules = new ArrayList<CmsModuleInstance>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Transient
    public List<CmsModuleInstance> getModules() {
        return modules;
    }

    @Transient
    public void setModules(List<CmsModuleInstance> modules) {
        this.modules = modules;
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
