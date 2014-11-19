package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.CmsModulePrototype;
import com.alibaba.ydt.portal.domain.ParameterValuePair;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 通用的参数处理器
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-10-29 下午2:37.
 */
public class CommonModuleParameterProcessor implements ModuleParameterProcessor {

    @Override
    public List<ParameterValuePair> processParams(CmsModulePrototype prototype, CmsModuleInstance instance, HttpServletRequest request) {
        return null;
    }

    @Override
    public boolean support(CmsModulePrototype modulePrototype) {
        return true;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
