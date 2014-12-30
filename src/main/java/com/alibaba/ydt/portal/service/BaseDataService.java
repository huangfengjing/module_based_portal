package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.dao.GenericDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 获取数据的基类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-24 上午10:54.
 */
abstract public class BaseDataService<T> implements DataService<T> {

    protected Class<T> entityClass;

    @Autowired
    protected GenericDaoImpl genericDao;

    public BaseDataService() {
        this.entityClass = null;
        Class c = getClass();
        Type type = c.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
            this.entityClass = (Class<T>) parameterizedType[0];
        }
    }

    @Override
    public T getById(Long id) {
        return genericDao.get(entityClass, id);
    }

    @Override
    public List<T> getByProperty(String propName, Object propVal) {
        return genericDao.getByProperty(entityClass, propName, propVal);
    }

    @Override
    public T getUniqueByProperty(String propName, Object propVal) {
        return genericDao.getUniqueByProperty(entityClass, propName, propVal);
    }

    @Override
    public void removeById(Long id) {
        genericDao.remove(entityClass, id);
    }

    @Override
    public void removeById(List<Long> idList) {
        genericDao.removeWithIds(entityClass, idList);
    }

    @Override
    public void save(T t) {
        genericDao.save(t);
    }
}
