package com.alibaba.ydt.portal.web.mvc;

import com.alibaba.ydt.portal.cache.ThreadLocalCacheAspect;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 清除线程级别的缓存
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 15/1/9 上午11:43.
 */
public class ThreadLocalCacheClearFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ThreadLocalCacheAspect.clearCache();
        filterChain.doFilter(request, response);
    }
}
