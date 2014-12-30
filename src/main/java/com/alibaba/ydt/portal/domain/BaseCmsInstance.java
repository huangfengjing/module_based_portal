package com.alibaba.ydt.portal.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.ydt.portal.domain.common.BaseModel;
import com.alibaba.ydt.portal.util.JsonUtils;
import com.alibaba.ydt.portal.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 实例基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-12-30 下午2:06.
 */
@MappedSuperclass
abstract public class BaseCmsInstance extends BaseModel implements ParameterSupportModel {


    @Transient
    private Log logger = LogFactory.getLog(getClass());

    /**
     * 模块参数
     */
    @Basic
    @Column(name = "PARAMS_4_STORE")
    private String params4Store;

    public String getParams4Store() {
        return params4Store;
    }

    public void setParams4Store(String params4Store) {
        this.params4Store = params4Store;
    }

    @Transient
    @SuppressWarnings("unchecked")
    @Override
    public List<ParameterValuePair> getParameters() {
        if (StringUtils.isBlank(params4Store)) {
            return Collections.emptyList();
        }
        List<ParameterValuePair> parameters = new ArrayList<ParameterValuePair>();
        try {
            JSONObject params = JSONObject.parseObject(params4Store);
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
        } catch (Exception e) {
            logger.error("解析模块参数失败", e);
        }
        return parameters;
    }

    @Transient
    @Override
    public void setParamsWithList(List<ParameterValuePair> paramList) {
        if (paramList == null || paramList.isEmpty()) {
            params4Store = null;
            return;
        }
        params4Store = JsonUtils.toCompatibleJSONString(paramList);
    }

    @Transient
    public void setParamsWithMap(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            params4Store = null;
            return;
        }
        params4Store = JsonUtils.toCompatibleJSONString(params);
    }

    @Override
    public long getInstanceId() {
        return dbId;
    }
}
