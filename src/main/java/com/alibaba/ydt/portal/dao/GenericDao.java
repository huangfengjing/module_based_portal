package com.alibaba.ydt.portal.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Generic dao, abstract some very common functions
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @since 2009-4-1 PM 03:22:18
 */
public interface GenericDao {

    /**
     * find data by a query string with more than one parameter
     *
     * @param queryString query string
     * @param values      parameter values
     * @return data list
     */
    public <T> List<T> find(String queryString, Object... values);

    /**
     * Get all data
     *
     * @param cls entity class
     * @return all data
     */
    public <T> List<T> getAll(Class<T> cls);

    /**
     * Get all data
     *
     * @param cls entity class
     * @return all data
     */
    public <T> List<T> getByProperty(Class<T> cls, String propertyName, Object value);

    /**
     * get unique object
     *
     * @param queryString query string
     * @param values      param values
     * @return object
     */
    public Object getUniqueResult(String queryString, Object[] values);

    /**
     * Get all data
     *
     * @param cls entity class
     * @return all data
     */
    public <T> T getUniqueByProperty(Class<T> cls, String propertyName, Object value);

    /**
     * 判断属性值是否唯一
     *
     * @param propertyName 属性名
     * @param oldValue     老的属性值
     * @param newValue     新的属性值
     * @param cls          entity class
     * @return 唯一时返回 true ，否则返回 false
     */
    public <T> boolean isUnique(Class<T> cls, String propertyName, Object oldValue, Object newValue);

    /**
     * 判断某属性值是否存在
     *
     * @param propertyName 属性名
     * @param value        属性值
     * @param cls          entity class
     * @return 如果存在，返回 true，否则返回 false
     */
    public <T> boolean isExist(Class<T> cls, String propertyName, Object value);

    /**
     * Get data from db with id
     *
     * @param id object id
     *                     @param cls entity class
     * @return data from db
     */
    public <T> T get(Class<T> cls, Long id);

    /**
     * Save data
     *
     * @param o data to save
     */
    public void save(Object o);

    /**
     * Insert new data record to db
     *
     * @param e data record to insert
     * @return new record id
     */
    public Serializable create(Object e);

    /**
     * Remove a record
     *
     * @param e data to remove
     */
    public void remove(Object e);

    /**
     * Remove a record by id
     * @param cls entity class
     *
     * @param id data id to remove
     */
    public <T> void remove(Class<T> cls, Long id);

    /**
     * Remove more than one records
     *
     * @param ids records id
     * @param cls entity class
     * @return result code
     */
    public <T> int removeWithIds(Class<T> cls, List<Long> ids);
}
