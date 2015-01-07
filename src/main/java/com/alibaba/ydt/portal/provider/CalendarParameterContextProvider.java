package com.alibaba.ydt.portal.provider;

import com.alibaba.ydt.portal.domain.BaseCmsInstance;
import com.alibaba.ydt.portal.domain.CmsModuleInstance;
import com.alibaba.ydt.portal.domain.ParameterValuePair;
import com.alibaba.ydt.portal.service.RenderContext;
import com.alibaba.ydt.portal.service.RenderContextBuilder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 日历参数供应器
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-11-24 上午10:53.
 */
@Component
public class CalendarParameterContextProvider extends UniversalParameterContextProvider {

    @Override
    public RenderContext createContext(BaseCmsInstance instance, HttpServletRequest request) {
        if (null == instance || !(instance instanceof CmsModuleInstance)) {
            return null;
        }
        List<ParameterValuePair> params = instance.getParameters();
        params.add(new ParameterValuePair("naked", true));
        RenderContextBuilder builder = RenderContextBuilder.newBuilder();
        builder.addModuleParams(params);
        return builder.build();
    }

    @Override
    public boolean support(BaseCmsInstance instance) {
        return instance instanceof CmsModuleInstance && instance.getPrototypeId() == 3;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
