package com.doleje.portlet.service;

import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.RenderResult;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import com.alibaba.ydt.portal.service.RenderInterceptor;
import com.doleje.portlet.base.BaseRenderTestCase;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器测试
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-24 下午3:13.
 */
public class RenderInterceptorBeforeTest extends BaseRenderTestCase {

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

        CmsModuleInstance module = cmsPageInstanceService.getById(1L).getLayouts().get(0).getColumns().get(0).getModules().get(1);
        RenderResult result = renderEngine.renderModule(module, context);

        System.out.println(StringUtils.center(" Product Mode RenderResult ", 80, "="));
        System.out.println(result.getRenderContent());
        System.out.println(StringUtils.center("=", 80, "="));

        Assert.isTrue(result.getResultType() == RenderResult.RESULT_TYPE_SKIPPED);
    }
}