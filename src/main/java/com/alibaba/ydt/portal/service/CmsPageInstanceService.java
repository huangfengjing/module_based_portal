package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.CmsColumnInstance;
import com.alibaba.ydt.portal.domain.CmsLayoutInstance;
import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.CmsPageInstance;
import com.alibaba.ydt.portal.util.CmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面服务
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-10-29 下午2:40.
 */
@Service
public class CmsPageInstanceService extends BaseDataService<CmsPageInstance> {

    @Autowired
    private CmsModuleInstanceService cmsModuleInstanceService;

    @Autowired
    private CmsColumnInstanceService cmsColumnInstanceService;

    @Autowired
    private CmsLayoutInstanceService cmsLayoutInstanceService;

    /**
     * 保存页面布局
     *
     * @param page         页面
     * @param delModuleIds 删除的模块 ID 列表
     * @return 是否保存成功的标识
     */
    @Transactional
    public boolean savePageLayout(CmsPageInstance page, List<Long> delModuleIds) {

        // 删除模块
        if(!delModuleIds.isEmpty()) {
            cmsModuleInstanceService.removeById(delModuleIds);
        }

        // 初始化那些新建的组件
        createNewComponentInstance(page);
        save(page);
        return true;
    }

    /**
     * 给布局中新增的模块生成实例 ID
     *
     * @param page 页面对象
     */
    private void createNewComponentInstance(CmsPageInstance page) {
        List<CmsLayoutInstance> newLayouts = new ArrayList<CmsLayoutInstance>();
        for(CmsLayoutInstance layout : page.getLayouts()) {
            if(layout.getDbId() == 0) {
                cmsLayoutInstanceService.create(layout);
            }
            for(CmsColumnInstance column : layout.getColumns()) {
                if(column.getDbId() == 0) {
                    cmsColumnInstanceService.create(column);
                }
                for(CmsModuleInstance module : column.getModules()) {
                    if(module.getDbId() == 0) {
                        cmsModuleInstanceService.create(module);
                    }
                }
            }
            newLayouts.add(layout);
        }
        page.setLayouts(newLayouts);
    }
}
