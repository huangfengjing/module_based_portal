package com.doleje.portlet.service;

import com.doleje.portlet.domain.CmsModuleInstance;
import com.doleje.portlet.domain.CmsModulePrototype;
import com.doleje.portlet.domain.ParameterValuePair;
import org.springframework.core.Ordered;

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
        return Ordered.LOWEST_PRECEDENCE;
    }
}
