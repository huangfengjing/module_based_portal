package com.alibaba.ydt.portal.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.ydt.portal.domain.common.BaseFeatureSupport;
import com.alibaba.ydt.portal.web.util.JsonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 页面模块实例
 * </p>
 * Time: 12-11-27 下午3:27
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class CmsModuleInstance extends BaseFeatureSupport {

    /**
     * 模块原型 ID
     */
    private long prototypeId;

    /**
     * 模块参数
     */
    private String params4Store;

    public long getPrototypeId() {
        return prototypeId;
    }

    public void setPrototypeId(long prototypeId) {
        this.prototypeId = prototypeId;
    }

    public String getParams4Store() {
        return params4Store;
    }

    public void setParams4Store(String params4Store) {
        this.params4Store = params4Store;
    }

    @SuppressWarnings("unchecked")
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