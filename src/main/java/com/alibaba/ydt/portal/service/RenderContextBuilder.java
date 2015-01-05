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
@SuppressWarnings("unchecked")
public class RenderContextBuilder {

    private RenderContext renderContext;

    /**
     * 创建一个新的 BUILDER
     * @return BUILDER
     */
    public static RenderContextBuilder newBuilder() {
        RenderContextBuilder builder = new RenderContextBuilder();
        builder.renderContext = new RenderContext();
        return builder;
    }

    /**
     * 从一个 RenderContext 克隆
     * @param original 原始的 RenderContext
     * @return this
     */
    public RenderContextBuilder cloneFrom(RenderContext original) {
        this.renderContext = original.clone();
        return this;
    }

    /**
     * 合并一个 context
     * @param context 被合并的 context
     * @return this
     */
    public RenderContextBuilder mergeContext(RenderContext context) {
        if(null == context) {
            return this;
        }
        if(context.containsKey(RenderContext.RENDER_MOD_KEY)) {
            RenderContext.RenderMode mode = (RenderContext.RenderMode) context.get(RenderContext.RENDER_MOD_KEY);
            if(null != mode) {
                setMode(mode);
            }
        }
        if(context.containsKey(RenderContext.RENDER_ENV_KEY)) {
            ((Map<String, Object>)renderContext.get(RenderContext.RENDER_ENV_KEY)).putAll((Map<String, Object>) context.get(RenderContext.RENDER_ENV_KEY));
        }
        if(context.containsKey(RenderContext.MODULE_PARAMS_KEY)) {
            ((Map<String, Object>)renderContext.get(RenderContext.MODULE_PARAMS_KEY)).putAll((Map<String, Object>) context.get(RenderContext.MODULE_PARAMS_KEY));
        }
        if(context.containsKey(RenderContext.COLUMN_PARAMS_KEY)) {
            ((Map<String, Object>)renderContext.get(RenderContext.COLUMN_PARAMS_KEY)).putAll((Map<String, Object>) context.get(RenderContext.COLUMN_PARAMS_KEY));
        }
        if(context.containsKey(RenderContext.LAYOUT_PARAMS_KEY)) {
            ((Map<String, Object>)renderContext.get(RenderContext.LAYOUT_PARAMS_KEY)).putAll((Map<String, Object>) context.get(RenderContext.LAYOUT_PARAMS_KEY));
        }
        if(context.containsKey(RenderContext.PAGE_PARAMS_KEY)) {
            ((Map<String, Object>)renderContext.get(RenderContext.PAGE_PARAMS_KEY)).putAll((Map<String, Object>) context.get(RenderContext.PAGE_PARAMS_KEY));
        }
        return this;
    }

    /**
     * 设置模式
     * @param mode 模式
     * @return this
     */
    public RenderContextBuilder setMode(RenderContext.RenderMode mode) {
        renderContext.setMode(mode);
        return this;
    }

    /**
     * 设置当前登录用户信息
     * @param appUser 当前登录用户信息
     * @return 当前登录用户信息
     */
    public RenderContextBuilder setAppUser(AppUser appUser) {
        renderContext.put(RenderContext.APP_USER_KEY, appUser);
        return this;
    }

    /**
     * 设置 request
     * @param request request
     * @return this
     */
    public RenderContextBuilder setRequest(HttpServletRequest request) {
        renderContext.put(RenderContext.RENDER_REQUEST_KEY, request);
        return this;
    }

    /**
     * 设置 response
     * @param response response
     * @return this
     */
    public RenderContextBuilder setResponse(HttpServletResponse response) {
        renderContext.put(RenderContext.RENDER_RESPONSE_KEY, response);
        return this;
    }

    /**
     * 设置 servlet context
     * @param servletContext servlet context
     * @return this
     */
    public RenderContextBuilder setServletContext(ServletContext servletContext) {
        renderContext.put(RenderContext.RENDER_SERVLET_CONTEXT_KEY, servletContext);
        return this;
    }

    /**
     * 设置页面原型
     * @param prototype 页面原型
     * @return this
     */
    public RenderContextBuilder setPagePrototype(CmsPagePrototype prototype) {
        renderContext.put(RenderContext.PAGE_PROTOTYPE_KEY, prototype);
        return this;
    }

    /**
     * 设置页面实例
     * @param page 页面实例
     * @return this
     */
    public RenderContextBuilder setPageInstance(CmsPageInstance page) {
        renderContext.put(RenderContext.PAGE_INSTANCE_KEY, page);
        return this;
    }

    /**
     * 设置页面参数
     * @param params 页面参数
     * @return this
     */
    public RenderContextBuilder setPageParams(Map<String, Object> params) {
        renderContext.put(RenderContext.PAGE_PARAMS_KEY, params);
        return this;
    }

    /**
     * 设置布局原型
     * @param prototype 布局原型
     * @return this
     */
    public RenderContextBuilder setLayoutPrototype(CmsLayoutPrototype prototype) {
        renderContext.put(RenderContext.LAYOUT_PROTOTYPE_KEY, prototype);
        return this;
    }

    /**
     * 设置布局实例
     * @param layout 布局实例
     * @return this
     */
    public RenderContextBuilder setLayoutInstance(CmsLayoutInstance layout) {
        renderContext.put(RenderContext.LAYOUT_INSTANCE_KEY, layout);
        return this;
    }

    /**
     * 设置布局参数
     * @param params 布局参数
     * @return this
     */
    public RenderContextBuilder setLayoutParams(Map<String, Object> params) {
        renderContext.put(RenderContext.LAYOUT_PARAMS_KEY, params);
        return this;
    }

    /**
     * 设置列原型
     * @param prototype 列原型
     * @return this
     */
    public RenderContextBuilder setColumnPrototype(CmsColumnPrototype prototype) {
        renderContext.put(RenderContext.COLUMN_PROTOTYPE_KEY, prototype);
        return this;
    }

    /**
     * 设置列实例
     * @param column 列实例
     * @return this
     */
    public RenderContextBuilder setColumnInstance(CmsColumnInstance column) {
        renderContext.put(RenderContext.COLUMN_INSTANCE_KEY, column);
        return this;
    }

    /**
     * 设置列参数
     * @param params 列参数
     * @return this
     */
    public RenderContextBuilder setColumnParams(Map<String, Object> params) {
        renderContext.put(RenderContext.COLUMN_PARAMS_KEY, params);
        return this;
    }

    /**
     * 设置模块实例
     * @param mod 模块实例
     * @return this
     */
    public RenderContextBuilder setModuleInstance(CmsModuleInstance mod) {
        renderContext.put(RenderContext.MODULE_INSTANCE_KEY, mod);
        return this;
    }

    /**
     * 设置模块原型
     * @param prototype 模块原型
     * @return this
     */
    public RenderContextBuilder setModulePrototype(CmsModulePrototype prototype) {
        renderContext.put(RenderContext.MODULE_PROTOTYPE_KEY, prototype);
        return this;
    }

    /**
     * 设置模块参数
     * @param params 模块参数
     * @return this
     */
    public RenderContextBuilder setModuleParams(Map<String, Object> params) {
        renderContext.put(RenderContext.MODULE_PARAMS_KEY, params);
        return this;
    }

    /**
     * 设置页面中所有布局的渲染结果
     * @param layoutContents 页面中所有布局的渲染结果
     * @return this
     */
    public RenderContextBuilder setPageLayoutList(List<String> layoutContents) {
        renderContext.put(RenderContext.CMS_LAYOUT_LIST_KEY, layoutContents);
        return this;
    }

    /**
     * 设置布局中所有列的渲染结果
     * @param columnContents 布局中所有列的渲染结果
     * @return this
     */
    public RenderContextBuilder setLayoutColumnList(List<String> columnContents) {
        renderContext.put(RenderContext.CMS_COLUMN_LIST_KEY, columnContents);
        return this;
    }

    /**
     * 设置列中所有模块的渲染结果
     * @param moduleContents 列中所有模块的渲染结果
     * @return this
     */
    public RenderContextBuilder setColumnModuleList(List<String> moduleContents) {
        renderContext.put(RenderContext.CMS_MODULE_LIST_KEY, moduleContents);
        return this;
    }

    /**
     * 页面头部的渲染内容
     * @param headerContent 页面头部的渲染内容
     * @return this
     */
    public RenderContextBuilder setPageHeaderContent(String headerContent) {
        renderContext.put(RenderContext.PAGE_HEADER_CONTENT_KEY, headerContent);
        return this;
    }

    /**
     * 页面尾部的渲染内容
     * @param footerContent 页面尾部的渲染内容
     * @return this
     */
    public RenderContextBuilder setPageFooterContent(String footerContent) {
        renderContext.put(RenderContext.PAGE_FOOTER_CONTENT_KEY, footerContent);
        return this;
    }

    /**
     * 添加参数
     * @param instanceParamKey 实例对应的参数容器 KEY
     * @param key KEY
     * @param value VALUE
     * @return this
     */
    private RenderContextBuilder addParam(String instanceParamKey, String key, Object value) {
        renderContext.addToParam(instanceParamKey, key, value);
        return this;
    }

    /**
     * 添加模块参数
     * @param param 模块参数
     * @return this
     */
    public RenderContextBuilder addModuleParam(ParameterValuePair param) {
        renderContext.addToParam(RenderContext.MODULE_PARAMS_KEY, param.getName(), param.getValue());
        return this;
    }

    /**
     * 批量设置模块参数
     * @param params 模块参数
     * @return this
     */
    public RenderContextBuilder addModuleParams(List<ParameterValuePair> params) {
        return addParams(RenderContext.MODULE_PARAMS_KEY, params);
    }

    /**
     * 添加列参数
     * @param param 列参数
     * @return this
     */
    public RenderContextBuilder addColumnPram(ParameterValuePair param) {
        renderContext.addToParam(RenderContext.COLUMN_PARAMS_KEY, param.getName(), param.getValue());
        return this;
    }

    /**
     * 批量设置列参数
     * @param params 列参数
     * @return this
     */
    public RenderContextBuilder addColumnParams(List<ParameterValuePair> params) {
        return addParams(RenderContext.COLUMN_PARAMS_KEY, params);
    }

    /**
     * 添加布局参数
     * @param param 布局参数
     * @return this
     */
    public RenderContextBuilder addLayoutParam(ParameterValuePair param) {
        renderContext.addToParam(RenderContext.LAYOUT_PARAMS_KEY, param.getName(), param.getValue());
        return this;
    }

    /**
     * 批量设置布局参数
     * @param params 布局参数
     * @return this
     */
    public RenderContextBuilder addLayoutParams(List<ParameterValuePair> params) {
        return addParams(RenderContext.LAYOUT_PARAMS_KEY, params);
    }

    /**
     * 添加页面参数
     * @param param 页面参数
     * @return this
     */
    public RenderContextBuilder addPagePram(ParameterValuePair param) {
        renderContext.addToParam(RenderContext.PAGE_PARAMS_KEY, param.getName(), param.getValue());
        return this;
    }

    /**
     * 批量设置页面参数
     * @param params 页面参数
     * @return this
     */
    public RenderContextBuilder addPageParams(List<ParameterValuePair> params) {
        return addParams(RenderContext.PAGE_PARAMS_KEY, params);
    }

    private RenderContextBuilder addParams(String instanceParamKey, List<ParameterValuePair> params) {
        if(null != params && !params.isEmpty()) {
            for(ParameterValuePair param : params) {
                addParam(instanceParamKey, param.getName(), param.getValue());
            }
        }
        return this;
    }

    /**
     * 设置环境变量
     * @param key KEY
     * @param value VALUE
     * @return this
     */
    public RenderContextBuilder addEnv(String key, String value) {
        renderContext.addToEnv(key, value);
        return this;
    }

    /**
     * 批量设置环境变量
     * @param envs 变量
     * @return this
     */
    public RenderContextBuilder addEnvs(Map<String, String> envs) {
        if(null != envs && !envs.isEmpty()) {
            for(String key : envs.keySet()) {
                addEnv(key, envs.get(key));
            }
        }
        return this;
    }

    public RenderContext build() {
        return renderContext;
    }
}
