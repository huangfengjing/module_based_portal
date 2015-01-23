package com.alibaba.ydt.portal.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ydt.portal.common.SpringUtils;
import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.service.*;
import com.alibaba.ydt.portal.util.CmsUtils;
import com.alibaba.ydt.portal.util.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.Toolbox;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.FileFactoryConfiguration;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.apache.velocity.tools.view.ViewToolContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * CMS 控制器基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-12-30 下午4:49.
 */
abstract public class BaseController implements ServletContextAware, InitializingBean {

    @Autowired
    private CacheManager cacheManager;

    private Cache renderCache;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected VelocityEngine velocityEngine;

    @Autowired
    protected RenderEngine renderEngine;

    @Autowired
    protected CmsModuleInstanceService cmsModuleInstanceService;

    @Autowired
    protected CmsPageInstanceService cmsPageInstanceService;

    @Autowired
    protected CmsLayoutInstanceService cmsLayoutInstanceService;

    @Autowired
    protected CmsColumnInstanceService cmsColumnInstanceService;

    @Autowired
    protected CmsModulePrototypeService cmsModulePrototypeService;

    @Autowired
    protected CmsPagePrototypeService cmsPagePrototypeService;

    @Autowired
    protected CmsLayoutPrototypeService cmsLayoutPrototypeService;

    @Autowired
    protected CmsColumnPrototypeService cmsColumnPrototypeService;

    private static ToolboxFactory toolboxFactory = null;
    private static Toolbox globalToolbox = null;

    protected ServletContext servletContext;

    public String getUserId() {
        return "10029";
    }

    /**
     * 获取通用的上下文
     *
     * @param request HTTP 请求
     * @return 上下文
     */
    protected RenderContext getCommonContext(HttpServletRequest request, HttpServletResponse response) {
        RenderContext context = RenderContextBuilder.newBuilder().setMode(RenderContext.RenderMode.product)
                .setRequest(request)
                .setResponse(response)
                .setServletContext(servletContext).build();
        context.putAll(createVelocityContext(request, response));
        return context;
    }

    /**
     * 创建 velocity context
     *
     * @param request  请求
     * @param response 响应
     * @return 上下文
     */
    protected RenderContext createVelocityContext(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (null == toolboxFactory) {
                FileFactoryConfiguration cfg = new XmlFactoryConfiguration(true);
                cfg.read(new ClassPathResource("velocity/velocity_toolbox.xml").getInputStream());
                toolboxFactory = cfg.createFactory();
                globalToolbox = toolboxFactory.createToolbox(Scope.APPLICATION);
            }
            ViewToolContext velocityContext = new ViewToolContext(velocityEngine, request, response, servletContext);
            if (null != globalToolbox) {
                velocityContext.addToolbox(globalToolbox);
            }
            velocityContext.addToolbox(toolboxFactory.createToolbox(Scope.REQUEST));
            velocityContext.addToolbox(toolboxFactory.createToolbox(Scope.SESSION));
            RenderContext context = new RenderContext();
            for (String key : velocityContext.keySet()) {
                context.put(key, velocityContext.get(key));
            }
            return context;
        } catch (Exception e) {
            logger.error("创建 velocity context 失败", e);
            return new RenderContext();
        }
    }

    /**
     * 将 JSON 结构的布局转换为布局对象
     * JSON 结构：{
     * "布局原型ID,布局实例ID":
     * {
     * "列原型ID,列实例ID":"模块原型ID,模块实例ID|模块原型ID,模块实例ID|模块原型ID,模块实例ID",
     * "列原型ID,列实例ID":"模块原型ID,模块实例ID|模块原型ID,模块实例ID|模块原型ID,模块实例ID"
     * }
     * }
     * 实例如下所求：{
     * "1,1":
     * {
     * "1,1":"1,1|1,1|1,1",
     * "1,2":"1,1|1,1|2,0"
     * }
     * }
     *
     * @param layoutIdentifier 标识
     * @param jsonLayout       布局内容
     * @return 布局对象
     */
    protected CmsLayoutInstance parseJsonLayout(String layoutIdentifier, JSONObject jsonLayout) {
        CmsLayoutInstance layout = new CmsLayoutInstance();
        String[] idPairStrArray = layoutIdentifier.split(",");
        if (!validIdPair(idPairStrArray)) {
            return null;
        }
        layout.setPrototypeId(Long.valueOf(idPairStrArray[0]));
        layout.setDbId(Long.valueOf(idPairStrArray[1]));
        List<CmsColumnInstance> columns = new ArrayList<CmsColumnInstance>();
        for (Object tmp : jsonLayout.keySet()) {
            CmsColumnInstance column = new CmsColumnInstance();
            String colIdentifier = tmp.toString();
            idPairStrArray = colIdentifier.split(",");
            if (!validIdPair(idPairStrArray)) {
                logger.error("错误的列标识：" + colIdentifier);
                continue;
            }
            column.setPrototypeId(Long.valueOf(idPairStrArray[0]));
            column.setDbId(Long.valueOf(idPairStrArray[1]));

            String[] moduleIdentifiers = jsonLayout.getString(colIdentifier).split("\\|");
            for (String moduleIdentifier : moduleIdentifiers) {
                idPairStrArray = moduleIdentifier.split(",");
                if (!validIdPair(idPairStrArray)) {
                    logger.error("错误的列标识：" + moduleIdentifier);
                    continue;
                }
                CmsModuleInstance instance = new CmsModuleInstance();
                instance.setPrototypeId(Long.valueOf(idPairStrArray[0]));
                instance.setDbId(Long.valueOf(idPairStrArray[1]));
                column.getModules().add(instance);
            }
            columns.add(column);
        }
        Collections.sort(columns, new Comparator<CmsColumnInstance>() {
            @Override
            public int compare(CmsColumnInstance o1, CmsColumnInstance o2) {
                if (o1.getDbId() == 0) {
                    return 1;
                }
                if (o2.getDbId() == 0) {
                    return -1;
                }
                return 0;
            }
        });
        layout.getColumns().addAll(columns);
        return layout;
    }

    private boolean validIdPair(String[] idPairStrArray) {
        if (null == idPairStrArray || idPairStrArray.length != 2) {
            return false;
        }
        if (StringUtils.isBlank(idPairStrArray[0]) || !StringUtils.isNumeric(idPairStrArray[0])) {
            return Long.valueOf(idPairStrArray[0]) > 0;
        }
        if (StringUtils.isBlank(idPairStrArray[1]) || !StringUtils.isNumeric(idPairStrArray[1])) {
            return Long.valueOf(idPairStrArray[1]) >= 0;
        }
        return true;
    }

    /**
     * 获取实例对象
     *
     * @param instanceTypeTag 实例类型
     * @param instanceId      实例 ID
     * @return 实例对象
     */
    protected BaseCmsInstance getInstance(String instanceTypeTag, long instanceId) {
        BaseCmsInstance instance = null;
        if (CmsPageInstance.TYPE_TAG.equals(instanceTypeTag)) {
            instance = cmsPageInstanceService.getById(instanceId);
        } else if (CmsLayoutInstance.TYPE_TAG.equals(instanceTypeTag)) {
            instance = cmsLayoutInstanceService.getById(instanceId);
        } else if (CmsColumnInstance.TYPE_TAG.equals(instanceTypeTag)) {
            instance = cmsColumnInstanceService.getById(instanceId);
        } else if (CmsModuleInstance.TYPE_TAG.equals(instanceTypeTag)) {
            instance = cmsModuleInstanceService.getById(instanceId);
        }
        return instance;
    }

    /**
     * 获取实例对象
     *
     * @param instanceTypeTag 实例类型
     * @param prototypeId      实例 ID
     * @return 实例对象
     */
    protected BaseCmsPrototype getPrototype(String instanceTypeTag, long prototypeId) {
        BaseCmsPrototype prototype = null;
        if (CmsPageInstance.TYPE_TAG.equals(instanceTypeTag)) {
            prototype = cmsPagePrototypeService.getById(prototypeId);
        } else if (CmsLayoutInstance.TYPE_TAG.equals(instanceTypeTag)) {
            prototype = cmsLayoutPrototypeService.getById(prototypeId);
        } else if (CmsColumnInstance.TYPE_TAG.equals(instanceTypeTag)) {
            prototype = cmsColumnPrototypeService.getById(prototypeId);
        } else if (CmsModuleInstance.TYPE_TAG.equals(instanceTypeTag)) {
            prototype = cmsModulePrototypeService.getById(prototypeId);
        }
        return prototype;
    }

    /**
     * 清除缓存
     *
     * @param page     页面对象
     * @param instance 实例对象
     * @param context  上下文环境
     */
    protected void evictCache(CmsPageInstance page, BaseCmsInstance instance, RenderContext context) {
        boolean find = false;
        if (instance instanceof CmsModuleInstance) {
            for (CmsLayoutInstance layout : page.getLayouts()) {
                for (CmsColumnInstance column : layout.getColumns()) {
                    for (CmsModuleInstance module : column.getModules()) {
                        if (module.getDbId() == instance.getDbId()) {
                            // 清除列、布局的缓存
                            evictCache(column, context);
                            evictCache(layout, context);
                            find = true;
                            break;
                        }
                    }
                    if (find) {
                        break;
                    }
                }
                if (find) {
                    break;
                }
            }
        } else if (instance instanceof CmsColumnInstance) {
            for (CmsLayoutInstance layout : page.getLayouts()) {
                for (CmsColumnInstance column : layout.getColumns()) {
                    if (column.getDbId() == instance.getDbId()) {
                        // 清除布局的缓存
                        evictCache(layout, context);
                        find = true;

                        // 清除模块的缓存
                        for (CmsModuleInstance module : column.getModules()) {
                            evictCache(module, context);
                        }

                        break;
                    }
                }
                if (find) {
                    break;
                }
            }
        }
        evictCache(instance, context);
    }

    private void evictCache(BaseCmsInstance instance, RenderContext context) {
        String tag = instance.getInstanceTypeTag();
        String cacheType = CmsUtils.isModuleTag(tag) ? RenderEngine.RENDER_CACHE_TYPE_FOR_MODULE
                : (CmsUtils.isColumnTag(tag) ? RenderEngine.RENDER_CACHE_TYPE_FOR_COLUMN
                : (CmsUtils.isLayoutTag(tag) ? RenderEngine.RENDER_CACHE_TYPE_FOR_LAYOUT : RenderEngine.RENDER_CACHE_TYPE_FOR_PAGE));
        // 删除各个模式下的缓存
        RenderContext tmp = context.clone();
        tmp.setMode(RenderContext.RenderMode.design);
        renderCache.evict(CmsUtils.generateCacheKey(cacheType, instance.getDbId(), tmp));
        tmp.setMode(RenderContext.RenderMode.preview);
        renderCache.evict(CmsUtils.generateCacheKey(cacheType, instance.getDbId(), tmp));
        tmp.setMode(RenderContext.RenderMode.product);
        renderCache.evict(CmsUtils.generateCacheKey(cacheType, instance.getDbId(), tmp));
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null != cacheManager) {
            renderCache = cacheManager.getCache(RenderEngine.RENDER_CACHE_NAME);
            if (null == renderCache) {
                throw new BeanCreationException("创建 CmsCacheSupportService 失败，找不到缓存，请检查缓存配置项！");
            }
        }
    }
}
