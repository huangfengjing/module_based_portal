package com.alibaba.ydt.portal.web.mvc;

import com.alibaba.ydt.portal.domain.common.AjaxResult;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Ajax 类型的返回数据处理器
 * </p>
 * Time: 12-6-10 上午1:31
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class AjaxReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return AjaxResult.class.equals(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        AjaxResult ajaxResult = (AjaxResult) returnValue;
        HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();

        if (ajaxResult.isHtml()) {
            response.setContentType("text/html; charset=UTF-8");
        } else {
            response.setContentType("application/json; charset=UTF-8");
        }

        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.getWriter().write(ajaxResult.toString());
        response.getWriter().flush();
    }
}
