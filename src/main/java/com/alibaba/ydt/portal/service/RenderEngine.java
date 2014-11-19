package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.exception.RenderException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.cache.CacheManager;

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
            RenderContextBuilder pageContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setPageInstance(page).setPagePrototype(prototype);
            List<String> layoutRenderResult = new ArrayList<String>(page.getLayouts().size());
            for (CmsLayoutInstance layout : page.getLayouts()) {
                RenderContext localContext = RenderContextBuilder.newBuilder().cloneFrom(pageContextBuilder.build()).build();
                layoutRenderResult.add(renderLayout(layout.getDbId(), localContext));
            }
            pageContextBuilder.setPageLayoutList(layoutRenderResult).addParams(page.getParameters());
            return render(prototype.getTemplate(), pageContextBuilder.build());
        } catch (Exception e) {
            if(null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(pageId, page, context, new RenderException(e));
            }
            return "<!-- 页面渲染失败，layoutId=" + pageId + " -->";
        }
    }

    /**
     * 渲染布局
     * @param layoutId 布局实例 ID
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public String renderLayout(long layoutId, RenderContext context) {
        CmsLayoutInstance layout = null;
        try {
            layout = cmsLayoutInstanceService.getById(layoutId);
            if (null == layout) {
                throw new RenderException("布局实例未找到");
            }
            CmsLayoutPrototype prototype = cmsLayoutPrototypeService.getById(layout.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("布局原型未找到");
            }
            RenderContextBuilder layoutContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setLayoutInstance(layout).setLayoutPrototype(prototype);
            List<String> columnRenderResult = new ArrayList<String>(layout.getColumns().size());
            for (CmsColumnInstance column : layout.getColumns()) {
                RenderContext localContext = RenderContextBuilder.newBuilder().cloneFrom(layoutContextBuilder.build()).build();
                columnRenderResult.add(renderColumn(column.getDbId(), localContext));
            }
            layoutContextBuilder.setLayoutColumnList(columnRenderResult).addParams(layout.getParameters());
            return render(prototype.getTemplate(), layoutContextBuilder.build());
        } catch (Exception e) {
            if(null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(layoutId, layout, context, new RenderException(e));
            }
            return "<!-- 布局渲染失败，layoutId=" + layoutId + " -->";
        }
    }

    /**
     * 渲染列
     * @param columnId 列实例 ID
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public String renderColumn(long columnId, RenderContext context) {
        CmsColumnInstance column = null;
        try {
            column = cmsColumnInstanceService.getById(columnId);
            if (null == column) {
                throw new RenderException("列实例未找到");
            }
            CmsColumnPrototype prototype = cmsColumnPrototypeService.getById(column.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("列原型未找到");
            }
            RenderContextBuilder columnContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setColumnInstance(column).setColumnPrototype(prototype);
            List<String> moduleRenderResult = new ArrayList<String>(column.getModules().size());
            for (CmsModuleInstance module : column.getModules()) {
                RenderContext localContext = RenderContextBuilder.newBuilder().cloneFrom(columnContextBuilder.build()).build();
                moduleRenderResult.add(renderModule(module.getDbId(), localContext));
            }
            columnContextBuilder.setLayoutColumnList(moduleRenderResult).addParams(column.getParameters());
            return render(prototype.getTemplate(), columnContextBuilder.build());
        } catch (Exception e) {
            if(null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(columnId, column, context, new RenderException(e));
            }
            return "<!-- 列渲染失败，columnId=" + columnId + " -->";
        }
    }

    /**
     * 渲染模块
     * @param moduleId 模块实例 ID
     * @param context 渲染上下文
     * @return 渲染结果
     * @throws RenderException 渲染异常
     */
    public String renderModule(long moduleId, RenderContext context) throws RenderException {
        CmsModuleInstance module = null;
        try {
            module = cmsModuleInstanceService.getById(moduleId);
            if (null == module) {
                throw new RenderException("没找到模块实例");
            }
            CmsModulePrototype prototype = cmsModulePrototypeService.getById(module.getPrototypeId());
            if (null == prototype) {
                throw new RenderException("没找到模块原型");
            }
            RenderContextBuilder moduleContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setModuleInstance(module).setModulePrototype(prototype).addParams(module.getParameters());
            return render(prototype.getTemplate(), moduleContextBuilder.build());
        } catch (Exception e) {
            if(null != renderExceptionHandler) {
                return renderExceptionHandler.handleException(moduleId, module, context, new RenderException(e));
            }
            return "<!-- 模块渲染失败，moduleId=" + moduleId + " -->";
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
        CmsModuleInstance module = null;
        try {
            module = cmsModuleInstanceService.getById(moduleId);
            if (null == module) {
                return null;
            }
            CmsModulePrototype prototype = cmsModulePrototypeService.getById(module.getPrototypeId());
            if (null == prototype) {
                return null;
            }
            RenderContextBuilder moduleContextBuilder = RenderContextBuilder.newBuilder().cloneFrom(context)
                    .setModuleInstance(module).setModulePrototype(prototype).addParams(module.getParameters());
            return render(prototype.getFormTemplate(), moduleContextBuilder.build());
        } catch (Exception e) {
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
}