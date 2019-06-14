package com.github.thetiscache.concurrent;

import com.github.thetiscache.console.Console;
import com.github.thetiscache.redis.RedisStorage;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class EhCacheEvictEvent implements CacheEventListener {
    @Autowired
    private RedisStorage redisStorage;

    @Override
    public void onEvent(CacheEvent cacheEvent) {
        if (cacheEvent.getType().equals(EventType.EXPIRED)) {
            Object objKey = cacheEvent.getKey();
            if (objKey != null) {
                String key = objKey.toString();
                redisStorage.del(key);
                Console.println("[Thetis-Cache] 数据已过期，Key：" + objKey);
            }
        }
    }
}
