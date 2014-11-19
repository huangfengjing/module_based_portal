package com.alibaba.ydt.portal.service;

/**
 * 数据服务基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-19 下午2:30.
 */
public interface BaseDataService<T> {

    /**
     * 根据 ID 获取数据
     * @param id ID
     * @return 数据对象
     */
    public T getById(Long id);

    /**
     * 删除指定 ID 的数据
     * @param id ID
     */
    public void removeById(Long id);

    /**
     * 保存数据对象
     * @param t 数据对象
     */
    public void save(T t);
}
