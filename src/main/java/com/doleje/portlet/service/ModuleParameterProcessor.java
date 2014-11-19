package com.doleje.portlet.service;

import com.doleje.portlet.domain.CmsModuleInstance;
import com.doleje.portlet.domain.CmsModulePrototype;
import com.doleje.portlet.domain.ParameterValuePair;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 模块参数处理器，用于处理比较复杂的模块参数
 * </p>
 * Time: 12-12-7 下午5:33
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public interface ModuleParameterProcessor extends Ordered {

    /**
     * 创建特殊模块的渲染环境
     * @param prototype 模块原型
     * @param instance 模块实例
     * @param request http 请求
     * @return 模块的渲染环境
     */
    public List<ParameterValuePair> processParams(CmsModulePrototype prototype, CmsModuleInstance instance, HttpServletRequest request);

    /**
     * 是否支持该模块
     * @param modulePrototype　模块原型
     * @return 如果支持，返回 true，否则返回 false
     */
    public boolean support(CmsModulePrototype modulePrototype);
}
