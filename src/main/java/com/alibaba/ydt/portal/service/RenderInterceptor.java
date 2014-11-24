package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.RenderResult;

/**
 * 渲染拦截器
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-10-29 下午3:03.
 */
public interface RenderInterceptor {

    /**
     * 渲染前置拦截器
     *
     * @param instance 要渲染的实例对象
     * @param context  渲染上下文环境
     * @return 如果返回 false 则将跳过该对象的渲染过程
     */
    public boolean before(Object instance, RenderContext context);

    /**
     * @param instance 要渲染的实例对象
     * @param result   渲染结果
     * @return 拦截器可以最后对渲染结果作变更后返回
     */
    public RenderResult after(Object instance, RenderResult result);
}
