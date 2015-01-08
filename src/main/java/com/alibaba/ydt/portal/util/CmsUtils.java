package com.alibaba.ydt.portal.util;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.service.RenderContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * <p>
 * Cms 工具类，用于解析 XML 与对象之间的映射
 * </p>
 * Time: 13-1-4 下午4:44
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public abstract class CmsUtils {

    private static Log logger = LogFactory.getLog(CmsUtils.class);

    /**
     * 解析 Page XML 内容为 CMS PAGE 对象
     *
     * @param pageContent Page XML 内容
     * @return CMS PAGE 对象
     */
    public static CmsPageInstance parsePage(String pageContent) {
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new ByteArrayInputStream(pageContent.getBytes()));
            Element pageElem = doc.getRootElement();
            CmsPageInstance page = new CmsPageInstance();
            page.setPrototypeId(Long.valueOf(pageElem.attributeValue("prototype-id")));
            String instId = pageElem.attributeValue("instance-id");
            if(StringUtils.isNotBlank(instId) && StringUtils.isNumeric(instId)) {
                page.setDbId(Long.valueOf(instId));
            }
            for (Element layoutElem : (List<Element>) pageElem.elements("layout")) {
                page.getLayouts().add(parseLayout(layoutElem));
            }
            return page;
        } catch (DocumentException e) {
            logger.error("解析布局失败", e);
            return null;
        }
    }

    /**
     * 将 layout 节点解析为 layout 实例对象
     * @param layoutElem layout 节点
     * @return layout 实例对象
     */
    public static CmsLayoutInstance parseLayout(Element layoutElem) {
        try {
            CmsLayoutInstance layout = new CmsLayoutInstance();
            layout.setPrototypeId(Long.valueOf(layoutElem.attributeValue("prototype-id")));
            layout.setDbId(Long.valueOf(layoutElem.attributeValue("instance-id")));
            for (Element columnElem : (List<Element>) layoutElem.elements("column")) {
                CmsColumnInstance column = parseColumn(columnElem);
                if(null != column) {
                    layout.getColumns().add(column);
                }
            }
            return layout;
        } catch (Exception e) {
            logger.error("解析列数据失败：" + layoutElem, e);
            return null;
        }
    }

    /**
     * 将 column 节点解析为 column 实例对象
     * @param columnElem column 节点
     * @return column 实例对象
     */
    public static CmsColumnInstance parseColumn(Element columnElem) {
        try {
            CmsColumnInstance column = new CmsColumnInstance();
            column.setPrototypeId(Long.valueOf(columnElem.attributeValue("prototype-id")));
            column.setDbId(Long.valueOf(columnElem.attributeValue("instance-id")));
            for (Element moduleElem : (List<Element>) columnElem.elements("module")) {
                CmsModuleInstance module = parseModule(moduleElem);
                if(null != module) {
                    column.getModules().add(module);
                }
            }
            return column;
        } catch (Exception e) {
            logger.error("解析列数据失败：" + columnElem, e);
            return null;
        }
    }

    /**
     * 将 module 节点解析为 module 实例对象
     * @param moduleElem module 节点
     * @return module 实例对象
     */
    public static CmsModuleInstance parseModule(Element moduleElem) {
        try {
            CmsModuleInstance module = new CmsModuleInstance();
            module.setPrototypeId(Long.valueOf(moduleElem.attributeValue("prototype-id")));
            module.setDbId(Long.valueOf(moduleElem.attributeValue("instance-id")));
            return module;
        } catch (Exception e) {
            logger.error("解析模块数据失败：" + moduleElem, e);
            return null;
        }
    }

    /**
     * 将 page 对象转换为 XML 内容
     *
     * @param page 对象
     * @return XML 内容
     */
    public static String pageToXmlString(CmsPageInstance page) {
        Document doc = DocumentHelper.createDocument();
        Element pageElem = DocumentHelper.createElement("page");
        if(page.getDbId() > 0) {
            pageElem.addAttribute("instance-id", String.valueOf(page.getDbId()));
        }
        pageElem.addAttribute("prototype-id", String.valueOf(page.getPrototypeId()));
        for(CmsLayoutInstance layout : page.getLayouts()) {
            Element layoutElem = DocumentHelper.createElement("layout");
            layoutElem.addAttribute("instance-id", String.valueOf(layout.getDbId()));
            layoutElem.addAttribute("prototype-id", String.valueOf(layout.getPrototypeId()));
            for (CmsColumnInstance column : layout.getColumns()) {
                Element columnElem = DocumentHelper.createElement("column");
                columnElem.addAttribute("instance-id", String.valueOf(column.getDbId()));
                columnElem.addAttribute("prototype-id", String.valueOf(column.getPrototypeId()));
                for (CmsModuleInstance module : column.getModules()) {
                    Element modElem = DocumentHelper.createElement("module");
                    modElem.addAttribute("instance-id", String.valueOf(module.getDbId()));
                    modElem.addAttribute("prototype-id", String.valueOf(module.getPrototypeId()));
                    columnElem.add(modElem);
                }
                layoutElem.add(columnElem);
            }
            pageElem.add(layoutElem);
        }
        doc.setRootElement(pageElem);
        return doc.asXML();
    }

    /**
     * 生成缓存的 KEY，目前的 KEY 生成仅依赖 RenderContext 里面的 app user, render mode 和 env 数据，其它值将过滤掉
     * @param targetType 缓存的对象类型
     * @param targetId 缓存的实例 ID
     * @param context 渲染上下文环境
     * @return 缓存 KEY
     */
    public static String generateCacheKey(String targetType, long targetId, RenderContext context) {
        if(StringUtils.isBlank(targetType) || targetId <= 0 || null == context) {
            return null;
        }
        StringBuilder sb = new StringBuilder("_CACHE");
        sb.append("_").append(targetType)
                .append(";").append("id=").append(targetId)
                .append(";").append("mode=").append(context.getMode());

        AppUser appUser = (AppUser) context.get(RenderContext.APP_USER_KEY);
        if(null != appUser) {
            sb.append(";").append("user=").append(appUser.getIdentifier());
        }

        // 环境变量按 KEY 排序，并获取键值的 hash code 作为缓存KEY组成部分
//        List<String> keys = new ArrayList<String>(((Map<String, String>)context.get(RenderContext.RENDER_ENV_KEY)).keySet());
//        Collections.sort(keys);
//        StringBuilder envSb = new StringBuilder();
//        for(String key : keys) {
//            envSb.append(key).append("=").append(context.get(key)).append(";");
//        }
//        sb.append(";env=").append(envSb.toString().hashCode());
        return sb.toString();
    }
}
