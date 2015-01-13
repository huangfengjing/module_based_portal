package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.RenderResult;
import com.alibaba.ydt.portal.exception.RenderException;
import org.springframework.core.PriorityOrdered;

/**
 * 异常处理器，当模块渲染出现异常时，将通过该处理器来处理失败信息
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-19 下午5:06.
 */
public interface RenderExceptionHandler extends PriorityOrdered {

    /**
     * 处理渲染异常
     * @param instance 渲染的实例对象
     * @param context 渲染时上下文环境
     * @param e 异常信息
     * @return 处理结果
     */
    public RenderResult handleException(Object instance, RenderContext context, RenderException e);
}