package com.alibaba.ydt.portal.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 渲染上下文
 * </p>
 * Time: 13-1-4 下午1:54
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class RenderContext extends HashMap<String, Object> implements Cloneable {

    private Log logger = LogFactory.getLog(getClass());

    public static final String PAGE_KEY = "cmsPage";
    public static final String PAGE_HEADER_CONTENT_KEY = "cms_header";
    public static final String PAGE_BODY_CONTENT_KEY = "cms_body";
    public static final String PAGE_FOOTER_CONTENT_KEY = "cms_footer";

    public static final String MODULE_PROTOTYPE_KEY = "prototype";
    public static final String MODULE_INSTANCE_KEY = "instance";
    public static final String MODULE_INSTANCE_ID_KEY = "instanceId";
    public static final String MODULE_PARAMS_KEY = "params";

    public static final String RENDER_REQUEST_KEY = "request";
    public static final String RENDER_RESPONSE_KEY = "response";
    public static final String RENDER_SERVLET_CONTEXT_KEY = "servletContext";
    public static final String RENDER_MOD_KEY = "mode";
    public static final String RENDER_ENV_KEY = "env";

    // 特殊保留的 KEY 值，参数或者环境变量不应该使用这些 KEY
    private Set<String> SPECIAL_KEY_SET = new HashSet<String>() {{
        add(PAGE_HEADER_CONTENT_KEY);
        add(PAGE_BODY_CONTENT_KEY);
        add(PAGE_FOOTER_CONTENT_KEY);
        add(PAGE_KEY);
        add(MODULE_PROTOTYPE_KEY);
        add(MODULE_INSTANCE_KEY);
        add(MODULE_INSTANCE_ID_KEY);
        add(RENDER_REQUEST_KEY);
        add(RENDER_RESPONSE_KEY);
        add(RENDER_SERVLET_CONTEXT_KEY);
    }};

    public RenderContext() {
        init();
    }

    public RenderContext(RenderContext subContext) {
        init();
        merge(subContext);
    }

    private void init() {
        put(RENDER_MOD_KEY, RenderMode.product);
        put(MODULE_PARAMS_KEY, new HashMap<String, Object>());
        put(RENDER_ENV_KEY, new HashMap<String, Object>());
    }

    /**
     * 添加一个属性
     * @param key 属性 KEY
     * @param value 属性值
     * @return 添加后的上下文环境
     */
    final public void addToEnv(String key, Object value) {
        if(SPECIAL_KEY_SET.contains(key)) {
            logger.error("添加环境变量失败，" + key + " 为特殊的保留KEY，请勿使用！");
            return;
        }
        ((Map<String, Object>) get(RENDER_ENV_KEY)).put(key, value);
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
     * @return 添加后的上下文环境
     */
    final public void addToParam(String key, Object value) {
        if(SPECIAL_KEY_SET.contains(key)) {
            logger.error("添加参数失败，" + key + " 为特殊的保留KEY，请勿使用！");
            return;
        }
        ((Map<String, Object>) get(MODULE_PARAMS_KEY)).put(key, value);
    }

    /**
     * 删除一个参数
     * @param key 参数 KEY
     */
    final public void removeParam(String key) {
        ((Map<String, Object>) get(MODULE_PARAMS_KEY)).remove(key);
    }

    /**
     * 合并2个上下文环境
     * @param context 上下文环境
     * @return 合并后的上下文环境
     */
    final public RenderContext merge(RenderContext context) {
        putAll(context);
        return this;
    }

    /**
     * 设置渲染模式
     * @param mode 渲染模式
     */
    final public void setMode(RenderMode mode) {
        put(RENDER_MOD_KEY, mode);
    }

    @Override
    final public Object remove(Object key) {
        throw new UnsupportedOperationException("请不要直接删除上下文中的数据，使用 removeParam 或者 removeEnv 代替");
    }

    @Override
    final public Object put(String key, Object value) {
        if(!SPECIAL_KEY_SET.contains(key)) {
            throw new UnsupportedOperationException("请不要直接在上下文中添加数据，使用 addParam 或者 addEnv 代替");
        }
        return super.put(key, value);
    }

    @Override
    final public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException("请不要直接在上下文中添加数据，使用 addParam 或者 addEnv 代替");
    }

    @Override
    final public void clear() {
        super.clear();
        init();
    }

    /**
     * 渲染模式，分为编辑模式，预览模式和产品模式
     */
    public static enum RenderMode {
        design, preview, product
    }
}
