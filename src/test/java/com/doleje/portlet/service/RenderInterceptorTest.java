package com.doleje.portlet.service;

import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.RenderResult;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import com.alibaba.ydt.portal.service.RenderInterceptor;
import com.doleje.portlet.base.BaseTest;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器测试
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-24 下午3:13.
 */
public class RenderInterceptorTest extends RenderContextProviderTest {

    @Test
    public void testInterceptorBeforeAction() {
        List<RenderInterceptor> interceptors = new ArrayList<RenderInterceptor>();
        interceptors.add(new RenderInterceptor() {
            @Override
            public boolean before(Object instance, RenderContext context) {
                // 通过拦截器跳过原型为2的模块渲染
                return !(instance instanceof CmsModuleInstance && ((CmsModuleInstance) instance).getPrototypeId() == 2);
            }

            @Override
            public RenderResult after(Object instance, RenderResult result) {
                return result;
            }
        });
        renderEngine.setRenderInterceptors(interceptors);

        RenderContext context = RenderContextBuilder.newBuilder().setMode(RenderContext.RenderMode.product)
                .setRequest(new MockHttpServletRequest())
                .setResponse(new MockHttpServletResponse())
                .setServletContext(new MockServletContext()).build();
        RenderResult result = renderEngine.renderPage(1L, context);
        System.out.println(StringUtils.center(" Product Mode RenderResult ", 80, "="));
        System.out.println(result.getRenderContent());
        System.out.println(StringUtils.center("=", 80, "="));
    }

    @Test
    public void testInterceptorAfterAction() {
        List<RenderInterceptor> interceptors = new ArrayList<RenderInterceptor>();
        interceptors.add(new RenderInterceptor() {
            @Override
            public boolean before(Object instance, RenderContext context) {
                return true;
            }

            @Override
            public RenderResult after(Object instance, RenderResult result) {
                if (instance instanceof CmsModuleInstance && ((CmsModuleInstance) instance).getPrototypeId() == 2) {
                    result.setRenderContent("<!-- 模块原型为 2 的实例，全部都被我拦截并修改渲染内容啦！看我！！ -->");
                }
                return result;
            }
        });
        renderEngine.setRenderInterceptors(interceptors);

        RenderContext context = RenderContextBuilder.newBuilder().setMode(RenderContext.RenderMode.product)
                .setRequest(new MockHttpServletRequest())
                .setResponse(new MockHttpServletResponse())
                .setServletContext(new MockServletContext()).build();
        RenderResult result = renderEngine.renderPage(1L, context);
        System.out.println(StringUtils.center(" Product Mode RenderResult ", 80, "="));
        System.out.println(result.getRenderContent());
        System.out.println(StringUtils.center("=", 80, "="));
    }
}
