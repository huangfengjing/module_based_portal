package com.doleje.portlet.service;

import com.alibaba.ydt.portal.service.ContextProvider;
import com.alibaba.ydt.portal.service.UniversalParameterContextProvider;
import com.doleje.portlet.base.BaseRenderTestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块表单渲染测试用例
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-27 下午4:56.
 */
public class ModuleFormRenderTest extends BaseRenderTestCase {


    @Autowired
    protected List<ContextProvider> contextProviders = new ArrayList<ContextProvider>();

    @Before
    public void setupContextProvider() {
        // 设置通用参数供应器的数据服务
        for(ContextProvider provider : contextProviders) {
        }
        renderEngine.setContextProviders(contextProviders);
    }

    @Test
    public void testModuleFormRender() throws Exception {
        String formHtml = renderEngine.renderModuleForm(1L, mockRenderContext());
        System.out.println(formHtml);
        Assert.isTrue(formHtml.contains("form"));
    }
}
