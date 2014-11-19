package com.alibaba.ydt.portal.domain.common;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Represent a feature
 *
 * @author <a href="huangfengjing@gmail.com">Ivan</a>
 */
public class FeatureDO implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8246983265685247612L;

    /**
     * name
     */
    private String key;

    /**
     * value
     */
    private String value;

    /**
     * Flag indicate can whether or not been extended by children
     */
    private boolean hierarchy;

    /**
     * Check if matches another feature, only key used to match
     *
     * @param f feature to match
     * @return TRUE OR FALSE
     */
    public boolean match(BaseFeatureSupport f) {
        boolean ret = false;
        if (null != f) {
            if (null != f.getFeature(this.key)
                    && (null == this.value
                    || this.value.equals(f.getFeature(this.key).getValue()))) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the hierarchy
     */
    public boolean isHierarchy() {
        return hierarchy;
    }

    /**
     * @param hierarchy the hierarchy to set
     */
    public void setHierarchy(boolean hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FeatureDO)) {
            return false;
        }

        FeatureDO f = (FeatureDO) obj;
        return StringUtils.equals(key, f.getKey()) && StringUtils.equals(value, f.getValue());
    }
}
