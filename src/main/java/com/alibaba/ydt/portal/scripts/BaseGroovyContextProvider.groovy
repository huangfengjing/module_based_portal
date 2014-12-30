package com.alibaba.ydt.portal.scripts

import com.alibaba.ydt.portal.service.RenderContext
import com.alibaba.ydt.portal.service.RenderContextBuilder

import javax.servlet.http.HttpServletRequest

/**
 * 基于 Groovy 的 Context Provider 基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 * Created on 14-12-1 上午10:44.
 */
class BaseGroovyContextProvider {

    /**
     * 创建模块的渲染环境
     * @param instance 渲染对象实例
     * @param request http 请求
     * @return 模块的渲染环境
     */
    public RenderContext createContext(Object instance, HttpServletRequest request) {
        return RenderContextBuilder.newBuilder().build();
    }

    /**
     * 创建模块表单的渲染环境
     * @param instance 渲染对象实例
     * @param request http 请求
     * @return 模块的渲染环境
     */
    public RenderContext createFormContext(Object instance, HttpServletRequest request) {
        return RenderContextBuilder.newBuilder().build();
    }
}