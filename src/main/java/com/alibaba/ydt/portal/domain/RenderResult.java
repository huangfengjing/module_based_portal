package com.alibaba.ydt.portal.domain;

import java.util.Date;

/**
 * 渲染结果
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-24 上午10:48.
 */
public class RenderResult {

    /**
     * 结果类型：普通
      */
    public static final int RESULT_TYPE_NORMAL = 1;

    /**
     * 结果类型：缓存
     */
    public static final int RESULT_TYPE_FROM_CACHE = 2;

    /**
     * 结果类型：异常处理的结果
     */
    public static final int RESULT_TYPE_HANDLE_ERROR = 3;

    /**
     * 结果类型：被拦截器跳过
     */
    public static final int RESULT_TYPE_SKIPPED = 4;

    private String renderContent;

    private Date renderTime;

    private int resultType;

    public RenderResult() {
        this.renderTime = new Date();
    }

    public RenderResult(String renderContent) {
        this.renderTime = new Date();
        this.renderContent = renderContent;
        this.resultType = RESULT_TYPE_NORMAL;
    }

    public RenderResult(String renderContent, int resultType) {
        this.renderTime = new Date();
        this.renderContent = renderContent;
        this.resultType = resultType;
    }

    public String getRenderContent() {
        return renderContent;
    }

    public void setRenderContent(String renderContent) {
        this.renderContent = renderContent;
    }

    public Date getRenderTime() {
        return renderTime;
    }

    public void setRenderTime(Date renderTime) {
        this.renderTime = renderTime;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }
}
