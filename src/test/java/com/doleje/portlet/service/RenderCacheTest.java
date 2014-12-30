package com.doleje.portlet.service;

import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.CmsPageInstance;
import com.alibaba.ydt.portal.domain.RenderResult;
import com.alibaba.ydt.portal.service.ContextProvider;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import com.alibaba.ydt.portal.service.UniversalParameterContextProvider;
import com.doleje.portlet.base.BaseRenderTestCase;
import com.doleje.portlet.mock.MockHttpServletRequest;
import com.doleje.portlet.mock.MockHttpServletResponse;
import com.doleje.portlet.mock.MockServletContext;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存测试用例
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-25 下午3:37.
 */
public class RenderCacheTest extends BaseRenderTestCase {


    @Autowired
    protected List<ContextProvider> contextProviders = new ArrayList<ContextProvider>();

    @Before
    public void setupContextProvider() {
        // 设置通用参数供应器的数据服务
        for(ContextProvider provider : contextProviders) {
            if(provider instanceof UniversalParameterContextProvider) {
            }
        }
        renderEngine.setContextProviders(contextProviders);
    }

    @Test
    public void testCache() throws Exception {
        RenderContext context = RenderContextBuilder.newBuilder().setMode(RenderContext.RenderMode.product)
                .setRequest(new MockHttpServletRequest())
                .setResponse(new MockHttpServletResponse())
                .setServletContext(new MockServletContext()).build();
        CmsPageInstance page = cmsPageInstanceService.getById(1L);
        CmsModuleInstance module = page.getLayouts().get(0).getColumns().get(0).getModules().get(0);

        // 初次渲染
        RenderResult result = renderEngine.renderModule(module, context.clone());
        Assert.isTrue(result.getResultType() == RenderResult.RESULT_TYPE_NORMAL);

        System.out.println("初次渲染");
        System.out.println(StringUtils.center(" From Cache: " + (result.getResultType() == RenderResult.RESULT_TYPE_FROM_CACHE) + " ", 80, "="));
        System.out.println(StringUtils.center(" Product Mode RenderResult ", 80, "="));
        System.out.println(result.getRenderContent());
        System.out.println(StringUtils.center("=", 80, "="));

        // 第二次渲染，应该还在缓存期内，从缓存获取
        result = renderEngine.renderModule(module, context.clone());
        Assert.isTrue(result.getResultType() == RenderResult.RESULT_TYPE_FROM_CACHE);
        System.out.println("第二次渲染，应该还在缓存期内，从缓存获取");
        System.out.println(StringUtils.center(" From Cache: " + (result.getResultType() == RenderResult.RESULT_TYPE_FROM_CACHE) + " ", 80, "="));
        System.out.println(StringUtils.center(" Product Mode RenderResult ", 80, "="));
        System.out.println(result.getRenderContent());
        System.out.println(StringUtils.center("=", 80, "="));

        // 第三次渲染，应该是缓存过期，重新渲染了
        Thread.sleep(10 * 1000);
        result = renderEngine.renderModule(module, context.clone());
        Assert.isTrue(result.getResultType() == RenderResult.RESULT_TYPE_NORMAL);
        System.out.println("第三次渲染，应该是缓存过期，重新渲染了");
        System.out.println(StringUtils.center(" From Cache: " + (result.getResultType() == RenderResult.RESULT_TYPE_FROM_CACHE) + " ", 80, "="));
        System.out.println(StringUtils.center(" Product Mode RenderResult ", 80, "="));
        System.out.println(result.getRenderContent());
        System.out.println(StringUtils.center("=", 80, "="));
    }
}
