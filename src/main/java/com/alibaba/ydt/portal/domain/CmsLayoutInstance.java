package com.alibaba.ydt.portal.domain;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * CMS 页面布局对象
 * </p>
 * Time: 13-1-4 下午2:44
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
@Entity
@Table(name = "portal_cms_layout_instance")
public class CmsLayoutInstance extends BaseCmsInstance {

    public static final String TYPE_TAG = "layout";

    /**
     * 布局名称
     */
    @Basic
    private String title;

    /**
     * 布局所包含的列
     */
    @Transient
    private List<CmsColumnInstance> columns = new ArrayList<CmsColumnInstance>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Transient
    public List<CmsColumnInstance> getColumns() {
        return columns;
    }

    @Transient
    public void setColumns(List<CmsColumnInstance> columns) {
        this.columns = columns;
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
