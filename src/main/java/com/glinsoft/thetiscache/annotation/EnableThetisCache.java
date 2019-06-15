package com.glinsoft.thetiscache.annotation;

import com.glinsoft.thetiscache.concurrent.DataCoordinate;
import com.glinsoft.thetiscache.concurrent.EhCacheEvictEvent;
import com.glinsoft.thetiscache.context.ContextStorage;
import com.glinsoft.thetiscache.ehcache.EhCacheManagerConfiguration;
import com.glinsoft.thetiscache.ehcache.EhCacheStorage;
import com.glinsoft.thetiscache.redis.RedisStorage;
import com.glinsoft.thetiscache.redis.RedisTemplateConfiguration;
import com.glinsoft.thetiscache.session.SessionStorage;
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
