package com.alibaba.ydt.portal.web.mvc;

import com.alibaba.ydt.portal.web.util.StringUtils;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

import java.text.NumberFormat;

/**
 * <p>
 * 自定义的数字编辑器，处理各种数字输入，主要是为了防止 null 或者空字符串在做数字类型转换时出错
 * </p>
 * Time: 12-2-29 下午6:08
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class PortalNumberEditor extends CustomNumberEditor {

    public PortalNumberEditor(Class<? extends Number> numberClass, boolean allowEmpty) throws IllegalArgumentException {
        super(numberClass, allowEmpty);
    }

    public PortalNumberEditor(Class<? extends Number> numberClass, NumberFormat numberFormat, boolean allowEmpty) throws IllegalArgumentException {
        super(numberClass, numberFormat, allowEmpty);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setAsText(String text) throws IllegalArgumentException {
        super.setAsText(StringUtils.defaultIfEmpty(text, "0"));
    }

    @Override
    public void setValue(Object value) {
        if(null == value) {
            super.setAsText("0");
            return;
        }
        if(value instanceof String) {
            value = StringUtils.defaultIfEmpty((String)value, "0");
        }
        super.setValue(value);
    }
}
