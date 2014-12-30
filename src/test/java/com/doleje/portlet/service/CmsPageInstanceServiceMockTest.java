package com.doleje.portlet.service;

import com.alibaba.ydt.portal.domain.CmsLayoutInstance;
import com.alibaba.ydt.portal.domain.CmsPageInstance;
import com.alibaba.ydt.portal.service.CmsPageInstanceService;
import com.doleje.portlet.base.BaseRenderTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * cms page instance mock test
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-20 下午2:46.
 */
public class CmsPageInstanceServiceMockTest extends BaseRenderTestCase {

    @Autowired
    private CmsPageInstanceService cmsPageInstanceService;

    @Test
    public void testGetById() throws Exception {

        CmsPageInstance page = cmsPageInstanceService.getById(1L);
        Assert.notNull(page);

        for(CmsLayoutInstance layout : page.getLayouts()) {
            System.out.println(layout.getPrototypeId() + ":" + layout.getDbId());
        }
    }
}