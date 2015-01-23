package com.alibaba.ydt.portal.dao;

import com.alibaba.ydt.portal.common.AppConstants;
import com.alibaba.ydt.portal.util.StringUtils;
import org.hibernate.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * GenericDao implementation
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @since 2009-4-1 PM 03:34:51
 */
@SuppressWarnings("unchecked")
@Repository
public class GenericDaoImpl extends HibernateDaoSupport implements GenericDao, AppConstants {

    public Serializable create(Object o) {
        return this.getHibernateTemplate().save(o);
    }

    public void save(Object o) {
        this.getHibernateTemplate().saveOrUpdate(o);
    }

    @Override
    public <T> List<T> find(String queryString, Object... values) {
        return super.getHibernateTemplate().find(queryString, values);
    }

    @Override
    public <T> T get(Class<T> cls, Long id) {
        return super.getHibernateTemplate().get(cls, id);
    }

    @Override
    public <T> List<T> getAll(Class<T> cls) {
        return super.getHibernateTemplate().loadAll(cls);
    }

    @Override
    public <T> List<T> getByProperty(Class<T> cls, String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName must not be empty");
        Assert.notNull(value, "value is required");
        String hql = "from " + cls.getName() + " as model where model." + propertyName + " = ?";
        return find(hql, value);
    }

    @Override
    public Object getUniqueResult(String queryString, Object[] values) {
        Query query = getSession().createQuery(queryString);
        if (null != values) {
            for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
                query.setParameter(i, values[i]);
            }
        }
        try {
            return query.uniqueResult();
        } catch (NonUniqueResultException e) {
            logger.error("您检索的条件返回的数据并不唯一，请检查条件", e);
            return null;
        }
    }

    @Override
    public <T> T getUniqueByProperty(Class<T> cls, String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName must not be empty");
        Assert.notNull(value, "value is required");
        String hql = "from " + cls.getName() + " as model where model." + propertyName + " = ?";
        return (T) getUniqueResult(hql, new Object[]{value});
    }

    @Override
    public <T> boolean isUnique(Class<T> cls, String propertyName, Object oldValue, Object newValue) {
        Assert.hasText(propertyName, "propertyName must not be empty");
        Assert.notNull(newValue, "newValue is required");
        if (newValue == oldValue || newValue.equals(oldValue)) {
            return true;
        }
        if (newValue instanceof String) {
            if (oldValue != null && StringUtils.equalsIgnoreCase((String) oldValue, (String) newValue)) {
                return true;
            }
        }
        return getUniqueByProperty(cls, propertyName, newValue) == null;
    }

    @Override
    public <T> boolean isExist(Class<T> cls, String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName must not be empty");
        Assert.notNull(value, "value is required");
        return null != getUniqueByProperty(cls, propertyName, value);
    }

    @Override
    public <T> void remove(Class<T> cls, Long id) {
        T e = this.get(cls, id);
        if (null == e) {
            return;
        }
        super.getHibernateTemplate().delete(e);
        super.getHibernateTemplate().flush();
    }

    public void remove(Object e) {
        if (null == e) {
            return;
        }
        super.getHibernateTemplate().delete(e);
        super.getHibernateTemplate().flush();
    }

    public <T> int removeWithIds(Class<T> cls, List<Long> ids) {
        if (null == ids || ids.isEmpty()) {
            return RET_CODE_OK;
        }
        /*
           * the following statements has a high performance but the defect is hard code the "id" as the identifier
           *
           * StringBuilder sb = new StringBuilder("delete from ");
           * sb.append(clzz.getName()).append(" where id in (:idList)");
           * Map<String, Object> params = new HashMap<String, Object>();
           * params.put("idList", ids);
           * executeCustomUpdate(sb.toString(), params);
           */

        for (Serializable id : ids) {
            this.remove(get(cls, (Long)id));
        }
        return RET_CODE_OK;
    }

    public List<Object> executeCustomQuery(final String queryString, final Map<String, Object> paramValues) {
        return (List<Object>) super.getHibernateTemplate().execute(new HibernateCallback() {

            public List<Object> doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Query query = session.createQuery(queryString);
                for (Map.Entry<String, Object> entry : paramValues.entrySet()) {
                    if (entry.getValue() instanceof List) {
                        query.setParameterList(entry.getKey(), (List) entry.getValue());
                    } else {
                        query.setParameter(entry.getKey(), entry.getValue());
                    }
                }
                return query.list();
            }

        });
    }

    public Integer executeCustomUpdate(final String queryString, final Map<String, Object> paramValues) {
        return (Integer) super.getHibernateTemplate().execute(new HibernateCallback() {

            public Integer doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Query query = session.createQuery(queryString);
                for (Map.Entry<String, Object> entry : paramValues.entrySet()) {
                    if (entry.getValue() instanceof List) {
                        query.setParameterList(entry.getKey(), (List) entry.getValue());
                    } else {
                        query.setParameter(entry.getKey(), entry.getValue());
                    }
                }
                return query.executeUpdate();
            }

        });
    }
}
