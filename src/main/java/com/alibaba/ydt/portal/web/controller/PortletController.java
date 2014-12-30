package com.alibaba.ydt.portal.web.controller;

import com.alibaba.ydt.portal.common.SpringUtils;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import com.alibaba.ydt.portal.service.RenderEngine;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Simple hello world controller.
 * Presents basic usage of SpringMVC and Velocity.
 *
 * @author pmendelski
 */
@Controller
@RequestMapping("portal/")
public class PortletController implements ServletContextAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private RenderEngine renderEngine;

    private ServletContext servletContext;

    private static ToolboxFactory toolboxFactory = null;
    private static Toolbox globalToolbox = null;

    @RequestMapping(value = "/index.html")
    public String portlet(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        RenderContext context = getCommonContext(request, response);
        context.setMode(RenderContext.RenderMode.design);
        modelMap.put("cmsPageBody", renderEngine.renderPage(1L, context).getRenderContent());
        modelMap.putAll(context);
        return "template/portlet";
    }


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

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}