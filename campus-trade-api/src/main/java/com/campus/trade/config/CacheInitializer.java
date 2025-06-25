package com.campus.trade.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 缓存初始化器
 * 这个组件的作用是在整个Spring应用成功启动并准备就绪后，
 * 自动执行一次缓存清理操作。
 * 这是一个在开发和调试阶段非常有用的技巧，可以确保每次重启应用后，
 * 都能从数据库加载最新的数据，避免受到旧缓存的影响。
 */
@Component
public class CacheInitializer {

    private static final Logger log = LoggerFactory.getLogger(CacheInitializer.class);

    private final CacheManager cacheManager;

    // 通过构造函数注入Spring的缓存管理器
    public CacheInitializer(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * @EventListener(ApplicationReadyEvent.class) 注解
     * 标记这个方法是一个事件监听器，它会监听 "应用已就绪" 这个特定事件。
     * 当Spring Boot应用完全启动成功后，会发布这个事件，从而自动触发此方法的执行。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info(">>> [缓存初始化] 应用已启动，开始清空所有已知缓存...");

        // 遍历所有在用的缓存名称，并逐一清空
        cacheManager.getCacheNames().forEach(cacheName -> {
            log.info("--- 正在清空缓存: {}", cacheName);
            cacheManager.getCache(cacheName).clear();
        });

        log.info("<<< [缓存初始化] 所有缓存已成功清空。");
    }
}
