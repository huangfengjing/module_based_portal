package com.alibaba.ydt.portal.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>
 * 工具类 - Spring 容器 Holder，获取容器内的配置对象
 * </p>
 * Time: 12-6-5 上午11:24
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 资源资源
     * @param location 资源路径
     * @return 资源
     */
    public static Resource getResource(String location) {
        return applicationContext.getResource(location);
    }

    /**
     * 资源资源
     * @param locationPattern 资源匹配模式
     * @return 资源
     */
    public static Resource[] getResources(String locationPattern) throws IOException{
        return applicationContext.getResources(locationPattern);
    }

    /**
     * 根据Bean名称获取实例
     *
     * @param name Bean注册名称
     * @return bean实例
     * @throws org.springframework.beans.BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 根据Bean类型获取实例
     *
     * @param cls Bean类型
     * @return bean实例
     * @throws org.springframework.beans.BeansException
     */
    public static <T> T getBean(Class<T> cls) throws BeansException {
        return applicationContext.getBean(cls);
    }
}