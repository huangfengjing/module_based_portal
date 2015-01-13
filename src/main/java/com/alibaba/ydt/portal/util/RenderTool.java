package com.alibaba.ydt.portal.util;

import com.alibaba.ydt.portal.common.PriorityComparator;
import com.alibaba.ydt.portal.common.SpringUtils;
import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.CmsModulePrototype;
import com.alibaba.ydt.portal.exception.RenderException;
import com.alibaba.ydt.portal.service.*;
import org.apache.velocity.VelocityContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 渲染工具，用来处理 CMS 相关的内容
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-12-30 下午4:00.
 */
public class RenderTool extends BaseRequestTool {

    private static CmsPagePrototypeService cmsPagePrototypeService;
    private static CmsLayoutPrototypeService cmsLayoutPrototypeService;
    private static CmsColumnPrototypeService cmsColumnPrototypeService;
    private static CmsModulePrototypeService cmsModulePrototypeService;
    private static CmsPageInstanceService cmsPageInstanceService;
    private static CmsLayoutInstanceService cmsLayoutInstanceService;
    private static CmsColumnInstanceService cmsColumnInstanceService;
    private static CmsModuleInstanceService cmsModuleInstanceService;

    private static RenderExceptionHandler renderExceptionHandler;

    private static RenderEngine renderEngine;

    @Override
    public void init(Object obj) throws Exception {
        super.init(obj);

        if (null == velocityContext) {
            velocityContext = new VelocityContext();
        }

        if (null == cmsPagePrototypeService) {
            cmsPagePrototypeService = SpringUtils.getBean(CmsPagePrototypeService.class);
        }
        if (null == cmsLayoutPrototypeService) {
            cmsLayoutPrototypeService = SpringUtils.getBean(CmsLayoutPrototypeService.class);
        }
        if (null == cmsColumnPrototypeService) {
            cmsColumnPrototypeService = SpringUtils.getBean(CmsColumnPrototypeService.class);
        }
        if (null == cmsModulePrototypeService) {
            cmsModulePrototypeService = SpringUtils.getBean(CmsModulePrototypeService.class);
        }
        if (null == cmsPageInstanceService) {
            cmsPageInstanceService = SpringUtils.getBean(CmsPageInstanceService.class);
        }
        if (null == cmsLayoutInstanceService) {
            cmsLayoutInstanceService = SpringUtils.getBean(CmsLayoutInstanceService.class);
        }
        if (null == cmsColumnInstanceService) {
            cmsColumnInstanceService = SpringUtils.getBean(CmsColumnInstanceService.class);
        }
        if (null == cmsModuleInstanceService) {
            cmsModuleInstanceService = SpringUtils.getBean(CmsModuleInstanceService.class);
        }
    }

    /**
     * 渲染全局模块
     *
     * @param prototypeId 模块原型 ID
     * @return 渲染结果
     */
    public String renderGlobalModule(Number prototypeId) {
        return renderGlobalModule(prototypeId.longValue(), createContext());
    }

    /**
     * 渲染全局模块
     *
     * @param prototypeId 模块原型 ID
     * @param context       上下文环境
     * @return 渲染结果
     */
    public String renderGlobalModule(long prototypeId, RenderContext context) {
        return renderGlobalModule(cmsModulePrototypeService.getById(prototypeId), context);
    }

    /**
     * 渲染全局模块
     *
     * @param prototypeName 模块原型名字
     * @return 渲染结果
     */
    public String renderGlobalModule(String prototypeName) {
        return renderGlobalModule(prototypeName, createContext());
    }

    /**
     * 渲染全局模块
     *
     * @param prototypeName 模块原型名字
     * @param context       上下文环境
     * @return 渲染结果
     */
    public String renderGlobalModule(String prototypeName, RenderContext context) {
        return renderGlobalModule(cmsModulePrototypeService.getUniqueByProperty("name", prototypeName), context);
    }

    /**
     * 渲染全局模块
     *
     * @param prototype 模块原型
     * @param context       上下文环境
     * @return 渲染结果
     */
    public String renderGlobalModule(CmsModulePrototype prototype, RenderContext context) {
        try {
            if (null == prototype) {
                throw new RenderException("模块原型不能为空");
            }
            if (!prototype.isGlobal()) {
                throw new RenderException("该模块原型并不是一个全局模块，请确认！prototype: " + prototype);
            }
            CmsModuleInstance instance = cmsModuleInstanceService.getUniqueByProperty("prototypeId", prototype.getDbId());
            if (null == instance) {
                throw new RenderException("未找到实例，原型：" + prototype);
            }
            if (null == renderEngine) {
                renderEngine = SpringUtils.getBean(RenderEngine.class);
            }
            return renderEngine.renderModule(instance, context).getRenderContent();
        } catch (Exception e) {
            if (null == renderExceptionHandler) {
                renderExceptionHandler = getHighestPriorityExceptionHandler();
            }
            if (null == renderExceptionHandler) {
                return "<!-- 未找到模块原型：" + prototype + " -->";
            } else {
                return renderExceptionHandler.handleException(null, context, new RenderException(e)).getRenderContent();
            }
        }
    }


    /**
     * 创建一个默认的基于 request, response 的上下文环境
     *
     * @return 上下文环境
     */
    public RenderContext createContext() {
        RenderContext context = RenderContextBuilder.newBuilder().setMode(RenderContext.RenderMode.product)
                .setRequest(request)
                .setResponse(response)
                .setServletContext(servletContext).build();
        if(null != velocityContext) {
            for (Object key : velocityContext.getKeys()) {
                context.put(key.toString(), velocityContext.get(key.toString()));
            }
        }
        return context;
    }

    /**
     * 获取优先级最高的异常处理器
     *
     * @return 优先级最高的异常处理器
     */
    private RenderExceptionHandler getHighestPriorityExceptionHandler() {
        List<RenderExceptionHandler> renderExceptionHandlers = new ArrayList<RenderExceptionHandler>();
        renderExceptionHandlers.addAll(SpringUtils.getBeans(RenderExceptionHandler.class));
        if (renderExceptionHandlers.isEmpty()) {
            return null;
        }
        Collections.sort(renderExceptionHandlers, new PriorityComparator(PriorityComparator.ORDER_DIR_DESC));
        return renderExceptionHandlers.get(0);
    }
}
