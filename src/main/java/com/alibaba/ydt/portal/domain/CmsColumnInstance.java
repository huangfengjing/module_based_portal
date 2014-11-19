package com.alibaba.ydt.portal.domain;

import com.alibaba.ydt.portal.domain.common.ParameterSupportModel;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * CMS 页面列对象
 * </p>
 * Time: 13-1-4 下午2:44
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class CmsColumnInstance extends ParameterSupportModel {

    /**
     * 原型 ID
     */
    private long prototypeId;

    /**
     * 布局名称
     */
    private String name;

    /**
     * 布局栏目列表
     */
    private List<CmsModuleInstance> modules = new ArrayList<CmsModuleInstance>();

    public long getPrototypeId() {
        return prototypeId;
    }

    public void setPrototypeId(long prototypeId) {
        this.prototypeId = prototypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CmsModuleInstance> getModules() {
        return modules;
    }

    public void setModules(List<CmsModuleInstance> modules) {
        this.modules = modules;
    }
}
