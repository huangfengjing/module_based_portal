package com.alibaba.ydt.portal.domain;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
* 参数键值对
*
* @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
* @version 1.0
*          Created on 14-10-29 上午11:27.
*/
public class ParameterValuePair implements Serializable {

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数值
     */
    private Object value;

    public ParameterValuePair() {}

    public ParameterValuePair(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", name).add("value", value).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterValuePair)) return false;

        ParameterValuePair that = (ParameterValuePair) o;

        if (!name.equals(that.name)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
