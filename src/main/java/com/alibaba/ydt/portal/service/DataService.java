package com.alibaba.ydt.portal.service;

import java.util.List;

/**
 * 数据服务基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-19 下午2:30.
 */
public interface DataService<T> {

    /**
     * 根据 ID 获取数据
     * @param id ID
     * @return 数据对象
     */
    public T getById(Long id);

    /**
     * 根据属性获取对象列表
     * @param propName 属性名称
     * @param propVal 属性值
     * @return 对象列表
     */
    public List<T> getByProperty(String propName, Object propVal);

    /**
     * 根据属性获取对象列表
     * @param propName 属性名称
     * @param propVal 属性值
     * @return 对象列表
     */
    public T getUniqueByProperty(String propName, Object propVal);

    /**
     * 删除指定 ID 的数据
     * @param id ID
     */
    public void removeById(Long id);

    /**
     * 删除指定 ID 的数据
     * @param id ID
     */
    public void removeById(List<Long> id);

    /**
     * 保存数据对象
     * @param t 数据对象
     */
    public void save(T t);
}
