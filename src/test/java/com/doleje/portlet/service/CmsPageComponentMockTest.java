package com.doleje.portlet.service;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.service.*;
import com.doleje.portlet.base.BaseTest;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面各个组件的 MOCK 测试
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-20 下午3:08.
 */
public class CmsPageComponentMockTest extends BaseTest {

    @Mocked
    private CmsPageInstanceService cmsPageInstanceService;

    @Mocked
    private CmsPagePrototypeService cmsPagePrototypeService;

    @Mocked
    private CmsLayoutInstanceService cmsLayoutInstanceService;

    @Mocked
    private CmsLayoutPrototypeService cmsLayoutPrototypeService;

    @Mocked
    private CmsColumnInstanceService cmsColumnInstanceService;

    @Mocked
    private CmsColumnPrototypeService cmsColumnPrototypeService;

    @Mocked
    private CmsModuleInstanceService cmsModuleInstanceService;

    @Mocked
    private CmsModulePrototypeService cmsModulePrototypeService;

    @Autowired
    private RenderEngine renderEngine;

    @Test
    public void testMockComponents() throws Exception {

        // mock page instance service
        new NonStrictExpectations() {
            {
                cmsPageInstanceService.getById(1L);
                CmsPageInstance instance = new CmsPageInstance();
                instance.setDbId(1L);
                instance.setPrototypeId(1L);
                instance.setPageXmlContent(IOUtils.toString(new ClassPathResource("demo_page.xml").getInputStream()));
                returns(instance);
            }
        };
        // mock page prototype service
        new NonStrictExpectations() {
            {
                cmsPagePrototypeService.getById(1L);
                CmsPagePrototype prototype = new CmsPagePrototype();
                prototype.setDbId(1L);
                prototype.setName("测试页面");
                prototype.setFormTemplate(IOUtils.toString(new ClassPathResource("velocity/template/page_form.vm").getInputStream()));
                prototype.setTemplate(IOUtils.toString(new ClassPathResource("velocity/template/page_template.vm").getInputStream()));
                returns(prototype);
            }
        };

        // mock layout instance service
        new NonStrictExpectations() {
            {
                cmsLayoutInstanceService.getById(1L);
                CmsLayoutInstance instance = new CmsLayoutInstance();
                instance.setDbId(1L);
                instance.setTitle("左右栏布局");
                instance.setPrototypeId(1L);
                returns(instance);
            }
        };
        // mock layout prototype service
        new NonStrictExpectations() {
            {
                cmsLayoutPrototypeService.getById(1L);
                CmsLayoutPrototype prototype = new CmsLayoutPrototype();
                prototype.setDbId(1L);
                prototype.setFormTemplate(IOUtils.toString(new ClassPathResource("velocity/template/layout_form.vm").getInputStream()));
                prototype.setTemplate(IOUtils.toString(new ClassPathResource("velocity/template/layout_template.vm").getInputStream()));
                returns(prototype);
            }
        };

        // mock column instance service
        new NonStrictExpectations() {
            {
                cmsColumnInstanceService.getById(1L);
                CmsColumnInstance instance = new CmsColumnInstance();
                instance.setDbId(1L);
                instance.setTitle("25%宽度列");
                instance.setPrototypeId(1L);
                returns(instance);
            }
        };
        // mock column prototype service
        new NonStrictExpectations() {
            {
                cmsColumnPrototypeService.getById(1L);
                CmsColumnPrototype prototype = new CmsColumnPrototype();
                prototype.setDbId(1L);
                prototype.setFormTemplate(IOUtils.toString(new ClassPathResource("velocity/template/column_form.vm").getInputStream()));
                prototype.setTemplate(IOUtils.toString(new ClassPathResource("velocity/template/column_template.vm").getInputStream()));
                returns(prototype);
            }
        };

        // mock module instance service
        new NonStrictExpectations() {
            {
                cmsModuleInstanceService.getById(1L);
                CmsModuleInstance instance = new CmsModuleInstance();
                instance.setDbId(1L);
                instance.setTitle("测试模块");
                instance.setPrototypeId(1L);
                List<ParameterValuePair> params = new ArrayList<ParameterValuePair>();
                params.add(new ParameterValuePair("content", "测试内容"));
                instance.setParamsWithList(params);
                returns(instance);
            }
        };
        // mock module prototype service
        new NonStrictExpectations() {
            {
                cmsModulePrototypeService.getById(1L);
                CmsModulePrototype prototype = new CmsModulePrototype();
                prototype.setDbId(1L);
                prototype.setTitle("自定义模块");
                prototype.setFormTemplate(IOUtils.toString(new ClassPathResource("velocity/template/module_form.vm").getInputStream()));
                prototype.setTemplate(IOUtils.toString(new ClassPathResource("velocity/template/module_template.vm").getInputStream()));
                returns(prototype);
            }
        };

        renderEngine.setCmsPagePrototypeService(cmsPagePrototypeService);
        renderEngine.setCmsPageInstanceService(cmsPageInstanceService);
        renderEngine.setCmsLayoutPrototypeService(cmsLayoutPrototypeService);
        renderEngine.setCmsLayoutInstanceService(cmsLayoutInstanceService);
        renderEngine.setCmsColumnPrototypeService(cmsColumnPrototypeService);
        renderEngine.setCmsColumnInstanceService(cmsColumnInstanceService);
        renderEngine.setCmsModulePrototypeService(cmsModulePrototypeService);
        renderEngine.setCmsModuleInstanceService(cmsModuleInstanceService);


        RenderContext context = RenderContextBuilder.newBuilder().setMode(RenderContext.RenderMode.design)
                .setRequest(new MockHttpServletRequest())
                .setResponse(new MockHttpServletResponse())
                .setServletContext(new MockServletContext()).build();
        String content = renderEngine.renderPage(1L, context);
        System.out.println(StringUtils.center(" RenderResult ", 80, "="));
        System.out.println(content);
        System.out.println(StringUtils.center("=", 80, "="));
    }
}
