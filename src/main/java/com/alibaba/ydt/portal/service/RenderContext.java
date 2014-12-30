package com.alibaba.ydt.portal.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;

/**
 * <p>
 * 渲染上下文
 * </p>
 * Time: 13-1-4 下午1:54
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class RenderContext extends HashMap<String, Object> implements Map<String, Object>, Cloneable, Serializable {

    private Log logger = LogFactory.getLog(getClass());

    public static final String PAGE_HEADER_CONTENT_KEY = "cms_header";
    public static final String PAGE_BODY_CONTENT_KEY = "cms_body";
    public static final String PAGE_FOOTER_CONTENT_KEY = "cms_footer";

    public static final String PAGE_INSTANCE_KEY = "pageInstance";
    public static final String PAGE_PROTOTYPE_KEY = "pagePrototype";
    public static final String PAGE_PARAMS_KEY = "pageParams";

    public static final String LAYOUT_PROTOTYPE_KEY = "layoutPrototype";
    public static final String LAYOUT_INSTANCE_KEY = "layoutInstance";
    public static final String LAYOUT_PARAMS_KEY = "layoutParams";

    public static final String COLUMN_PROTOTYPE_KEY = "columnPrototype";
    public static final String COLUMN_INSTANCE_KEY = "columnInstance";
    public static final String COLUMN_PARAMS_KEY = "columnParams";

    public static final String MODULE_PROTOTYPE_KEY = "modulePrototype";
    public static final String MODULE_INSTANCE_KEY = "moduleInstance";
    public static final String MODULE_PARAMS_KEY = "moduleParams";

    public static final String RENDER_REQUEST_KEY = "request";
    public static final String RENDER_RESPONSE_KEY = "response";
    public static final String RENDER_SERVLET_CONTEXT_KEY = "servletContext";

    public static final String RENDER_MOD_KEY = "renderMode";
    public static final String RENDER_ENV_KEY = "env";

    public static final String CMS_LAYOUT_LIST_KEY = "_cms_layouts";
    public static final String CMS_COLUMN_LIST_KEY = "_cms_columns";
    public static final String CMS_MODULE_LIST_KEY = "_cms_modules";

    public static final String TOOL_BOX_INJECTED = "_toolbox_injected";

    // 特殊保留的 KEY 值，参数或者环境变量不应该使用这些 KEY
    private Set<String> SPECIAL_KEY_SET = new HashSet<String>() {{
        add(PAGE_HEADER_CONTENT_KEY);
        add(PAGE_BODY_CONTENT_KEY);
        add(PAGE_FOOTER_CONTENT_KEY);

        add(PAGE_INSTANCE_KEY);
        add(PAGE_PROTOTYPE_KEY);
        add(PAGE_PARAMS_KEY);

        add(LAYOUT_PROTOTYPE_KEY);
        add(LAYOUT_INSTANCE_KEY);
        add(LAYOUT_PARAMS_KEY);

        add(COLUMN_PROTOTYPE_KEY);
        add(COLUMN_INSTANCE_KEY);
        add(COLUMN_PARAMS_KEY);

        add(MODULE_PROTOTYPE_KEY);
        add(MODULE_INSTANCE_KEY);
        add(MODULE_PARAMS_KEY);

        add(RENDER_REQUEST_KEY);
        add(RENDER_RESPONSE_KEY);
        add(RENDER_SERVLET_CONTEXT_KEY);

        add(RENDER_MOD_KEY);
        add(RENDER_ENV_KEY);

        add(CMS_LAYOUT_LIST_KEY);
        add(CMS_COLUMN_LIST_KEY);
        add(CMS_MODULE_LIST_KEY);

        add(TOOL_BOX_INJECTED);
    }};

    public RenderContext() {
        init();
    }

    public RenderContext(Map<String, Object> map) {
        if(null != map) {
            putAll(map);
        }
    }

    private void init() {
        put(RENDER_MOD_KEY, RenderMode.product);
        put(RENDER_ENV_KEY, new HashMap<String, String>());
        put(MODULE_PARAMS_KEY, new HashMap<String, Object>());
        put(COLUMN_PARAMS_KEY, new HashMap<String, Object>());
        put(LAYOUT_PARAMS_KEY, new HashMap<String, Object>());
        put(PAGE_PARAMS_KEY, new HashMap<String, Object>());
    }

    /**
     * 添加一个属性
     * @param key 属性 KEY
     * @param value 属性值
     */
    final public void addToEnv(String key, String value) {
        if(SPECIAL_KEY_SET.contains(key)) {
            logger.error("添加环境变量失败，" + key + " 为特殊的保留KEY，请勿使用！");
            return;
        }
        ((Map<String, String>) get(RENDER_ENV_KEY)).put(key, value);
    }

    /**
     * 删除一个环境变量
     * @param key 环境变量 KEY
     */
    final public void removeEnv(String key) {
        ((Map<String, Object>) get(RENDER_ENV_KEY)).remove(key);
    }

    /**
     * 添加一个参数
     * @param key 参数 KEY
     * @param value 参数值
     */
    final public void addToParam(String instanceParamKey, String key, Object value) {
        if(SPECIAL_KEY_SET.contains(key)) {
            logger.error("添加参数失败，" + key + " 为特殊的保留KEY，请勿使用！");
            return;
        }
        if(containsKey(instanceParamKey)) {
            ((Map<String, Object>) get(instanceParamKey)).put(key, value);
        }
    }

    /**
     * 删除一个参数
     * @param key 参数 KEY
     */
    final public void removeParam(String instanceParamKey, String key) {
        if(containsKey(instanceParamKey)) {
            ((Map<String, Object>) get(instanceParamKey)).remove(key);
        }
    }

    /**
     * 设置渲染模式
     * @param mode 渲染模式
     */
    final public void setMode(RenderMode mode) {
        ((Map<String, Object>) get(RENDER_ENV_KEY)).put(RENDER_MOD_KEY, mode);
    }

    @Override
    final public Object remove(Object key) {
        if(null != key && SPECIAL_KEY_SET.contains(key.toString())) {
            throw new UnsupportedOperationException("请不要直接删除保留数据，使用 removeParam 或者 removeEnv 代替，key=" + key);
        }
        return super.remove(key);
    }

    @Override
    final public void clear() {
        super.clear();
        init();
    }

    @Override
    public RenderContext clone() {
        Map<String, Object> cloned = new HashMap<String, Object>();
        for(String key : keySet()) {
            Object value = get(key);
            if(value instanceof Serializable) {
                cloned.put(key, cloneSerializableObject(value));
            } else {
                cloned.put(key, value);
            }
        }
        return new RenderContext(cloned);
    }

    public Object cloneSerializableObject(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return ois.readObject();
        } catch (Exception e) {
            logger.error("克隆失败", e);
            return null;
        }
    }

    /**
     * 渲染模式，分为编辑模式，预览模式和产品模式
     */
    public static enum RenderMode {
        design, preview, product
    }
}
