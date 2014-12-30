package com.alibaba.ydt.portal.web.controller;

import com.alibaba.ydt.portal.common.SpringUtils;
import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.CmsModulePrototype;
import com.alibaba.ydt.portal.domain.common.AjaxResult;
import com.alibaba.ydt.portal.service.*;
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
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("cms/design")
public class CmsDesignController extends BaseController {

    @Autowired
    private CmsModuleInstanceService cmsModuleInstanceService;

    @Autowired
    private CmsPagePrototypeService cmsPagePrototypeService;

    /**
     * 浏览前台页面
     *
     * @param prototypeId 模块原型 ID
     * @param instanceId  模块实例 ID
     * @return 页面 VM
     */
    @RequestMapping("/render-module.html")
    public AjaxResult renderModule(long prototypeId, long instanceId, HttpServletRequest request, HttpServletResponse response) {
        if (prototypeId <= 0) {
            return AjaxResult.rawResult("<!-- 请先指定模块原型 -->");
        }
        CmsModuleInstance instance;
        if (instanceId <= 0) {
            instance = new CmsModuleInstance();
            instance.setPrototypeId(prototypeId);
        } else {
            instance = cmsModuleInstanceService.getById(instanceId);
        }
        if (null == instance) {
            return AjaxResult.rawResult("<!-- 找不到您要渲染的模块 -->");
        }
        RenderContext context = getCommonContext(request, response);
        context.setMode(RenderContext.RenderMode.design);
        return AjaxResult.rawResult(renderEngine.renderModule(instance, context).getRenderContent());
    }

    /**
     * 渲染模块表单
     *
     * @param pageId   模块所在的页面 ID
     * @param moduleId 模块实例 ID
     * @param request  请求
     * @param response 响应
     * @return 模块表单页面
     */
    @RequestMapping("/render-module-form.html")
    public AjaxResult renderModuleForm(long pageId, long moduleId, HttpServletRequest request, HttpServletResponse response) {
        RenderContext context = getCommonContext(request, response);
        context.setMode(RenderContext.RenderMode.design);
        return AjaxResult.rawResult(renderEngine.renderModuleForm(moduleId, context));
    }

    /**
     * 模块原型列表
     *
     * @param modelMap 模型 HOLDER
     * @return 模块原型列表页
     */
    @RequestMapping("/module-prototype-list.html")
    public String modulePrototypeList(ModelMap modelMap) {
        modelMap.put("cmsModuleList", cmsPagePrototypeService.getAll());
        return "cms/module_prototype_list";
    }
}