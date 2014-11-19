package com.doleje.portlet.service;

import com.doleje.portlet.domain.CmsModuleInstance;
import com.doleje.portlet.domain.CmsModulePrototype;
import com.doleje.portlet.domain.CmsPageInstance;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * RenderContext Builder
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-18 下午2:54.
 */
public class RenderContextBuilder {

    private RenderContext renderContext;

    public static RenderContextBuilder newBuilder() {
        RenderContextBuilder builder = new RenderContextBuilder();
        builder.renderContext = new RenderContext();
        return builder;
    }

    public RenderContextBuilder setMode(RenderContext.RenderMode mode) {
        renderContext.setMode(mode);
        return this;
    }

    public RenderContextBuilder setRequest(HttpServletRequest request) {
        renderContext.put(RenderContext.RENDER_REQUEST_KEY, request);
        return this;
    }

    public RenderContextBuilder setResponse(HttpServletResponse response) {
        renderContext.put(RenderContext.RENDER_RESPONSE_KEY, response);
        return this;
    }

    public RenderContextBuilder setServletContext(ServletContext servletContext) {
        renderContext.put(RenderContext.RENDER_SERVLET_CONTEXT_KEY, servletContext);
        return this;
    }

    public RenderContextBuilder setCmsPage(CmsPageInstance page) {
        renderContext.put(RenderContext.PAGE_KEY, page);
        return this;
    }

    public RenderContextBuilder setModInstance(CmsModuleInstance mod) {
        renderContext.put(RenderContext.MODULE_INSTANCE_KEY, mod);
        return this;
    }

    public RenderContextBuilder setModPrototype(CmsModulePrototype prototype) {
        renderContext.put(RenderContext.MODULE_PROTOTYPE_KEY, prototype);
        return this;
    }

    public RenderContextBuilder setModInstanceId(long instanceId) {
        renderContext.put(RenderContext.MODULE_INSTANCE_ID_KEY, instanceId);
        return this;
    }

    public RenderContextBuilder setPageHeaderContent(String headerContent) {
        renderContext.put(RenderContext.PAGE_HEADER_CONTENT_KEY, headerContent);
        return this;
    }

    public RenderContextBuilder setPageFooterContent(String footerContent) {
        renderContext.put(RenderContext.PAGE_FOOTER_CONTENT_KEY, footerContent);
        return this;
    }

    public RenderContextBuilder addParam(String key, Object value) {
        renderContext.addToParam(key, value);
        return this;
    }

    public RenderContextBuilder addParams(Map<String, Object> params) {
        if(null != params && !params.isEmpty()) {
            for(String key : params.keySet()) {
                addParam(key, params.get(key));
            }
        }
        return this;
    }

    public RenderContextBuilder addEnv(String key, Object value) {
        renderContext.addToEnv(key, value);
        return this;
    }

    public RenderContextBuilder addEnvs(Map<String, Object> params) {
        if(null != params && !params.isEmpty()) {
            for(String key : params.keySet()) {
                addEnv(key, params.get(key));
            }
        }
        return this;
    }

    public RenderContext build() {
        return renderContext;
    }
}
