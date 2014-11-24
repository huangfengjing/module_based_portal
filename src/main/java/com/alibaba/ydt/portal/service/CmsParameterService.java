package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.ParameterValuePair;

import java.util.List;

/**
 * 参数服务类
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-24 上午11:02.
 */
public interface CmsParameterService {

    /**
     * 根据实例类型及ID获取实例的所有参数
     * @param type 实例类型
     * @param instanceId 实例 ID
     * @return 所有的参数列表
     */
    public List<ParameterValuePair> getParamsByTypeAndInstanceId(String type, long instanceId);

    /**
     * 保存实例参数
     * @param type 实例类型
     * @param instanceId 实例 ID
     * @param parameterValuePair 参数健值
     */
    public void save(String type, long instanceId, ParameterValuePair parameterValuePair);

    /**
     * 根据实例类型及ID删除实例的所有参数
     * @param type 实例类型
     * @param instanceId 实例 ID
     */
    public void removeParamByTypeAndInstanceId(String type, long instanceId);
}
