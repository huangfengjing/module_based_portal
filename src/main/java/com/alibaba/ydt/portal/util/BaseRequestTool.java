/**
 *
 */
package com.alibaba.ydt.portal.util;

import com.alibaba.ydt.portal.service.RenderContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.ViewContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Velocity base tool, provide a spring context, request, servlet, velocity engine, velocty context and so on.
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @since 2009-11-1 AM 12:27:09
 */
public abstract class BaseRequestTool {

    protected Log logger = LogFactory.getLog(BaseRequestTool.class);

    /**
     * Spring context
     */
    protected static ApplicationContext ctx;

    /**
     * Request
     */
    protected HttpServletRequest request;

    /**
     * Response
     */
    protected HttpServletResponse response;

    /**
     * Servlet context
     */
    protected ServletContext servletContext;

    /**
     * Velocity engine
     */
    protected VelocityEngine velocityEngine;

    /**
     * Velocity context
     */
    protected Context velocityContext;

    /**
     * Initialize tool context and property inject.
     *
     * @param obj view context
     */
    public void init(Object obj) throws Exception {

        if (obj instanceof ServletContext) {
            servletContext = (ServletContext) obj;
        } else if (obj instanceof ViewContext) {
            servletContext = ((ViewContext) obj).getServletContext();
            request = ((ViewContext) obj).getRequest();
            response = ((ViewContext) obj).getResponse();
            velocityContext = ((ViewContext) obj).getVelocityContext();
            velocityEngine = ((ViewContext) obj).getVelocityEngine();
        } else if (obj instanceof RenderContext) {
            request = (HttpServletRequest) ((RenderContext) obj).get(RenderContext.RENDER_REQUEST_KEY);
            response = (HttpServletResponse) ((RenderContext) obj).get(RenderContext.RENDER_RESPONSE_KEY);
        } else {
            logger.warn("Velocity工具 " + getClass() + " 初始化失败，obj 类型错误");
            return;
        }
        if (null == ctx) {
            ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        }
    }

    /**
     * Get spring context
     *
     * @return Spring context
     */
    protected ApplicationContext getApplicationContext() {
        return ctx;
    }

    /**
     * Get request object
     *
     * @return the request
     */
    protected HttpServletRequest getRequest() {
        return request;
    }

    /**
     * @return the response
     */
    protected HttpServletResponse getResponse() {
        return response;
    }

    /**
     * @return the servletContext
     */
    protected ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * @return the velocityEngine
     */
    protected VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    /**
     * @return the velocityContext
     */
    protected Context getVelocityContext() {
        return velocityContext;
    }
}
