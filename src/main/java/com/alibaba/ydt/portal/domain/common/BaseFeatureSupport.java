package com.alibaba.ydt.portal.domain.common;

import com.alibaba.ydt.portal.util.FeatureUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.*;

/**
 * Base feature support
 */
public class BaseFeatureSupport extends BaseModel implements Serializable {

    public static final String REMOVABLE_FEATURE_KEY = "removable";
    public static final String EDITABLE_FEATURE_KEY = "editable";

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3896451590403307619L;

    /**
     * Used for ORM
     */
    private String features;

    /**
     * Query feature by name
     *
     * @param name key
     * @return feature value
     */
    public FeatureDO getFeature(String name) {
        Map<String, FeatureDO> featureMap = FeatureUtils.fromString(features);
        return featureMap != null ? featureMap.get(name) : null;
    }

    /**
     * Query feature by name
     *
     * @param name key
     * @return string represented feature value
     */
    public String getFeatureValue(String name) {
        Map<String, FeatureDO> featureMap = FeatureUtils.fromString(features);
        return featureMap != null ? (featureMap.containsKey(name) ? featureMap.get(name).getValue() : null) : null;
    }

    /**
     * Set a feature
     *
     * @param name  feature name
     * @param value feature value
     */
    public void setFeature(String name, Object value) {
        FeatureDO f = new FeatureDO();
        f.setKey(name);
        f.setValue(value.toString());
        setFeature(f);
    }

    /**
     * Set features in a batch
     *
     * @param featureMap features to set
     */
    public void setFeatureMap(List<FeatureDO> featureMap) {
        if (null != featureMap) {
            for (FeatureDO f : featureMap) {
                setFeature(f);
            }
        }
    }

    /**
     * Set a feature
     *
     * @param feature feature to set
     */
    public void setFeature(FeatureDO feature) {
        if (null != feature) {
            Map<String, FeatureDO> map = FeatureUtils.fromString(features);
            map.put(feature.getKey(), feature);
            features = FeatureUtils.toString(map);
        }
    }

    /**
     * Get all feature's key
     *
     * @return keys keys
     */
    public Set<String> getFeatureKeys() {
        return (null != FeatureUtils.fromString(features) ? FeatureUtils.fromString(features).keySet() : new HashSet<String>());
    }

    /**
     * Get features as map
     *
     * @return featureMap Map
     */
    protected Map<String, FeatureDO> getFeatureMap() {
        return FeatureUtils.fromString(features);
    }

    /**
     * Get features as list
     *
     * @return featureMap List
     */
    public List<FeatureDO> getFeatureList() {
        if (FeatureUtils.fromString(features) != null) {
            return new ArrayList<FeatureDO>(FeatureUtils.fromString(features).values());
        }
        return Collections.emptyList();
    }

    /**
     * Remove a feature by key
     *
     * @param key key
     */
    public void removeFeature(String key) {
        if (key == null) {
            return;
        }
        if (FeatureUtils.fromString(features) == null) {
            return;
        }
        Map<String, FeatureDO> map = FeatureUtils.fromString(features);
        map.remove(key);
        features = FeatureUtils.toString(map);
    }

    /**
     * Remove a feature
     *
     * @param feature FeatureDO
     */
    public void removeFeature(FeatureDO feature) {
        if (feature == null) {
            return;
        }
        if (FeatureUtils.fromString(features) == null) {
            return;
        }
        Map<String, FeatureDO> map = FeatureUtils.fromString(features);
        map.remove(feature.getKey());
        features = FeatureUtils.toString(map);
    }

    /**
     * Clear all features
     */
    public void clearAllFeature() {
        features = "";
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~ Following methods used for ORM ~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Set string represent feature
     *
     * @param features string represent feature
     */
    public void setFeatures(String features) {
        this.features = features;
    }

    /**
     * @return get features as a string
     */
    @JsonIgnore
    public String getFeatures() {
        return features;
    }
}
