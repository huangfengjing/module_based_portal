package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.CmsColumnInstance;
import com.alibaba.ydt.portal.domain.CmsLayoutInstance;
import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.CmsPageInstance;
import com.alibaba.ydt.portal.util.CmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
     * @return 是否保存成功的标识
     */
    @Transactional
    public boolean savePageLayout(CmsPageInstance page) {

        // 删除被用户去掉的模块实例数据
        Map<String, List<Long>> removedInstIds = diffInstanceIds(page);
        List<Long> layoutIds = removedInstIds.get(CmsLayoutInstance.TYPE_TAG);
        if(null != layoutIds && !layoutIds.isEmpty()) {
            cmsLayoutInstanceService.removeById(layoutIds);
        }
        List<Long> columnIds = removedInstIds.get(CmsColumnInstance.TYPE_TAG);
        if(null != columnIds && !columnIds.isEmpty()) {
            cmsColumnInstanceService.removeById(columnIds);
        }
        List<Long> moduleIds = removedInstIds.get(CmsModuleInstance.TYPE_TAG);
        if(null != moduleIds && !moduleIds.isEmpty()) {
            cmsLayoutInstanceService.removeById(moduleIds);
        }

        // 初始化那些新建的组件
        createNewComponentInstance(page);
        save(page);
        return true;
    }

    // TODO 分析被用户删除的模块
    private Map<String, List<Long>> diffInstanceIds(CmsPageInstance page) {
        return Collections.emptyMap();
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
