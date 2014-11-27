package com.doleje.portlet.base;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.service.*;
import com.google.common.io.Files;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 * Created on 14-11-20 上午11:47.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:applicationContext-core.xml"
})
/*
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional*/
public abstract  class BaseTest extends AbstractJUnit4SpringContextTests {
	protected Logger logger = LoggerFactory.getLogger(getClass());

    public static Charset charset = Charset.forName("UTF-8");

    @Mocked
    protected CmsPageInstanceService cmsPageInstanceService;

    @Mocked
    protected CmsPagePrototypeService cmsPagePrototypeService;

    @Mocked
    protected CmsLayoutInstanceService cmsLayoutInstanceService;

    @Mocked
    protected CmsLayoutPrototypeService cmsLayoutPrototypeService;

    @Mocked
    protected CmsColumnInstanceService cmsColumnInstanceService;

    @Mocked
    protected CmsColumnPrototypeService cmsColumnPrototypeService;

    @Mocked
    protected CmsModuleInstanceService cmsModuleInstanceService;

    @Mocked
    protected CmsModulePrototypeService cmsModulePrototypeService = new MockedCmsModulePrototypeService();

    @Mocked
    protected CmsParameterService cmsParameterService;

    @Autowired
    protected RenderEngine renderEngine;

    @Before
    public void initMockContext() throws Exception {

        // mock page instance service
        new NonStrictExpectations() {
            {
                cmsPageInstanceService.getById(1L);
                CmsPageInstance instance = new CmsPageInstance();
                instance.setDbId(1L);
                instance.setPrototypeId(1L);
                instance.setPageXmlContent(Files.toString(new ClassPathResource("page.xml").getFile(), charset));
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
                prototype.setFormTemplate(Files.toString(new ClassPathResource("velocity/template/page_form.vm").getFile(), charset));
                prototype.setTemplate(Files.toString(new ClassPathResource("velocity/template/page_template.vm").getFile(), charset));
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
                prototype.setFormTemplate(Files.toString(new ClassPathResource("velocity/template/layout_form.vm").getFile(), charset));
                prototype.setTemplate(Files.toString(new ClassPathResource("velocity/template/layout_template.vm").getFile(), charset));
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
                prototype.setFormTemplate(Files.toString(new ClassPathResource("velocity/template/column_form.vm").getFile(), charset));
                prototype.setTemplate(Files.toString(new ClassPathResource("velocity/template/column_template.vm").getFile(), charset));
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
                returns(instance);
            }
        };

        // mock parameter service
        new NonStrictExpectations() {
            {
                cmsParameterService.getParamsByTypeAndInstanceId(CmsModuleInstance.TYPE_TAG, 1);
                List<ParameterValuePair> params = new ArrayList<ParameterValuePair>();
                params.add(new ParameterValuePair("content", "测试内容"));
                returns(params);
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
    }

    public static class MockedCmsModulePrototypeService implements CmsModulePrototypeService {

        @Override
        public CmsModulePrototype getById(Long id) {
            try {
                CmsModulePrototype prototype = new CmsModulePrototype();
                prototype.setDbId(id);
                prototype.setName("我是模块：" + id);
                prototype.setFormTemplate(Files.toString(new ClassPathResource("velocity/template/module_form.vm").getFile(), charset));
                prototype.setTemplate(Files.toString(new ClassPathResource("velocity/template/module_template_" + id + ".vm").getFile(), charset));
                prototype.setCacheExpiredSeconds("60,20");
                return prototype;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void removeById(Long id) {

        }

        @Override
        public void save(CmsModulePrototype cmsModulePrototype) {

        }
    }
}