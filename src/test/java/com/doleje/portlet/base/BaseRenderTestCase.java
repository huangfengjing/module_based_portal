package com.doleje.portlet.base;

import com.alibaba.ydt.portal.domain.CmsModulePrototype;
import com.alibaba.ydt.portal.service.*;
import com.google.common.io.Files;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

/**
 * 渲染测试基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 * Created on 14-11-20 上午11:47.
 */
public abstract  class BaseRenderTestCase extends BaseTestCase {

    @Autowired
    protected CmsPageInstanceService cmsPageInstanceService;

    @Autowired
    protected CmsPagePrototypeService cmsPagePrototypeService;

    @Autowired
    protected CmsLayoutInstanceService cmsLayoutInstanceService;

    @Autowired
    protected CmsLayoutPrototypeService cmsLayoutPrototypeService;

    @Autowired
    protected CmsColumnInstanceService cmsColumnInstanceService;

    @Autowired
    protected CmsColumnPrototypeService cmsColumnPrototypeService;

    @Autowired
    protected CmsModuleInstanceService cmsModuleInstanceService;

    @Autowired
    protected CmsModulePrototypeService cmsModulePrototypeService;

    @Autowired
    protected RenderEngine renderEngine;
}