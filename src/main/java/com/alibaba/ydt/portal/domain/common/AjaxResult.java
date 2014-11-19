package com.alibaba.ydt.portal.domain.common;

import com.alibaba.ydt.portal.web.util.JsonUtils;
import com.alibaba.ydt.portal.web.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Ajax result
 *
 * @author <a href="huangfengjing@gmail.com">Ivan</a>
 */
public class AjaxResult {

    public static final String STATUS_KEY  = "status";
    public static final String MESSAGE_KEY  = "message";

     public static final String STATUS_SUCCESS  = "success";
     public static final String STATUS_ERROR  = "error";
     public static final String STATUS_AUTH_ERROR  = "auth";

    public static final String CONTENT_TYPE_HTML = "html";
    public static final String CONTENT_TYPE_JSON = "json";

     /**
      * Result message carrier
      */
     private Map<String, Object> data = new HashMap<String, Object>();

     /**
     * JSON数据，如果客户端设置了该字段，则会直接输入该字段而忽悠其它的数据
     */
    private String rawData;

    private String textType;

    /**
     * Default constructor
     */
    public AjaxResult() {
    }

    /**
     * Default constructor
     */
    public AjaxResult(String key, Object value) {
        data.put(key, value);
    }

    /**
     * 成功返回结果
     * @return 标识成功的 AJAX 返回结果
     */
    public static AjaxResult successResult() {
        AjaxResult ajaxResult = new AjaxResult(STATUS_KEY, STATUS_SUCCESS);
        ajaxResult.addData(MESSAGE_KEY,  "操作成功");
        return ajaxResult;
    }

    /**
     * 成功返回结果
     * @param message 成功信息
     * @return 标识成功的 AJAX 返回结果
     */
    public static AjaxResult successResult(String message) {
        AjaxResult ajaxResult = new AjaxResult(STATUS_KEY, STATUS_SUCCESS);
        ajaxResult.addData(MESSAGE_KEY,  message);
        return ajaxResult;
    }

    /**
     * 失败返回结果
     * @return 标识失败的 AJAX 返回结果
     */
    public static AjaxResult errorResult() {
        AjaxResult ajaxResult = new AjaxResult(STATUS_KEY, STATUS_ERROR);
        ajaxResult.addData(MESSAGE_KEY,  "操作失败");
        return ajaxResult;
    }

    /**
     * 失败返回结果
     * @param message 成功信息
     * @return 标识失败的 AJAX 返回结果
     */
    public static AjaxResult errorResult(String message) {
        AjaxResult ajaxResult = new AjaxResult(STATUS_KEY, STATUS_ERROR);
        ajaxResult.addData(MESSAGE_KEY,  message);
        return ajaxResult;
    }

    /**
     * 失败返回结果
     * @return 标识失败的 AJAX 返回结果
     */
    public static AjaxResult authErrorResult() {
        AjaxResult ajaxResult = new AjaxResult(STATUS_KEY, STATUS_AUTH_ERROR);
        ajaxResult.addData(MESSAGE_KEY,  "您未登陆或者登陆超时，请先登陆");
        return ajaxResult;
    }

    /**
     * 失败返回结果
     * @param message 成功信息
     * @return 标识失败的 AJAX 返回结果
     */
    public static AjaxResult authErrorResult(String message) {
        AjaxResult ajaxResult = new AjaxResult(STATUS_KEY, STATUS_AUTH_ERROR);
        ajaxResult.addData(MESSAGE_KEY,  message);
        return ajaxResult;
    }

    /**
     * 成功返回结果
     * @return 标识成功的 AJAX 返回结果
     */
    public static AjaxResult rawResult(String rawData) {
        AjaxResult ajaxResult = new AjaxResult(STATUS_KEY, STATUS_SUCCESS);
        ajaxResult.setRawData(rawData);
        return ajaxResult;
    }

    /**
     * Set message data
     *
     * @param data messages
     * @return this
     */
    public AjaxResult setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    /**
     * Add a message
     *
     * @param key   message key
     * @param value message value
     * @return this
     */
    public AjaxResult addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * Add messages
     *
     * @param data messages
     * @return this
     */
    public AjaxResult addData(Map<String, Object> data) {
        this.data.putAll(data);
        return this;
    }

    public String getRawData() {
        return rawData;
    }

    public AjaxResult setRawData(Object rawData) {
        this.rawData = null == rawData ? "" : rawData.toString();
        return this;
    }

    /**
     * 判断是否为成功结果
     * @return 如果为成功结果返回 true 否则返回 false
     */
    public boolean isSuccess() {
        return STATUS_SUCCESS.equals(data.get(STATUS_KEY));
    }

    public boolean isHTMLContent() {
        return CONTENT_TYPE_HTML.equals(textType);
    }

    public AjaxResult setTextType(String textType) {
        this.textType = textType;
        return this;
    }

    /**
     * Convert the result to a JSON string
     *
     * @return JSON represent string
     */
    @Override
    public String toString() {

        if(StringUtils.isNotBlank(rawData)) {
            return rawData;
        }
        return JsonUtils.toCompatibleJSONString(data);
    }
}