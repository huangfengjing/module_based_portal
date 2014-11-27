package com.alibaba.ydt.portal.domain;

import com.alibaba.ydt.portal.domain.common.BaseModel;
import com.google.common.base.MoreObjects;

/**
 * <p>
 * 页面模块实例
 * </p>
 * Time: 12-11-27 下午3:27
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class CmsModuleInstance extends BaseModel implements ParameterSupportModel {

    public static final String TYPE_TAG = "module";

    /**
     * 模块原型 ID
     */
    private long prototypeId;

    /**
     * 模块名称
     */
    private String title;

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
        return MoreObjects.toStringHelper(this).add("id", dbId).add("prototypeId", prototypeId).add("title", title).toString();
    }
}