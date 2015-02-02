package com.alibaba.ydt.portal.util;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端资源工具
 * </p>
 * Time: 13-2-20 下午6:47
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class WebResourceTool extends BaseRequestTool {

    /**
     * CSS 资源列表
     */
    private static List<String> cssResources = new ArrayList<String>();

    /**
     * JS 资源列表
     */
    private static List<String> jsResources = new ArrayList<String>();

    /**
     * 增加绝对 CSS 资源
     *
     * @param resourceUrl 绝对 CSS 资源
     */
    public void addCss(String resourceUrl) {
        if(!cssResources.contains(resourceUrl)) {
            cssResources.add(resourceUrl);
        }
    }

    /**
     * 增加绝对 JS 资源
     *
     * @param resourceUrl 绝对 JS 资源
     */
    public void addJs(String resourceUrl) {
        if(!jsResources.contains(resourceUrl)) {
            jsResources.add(resourceUrl);
        }
    }

    /**
     * 获取绝对 CSS 资源
     *
     * @return 绝对 CSS 资源
     */
    public List<String> getCssResources() {
        return cssResources;
    }

    /**
     * 获取绝对 JS 资源
     *
     * @return 绝对 JS 资源
     */
    public List<String> getJsResources() {
        return jsResources;
    }

    /**
     * 清除资源，每次请求结束后都将执行
     */
    public static void clear() {
        cssResources.clear();
        jsResources.clear();
    }
}