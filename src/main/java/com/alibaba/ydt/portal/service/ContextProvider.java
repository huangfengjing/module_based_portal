package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.BaseCmsInstance;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 渲染环境提供器，提供渲染环境
 * </p>
 * Time: 12-12-7 下午5:33
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public interface ContextProvider extends Ordered {

    /**
     * 创建模块的渲染环境
     * @param instance 渲染对象实例
     * @param request http 请求
     * @return 模块的渲染环境
     */
    public RenderContext createContext(BaseCmsInstance instance, HttpServletRequest request);

    /**
     * 创建模块表单的渲染环境
     * @param instance 渲染对象实例
     * @param request http 请求
     * @return 模块的渲染环境
     */
    public RenderContext createFormContext(BaseCmsInstance instance, HttpServletRequest request);

    /**
     * 是否支持该模块
     * @param instance 渲染对象实例
     * @return 如果支持，返回 true，否则返回 false
     */
    public boolean support(BaseCmsInstance instance);
}
