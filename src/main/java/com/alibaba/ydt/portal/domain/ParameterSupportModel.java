package com.alibaba.ydt.portal.domain;

/**
 * 参数支持模型
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-19 下午3:45.
 */
public interface ParameterSupportModel {

    /**
     * 获取实例 ID
     * @return 实例 ID
     */
    public long getInstanceId();

    /**
     * 获取实例类型标签
     * @return 类型标签
     */
    public String getInstanceTypeTag();
}