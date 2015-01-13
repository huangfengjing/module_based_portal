package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.exception.RenderException;
import com.alibaba.ydt.portal.util.CmsUtils;
import com.alibaba.ydt.portal.util.StringUtils;
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
import org.springframework.beans.factory.BeanCreationException;
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

    @Autowired
    private List<RenderInterceptor> renderInterceptors = new ArrayList<RenderInterceptor>();

    @Autowired
    private CacheManager cacheManager;

    private Cache renderCache;

    @Autowired
    private RenderExceptionHandler renderExceptionHandler;

    @Autowired
    private CmsPagePrototypeService cmsPagePrototypeService;

    @Autowired
    private CmsPageInstanceService cmsPageInstanceService;

    @Autowired
    private CmsLayoutPrototypeService cmsLayoutPrototypeService;

    @Autowired
    private CmsLayoutInstanceService cmsLayoutInstanceService;

    @Autowired
    private CmsColumnPrototypeService cmsColumnPrototypeService;

    @Autowired
    private CmsColumnInstanceService cmsColumnInstanceService;

    @Autowired
    private CmsModulePrototypeService cmsModulePrototypeService;

    @Autowired
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
        return renderPage(cmsPageInstanceService.getById(pageId), context);
    }

    /**
     * 渲染页面
     *
     * @param page  页面实例
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public RenderResult renderPage(CmsPageInstance page, RenderContext context) throws RenderException {
        try {
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
            String cacheKey = CmsUtils.generateCacheKey(RENDER_CACHE_TYPE_FOR_PAGE, page.getDbId(), context);
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
//            String content = render(prototype.getTemplate(), pageContextBuilder.build());
            String content = StringUtils.join(layoutRenderResult, "");
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
            logger.error("<!-- 页面渲染失败，page=" + page + " -->", e);
            return new RenderResult("<!-- 页面渲染失败，page=" + page + " -->", RenderResult.RESULT_TYPE_HANDLE_ERROR);
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
            if(StringUtils.isBlank(layout.getParams4Store())) {
                List<CmsColumnInstance> cols = layout.getColumns();
                layout = cmsLayoutInstanceService.getById(layout.getDbId());
                layout.setColumns(cols);
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
                if(StringUtils.isBlank(column.getParams4Store()) && column.getDbId() > 0) {
                    CmsColumnInstance fromDb = cmsColumnInstanceService.getById(column.getDbId());
                    column.setTitle(fromDb.getTitle());
                    column.setParams4Store(StringUtils.defaultIfEmpty(fromDb.getParams4Store(), ""));
                }
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
            if(StringUtils.isBlank(column.getParams4Store()) && column.getDbId() > 0) {
                CmsColumnInstance fromDb = cmsColumnInstanceService.getById(column.getDbId());
                column.setTitle(fromDb.getTitle());
                column.setParams4Store(StringUtils.defaultIfEmpty(fromDb.getParams4Store(), ""));
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
                if(StringUtils.isBlank(module.getParams4Store()) && module.getDbId() > 0) {
                    CmsModuleInstance fromDb = cmsModuleInstanceService.getById(module.getDbId());
                    module.setParams4Store(StringUtils.defaultIfEmpty(fromDb.getParams4Store(), ""));
                    module.setTitle(fromDb.getTitle());
                }
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
            if(StringUtils.isBlank(module.getParams4Store()) && module.getDbId() > 0) {
                CmsModuleInstance fromDb = cmsModuleInstanceService.getById(module.getDbId());
                module.setParams4Store(StringUtils.defaultIfEmpty(fromDb.getParams4Store(), ""));
                module.setTitle(fromDb.getTitle());
            }

            // 查看缓存
            String cacheKey = CmsUtils.generateCacheKey(RENDER_CACHE_TYPE_FOR_MODULE, module.getDbId(), context);
            Cache.ValueWrapper tmp = renderCache.get(cacheKey);
            if (null != tmp && null != tmp.get()) {
                RenderResult cached = (RenderResult) tmp.get();
                long delta = new Date().getTime() - cached.getRenderTime().getTime();
                if (delta < prototype.getInstanceExpiredSeconds() * 1000) {
                    cached.setResultType(RenderResult.RESULT_TYPE_FROM_CACHE);

                    // 后置拦截器处理
                    for(RenderInterceptor interceptor : renderInterceptors) {
                        cached = interceptor.after(module, cached);
                    }

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

            // 注入父容器信息
            CmsColumnInstance column = (CmsColumnInstance) context.get(RenderContext.COLUMN_INSTANCE_KEY);
            if(null == column) {
                CmsPageInstance page = (CmsPageInstance) context.get(RenderContext.PAGE_INSTANCE_KEY);
                if (null != page) {
                    boolean injected = false;
                    for(CmsLayoutInstance layoutInstance : page.getLayouts()) {
                        for(CmsColumnInstance columnInstance : layoutInstance.getColumns()) {
                            for(CmsModuleInstance moduleInstance : columnInstance.getModules()) {
                                if(moduleInstance.getDbId() == module.getDbId()) {
                                    // 暂时不支持 Layout 参数编辑，所以不需要处理参数，增强性能
//                                    if(StringUtils.isBlank(layoutInstance.getParams4Store())) {
//                                        CmsLayoutInstance layoutFromDb = cmsLayoutInstanceService.getById(layoutInstance.getDbId());
//                                        layoutInstance.setParams4Store(layoutFromDb.getParams4Store());
//                                    }
                                    if(StringUtils.isBlank(columnInstance.getParams4Store())) {
                                        CmsColumnInstance columnFromDb = cmsColumnInstanceService.getById(columnInstance.getDbId());
                                        columnInstance.setParams4Store(StringUtils.defaultIfEmpty(columnFromDb.getParams4Store(), ""));
                                    }
                                    moduleContextBuilder.setLayoutInstance(layoutInstance);
                                    moduleContextBuilder.setColumnInstance(columnInstance);
                                    injected = true;
                                    break;
                                }
                            }
                            if(injected) {
                                break;
                            }
                        }
                        if(injected) {
                            break;
                        }
                    }
                }
            }

            injectProvidersContext(module, (HttpServletRequest) context.get(RenderContext.RENDER_REQUEST_KEY), moduleContextBuilder);
            RenderResult result = new RenderResult(render(prototype.getTemplate(), moduleContextBuilder.build()));

            // 后置拦截器处理
            for(RenderInterceptor interceptor : renderInterceptors) {
                result = interceptor.after(module, result);
            }

            // put 缓存
            if(StringUtils.isNotBlank(cacheKey)) {
                renderCache.put(cacheKey, result);
            }

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
     * @param instanceTypeTag 实例类型
     * @param instanceId 实例 ID
     * @param context  渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public String renderCompForm(String instanceTypeTag, long instanceId, RenderContext context) throws RenderException {
        BaseCmsInstance instance = null;
        try {
            if(CmsPageInstance.TYPE_TAG.equals(instanceTypeTag)) {
                instance = cmsPageInstanceService.getById(instanceId);
            } else if (CmsLayoutInstance.TYPE_TAG.equals(instanceTypeTag)) {
                instance = cmsLayoutInstanceService.getById(instanceId);
            } else if (CmsColumnInstance.TYPE_TAG.equals(instanceTypeTag)) {
                instance = cmsColumnInstanceService.getById(instanceId);
            } else if (CmsModuleInstance.TYPE_TAG.equals(instanceTypeTag)) {
                instance = cmsModuleInstanceService.getById(instanceId);
            }
            if (null == instance) {
                return null;
            }

            BaseCmsPrototype prototype = null;
            if(CmsPageInstance.TYPE_TAG.equals(instanceTypeTag)) {
                prototype = cmsPagePrototypeService.getById(instance.getPrototypeId());
            } else if (CmsLayoutInstance.TYPE_TAG.equals(instanceTypeTag)) {
                prototype = cmsLayoutPrototypeService.getById(instance.getPrototypeId());
            } else if (CmsColumnInstance.TYPE_TAG.equals(instanceTypeTag)) {
                prototype = cmsColumnPrototypeService.getById(instance.getPrototypeId());
            } else if (CmsModuleInstance.TYPE_TAG.equals(instanceTypeTag)) {
                prototype = cmsModulePrototypeService.getById(instance.getPrototypeId());
            }
            if (null == prototype) {
                return null;
            }

            if (!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            RenderContextBuilder contextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context);
            if(CmsPageInstance.TYPE_TAG.equals(instanceTypeTag) && instance instanceof CmsPageInstance) {
                contextBuilder.setPageInstance((CmsPageInstance) instance);
            } else if (CmsLayoutInstance.TYPE_TAG.equals(instanceTypeTag) && instance instanceof CmsLayoutInstance) {
                contextBuilder.setLayoutInstance((CmsLayoutInstance) instance);
            } else if (CmsColumnInstance.TYPE_TAG.equals(instanceTypeTag) && instance instanceof CmsColumnInstance) {
                contextBuilder.setColumnInstance((CmsColumnInstance) instance);
            } else if (CmsModuleInstance.TYPE_TAG.equals(instanceTypeTag) && instance instanceof CmsModuleInstance) {
                contextBuilder.setModuleInstance((CmsModuleInstance) instance);
            }
            injectProvidersFormContext(instance, (HttpServletRequest) context.get(RenderContext.RENDER_REQUEST_KEY), contextBuilder);
            return render(prototype.getFormTemplate(), contextBuilder.build());
        } catch (Exception e) {
            logger.error("<!-- 模块表单渲染失败，instanceTagType=" + instanceTypeTag + "instanceId=" + instanceId + " -->", e);
            return "<!-- 模块表单渲染失败，instanceTagType=" + instanceTypeTag + "instanceId=" + instanceId + " -->";
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
        if(StringUtils.isBlank(vmContent)) {
            return "";
        }
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

    /**
     * 注入所有支持的上下文供应器生成的参数
     *
     * @param instance 要渲染的实例
     * @param request  请求
     * @param builder  渲染环境 builder
     */
    public void injectProvidersContext(BaseCmsInstance instance, HttpServletRequest request, RenderContextBuilder builder) {
        if(null == instance) {
            return;
        }
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
    public void injectProvidersFormContext(BaseCmsInstance instance, HttpServletRequest request, RenderContextBuilder builder) {
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

    public void setToolboxConfigLocation(Resource toolboxConfigLocation) {
        this.toolboxConfigLocation = toolboxConfigLocation;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(null != cacheManager) {
            renderCache = cacheManager.getCache(RENDER_CACHE_NAME);
            if (null == renderCache) {
                throw new BeanCreationException("创建 RenderEngine 失败，找不到缓存，请检查缓存配置项！");
            }
        }
    }
}