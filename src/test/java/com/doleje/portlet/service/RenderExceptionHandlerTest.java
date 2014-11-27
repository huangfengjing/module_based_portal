package com.doleje.portlet.service;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.exception.RenderException;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import com.alibaba.ydt.portal.service.RenderExceptionHandler;
import com.doleje.portlet.base.BaseRenderTestCase;
import com.google.common.io.Files;
import mockit.NonStrictExpectations;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.math.BigDecimal;

/**
 * 异常处理器测试用例
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-27 上午10:03.
 */
public class RenderExceptionHandlerTest extends BaseRenderTestCase {

    @Before
    public void changePageDemo() throws Exception {
        // mock page instance service
        new NonStrictExpectations() {
            {
                cmsPageInstanceService.getById(2L);
                CmsPageInstance instance = new CmsPageInstance();
                instance.setDbId(2L);
                instance.setPrototypeId(1L);
                instance.setPageXmlContent(Files.toString(new ClassPathResource("page_2.xml").getFile(), charset));
                returns(instance);
            }
        };

        renderEngine.setRenderExceptionHandler(new RenderExceptionHandler() {
            @Override
            public RenderResult handleException(Object instance, RenderContext context, RenderException e) {
                return new RenderResult("<!-- " + getInstanceType(instance) +" Render Exception: " + e.getMessage() + ", instance: " + instance + " -->", RenderResult.RESULT_TYPE_HANDLE_ERROR);
            }
        });
    }

    @Test
    public void testExceptionHandler() throws Exception {

        RenderContext context = RenderContextBuilder.newBuilder().setMode(RenderContext.RenderMode.product)
                .setRequest(new com.doleje.portlet.mock.MockHttpServletRequest())
                .setResponse(new com.doleje.portlet.mock.MockHttpServletResponse())
                .setServletContext(new com.doleje.portlet.mock.MockServletContext())
                .addModuleParam(new ParameterValuePair("amount", new BigDecimal(100)))
                .addModuleParam(new ParameterValuePair("divisor", new BigDecimal(0)))
                .build();
        CmsPageInstance page = cmsPageInstanceService.getById(2L);
        CmsModuleInstance module = page.getLayouts().get(0).getColumns().get(0).getModules().get(2);
        RenderResult result = renderEngine.renderModule(module, context);

        Assert.isTrue(result.getResultType() == RenderResult.RESULT_TYPE_HANDLE_ERROR);
        Assert.isTrue(result.getRenderContent().contains("Render Exception"));

        System.out.println(StringUtils.center(" Product Mode RenderResult ", 80, "="));
        System.out.println(result.getRenderContent());
        System.out.println(StringUtils.center("=", 80, "="));
    }

    private String getInstanceType(Object instance) {
        return (instance instanceof CmsModuleInstance) ? "Module"
                : (instance instanceof CmsColumnInstance) ? "Column"
                : (instance instanceof CmsLayoutInstance) ? "Layout"
                : (instance instanceof CmsPageInstance) ? "Page"
                : "Component";
    }
}
