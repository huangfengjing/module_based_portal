package com.doleje.portlet.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doleje.portlet.web.util.JsonUtils;
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
public class CmsLayoutInstance extends BaseModel {

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

    /**
     * 实例参数
     */
    private String params4Store;

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

    public String getParams4Store() {
        return params4Store;
    }

    public void setParams4Store(String params4Store) {
        this.params4Store = params4Store;
    }

    public List<ParameterValuePair> getParameters() {
        if(StringUtils.isBlank(params4Store)) {
            return Collections.emptyList();
        }
        JSONObject params = JSONObject.parseObject(params4Store);
        List<ParameterValuePair> parameters = new ArrayList<ParameterValuePair>();
        for (Object paramKey : params.keySet()) {
            ParameterValuePair parameter = new ParameterValuePair();
            parameter.setName(paramKey.toString());
            Object valueObject = params.get(paramKey.toString());
            if (valueObject instanceof JSONObject) {
                parameter.setValue(JsonUtils.processObject((JSONObject) valueObject));
            } else if (valueObject instanceof JSONArray) {
                parameter.setValue(JsonUtils.processArray((JSONArray) valueObject));
            } else {
                parameter.setValue(valueObject);
            }
            parameters.add(parameter);
        }
        return parameters;
    }

    public void setParamsWithList(List<ParameterValuePair> paramList) {
        if (paramList == null || paramList.isEmpty()) {
            params4Store = null;
            return;
        }
        params4Store = JSON.toJSONString(paramList);
    }

    public void setParamsWithMap(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            params4Store = null;
            return;
        }
        params4Store = JSON.toJSONString(params);
    }
}
