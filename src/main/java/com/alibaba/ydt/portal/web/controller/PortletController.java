package com.alibaba.ydt.portal.web.controller;

import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple hello world controller.
 * Presents basic usage of SpringMVC and Velocity.
 *
 * @author pmendelski
 */
@Controller
@RequestMapping("portal/")
public class PortletController extends BaseController {

    @RequestMapping(value = "/index.html")
    public String index(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        RenderContext context = getCommonContext(request, response);
        context.setMode(RenderContext.RenderMode.product);
        modelMap.put("cmsPageBody", renderEngine.renderPage(1L, context).getRenderContent());
        modelMap.putAll(context);
        return "template/portlet";
    }

    @RequestMapping(value = "/design.html")
    public String design(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        RenderContext context = getCommonContext(request, response);
        context.setMode(RenderContext.RenderMode.design);
        modelMap.put("cmsPageBody", renderEngine.renderPage(1L, context).getRenderContent());
        modelMap.putAll(context);
        return "template/portlet";
    }
}