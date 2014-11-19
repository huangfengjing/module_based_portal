package com.doleje.portlet.service;

import com.doleje.portlet.common.RenderException;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.cache.CacheManager;

import java.util.List;

/**
 * 渲染引擎
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-10-29 下午2:30.
 */
public class RenderEngine {

    private VelocityEngine velocityEngine;

    private List<ModuleContextProvider> moduleContextProviders;

    private List<ModuleParameterProcessor> moduleParameterProcessors;

    private List<RenderInterceptor> renderInterceptors;

    private CacheManager cacheManager;

    private CmsPageService cmsPageService;

    private CmsModulePrototypeService cmsModulePrototypeService;

    private CmsModuleInstanceService cmsModuleInstanceService;

    public String renderPage(long pageId, RenderContext context) throws RenderException {
        return null;
    }

    public String renderModule(long moduleInstanceId, RenderContext context) throws RenderException {
        return null;
    }

    public String renderModuleForm(long moduleInstanceId, RenderContext context) throws RenderException {
        return null;
    }
}