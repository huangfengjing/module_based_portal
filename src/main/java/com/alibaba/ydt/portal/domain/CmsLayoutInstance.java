package com.alibaba.ydt.portal.domain;

import com.alibaba.ydt.portal.domain.common.ParameterSupportModel;
import com.alibaba.ydt.portal.util.CmsUtils;
import com.alibaba.ydt.portal.util.JsonUtils;

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
public class CmsLayoutInstance extends ParameterSupportModel {

    /**
     * 原型 ID
     */
    private long prototypeId;

    /**
     * 布局名称
     */
    private String title;

    /**
     * 布局所包含的列
     */
    private List<CmsColumnInstance> columns = new ArrayList<CmsColumnInstance>();

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

    public List<CmsColumnInstance> getColumns() {
        return columns;
    }

    public void setColumns(List<CmsColumnInstance> columns) {
        this.columns = columns;
    }
}
