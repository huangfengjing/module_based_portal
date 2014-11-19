package com.doleje.portlet.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面
 *
 * @author <a href="mailto:huangfengjing@gmail.com>Ivan</a>
 * @since on 2012-12-17
 */
public class CmsPageInstance extends BaseModel {

    /**
     * 对应的原型 ID
     */
    private long prototypeId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String pageXmlContent;

    /**
     * 页面所包含的布局列表
     */
    private List<CmsLayoutInstance> layouts = new ArrayList<CmsLayoutInstance>();

    public long getPrototypeId() {
        return prototypeId;
    }

    public void setPrototypeId(long prototypeId) {
        this.prototypeId = prototypeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageXmlContent() {
        return pageXmlContent;
    }

    public void setPageXmlContent(String pageXmlContent) {
        this.pageXmlContent = pageXmlContent;
    }

    public List<CmsLayoutInstance> getLayouts() {
        return layouts;
    }

    public void setLayouts(List<CmsLayoutInstance> layouts) {
        this.layouts = layouts;
    }
}