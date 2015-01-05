package com.alibaba.ydt.portal.domain;

/**
 * 当前用户信息
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 15/1/5 下午4:20.
 */
public interface AppUser {

    /**
     * 获取用户的标识
     * @return 用户的标识
     */
    public String getIdentifier();
}
