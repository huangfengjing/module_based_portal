package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.CmsColumnInstance;
import com.alibaba.ydt.portal.domain.CmsLayoutInstance;
import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.CmsPageInstance;
import com.alibaba.ydt.portal.util.CmsUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private Log logger = LogFactory.getLog("CmsPageInstanceService");

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

        // 初始化那些新建的组件
        createNewComponentInstance(page);
        save(page);

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
        return true;
    }

    @Transactional
    public boolean removePage(long pageId) {
        return removePage(getById(pageId));
    }

    @Transactional
    public boolean removePage(CmsPageInstance page) {
        try {
            if(null == page) {
                return true;
            }
            List<Long> layoutIds = new ArrayList<Long>();
            List<Long> columnIds = new ArrayList<Long>();
            List<Long> moduleIds = new ArrayList<Long>();
            for(CmsLayoutInstance layout : page.getLayouts()) {
                layoutIds.add(layout.getDbId());
                for(CmsColumnInstance column : layout.getColumns()) {
                    columnIds.add(column.getDbId());
                    for(CmsModuleInstance module : column.getModules()) {
                        moduleIds.add(module.getDbId());
                    }
                }
            }

            if(!layoutIds.isEmpty()) {
                cmsLayoutInstanceService.removeById(layoutIds);
            }
            if(!columnIds.isEmpty()) {
                cmsColumnInstanceService.removeById(columnIds);
            }
            if(!moduleIds.isEmpty()) {
                cmsLayoutInstanceService.removeById(moduleIds);
            }
            removeById(page.getDbId());
            return true;
        } catch (Exception e) {
            logger.error("删除页面出错", e);
            return false;
        }
    }

    /**
     * 分析被用户删除的组件 ID
     * @param page 新的页面对象
     * @return 被删除的组件 ID 映射
     */
    private Map<String, List<Long>> diffInstanceIds(CmsPageInstance page) {
        if(null == page || page.getDbId() == 0) {
            return Collections.emptyMap();
        }
        CmsPageInstance fromDb = getById(page.getDbId());
        if(null == fromDb) {
            return Collections.emptyMap();
        }
        Set<Long> allLayoutIds = new HashSet<Long>();
        Set<Long> allColumnIds = new HashSet<Long>();
        Set<Long> allModuleIds = new HashSet<Long>();
        for(CmsLayoutInstance layout : fromDb.getLayouts()) {
            allLayoutIds.add(layout.getDbId());
            for(CmsColumnInstance column : layout.getColumns()) {
                allColumnIds.add(column.getDbId());
                for(CmsModuleInstance module : column.getModules()) {
                    allModuleIds.add(module.getDbId());
                }
            }
        }

        List<Long> diffLayoutIds = new ArrayList<Long>();
        List<Long> diffColumnIds = new ArrayList<Long>();
        List<Long> diffModuleIds = new ArrayList<Long>();
        for(CmsLayoutInstance layout : page.getLayouts()) {
            if(!allLayoutIds.contains(layout.getDbId())) {
                diffLayoutIds.add(layout.getDbId());
            }
            for(CmsColumnInstance column : layout.getColumns()) {
                if(!allColumnIds.contains(column.getDbId())) {
                    diffColumnIds.add(column.getDbId());
                }
                for(CmsModuleInstance module : column.getModules()) {
                    if(!allModuleIds.contains(module.getDbId())) {
                        diffModuleIds.add(module.getDbId());
                    }
                }
            }
        }

        Map<String, List<Long>> diff = new HashMap<String, List<Long>>();
        diff.put(CmsLayoutInstance.TYPE_TAG, diffLayoutIds);
        diff.put(CmsColumnInstance.TYPE_TAG, diffColumnIds);
        diff.put(CmsModuleInstance.TYPE_TAG, diffModuleIds);

        return diff;
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
