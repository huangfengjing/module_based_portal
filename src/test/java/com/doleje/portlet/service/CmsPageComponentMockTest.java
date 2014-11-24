package com.doleje.portlet.service;

import com.alibaba.ydt.portal.domain.RenderResult;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import com.doleje.portlet.base.BaseTest;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

/**
 * 页面各个组件的 MOCK 测试
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-20 下午3:08.
 */
public class CmsPageComponentMockTest extends BaseTest {

    @Test
    public void testDesign() throws Exception {
        RenderContext context = RenderContextBuilder.newBuilder().setMode(RenderContext.RenderMode.design)
                .setRequest(new MockHttpServletRequest())
                .setResponse(new MockHttpServletResponse())
                .setServletContext(new MockServletContext()).build();
        RenderResult result = renderEngine.renderPage(1L, context);
        System.out.println(StringUtils.center(" Design Mode RenderResult ", 80, "="));
        System.out.println(result.getRenderContent());
        System.out.println(StringUtils.center("=", 80, "="));
    }

    @Test
    public void testProduct() throws Exception {
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
    public void testGlobalToolbox() throws Exception {
        RenderContext context = RenderContextBuilder.newBuilder().setMode(RenderContext.RenderMode.product).build();
        RenderResult result = renderEngine.renderPage(1L, context);
        System.out.println(StringUtils.center(" NoRequest RenderResult ", 80, "="));
        System.out.println(result.getRenderContent());
        System.out.println(StringUtils.center("=", 80, "="));
    }
}
