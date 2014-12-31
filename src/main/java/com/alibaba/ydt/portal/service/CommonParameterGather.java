package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 通用的参数收集器
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-10-29 下午2:37.
 */
@SuppressWarnings("unchecked")
@Component("commonParameterGather")
public class CommonParameterGather implements ParameterGather {

    private Log logger = LogFactory.getLog(getClass());

    public Set<String> ignoredParamNames = new HashSet<String>() {
        {
            add("pageId");
            add("layoutId");
            add("columnId");
            add("moduleId");
        }
    };

    @Override
    public List<ParameterValuePair> gatherParams(BaseCmsPrototype prototype, BaseCmsInstance instance, HttpServletRequest request) {
        Map<String, Object> paramContainer = new HashMap<String, Object>();
        Enumeration<String> requestNames = request.getParameterNames();
        while (requestNames.hasMoreElements()) {
            String paramName = requestNames.nextElement();
            if (!ignoredParamNames.contains(paramName)) {
                mapRequestParam(paramContainer, paramName, ServletRequestUtils.getStringParameters(request, paramName));
            }
        }
        // 处理 bean 属性类型的参数
        paramContainer = processBeanProps(paramContainer);
        List<ParameterValuePair> pairs = new ArrayList<ParameterValuePair>();
        for(String key : paramContainer.keySet()) {
            pairs.add(new ParameterValuePair(key, paramContainer.get(key)));
        }
        return pairs;
    }

    @Override
    public boolean support(BaseCmsPrototype modulePrototype) {
        return true;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
    /**
     * 递归处理以 . 分隔层次的参数，生成嵌套 MAP
     *
     * @param container 最后生成的 MAP 容器
     * @param key       当前参数名
     * @param values    当前参数值
     */
    @SuppressWarnings("unchecked")
    private void mapRequestParam(Map<String, Object> container, String key, String[] values) {
        try {
            if (!key.contains(".")) {
                List<String> listValue = Arrays.asList(values);
                Object old = container.get(key);
                if (null == old) {
                    if (listValue.size() == 1) {
                        container.put(key, StringUtils.join(listValue, ","));
                    } else {
                        container.put(key, listValue);
                    }
                } else if (old instanceof String) {
                    List<String> newList = Arrays.asList((String) old);
                    newList.addAll(listValue);
                    container.put(key, newList);
                } else if (old instanceof List) {
                    ((List) old).addAll(listValue);
                    container.put(key, old);
                }
                return;
            }

            String parentParamName = key.substring(0, key.indexOf("."));
            String nestedParamName = key.substring(key.indexOf(".") + 1);

            Map<String, Object> subContainer;
            if (!container.containsKey(parentParamName)) {
                container.put(parentParamName, new HashMap<String, Object>());
            }
            subContainer = (Map<String, Object>) container.get(parentParamName);

            mapRequestParam(subContainer, nestedParamName, values);
            container.put(parentParamName, subContainer);
        } catch (Exception e) {
            logger.error("解析参数错误", e);
        }
    }


    /**
     * 处理参数中名称带 - 的参数， - 将参数将分为 2 个部分，前一部分为对象容器，后部分为对象的属性
     *
     * @param paramContainer 参数容器
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> processBeanProps(Map<String, Object> paramContainer) {
        Map<String, Object> newContainer = new HashMap<String, Object>();

        for (String key : paramContainer.keySet()) {
            if (!key.contains("-")) {
                newContainer.put(key, paramContainer.get(key));
                continue;
            }
            String beanName = key.substring(0, key.indexOf("-"));
            List<ParameterValuePair> beanPropList = gatherParamsByBeanName(paramContainer, beanName);

            // 构造属性集
            int valueListSize = 0;
            boolean isPrimitive = false;
            Map<String, Object> mapValue = new HashMap<String, Object>();
            for (ParameterValuePair pair : beanPropList) {
                String propName = pair.getName().substring(key.indexOf("-") + 1);
                Object propValue = pair.getValue();
                if (propValue instanceof String) {
                    // 数组对象只有一个时，其值为 String，则该 propName 下的所有字段构成一个对象
                    isPrimitive = true;
                } else if (propValue instanceof Map) {
                    propValue = processBeanProps((Map<String, Object>) propValue);
                } else if (propValue instanceof List) {
                    valueListSize = ((List) propValue).size();
                }
                mapValue.put(propName, propValue);
            }

            if (isPrimitive) {
                // 数组对象只有一个时，其值为 String，则该 propName 下的所有字段构成一个对象，将对象转换为 LIST 重新存储
                Map<String, Object> newMapValue = new HashMap<String, Object>();
                for (String tmp : mapValue.keySet()) {
                    newMapValue.put(tmp, mapValue.get(tmp));
                }
                List<Map<String, Object>> listValue = new ArrayList<Map<String, Object>>();
                listValue.add(newMapValue);

                newContainer.put(beanName, listValue);
            } else if (valueListSize > 0) {
                // 过滤那些大小不一致的数组，数据类型的对象，所有参数的长度必须保持一致
                Map<String, List<?>> valueSizeEqualsMap = new HashMap<String, List<?>>();
                for (Map.Entry<String, Object> entry : mapValue.entrySet()) {
                    if (entry.getValue() instanceof List && ((List) entry.getValue()).size() == valueListSize) {
                        valueSizeEqualsMap.put(entry.getKey(), (List) entry.getValue());
                    }
                }

                // 构建 LIST 对象
                List<Map<String, Object>> listValue = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < valueListSize; i++) {
                    Map<String, Object> tmp = new HashMap<String, Object>();
                    // 值为数组对象，调转包裹层次
                    for (Map.Entry<String, List<?>> entry : valueSizeEqualsMap.entrySet()) {
                        tmp.put(entry.getKey(), entry.getValue().get(i));
                    }
                    listValue.add(tmp);
                }

                newContainer.put(beanName, listValue);
            } else {
                newContainer.put(beanName, mapValue);
            }

        }

        return newContainer;
    }


    /**
     * 将参数容器中同一 bean name 的参数收集起来
     *
     * @param paramContainer 参数容器
     * @param beanName       bean name
     * @return 同 bean name 参数
     */
    private List<ParameterValuePair> gatherParamsByBeanName(Map<String, Object> paramContainer, String beanName) {
        List<ParameterValuePair> props = new ArrayList<ParameterValuePair>();
        for (String key : paramContainer.keySet()) {
            if (key.startsWith(beanName + "-")) {
                props.add(new ParameterValuePair(key, paramContainer.get(key)));
            }
        }
        return props;
    }
}