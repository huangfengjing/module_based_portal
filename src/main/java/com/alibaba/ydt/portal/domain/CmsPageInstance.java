package com.alibaba.ydt.portal.domain;

import com.alibaba.ydt.portal.util.CmsUtils;
import com.alibaba.ydt.portal.util.JsonUtils;
import com.alibaba.ydt.portal.util.StringUtils;
import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面
 *
 * @author <a href="mailto:huangfengjing@gmail.com>Ivan</a>
 * @since on 2012-12-17
 */
@Entity
@Table(name = "portal_cms_page_instance")
public class CmsPageInstance extends BaseCmsInstance {

    public static final String TYPE_TAG = "page";
    /**
     * 布局名称
     */
    @Basic
    private String title;

    /**
     * 内容
     */
    @Basic
    @Column(name = "XML_CONTENT")
    private String xmlContent;

    /**
     * 页面所包含的布局列表
     */
    @Transient
    private List<CmsLayoutInstance> layouts = new ArrayList<CmsLayoutInstance>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
        this.layouts = CmsUtils.parsePage(xmlContent).getLayouts();
    }

    @Transient
    public List<CmsLayoutInstance> getLayouts() {
        if((null == layouts || layouts.isEmpty()) && StringUtils.isNotBlank(xmlContent)) {
            this.layouts = CmsUtils.parsePage(xmlContent).getLayouts();
        }
        return layouts;
    }

    @Transient
    public void setLayouts(List<CmsLayoutInstance> layouts) {
        if(null == layouts) {
            return;
        }
        this.layouts = layouts;
        xmlContent = CmsUtils.pageToXmlString(this);
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