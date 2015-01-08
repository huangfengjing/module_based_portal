package com.alibaba.ydt.portal.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * <p>
 * 基于 ThreadLocal 的缓存，保证每次请求只会调用一次
 * </p> 
 * Time: 13-1-4 下午7:42
 * @version 1.0
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 */
@Aspect
@Component
public class ThreadLocalCacheAspect {

    private static final Log logger = LogFactory.getLog(ThreadLocalCacheAspect.class);

    private static ThreadLocal<Map<String, Object>> threadLocalCache = new ThreadLocal<Map<String, Object>>();

    @Around("@annotation(com.alibaba.ydt.portal.cache.ThreadLocalCacheable)")
    public Object invokeWithCache(ProceedingJoinPoint pjp) throws Throwable {
        if(null == threadLocalCache.get()) {
            threadLocalCache.set(new WeakHashMap<String, Object>());
        }
        Map<String, Object> cached = threadLocalCache.get();
        String key = genKey(pjp.getTarget(), pjp.getArgs());
        if(cached.containsKey(key)) {
            logger.debug("Thread local cache hit, return cached result");
            return cached.get(key);
        }
        Object result =  pjp.proceed();
        cached.put(key, result);
        threadLocalCache.set(cached);
        return result;
    }

    private String genKey(Object target, Object[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("_t_cache_key_").append(target.getClass().getName()).append("_");
        for(Object arg : args) {
            sb.append(arg.getClass().hashCode()).append("_").append(arg.hashCode());
        }
        return sb.toString();
    }
}
