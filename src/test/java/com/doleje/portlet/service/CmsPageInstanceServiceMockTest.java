package com.doleje.portlet.service;

import com.alibaba.ydt.portal.domain.CmsLayoutInstance;
import com.alibaba.ydt.portal.domain.CmsPageInstance;
import com.alibaba.ydt.portal.service.CmsPageInstanceService;
import com.doleje.portlet.base.BaseTest;
import com.google.common.io.Files;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * cms page instance mock test
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-20 下午2:46.
 */
public class CmsPageInstanceServiceMockTest extends BaseTest {

    @Mocked
    private CmsPageInstanceService cmsPageInstanceService;

    @Test
    public void testGetById() throws Exception {

        new NonStrictExpectations() {
            {
                cmsPageInstanceService.getById(1L);
                CmsPageInstance instance = new CmsPageInstance();
                instance.setDbId(1L);
                instance.setPrototypeId(1L);
                instance.setPageXmlContent(Files.toString(new ClassPathResource("demo_page.xml").getFile(), charset));
                returns(instance);
            }
        };

        CmsPageInstance page = cmsPageInstanceService.getById(1L);
        Assert.notNull(page);

        for(CmsLayoutInstance layout : page.getLayouts()) {
            System.out.println(layout.getPrototypeId() + ":" + layout.getDbId());
        }
    }
}