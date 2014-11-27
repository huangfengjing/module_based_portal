package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.CmsModulePrototype;
import com.alibaba.ydt.portal.domain.ParameterValuePair;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 模块参数收集器，用于收集请求中的参数信息
 * </p>
 * Time: 12-12-7 下午5:33
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public interface ParameterGather extends Ordered {

    /**
     * 创建特殊模块的渲染环境
     * @param prototype 模块原型
     * @param instance 模块实例
     * @param request http 请求
     * @return 模块的渲染环境
     */
    public List<ParameterValuePair> gatherParams(CmsModulePrototype prototype, CmsModuleInstance instance, HttpServletRequest request);

    /**
     * 是否支持该模块
     * @param modulePrototype　模块原型
     * @return 如果支持，返回 true，否则返回 false
     */
    public boolean support(CmsModulePrototype modulePrototype);
}
