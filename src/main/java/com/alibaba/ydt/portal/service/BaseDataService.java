package com.alibaba.ydt.portal.service;

/**
 * 获取数据的基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-24 上午10:54.
 */
abstract public class BaseDataService<T> implements DataService<T> {

    /**
     * 根据 ID 获取指定类型的数据
     * @param cls 类型
     * @param id 实例 ID
     * @param <E> 泛型
     * @return 实例
     */
    static public <E> E getData(Class<E> cls, long id) {
        // FIXME 以下实例为 MOCK
        try {
            return (E) Class.forName(cls.getName()).newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
