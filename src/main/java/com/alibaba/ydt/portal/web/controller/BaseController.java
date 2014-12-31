package com.alibaba.ydt.portal.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ydt.portal.common.SpringUtils;
import com.alibaba.ydt.portal.domain.CmsColumnInstance;
import com.alibaba.ydt.portal.domain.CmsLayoutInstance;
import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import com.alibaba.ydt.portal.service.RenderEngine;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * CMS 控制器基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-12-30 下午4:49.
 */
abstract public class BaseController implements ServletContextAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected VelocityEngine velocityEngine;

    @Autowired
    protected RenderEngine renderEngine;

    private static ToolboxFactory toolboxFactory = null;
    private static Toolbox globalToolbox = null;

    protected ServletContext servletContext;

    /**
     * 获取通用的上下文
     *
     * @param request  HTTP 请求
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
                cfg.read(SpringUtils.getApplicationContext().getResource("/WEB-INF/velocity_toolbox.xml").getInputStream());
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
        } catch (IOException e) {
            logger.error("创建 velocity context 失败", e);
            return null;
        }
    }

    /**
     * 将 JSON 结构的布局转换为布局对象
     * JSON 结构：{
     *              "布局原型ID,布局实例ID":
     *              {
     *                  "列原型ID,列实例ID":"模块原型ID,模块实例ID|模块原型ID,模块实例ID|模块原型ID,模块实例ID",
     *                  "列原型ID,列实例ID":"模块原型ID,模块实例ID|模块原型ID,模块实例ID|模块原型ID,模块实例ID"
     *              }
     *           }
     * 实例如下所求：{
     *              "1,1":
     *              {
     *                  "1,1":"1,1|1,1|1,1",
     *                  "1,2":"1,1|1,1|2,0"
     *              }
     *            }
     *
     * @param layoutIdentifier 标识
     * @param jsonLayout       布局内容
     * @return 布局对象
     */
    protected CmsLayoutInstance parseJsonLayout(String layoutIdentifier, JSONObject jsonLayout) {
        CmsLayoutInstance layout = new CmsLayoutInstance();
        String[] idPairStrArray = layoutIdentifier.split(",");
        if(!validIdPair(idPairStrArray)) {
            return null;
        }
        layout.setPrototypeId(Long.valueOf(idPairStrArray[0]));
        layout.setPrototypeId(Long.valueOf(idPairStrArray[1]));
        for (Object tmp : jsonLayout.keySet()) {
            CmsColumnInstance column = new CmsColumnInstance();
            String colIdentifier = tmp.toString();
            idPairStrArray = colIdentifier.split(",");
            if(!validIdPair(idPairStrArray)) {
                logger.error("错误的列标识：" + colIdentifier);
                continue;
            }
            column.setPrototypeId(Long.valueOf(idPairStrArray[0]));
            column.setPrototypeId(Long.valueOf(idPairStrArray[1]));

            String[] moduleIdentifiers = jsonLayout.getString(colIdentifier).split("\\|");
            for (String moduleIdentifier : moduleIdentifiers) {
                idPairStrArray = moduleIdentifier.split(",");
                if(!validIdPair(idPairStrArray)) {
                    logger.error("错误的列标识：" + moduleIdentifier);
                    continue;
                }
                CmsModuleInstance instance = new CmsModuleInstance();
                instance.setPrototypeId(Long.valueOf(idPairStrArray[0]));
                instance.setDbId(Long.valueOf(idPairStrArray[1]));
                column.getModules().add(instance);
            }
            layout.getColumns().add(column);
        }
        return layout;
    }

    private boolean validIdPair(String[] idPairStrArray) {
        if(null == idPairStrArray || idPairStrArray.length != 2) {
            return false;
        }
        if(StringUtils.isBlank(idPairStrArray[0]) || !StringUtils.isNumeric(idPairStrArray[0])) {
            return Long.valueOf(idPairStrArray[0]) >= 0;
        }
        if(StringUtils.isBlank(idPairStrArray[1]) || !StringUtils.isNumeric(idPairStrArray[1])) {
            return Long.valueOf(idPairStrArray[1]) >= 0;
        }
        return true;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
