package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.exception.RenderException;

/**
 * 异常处理器，当模块渲染出现异常时，将通过该处理器来处理失败信息
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-19 下午5:06.
 */
public interface RenderExceptionHandler {

    public String handleException(long instId, Object instance, RenderContext context, RenderException e);
}