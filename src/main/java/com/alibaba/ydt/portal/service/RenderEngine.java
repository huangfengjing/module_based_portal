package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.exception.RenderException;
import com.alibaba.ydt.portal.util.CmsUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.Toolbox;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.FileFactoryConfiguration;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.apache.velocity.tools.view.ViewToolContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * 渲染引擎
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-10-29 下午2:30.
 */
public class RenderEngine implements InitializingBean {

    private Log logger = LogFactory.getLog(getClass());

    public static final String RENDER_CACHE_NAME = "_RENDER_CACHE";
    public static final String RENDER_CACHE_TYPE_FOR_PAGE = "_RENDER_CACHE_TYPE_FOR_PAGE";
    public static final String RENDER_CACHE_TYPE_FOR_LAYOUT = "_RENDER_CACHE_TYPE_FOR_LAYOUT";
    public static final String RENDER_CACHE_TYPE_FOR_COLUMN = "_RENDER_CACHE_TYPE_FOR_COLUMN";
    public static final String RENDER_CACHE_TYPE_FOR_MODULE = "_RENDER_CACHE_TYPE_FOR_MODULE";

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private List<ContextProvider> contextProviders = new ArrayList<ContextProvider>();

    private List<ModuleParameterProcessor> moduleParameterProcessors;

    private List<RenderInterceptor> renderInterceptors = new ArrayList<RenderInterceptor>();

    @Autowired
    private CacheManager cacheManager;

    private Cache renderCache;

    private RenderExceptionHandler renderExceptionHandler;

    private CmsPagePrototypeService cmsPagePrototypeService;

    private CmsPageInstanceService cmsPageInstanceService;

    private CmsLayoutPrototypeService cmsLayoutPrototypeService;

    private CmsLayoutInstanceService cmsLayoutInstanceService;

    private CmsColumnPrototypeService cmsColumnPrototypeService;

    private CmsColumnInstanceService cmsColumnInstanceService;

    private CmsModulePrototypeService cmsModulePrototypeService;

    private CmsModuleInstanceService cmsModuleInstanceService;

    private Resource toolboxConfigLocation;
    private static ToolboxFactory toolboxFactory = null;
    private static Toolbox globalToolbox = null;

    /**
     * 渲染页面
     *
     * @param pageId  页面实例 ID
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public RenderResult renderPage(long pageId, RenderContext context) throws RenderException {
        CmsPageInstance page = null;
        try {

            page = cmsPageInstanceService.getById(pageId);
            if (null == page) {
                throw new RenderException("页面实例未找到");
            }

            for(RenderInterceptor interceptor : renderInterceptors) {
                if(!interceptor.before(page, context)) {
                    return new RenderResult("<!-- 被拦截器跳过，page=" + page + " -->", RenderResult.RESULT_TYPE_SKIPPED);
                }
            }

            CmsPagePrototype prototype = cmsPagePrototypeService.getById(page.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("页面原型未找到");
            }

            // 查看缓存
            String cacheKey = CmsUtils.generateCacheKey(RENDER_CACHE_TYPE_FOR_PAGE, pageId, context);
            Cache.ValueWrapper tmp = renderCache.get(cacheKey);
            if(null != tmp && null != tmp.get()) {
                RenderResult cached = (RenderResult) tmp.get();
                long delta = new Date().getTime() - cached.getRenderTime().getTime();
                if (delta < prototype.getInstanceExpiredSeconds() * 1000) {
                    cached.setResultType(RenderResult.RESULT_TYPE_FROM_CACHE);
                    return cached;
                }
            }

            if (!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            RenderContextBuilder pageContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setPageInstance(page).setPagePrototype(prototype);
            List<String> layoutRenderResult = new ArrayList<String>(page.getLayouts().size());
            for (CmsLayoutInstance layout : page.getLayouts()) {
                RenderContext localContext = RenderContextBuilder.newBuilder().cloneFrom(pageContextBuilder.build()).build();
                layoutRenderResult.add(renderLayout(layout, localContext).getRenderContent());
            }
            pageContextBuilder.setPageLayoutList(layoutRenderResult);
            injectProvidersContext(page, (HttpServletRequest) context.get(RenderContext.RENDER_REQUEST_KEY), pageContextBuilder);
            String content = render(prototype.getTemplate(), pageContextBuilder.build());
            RenderResult result = new RenderResult(content);
            for(RenderInterceptor interceptor : renderInterceptors) {
                result = interceptor.after(page, result);
            }

            // put 缓存
            renderCache.put(cacheKey, result);

            return result;
        } catch (Exception e) {
            if (null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(page, context, new RenderException(e));
            }
            logger.error("<!-- 页面渲染失败，layoutId=" + pageId + " -->", e);
            return new RenderResult("<!-- 页面渲染失败，layoutId=" + pageId + " -->", RenderResult.RESULT_TYPE_HANDLE_ERROR);
        }
    }

    /**
     * 渲染布局
     *
     * @param layout  布局实例
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public RenderResult renderLayout(CmsLayoutInstance layout, RenderContext context) {
        try {
            if (null == layout) {
                throw new RenderException("布局实例不存在");
            }
            CmsLayoutPrototype prototype = cmsLayoutPrototypeService.getById(layout.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("布局原型未找到");
            }

            // 查看缓存
            String cacheKey = CmsUtils.generateCacheKey(RENDER_CACHE_TYPE_FOR_LAYOUT, layout.getDbId(), context);
            Cache.ValueWrapper tmp = renderCache.get(cacheKey);
            if(null != tmp && null != tmp.get()) {
                RenderResult cached = (RenderResult) tmp.get();
                long delta = new Date().getTime() - cached.getRenderTime().getTime();
                if (delta < prototype.getInstanceExpiredSeconds() * 1000) {
                    cached.setResultType(RenderResult.RESULT_TYPE_FROM_CACHE);
                    return cached;
                }
            }

            if (!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            RenderContextBuilder layoutContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setLayoutInstance(layout).setLayoutPrototype(prototype);
            List<String> columnRenderResult = new ArrayList<String>(layout.getColumns().size());
            for (CmsColumnInstance column : layout.getColumns()) {
                RenderContext localContext = RenderContextBuilder.newBuilder().cloneFrom(layoutContextBuilder.build()).build();
                columnRenderResult.add(renderColumn(column, localContext).getRenderContent());
            }

            // 前置拦截器处理
            for(RenderInterceptor interceptor : renderInterceptors) {
                if(!interceptor.before(layout, context)) {
                    return new RenderResult("<!-- 被拦截器跳过，layout=" + layout + " -->", RenderResult.RESULT_TYPE_SKIPPED);
                }
            }

            layoutContextBuilder.setLayoutColumnList(columnRenderResult);
            injectProvidersContext(layout, (HttpServletRequest) context.get(RenderContext.RENDER_REQUEST_KEY), layoutContextBuilder);
            RenderResult result = new RenderResult(render(prototype.getTemplate(), layoutContextBuilder.build()));

            // 后置拦截器处理
            for(RenderInterceptor interceptor : renderInterceptors) {
                result = interceptor.after(layout, result);
            }

            // put 缓存
            renderCache.put(cacheKey, result);

            return result;
        } catch (Exception e) {
            if (null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(layout, context, new RenderException(e));
            }
            logger.error("<!-- 布局渲染失败，layout=" + layout + " -->", e);
            return new RenderResult("<!-- 布局渲染失败，layout=" + layout + " -->", RenderResult.RESULT_TYPE_HANDLE_ERROR);
        }
    }

    /**
     * 渲染列
     *
     * @param column  列实例
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public RenderResult renderColumn(CmsColumnInstance column, RenderContext context) {
        try {
            if (null == column) {
                throw new RenderException("列实例不存在");
            }
            CmsColumnPrototype prototype = cmsColumnPrototypeService.getById(column.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("列原型未找到");
            }

            // 查看缓存
            String cacheKey = CmsUtils.generateCacheKey(RENDER_CACHE_TYPE_FOR_COLUMN, column.getDbId(), context);
            Cache.ValueWrapper tmp = renderCache.get(cacheKey);
            if(null != tmp && null != tmp.get()) {
                RenderResult cached = (RenderResult) tmp.get();
                long delta = new Date().getTime() - cached.getRenderTime().getTime();
                if (delta < prototype.getInstanceExpiredSeconds() * 1000) {
                    cached.setResultType(RenderResult.RESULT_TYPE_FROM_CACHE);
                    return cached;
                }
            }

            if (!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            RenderContextBuilder columnContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setColumnInstance(column).setColumnPrototype(prototype);
            List<String> moduleRenderResult = new ArrayList<String>(column.getModules().size());
            for (CmsModuleInstance module : column.getModules()) {
                RenderContext localContext = RenderContextBuilder.newBuilder().cloneFrom(columnContextBuilder.build()).build();
                moduleRenderResult.add(renderModule(module, localContext).getRenderContent());
            }

            // 前置拦截器处理
            for(RenderInterceptor interceptor : renderInterceptors) {
                if(!interceptor.before(column, context)) {
                    return new RenderResult("<!-- 被拦截器跳过，column=" + column + " -->", RenderResult.RESULT_TYPE_SKIPPED);
                }
            }

            columnContextBuilder.setColumnModuleList(moduleRenderResult);
            injectProvidersContext(column, (HttpServletRequest) context.get(RenderContext.RENDER_REQUEST_KEY), columnContextBuilder);
            RenderResult result = new RenderResult(render(prototype.getTemplate(), columnContextBuilder.build()));

            // 后置拦截器处理
            for(RenderInterceptor interceptor : renderInterceptors) {
                result = interceptor.after(column, result);
            }

            // put 缓存
            renderCache.put(cacheKey, result);

            return result;
        } catch (Exception e) {
            if (null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(column, context, new RenderException(e));
            }
            logger.error("<!-- 列渲染失败，column=" + column + " -->", e);
            return new RenderResult("<!-- 列渲染失败，column=" + column + " -->", RenderResult.RESULT_TYPE_HANDLE_ERROR);
        }
    }

    /**
     * 渲染模块
     *
     * @param module  模块实例
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public RenderResult renderModule(CmsModuleInstance module, RenderContext context) throws RenderException {
        try {
            if (null == module) {
                throw new RenderException("没找到模块实例");
            }
            CmsModulePrototype prototype = cmsModulePrototypeService.getById(module.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("没找到模块原型");
            }

            // 查看缓存
            String cacheKey = CmsUtils.generateCacheKey(RENDER_CACHE_TYPE_FOR_MODULE, module.getDbId(), context);
            Cache.ValueWrapper tmp = renderCache.get(cacheKey);
            if(null != tmp && null != tmp.get()) {
                RenderResult cached = (RenderResult) tmp.get();
                long delta = new Date().getTime() - cached.getRenderTime().getTime();
                if (delta < prototype.getInstanceExpiredSeconds() * 1000) {
                    cached.setResultType(RenderResult.RESULT_TYPE_FROM_CACHE);
                    return cached;
                }
            }

            if (!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            // 前置拦截器处理
            for(RenderInterceptor interceptor : renderInterceptors) {
                if(!interceptor.before(module, context)) {
                    return new RenderResult("<!-- 被拦截器跳过，module=" + module + " -->", RenderResult.RESULT_TYPE_SKIPPED);
                }
            }

            RenderContextBuilder moduleContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setModuleInstance(module).setModulePrototype(prototype);
            injectProvidersContext(module, (HttpServletRequest) context.get(RenderContext.RENDER_REQUEST_KEY), moduleContextBuilder);
            RenderResult result = new RenderResult(render(prototype.getTemplate(), moduleContextBuilder.build()));

            // 后置拦截器处理
            for(RenderInterceptor interceptor : renderInterceptors) {
                result = interceptor.after(module, result);
            }

            // put 缓存
            renderCache.put(cacheKey, result);

            return result;
        } catch (Exception e) {
            if (null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(module, context, new RenderException(e));
            }
            logger.error("<!-- 模块渲染失败，module=" + module + " -->", e);
            return new RenderResult("<!-- 模块渲染失败，module=" + module + " -->", RenderResult.RESULT_TYPE_HANDLE_ERROR);
        }
    }

    /**
     * 渲染模块表单
     *
     * @param moduleId 模块实例 ID
     * @param context  渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public String renderModuleForm(long moduleId, RenderContext context) throws RenderException {
        CmsModuleInstance module;
        try {
            module = cmsModuleInstanceService.getById(moduleId);
            if (null == module) {
                return null;
            }
            CmsModulePrototype prototype = cmsModulePrototypeService.getById(module.getPrototypeId());
            if (null == prototype) {
                return null;
            }

            if (!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            RenderContextBuilder moduleContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setModuleInstance(module).setModulePrototype(prototype);
            injectProvidersFormContext(module, (HttpServletRequest) context.get(RenderContext.RENDER_REQUEST_KEY), moduleContextBuilder);
            return render(prototype.getFormTemplate(), moduleContextBuilder.build());
        } catch (Exception e) {
            logger.error("<!-- 模块表单渲染失败，moduleId=" + moduleId + " -->", e);
            return "<!-- 模块表单渲染失败，moduleId=" + moduleId + " -->";
        }
    }


    /**
     * 渲染 vm
     *
     * @param vmContent vm 内容
     * @param context   渲染上下文
     * @return 渲染结果
     * @throws com.alibaba.ydt.portal.exception.RenderException 模块渲染异常
     */
    private String render(String vmContent, RenderContext context) throws RenderException {
        if (null == context) {
            context = new RenderContext();
        }
        try {
            StringWriter sw = new StringWriter();
            velocityEngine.evaluate(new VelocityContext(context), sw, "RenderEngine", new StringReader(vmContent));
            return sw.toString();
        } catch (Exception e) {
            throw new RenderException(e);
        }
    }

    public void setCmsPagePrototypeService(CmsPagePrototypeService cmsPagePrototypeService) {
        this.cmsPagePrototypeService = cmsPagePrototypeService;
    }

    public void setCmsPageInstanceService(CmsPageInstanceService cmsPageInstanceService) {
        this.cmsPageInstanceService = cmsPageInstanceService;
    }

    public void setCmsLayoutPrototypeService(CmsLayoutPrototypeService cmsLayoutPrototypeService) {
        this.cmsLayoutPrototypeService = cmsLayoutPrototypeService;
    }

    public void setCmsLayoutInstanceService(CmsLayoutInstanceService cmsLayoutInstanceService) {
        this.cmsLayoutInstanceService = cmsLayoutInstanceService;
    }

    public void setCmsColumnPrototypeService(CmsColumnPrototypeService cmsColumnPrototypeService) {
        this.cmsColumnPrototypeService = cmsColumnPrototypeService;
    }

    public void setCmsColumnInstanceService(CmsColumnInstanceService cmsColumnInstanceService) {
        this.cmsColumnInstanceService = cmsColumnInstanceService;
    }

    public void setCmsModulePrototypeService(CmsModulePrototypeService cmsModulePrototypeService) {
        this.cmsModulePrototypeService = cmsModulePrototypeService;
    }

    public void setCmsModuleInstanceService(CmsModuleInstanceService cmsModuleInstanceService) {
        this.cmsModuleInstanceService = cmsModuleInstanceService;
    }


    /**
     * 注入 toolbox 配置
     */
    protected void injectToolbox(RenderContext context) {
        try {
            if (null == toolboxFactory && null != toolboxConfigLocation) {
                FileFactoryConfiguration cfg = new XmlFactoryConfiguration(true);
                cfg.read(toolboxConfigLocation.getInputStream());
                toolboxFactory = cfg.createFactory();
                globalToolbox = toolboxFactory.createToolbox(Scope.APPLICATION);
            }
            for (String key : globalToolbox.getKeys()) {
                context.put(key, globalToolbox.get(key));
            }

            HttpServletRequest request = (HttpServletRequest) context.get(RenderContext.RENDER_REQUEST_KEY);
            HttpServletResponse response = (HttpServletResponse) context.get(RenderContext.RENDER_RESPONSE_KEY);
            ServletContext servletContext = (ServletContext) context.get(RenderContext.RENDER_SERVLET_CONTEXT_KEY);
            if (null != request && null != response && null != servletContext) {
                ViewToolContext velocityContext = new ViewToolContext(velocityEngine, request, response, servletContext);
                velocityContext.addToolbox(toolboxFactory.createToolbox(Scope.REQUEST));
                velocityContext.addToolbox(toolboxFactory.createToolbox(Scope.SESSION));
                for (String key : velocityContext.keySet()) {
                    context.put(key, velocityContext.get(key));
                }
            }
            context.put(RenderContext.TOOL_BOX_INJECTED, true);
        } catch (IOException e) {
            logger.error("创建 velocity context 失败", e);
        }
    }

    public void setToolboxConfigLocation(Resource toolboxConfigLocation) {
        this.toolboxConfigLocation = toolboxConfigLocation;
    }

    /**
     * 注入所有支持的环境变量供应器生成的参数
     *
     * @param instance 要渲染的实例
     * @param request  请求
     * @param builder  渲染环境 builder
     */
    public void injectProvidersContext(Object instance, HttpServletRequest request, RenderContextBuilder builder) {
        List<ContextProvider> supportedProvider = new ArrayList<ContextProvider>();
        for (ContextProvider provider : contextProviders) {
            if (provider.support(instance)) {
                supportedProvider.add(provider);
            }
        }
        Collections.sort(supportedProvider, new Comparator<ContextProvider>() {
            @Override
            public int compare(ContextProvider o1, ContextProvider o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        for (ContextProvider provider : supportedProvider) {
            builder.mergeContext(provider.createContext(instance, request));
        }
    }

    /**
     * 注入所有支持的环境变量供应器生成的表单参数
     *
     * @param instance 要渲染的实例
     * @param request  请求
     * @param builder  渲染环境 builder
     */
    public void injectProvidersFormContext(Object instance, HttpServletRequest request, RenderContextBuilder builder) {
        List<ContextProvider> supportedProvider = new ArrayList<ContextProvider>();
        for (ContextProvider provider : contextProviders) {
            if (provider.support(instance)) {
                supportedProvider.add(provider);
            }
        }
        Collections.sort(supportedProvider, new Comparator<ContextProvider>() {
            @Override
            public int compare(ContextProvider o1, ContextProvider o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        for (ContextProvider provider : supportedProvider) {
            builder.mergeContext(provider.createFormContext(instance, request));
        }
    }


    public void setContextProviders(List<ContextProvider> contextProviders) {
        this.contextProviders = contextProviders;
    }

    public void setRenderInterceptors(List<RenderInterceptor> renderInterceptors) {
        this.renderInterceptors = renderInterceptors;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        renderCache = cacheManager.getCache(RENDER_CACHE_NAME);
    }
}