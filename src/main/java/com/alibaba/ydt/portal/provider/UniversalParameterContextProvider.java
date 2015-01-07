package com.alibaba.ydt.portal.provider;

import com.alibaba.ydt.portal.domain.*;
import com.alibaba.ydt.portal.service.ContextProvider;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 统一的参数环境供应器，将获取并注入模块的参数
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-24 上午10:53.
 */
@Component
public class UniversalParameterContextProvider implements ContextProvider {

    @Override
    public RenderContext createContext(BaseCmsInstance instance, HttpServletRequest request) {
        if (null != instance) {
            List<ParameterValuePair> params = instance.getParameters();
            RenderContextBuilder builder = RenderContextBuilder.newBuilder();
            if(instance instanceof CmsModuleInstance) {
                builder.addModuleParams(params);
            } else if (instance instanceof CmsColumnInstance) {
                builder.addColumnParams(params);
            } else if (instance instanceof CmsLayoutInstance) {
                builder.addLayoutParams(params);
            } else if (instance instanceof CmsPageInstance) {
                builder.addPageParams(params);
            }
            return builder.build();
        }
        return null;
    }

    @Override
    public RenderContext createFormContext(BaseCmsInstance instance, HttpServletRequest request) {
        if (null != instance) {
            List<ParameterValuePair> params = instance.getParameters();
            RenderContextBuilder builder = RenderContextBuilder.newBuilder();
            if(instance instanceof CmsModuleInstance) {
                builder.addModuleParams(params);
            } else if (instance instanceof CmsColumnInstance) {
                builder.addColumnParams(params);
            } else if (instance instanceof CmsLayoutInstance) {
                builder.addLayoutParams(params);
            } else if (instance instanceof CmsPageInstance) {
                builder.addPageParams(params);
            }
            return builder.build();
        }
        return null;
    }

    @Override
    public boolean support(BaseCmsInstance instance) {
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
