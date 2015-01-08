package com.alibaba.ydt.portal.service;

import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.RenderResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * 渲染日志审计服务
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 15/1/8 上午10:25.
 */
@Component
public class RenderLogAuditService implements RenderAuditService {

    private Log logger = LogFactory.getLog(getClass());

    public boolean before(Object instance, RenderContext context) {
        return true;
    }

    @Override
    public RenderResult after(Object instance, RenderResult result) {
        String str = "内容为空";
        switch (null != result ? result.getResultType() : 0) {
            case 1: str = "正常渲染";break;
            case 2: str = "缓存渲染";break;
            case 3: str = "异常渲染";break;
            case 4: str = "拦截跳过";break;
        }
        logger.info("渲染组件：" + instance + ", result type: " + str);
        return result;
    }
}
