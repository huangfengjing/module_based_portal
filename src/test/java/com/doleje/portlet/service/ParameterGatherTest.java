package com.doleje.portlet.service;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.domain.ParameterValuePair;
import com.alibaba.ydt.portal.service.ParameterGather;
import com.alibaba.ydt.portal.util.StringUtils;
import com.doleje.portlet.base.BaseRenderTestCase;
import com.doleje.portlet.mock.MockHttpServletRequest;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 参数收集器测试用例
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-27 下午2:31.
 */
public class ParameterGatherTest extends BaseRenderTestCase {

    @Autowired
    private List<ParameterGather> parameterGathers = new ArrayList<ParameterGather>();

    private MockHttpServletRequest request;

    @Before
    public void setupRequest() throws Exception {
        request = new MockHttpServletRequest();
        request.addParameter("moduleId", "1");
        request.addParameter("pageId", "2");
        request.addParameter("title", "测试用的模块标题");
        request.addParameter("content", "测试用的模块内容");
        request.addParameter("comments", new String[] {"Andy", "Amy", "Joe", "Chandler"});
    }

    @Test
    public void testCommonParameterGather() throws Exception {

        CmsModulePrototype prototype = cmsModulePrototypeService.getById(1L);
        List<ParameterGather> supportedGather = new ArrayList<ParameterGather>();
        for(ParameterGather gather : parameterGathers) {
            if(gather.support(prototype)) {
                supportedGather.add(gather);
            }
        }
        Collections.sort(supportedGather, new Comparator<ParameterGather>() {
            @Override
            public int compare(ParameterGather o1, ParameterGather o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        CmsModuleInstance instance = cmsModuleInstanceService.getById(1L);

        // 优先级高的收集器收集的数据将覆盖低优先级的数据
        Map<String, ParameterValuePair> paramMap = new HashMap<String, ParameterValuePair>();
        for(ParameterGather gather : supportedGather) {
            List<ParameterValuePair> params = gather.gatherParams(prototype, instance, request);
            for(ParameterValuePair pair : params) {
                paramMap.put(pair.getName(), pair);
            }
        }

        ImmutableList<ParameterValuePair> params = ImmutableList.copyOf(paramMap.values());

        Assert.notEmpty(params);
        Assert.isTrue(params.size() == 3);

        System.out.println(StringUtils.center(" Params " , 80, "="));
        for(ParameterValuePair pair : params) {
            System.out.println(pair);
        }
        System.out.println(StringUtils.center("=" , 80, "="));
    }

    @Test
    public void testMultiParameterGather() throws Exception {

        ParameterGather another = new ParameterGather() {
            @Override
            public List<ParameterValuePair> gatherParams(BaseCmsPrototype prototype, BaseCmsInstance instance, HttpServletRequest request) {
                List<ParameterValuePair> pairs = new ArrayList<ParameterValuePair>();
                pairs.add(new ParameterValuePair("test", "mock"));
                return pairs;
            }

            @Override
            public boolean support(BaseCmsPrototype modulePrototype) {
                return modulePrototype.getDbId() == 1;
            }

            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }
        };
        parameterGathers.add(another);

        CmsModulePrototype prototype = cmsModulePrototypeService.getById(1L);
        List<ParameterGather> supportedGather = new ArrayList<ParameterGather>();
        for(ParameterGather gather : parameterGathers) {
            if(gather.support(prototype)) {
                supportedGather.add(gather);
            }
        }
        Collections.sort(supportedGather, new Comparator<ParameterGather>() {
            @Override
            public int compare(ParameterGather o1, ParameterGather o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        CmsModuleInstance instance = cmsModuleInstanceService.getById(1L);

        // 优先级高的收集器收集的数据将覆盖低优先级的数据
        Map<String, ParameterValuePair> paramMap = new HashMap<String, ParameterValuePair>();
        for(ParameterGather gather : supportedGather) {
            List<ParameterValuePair> params = gather.gatherParams(prototype, instance, request);
            for(ParameterValuePair pair : params) {
                paramMap.put(pair.getName(), pair);
            }
        }

        ImmutableList<ParameterValuePair> params = ImmutableList.copyOf(paramMap.values());

        Assert.notEmpty(params);
        Assert.isTrue(params.size() == 4);

        System.out.println(StringUtils.center(" Params " , 80, "="));
        for(ParameterValuePair pair : params) {
            System.out.println(pair);
        }
        System.out.println(StringUtils.center("=" , 80, "="));
    }
}