package com.alibaba.ydt.portal.common;

/**
 * 应用中的常量
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 14-12-30 上午10:14.
 */
public interface AppConstants {

    /**
     * 返回结果正常
     */
    public static final int RET_CODE_OK = 0;

    /**
     * 返回结果错误，异常码未定义
     */
    public static final int RET_CODE_ERR = -1;

    /**
     * 异常码：org.hibernate.exception.ConstraintViolationException
     */
    public static final int RET_CODE_ERR_CVE = -101;
}
