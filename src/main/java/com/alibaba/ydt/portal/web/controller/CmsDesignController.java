package com.alibaba.ydt.portal.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.domain.common.AjaxResult;
import com.alibaba.ydt.portal.service.CmsModulePrototypeService;
import com.alibaba.ydt.portal.service.ParameterGather;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import com.alibaba.ydt.portal.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private CmsModulePrototypeService cmsModulePrototypeService;

    @Autowired
    private List<ParameterGather> parameterGathers = new ArrayList<ParameterGather>();

    /**
     * 浏览前台页面
     *
     * @param prototypeId 模块原型 ID
     * @param instanceId  模块实例 ID
     * @return 页面 VM
     */
    @RequestMapping("/render-comp.html")
    public AjaxResult renderComp(long prototypeId, String instanceTypeTag,
                                 @RequestParam(value = "instanceId", defaultValue = "0") long instanceId, HttpServletRequest request, HttpServletResponse response) {
        if (prototypeId <= 0) {
            return AjaxResult.rawResult("<!-- 请先指定模块原型 -->");
        }
        BaseCmsInstance instance = null;
        if (instanceId <= 0) {
            if (isModuleTag(instanceTypeTag)) {
                instance = new CmsModuleInstance();
            } else if (isColumnTag(instanceTypeTag)) {
                instance = new CmsColumnInstance();
            } else if (isLayoutTag(instanceTypeTag)) {
                instance = new CmsLayoutInstance();
            } else if (isPageTag(instanceTypeTag)) {
                instance = new CmsPageInstance();
            }
            if(null != instance) {
                instance.setPrototypeId(prototypeId);
            }
        } else {
            instance = getInstance(instanceTypeTag, instanceId);
        }
        if (null == instance) {
            return AjaxResult.rawResult("<!-- 找不到您要渲染的模块 -->");
        }
        RenderContext context = getCommonContext(request, response);
        context.setMode(RenderContext.RenderMode.design);
        RenderContextBuilder builder = RenderContextBuilder.newBuilder().mergeContext(context);
        if (isModuleTag(instanceTypeTag) && instance instanceof CmsModuleInstance) {
            builder.setModuleInstance((CmsModuleInstance) instance);
            return AjaxResult.rawResult(renderEngine.renderModule((CmsModuleInstance) instance, builder.build()).getRenderContent());
        } else if (isColumnTag(instanceTypeTag) && instance instanceof CmsColumnInstance) {
            builder.setColumnInstance((CmsColumnInstance) instance);
            return AjaxResult.rawResult(renderEngine.renderColumn((CmsColumnInstance) instance, builder.build()).getRenderContent());
        } else if (isLayoutTag(instanceTypeTag) && instance instanceof CmsLayoutInstance) {
            builder.setLayoutInstance((CmsLayoutInstance) instance);
            return AjaxResult.rawResult(renderEngine.renderLayout((CmsLayoutInstance) instance, builder.build()).getRenderContent());
        } else if (isPageTag(instanceTypeTag) && instance instanceof CmsPageInstance) {
            builder.setPageInstance((CmsPageInstance) instance);
            return AjaxResult.rawResult(renderEngine.renderPage(instance.getInstanceId(), builder.build()).getRenderContent());
        }
        return AjaxResult.errorResult();
    }

    /**
     * 渲染模块表单
     *
     * @param pageId     模块所在的页面 ID
     * @param instanceId 模块实例 ID
     * @param request    请求
     * @param response   响应
     * @return 模块表单页面
     */
    @RequestMapping("/render-comp-form.html")
    public AjaxResult renderCompForm(long pageId, long instanceId, String instanceTypeTag, HttpServletRequest request, HttpServletResponse response) {
        RenderContext context = getCommonContext(request, response);
        context.setMode(RenderContext.RenderMode.design);
        return AjaxResult.rawResult(renderEngine.renderModuleForm(instanceTypeTag, instanceId, context));
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
     * "布局原型ID,布局实例ID":
     * {
     * "列原型ID,列实例ID":"模块原型ID,模块实例ID|模块原型ID,模块实例ID|模块原型ID,模块实例ID",
     * "列原型ID,列实例ID":"模块原型ID,模块实例ID|模块原型ID,模块实例ID|模块原型ID,模块实例ID"
     * },
     * "布局原型ID,布局实例ID":
     * {
     * "列原型ID,列实例ID":"模块原型ID,模块实例ID|模块原型ID,模块实例ID|模块原型ID,模块实例ID",
     * "列原型ID,列实例ID":"模块原型ID,模块实例ID|模块原型ID,模块实例ID|模块原型ID,模块实例ID"
     * }
     * }
     * 实例如下所求：{
     * "1,1":
     * {
     * "1,1":"1,1|1,1|1,1",
     * "1,2":"1,1|1,1|2,0"
     * },
     * "1,2":
     * {
     * "1,1":"1,1|1,1|1,1",
     * "1,2":"1,1|1,1|2,0"
     * }
     * }
     *
     * @param pageLayout 页面布局
     * @return JSON 数据
     */
    @RequestMapping("/save-page.html")
    public AjaxResult savePage(long pageId, String pageLayout) {

        JSONObject jsonLayout = JSON.parseObject(pageLayout);
        List<CmsLayoutInstance> layouts = new ArrayList<CmsLayoutInstance>();
        for (Object key : jsonLayout.keySet()) {
            String layoutIdentifier = key.toString();
            layouts.add(parseJsonLayout(layoutIdentifier, jsonLayout.getJSONObject(layoutIdentifier)));
        }

        CmsPageInstance page = pageId >= 0 ? cmsPageInstanceService.getById(pageId) : new CmsPageInstance();
        if (null == page) {
            return AjaxResult.errorResult("找不到您要编辑的页面！");
        }
        page.setLayouts(layouts);
        return cmsPageInstanceService.savePageLayout(page) ? AjaxResult.successResult() : AjaxResult.errorResult();
    }


    /**
     * 渲染模块表单
     *
     * @param request 请求
     * @return 模块表单页面
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/save-comp-params.html")
    public AjaxResult saveCompParams(HttpServletRequest request, HttpServletResponse response) {
        try {
            String instanceTypeTag = ServletRequestUtils.getStringParameter(request, "instanceTypeTag", "");
            long instanceId = ServletRequestUtils.getLongParameter(request, "dbId", 0);
            if (instanceId == 0 || StringUtils.isBlank(instanceTypeTag)) {
                return AjaxResult.errorResult("找不到您要编辑的模块");
            }

            BaseCmsInstance instance = getInstance(instanceTypeTag, instanceId);
            if (null == instance) {
                return AjaxResult.errorResult("找不到您要编辑的组件");
            }


            CmsModulePrototype prototype = cmsModulePrototypeService.getById(instance.getPrototypeId());
            if (null == prototype) {
                return AjaxResult.errorResult("找不到您要编辑的组件原型");
            }
            List<ParameterGather> supportedGather = new ArrayList<ParameterGather>();
            for (ParameterGather gather : parameterGathers) {
                if (gather.support(prototype)) {
                    supportedGather.add(gather);
                }
            }

            // 优先级高的收集器收集的数据将覆盖低优先级的数据
            Map<String, ParameterValuePair> paramMap = new HashMap<String, ParameterValuePair>();
            for (ParameterGather gather : supportedGather) {
                List<ParameterValuePair> params = gather.gatherParams(prototype, instance, request);
                for (ParameterValuePair pair : params) {
                    paramMap.put(pair.getName(), pair);
                }
            }

            instance.setParamsWithList(paramMap.values());

            RenderContext context = getCommonContext(request, response);
            context.setMode(RenderContext.RenderMode.design);
            if (CmsPageInstance.TYPE_TAG.equals(instanceTypeTag) && instance instanceof CmsPageInstance) {
                cmsPageInstanceService.save((CmsPageInstance) instance);
            } else if (CmsLayoutInstance.TYPE_TAG.equals(instanceTypeTag) && instance instanceof CmsLayoutInstance) {
                cmsLayoutInstanceService.save((CmsLayoutInstance) instance);
            } else if (CmsColumnInstance.TYPE_TAG.equals(instanceTypeTag) && instance instanceof CmsColumnInstance) {
                cmsColumnInstanceService.save((CmsColumnInstance) instance);
            } else if (CmsModuleInstance.TYPE_TAG.equals(instanceTypeTag) && instance instanceof CmsModuleInstance) {
                cmsModuleInstanceService.save((CmsModuleInstance) instance);
            }
            evictCache(instance, context);

            AjaxResult renderResult = renderComp(prototype.getDbId(), instanceTypeTag, instanceId, request, response);
            if (renderResult.isSuccess()) {
                return AjaxResult.successResult().addData("compContent", renderResult.getRawData());
            } else {
                return AjaxResult.errorResult();
            }
        } catch (Exception e) {
            logger.error("保存模块参数错误！", e);
            return AjaxResult.errorResult();
        }
    }
}