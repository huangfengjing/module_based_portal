package com.alibaba.ydt.portal.domain;

import com.alibaba.ydt.portal.domain.common.BaseModel;
import com.google.common.base.MoreObjects;

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
public class CmsLayoutInstance extends BaseModel implements ParameterSupportModel {

    public static final String TYPE_TAG = "layout";


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

    @Override
    public long getInstanceId() {
        return dbId;
    }

    @Override
    public String getInstanceTypeTag() {
        return TYPE_TAG;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues().toString();
    }
}
