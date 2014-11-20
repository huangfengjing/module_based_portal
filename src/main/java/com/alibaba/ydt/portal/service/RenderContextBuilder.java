package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
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

    public RenderContextBuilder cloneFrom(RenderContext original) {
        this.renderContext = original.clone();
        return this;
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

    public RenderContextBuilder setPagePrototype(CmsPagePrototype prototype) {
        renderContext.put(RenderContext.PAGE_PROTOTYPE_KEY, prototype);
        return this;
    }

    public RenderContextBuilder setPageInstance(CmsPageInstance page) {
        renderContext.put(RenderContext.PAGE_INSTANCE_KEY, page);
        return this;
    }

    public RenderContextBuilder setPageParams(Map<String, Object> params) {
        renderContext.put(RenderContext.PAGE_PARAMS_KEY, params);
        return this;
    }

    public RenderContextBuilder setLayoutPrototype(CmsLayoutPrototype prototype) {
        renderContext.put(RenderContext.LAYOUT_PROTOTYPE_KEY, prototype);
        return this;
    }

    public RenderContextBuilder setLayoutInstance(CmsLayoutInstance layout) {
        renderContext.put(RenderContext.LAYOUT_INSTANCE_KEY, layout);
        return this;
    }

    public RenderContextBuilder setLayoutParams(Map<String, Object> params) {
        renderContext.put(RenderContext.LAYOUT_PARAMS_KEY, params);
        return this;
    }

    public RenderContextBuilder setColumnPrototype(CmsColumnPrototype prototype) {
        renderContext.put(RenderContext.COLUMN_PROTOTYPE_KEY, prototype);
        return this;
    }

    public RenderContextBuilder setColumnInstance(CmsColumnInstance column) {
        renderContext.put(RenderContext.COLUMN_INSTANCE_KEY, column);
        return this;
    }

    public RenderContextBuilder setColumnParams(Map<String, Object> params) {
        renderContext.put(RenderContext.COLUMN_PARAMS_KEY, params);
        return this;
    }

    public RenderContextBuilder setModuleInstance(CmsModuleInstance mod) {
        renderContext.put(RenderContext.MODULE_INSTANCE_KEY, mod);
        return this;
    }

    public RenderContextBuilder setModulePrototype(CmsModulePrototype prototype) {
        renderContext.put(RenderContext.MODULE_PROTOTYPE_KEY, prototype);
        return this;
    }

    public RenderContextBuilder setModuleParams(Map<String, Object> params) {
        renderContext.put(RenderContext.MODULE_PARAMS_KEY, params);
        return this;
    }

    public RenderContextBuilder setPageLayoutList(List<String> layoutContents) {
        renderContext.put(RenderContext.CMS_LAYOUT_LIST_KEY, layoutContents);
        return this;
    }

    public RenderContextBuilder setLayoutColumnList(List<String> columnContents) {
        renderContext.put(RenderContext.CMS_COLUMN_LIST_KEY, columnContents);
        return this;
    }

    public RenderContextBuilder setColumnModuleList(List<String> moduleContents) {
        renderContext.put(RenderContext.CMS_MODULE_LIST_KEY, moduleContents);
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

    public RenderContextBuilder addParams(List<ParameterValuePair> params) {
        if(null != params && !params.isEmpty()) {
            for(ParameterValuePair param : params) {
                addParam(param.getName(), param.getValue());
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
