package com.doleje.portlet.service;

import com.doleje.portlet.base.BaseTest;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * 环境配置测试
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-20 下午1:41.
 */
public class ContextSetupTest extends BaseTest {

    @Autowired
    private VelocityEngine velocityEngine;

    @Test
    public void testSpringSetup() throws Exception {
        Assert.notNull(velocityEngine);
    }
}
