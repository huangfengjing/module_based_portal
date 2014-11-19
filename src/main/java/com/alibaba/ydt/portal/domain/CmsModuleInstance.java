package com.alibaba.ydt.portal.domain;

import com.alibaba.ydt.portal.domain.common.ParameterSupportModel;

/**
 * <p>
 * 页面模块实例
 * </p>
 * Time: 12-11-27 下午3:27
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class CmsModuleInstance extends ParameterSupportModel {

    /**
     * 模块原型 ID
     */
    private long prototypeId;

    public long getPrototypeId() {
        return prototypeId;
    }

    public void setPrototypeId(long prototypeId) {
        this.prototypeId = prototypeId;
    }
}