package com.alibaba.ydt.portal.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.ydt.portal.common.SpringUtils;
import com.alibaba.ydt.portal.domain.CmsLayoutInstance;
import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.CmsModulePrototype;
import com.alibaba.ydt.portal.domain.CmsPageInstance;
import com.alibaba.ydt.portal.domain.common.AjaxResult;
import com.alibaba.ydt.portal.service.*;
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
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private CmsModulePrototypeService cmsModulePrototypeService;

    @Autowired
    private CmsPageInstanceService cmsPageInstanceService;

    /**
     * 浏览前台页面
     *
     * @param prototypeId 模块原型 ID
     * @param instanceId  模块实例 ID
     * @return 页面 VM
     */
    @RequestMapping("/render-module.html")
    public AjaxResult renderModule(long prototypeId,
                                   @RequestParam(value = "instanceId", defaultValue = "0")long instanceId, HttpServletRequest request, HttpServletResponse response) {
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
        RenderContextBuilder builder = RenderContextBuilder.newBuilder().mergeContext(context).setModuleInstance(instance);
        return AjaxResult.rawResult(renderEngine.renderModule(instance, builder.build()).getRenderContent());
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
        modelMap.put("cmsModuleList", cmsModulePrototypeService.getAll());
        return "cms/module_prototype_list";
    }


    /**
     * 保存页面结构，布局数据结构为：
     * JSON 结构：{
     *              "布局原型ID,布局实例ID":
     *              {
     *                  "列原型ID,列实例ID":"模块原型ID,模块实例ID|模块原型ID,模块实例ID|模块原型ID,模块实例ID",
     *                  "列原型ID,列实例ID":"模块原型ID,模块实例ID|模块原型ID,模块实例ID|模块原型ID,模块实例ID"
     *              },
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
     *              },
     *              "1,2":
     *              {
     *                  "1,1":"1,1|1,1|1,1",
     *                  "1,2":"1,1|1,1|2,0"
     *              }
     *            }
     *
     * @param delModules      删除的模块实例 ID 列表
     * @param pageLayout      页面布局
     * @return JSON 数据
     */
    @RequestMapping("/save-page.html")
    public AjaxResult savePage(long pageId, String delModules, String pageLayout) {

        JSONObject jsonLayout = JSON.parseObject(pageLayout);
        List<CmsLayoutInstance> layouts = new ArrayList<CmsLayoutInstance>();
        for (Object key : jsonLayout.keySet()) {
            String layoutIdentifier = key.toString();
            layouts.add(parseJsonLayout(layoutIdentifier, jsonLayout.getJSONObject(layoutIdentifier)));
        }
        // 删除的模块列表
        List<Long> delModuleIds = new ArrayList<Long>();
        for (String tmp : delModules.split(",")) {
            if (StringUtils.isNotBlank(tmp) && StringUtils.isNumeric(tmp)) {
                delModuleIds.add(Long.valueOf(tmp));
            }
        }

        CmsPageInstance page = pageId >= 0 ? cmsPageInstanceService.getById(pageId) : new CmsPageInstance();
        if (null == page) {
            return AjaxResult.errorResult("找不到您要编辑的页面！");
        }
        page.setLayouts(layouts);
        return cmsPageInstanceService.savePageLayout(page, delModuleIds) ? AjaxResult.successResult() : AjaxResult.errorResult();
    }

}