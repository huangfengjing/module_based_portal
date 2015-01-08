package com.doleje.portlet.service;

import com.alibaba.ydt.portal.domain.RenderResult;
import com.alibaba.ydt.portal.service.ContextProvider;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import com.doleje.portlet.base.BaseRenderTestCase;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 上下文环境供应器测试
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-24 下午12:02.
 */
public class RenderContextProviderTest extends BaseRenderTestCase {


    @Autowired
    protected List<ContextProvider> contextProviders = new ArrayList<ContextProvider>();

    @Before
    public void setupContextProvider() {
//        renderEngine.setContextProviders(contextProviders);
    }

    @Test
    public void testUniversalParameterContextProvider() throws Exception {

        RenderContext context = RenderContextBuilder.newBuilder().setMode(RenderContext.RenderMode.product)
                .setRequest(new MockHttpServletRequest())
                .setResponse(new MockHttpServletResponse())
                .setServletContext(new MockServletContext()).build();
        RenderResult result = renderEngine.renderPage(1L, context);
        System.out.println(StringUtils.center(" Product Mode RenderResult ", 80, "="));
        System.out.println(result.getRenderContent());
        System.out.println(StringUtils.center("=", 80, "="));
        Assert.isTrue(!result.getRenderContent().contains("moduleParams"));
    }
}
