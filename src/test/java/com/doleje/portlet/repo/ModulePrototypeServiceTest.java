package com.doleje.portlet.repo;

import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.CmsModulePrototype;
import com.alibaba.ydt.portal.service.CmsModuleInstanceService;
import com.alibaba.ydt.portal.service.CmsModulePrototypeService;
import com.doleje.portlet.base.BaseTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * 模块原型服务测试
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-12-30 下午1:58.
 */
public class ModulePrototypeServiceTest extends BaseTestCase {

    @Autowired
    private CmsModulePrototypeService cmsModulePrototypeService;

    @Test
    public void getModulePrototype() {
        CmsModulePrototype prototype = cmsModulePrototypeService.getById(1L);
        Assert.notNull(prototype);
        Assert.isTrue("测试".equals(prototype.getName()));

        prototype = cmsModulePrototypeService.getUniqueByProperty("name", "测试");
        Assert.isTrue("测试".equals(prototype.getName()));
    }
}
