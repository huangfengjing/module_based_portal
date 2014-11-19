package com.alibaba.ydt.portal.exception;

/**
 * <p>
 * 模块渲染异常
 * </p>
 * Time: 12-11-30 上午10:47
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class RenderException extends RuntimeException {

    /**
     * Default constructor
     */
    public RenderException() {
        super("VM渲染失败");
    }

    /**
     * Constructor with the exception message
     *
     * @param msg exception message
     */
    public RenderException(String msg) {
        super(msg);
    }

    /**
     * Constructor with the exception source
     *
     * @param e exception source
     */
    public RenderException(Throwable e) {
        super("VM渲染失败", e);
    }

    /**
     * Constructor with the exception source and message
     *
     * @param msg exception message
     * @param e   exception source
     */
    public RenderException(String msg, Throwable e) {
        super("VM渲染失败:" + msg, e);
    }
}
