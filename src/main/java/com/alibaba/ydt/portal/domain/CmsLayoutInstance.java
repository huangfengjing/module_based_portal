package com.alibaba.ydt.portal.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.ydt.portal.domain.common.BaseModel;
import com.alibaba.ydt.portal.domain.common.ParameterSupportModel;
import com.alibaba.ydt.portal.web.util.CmsUtils;
import com.alibaba.ydt.portal.web.util.JsonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CmsColumnInstance> getColumns() {
        return columns;
    }

    public void setColumns(List<CmsColumnInstance> columns) {
        this.columns = columns;
    }
}
