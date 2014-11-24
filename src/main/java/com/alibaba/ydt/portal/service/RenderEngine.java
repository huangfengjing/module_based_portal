package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.exception.RenderException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 渲染引擎
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-10-29 下午2:30.
 */
public class RenderEngine {

    private Log logger = LogFactory.getLog(getClass());

    @Autowired
    private VelocityEngine velocityEngine;

    private List<ModuleContextProvider> moduleContextProviders;

    private List<ModuleParameterProcessor> moduleParameterProcessors;

    private List<RenderInterceptor> renderInterceptors;

    private CacheManager cacheManager;

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
     * @param pageId 页面实例 ID
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public String renderPage(long pageId, RenderContext context) throws RenderException {
        CmsPageInstance page = null;
        try {
            page = cmsPageInstanceService.getById(pageId);
            if (null == page) {
                throw new RenderException("页面实例未找到");
            }
            CmsPagePrototype prototype = cmsPagePrototypeService.getById(page.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("页面原型未找到");
            }

            if(!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            RenderContextBuilder pageContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setPageInstance(page).setPagePrototype(prototype);
            List<String> layoutRenderResult = new ArrayList<String>(page.getLayouts().size());
            for (CmsLayoutInstance layout : page.getLayouts()) {
                RenderContext localContext = RenderContextBuilder.newBuilder().cloneFrom(pageContextBuilder.build()).build();
                layoutRenderResult.add(renderLayout(layout, localContext));
            }
            pageContextBuilder.setPageLayoutList(layoutRenderResult).addParams(page.getParameters());
            return render(prototype.getTemplate(), pageContextBuilder.build());
        } catch (Exception e) {
            if(null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(page, context, new RenderException(e));
            }
            logger.error("<!-- 页面渲染失败，layoutId=" + pageId + " -->", e);
            return "<!-- 页面渲染失败，layoutId=" + pageId + " -->";
        }
    }

    /**
     * 渲染布局
     * @param layout 布局实例
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public String renderLayout(CmsLayoutInstance layout, RenderContext context) {
        try {
            if (null == layout) {
                throw new RenderException("布局实例不存在");
            }
            CmsLayoutPrototype prototype = cmsLayoutPrototypeService.getById(layout.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("布局原型未找到");
            }

            if(!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            RenderContextBuilder layoutContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setLayoutInstance(layout).setLayoutPrototype(prototype);
            List<String> columnRenderResult = new ArrayList<String>(layout.getColumns().size());
            for (CmsColumnInstance column : layout.getColumns()) {
                RenderContext localContext = RenderContextBuilder.newBuilder().cloneFrom(layoutContextBuilder.build()).build();
                columnRenderResult.add(renderColumn(column, localContext));
            }
            if(null == layout.getParams4Store()) {
                CmsLayoutInstance inst = cmsLayoutInstanceService.getById(layout.getDbId());
                layout.setParams4Store(inst.getParams4Store());
            }
            layoutContextBuilder.setLayoutColumnList(columnRenderResult).addParams(layout.getParameters());
            return render(prototype.getTemplate(), layoutContextBuilder.build());
        } catch (Exception e) {
            if(null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(layout, context, new RenderException(e));
            }
            logger.error("<!-- 布局渲染失败，layout=" + layout + " -->", e);
            return "<!-- 布局渲染失败，layout=" + layout + " -->";
        }
    }

    /**
     * 渲染列
     * @param column 列实例
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public String renderColumn(CmsColumnInstance column, RenderContext context) {
        try {
            if (null == column) {
                throw new RenderException("列实例不存在");
            }
            CmsColumnPrototype prototype = cmsColumnPrototypeService.getById(column.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("列原型未找到");
            }

            if(!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            RenderContextBuilder columnContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setColumnInstance(column).setColumnPrototype(prototype);
            List<String> moduleRenderResult = new ArrayList<String>(column.getModules().size());
            for (CmsModuleInstance module : column.getModules()) {
                RenderContext localContext = RenderContextBuilder.newBuilder().cloneFrom(columnContextBuilder.build()).build();
                moduleRenderResult.add(renderModule(module, localContext));
            }
            if(null == column.getParams4Store()) {
                CmsColumnInstance inst = cmsColumnInstanceService.getById(column.getDbId());
                column.setParams4Store(inst.getParams4Store());
            }
            columnContextBuilder.setColumnModuleList(moduleRenderResult).addParams(column.getParameters());
            return render(prototype.getTemplate(), columnContextBuilder.build());
        } catch (Exception e) {
            if(null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(column, context, new RenderException(e));
            }
            logger.error("<!-- 列渲染失败，column=" + column + " -->", e);
            return "<!-- 列渲染失败，column=" + column + " -->";
        }
    }

    /**
     * 渲染模块
     * @param module 模块实例
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public String renderModule(CmsModuleInstance module, RenderContext context) throws RenderException {
        try {
            if (null == module) {
                throw new RenderException("没找到模块实例");
            }
            CmsModulePrototype prototype = cmsModulePrototypeService.getById(module.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("没找到模块原型");
            }

            if(!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            if(null == module.getParams4Store()) {
                CmsModuleInstance inst = cmsModuleInstanceService.getById(module.getDbId());
                module.setParams4Store(inst.getParams4Store());
            }
            RenderContextBuilder moduleContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setModuleInstance(module).setModulePrototype(prototype).addParams(module.getParameters());
            return render(prototype.getTemplate(), moduleContextBuilder.build());
        } catch (Exception e) {
            if(null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(module, context, new RenderException(e));
            }
            logger.error("<!-- 模块渲染失败，module=" + module + " -->", e);
            return "<!-- 模块渲染失败，module=" + module + " -->";
        }
    }

    /**
     * 渲染模块表单
     * @param moduleId 模块实例 ID
     * @param context 渲染上下文
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

            if(!context.containsKey(RenderContext.TOOL_BOX_INJECTED)) {
                injectToolbox(context);
            }

            RenderContextBuilder moduleContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setModuleInstance(module).setModulePrototype(prototype).addParams(module.getParameters());
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
     * @throws com.alibaba.ydt.portal.exception.RenderException
     *          模块渲染异常
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
            if(null == toolboxFactory && null != toolboxConfigLocation) {
                FileFactoryConfiguration cfg = new XmlFactoryConfiguration(true);
                cfg.read(toolboxConfigLocation.getInputStream());
                toolboxFactory = cfg.createFactory();
                globalToolbox = toolboxFactory.createToolbox(Scope.APPLICATION);
            }
            for(String key : globalToolbox.getKeys()) {
                context.put(key, globalToolbox.get(key));
            }

            HttpServletRequest request = (HttpServletRequest)context.get(RenderContext.RENDER_REQUEST_KEY);
            HttpServletResponse response = (HttpServletResponse)context.get(RenderContext.RENDER_RESPONSE_KEY);
            ServletContext servletContext = (ServletContext)context.get(RenderContext.RENDER_SERVLET_CONTEXT_KEY);
            if(null != request && null != response && null != servletContext) {
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
}