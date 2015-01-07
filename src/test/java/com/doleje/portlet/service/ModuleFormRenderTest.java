package com.doleje.portlet.service;

import com.doleje.portlet.base.BaseRenderTestCase;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * 模块表单渲染测试用例
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-27 下午4:56.
 */
public class ModuleFormRenderTest extends BaseRenderTestCase {

    @Test
    public void testModuleFormRender() throws Exception {
        String formHtml = renderEngine.renderCompForm("module", 1L, mockRenderContext());
        System.out.println(formHtml);
        Assert.isTrue(formHtml.contains("form"));
    }
}
