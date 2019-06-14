package com.github.thetiscache.annotation;

import com.github.thetiscache.concurrent.DataCoordinate;
import com.github.thetiscache.concurrent.EhCacheEvictEvent;
import com.github.thetiscache.context.ContextStorage;
import com.github.thetiscache.ehcache.EhCacheManagerConfiguration;
import com.github.thetiscache.ehcache.EhCacheStorage;
import com.github.thetiscache.redis.RedisStorage;
import com.github.thetiscache.redis.RedisTemplateConfiguration;
import com.github.thetiscache.session.SessionStorage;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({
        EhCacheManagerConfiguration.class,
        EhCacheStorage.class,
        RedisTemplateConfiguration.class,
        RedisStorage.class,
        DataCoordinate.class,
        EhCacheEvictEvent.class,
        SessionStorage.class,
        ContextStorage.class,
        CacheBreakAspect.class,
        CacheThisAspect.class
        })
public @interface EnableThetisCache {
}
