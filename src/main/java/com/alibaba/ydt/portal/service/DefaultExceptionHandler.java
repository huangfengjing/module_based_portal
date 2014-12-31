package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.ParameterSupportModel;
import com.alibaba.ydt.portal.domain.RenderResult;
import com.alibaba.ydt.portal.exception.RenderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * 默认的异常处理器，将直接打印一行异常注释信息
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-12-30 下午3:10.
 */
@Component
public class DefaultExceptionHandler implements RenderExceptionHandler {

    private Log logger = LogFactory.getLog(getClass());

    public RenderResult handleException(Object instance, RenderContext context, RenderException e) {
        logger.error("渲染出现异常", e);
        String type = (instance instanceof ParameterSupportModel) ? ((ParameterSupportModel) instance).getInstanceTypeTag() : "";
        return new RenderResult("<!-- " + type +" Render Exception: " + e.getMessage() + ", instance: " + instance + " -->", RenderResult.RESULT_TYPE_HANDLE_ERROR);
    }
}
