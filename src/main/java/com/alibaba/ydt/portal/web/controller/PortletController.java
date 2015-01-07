package com.alibaba.ydt.portal.web.controller;

import com.alibaba.ydt.portal.domain.CmsPageInstance;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple hello world controller.
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 * Created on 15/1/7 下午2:09.
 */
@Controller
@RequestMapping("portal/")
public class PortletController extends BaseController {

    @RequestMapping(value = "/view.html")
    public String view(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        long pageId = ServletRequestUtils.getLongParameter(request, "pageId", 1);
        CmsPageInstance page = cmsPageInstanceService.getById(pageId);
        RenderContext context = getCommonContext(request, response);
        context.setMode(RenderContext.RenderMode.product);
        modelMap.put("cmsPageBody", renderEngine.renderPage(page, context).getRenderContent());
        modelMap.put("allPageList", cmsPageInstanceService.getAll());
        modelMap.put("curPage", page);
        modelMap.putAll(context);
        return "template/portlet";
    }

    @RequestMapping(value = "/design.html")
    public String design(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        long pageId = ServletRequestUtils.getLongParameter(request, "pageId", 1);
        CmsPageInstance page = cmsPageInstanceService.getById(pageId);
        RenderContext context = getCommonContext(request, response);
        context.setMode(RenderContext.RenderMode.design);
        modelMap.put("cmsPageBody", renderEngine.renderPage(page, context).getRenderContent());
        modelMap.put("allPageList", cmsPageInstanceService.getAll());
        modelMap.put("curPage", page);
        modelMap.putAll(context);
        return "template/portlet";
    }
}