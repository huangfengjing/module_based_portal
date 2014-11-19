package com.alibaba.ydt.portal.domain.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.ydt.portal.domain.ParameterValuePair;
import com.alibaba.ydt.portal.web.util.CmsUtils;

import java.util.List;
import java.util.Map;

/**
 * 参数支持模型
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-19 下午3:45.
 */
abstract public class ParameterSupportModel extends BaseModel {

    /**
     * 实例参数
     */
    private String params4Store;

    public String getParams4Store() {
        return params4Store;
    }

    public void setParams4Store(String params4Store) {
        this.params4Store = params4Store;
    }

    public List<ParameterValuePair> getParameters() {
        return CmsUtils.parseParameters(params4Store);
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
