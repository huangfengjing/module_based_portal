package com.alibaba.ydt.portal.util;

import com.alibaba.ydt.portal.domain.common.FeatureDO;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * FEATURE related utility
 *
 * @author <a href="huangfengjing@gmail.com">Ivan</a>
 */
public class FeatureUtils {
    static final String SP = ";";
    static final String SSP = "=";
    static final String HIERARCHY = "*";

    static final String R_SP = "#3A";
    static final String R_SSP = "#3B";
    static final String R_HIERARCHY = "#2A";

    /**
     * Convert map present features data to a string
     *
     * @param features feature map
     * @return string converted from map, such as: cms:11.htm;*pageSize:40
     */
    public static String toString(Map<String, FeatureDO> features) {
        StringBuilder sb = new StringBuilder();
        if (null != features && !features.isEmpty()) {
            sb.append(SP);
            for (String key : features.keySet()) {
                FeatureDO val = features.get(key);
                if (null != val && StringUtils.isNotEmpty(val.getValue())) {
                    if (val.isHierarchy()) {
                        sb.append(HIERARCHY);
                    }
                    sb.append(encode(key)).append(SSP).append(encode(val.getValue())).append(SP);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Convert the feature object to a string
     *
     * @param feature feature to convert
     * @return string represent feature
     */
    public static String toString(FeatureDO feature) {
        StringBuilder sb = new StringBuilder();
        if (null != feature && StringUtils.isNotEmpty(feature.getKey()) && StringUtils.isNotEmpty(feature.getValue())) {
            sb.append(SP);
            if (feature.isHierarchy()) {
                sb.append(HIERARCHY);
            }
            sb.append(encode(feature.getKey())).append(SSP).append(encode(feature.getValue()));
            sb.append(SP);
        }
        return sb.toString();
    }

    /**
     * Convert the feature object to a string, feature value can be null
     *
     * @param feature feature to convert
     * @return string represent feature
     */
    public static String toStringForQuery(FeatureDO feature) {
        StringBuilder sb = new StringBuilder();
        if (null != feature && StringUtils.isNotEmpty(feature.getKey())) {
            sb.append(SP);
            if (feature.isHierarchy()) {
                sb.append(HIERARCHY);
            }
            sb.append(encode(feature.getKey())).append(SSP).append(encode(feature.getValue()));
            if (StringUtils.isNotEmpty(feature.getValue())) {
                sb.append(SP);
            }
        }
        return sb.toString();
    }

    /**
     * Parse a string to a FeatureDO map
     *
     * @param str string to parse
     * @return feature map, feature key will be the map key
     */
    public static Map<String, FeatureDO> fromString(String str) {
        Map<String, FeatureDO> features = new HashMap<String, FeatureDO>();
        if (StringUtils.isNotBlank(str)) {
            String[] arr = str.split(SP);
            if (null != arr) {
                for (String kv : arr) {
                    if (StringUtils.isNotBlank(kv)) {
                        String[] ar = kv.split(SSP);
                        if (null != ar && ar.length == 2) {
                            String key = decode(ar[0]);
                            String val = decode(ar[1]);
                            boolean hi = false;
                            if (key.startsWith(HIERARCHY)) {
                                hi = true;
                                key = key.substring(1);
                            }
                            if (StringUtils.isNotEmpty(val)) {
                                FeatureDO feature = new FeatureDO();
                                feature.setKey(key);
                                feature.setValue(val);
                                feature.setHierarchy(hi);
                                features.put(key, feature);
                            }
                        }
                    }
                }
            }
        }
        return features;
    }

    private static String encode(String val) {
        if (null == val) {
            return "";
        }
        return StringUtils.replace(StringUtils.replace(StringUtils.replace(val, SP, R_SP), SSP, R_SSP), HIERARCHY, R_HIERARCHY);
    }

    private static String decode(String val) {
        return StringUtils.replace(StringUtils.replace(StringUtils.replace(val, R_SP, SP), R_SSP, SSP), R_HIERARCHY, HIERARCHY);
    }
}
