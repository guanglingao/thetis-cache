package com.glinsoft.thetiscache.ehcache;


import com.glinsoft.thetiscache.concurrent.EhCacheEvictEvent;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Order(2)
public class EhCacheStorage {

    protected static final String EHCACHE_NAME = "EHCACHE_FOR_THETIS";

    private static Cache cache = null;

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private EhCacheEvictEvent event;

    /**
     * 保存缓存值
     * @param key
     * @param value
     */
    public void set(String key, Object value){
        Cache cache = getCache();
        cache.put(key,value);
    }

    /**
     * 保存，并在 duration 后删除
     * @param key
     * @param value
     * @param duration
     */
    public void set(String key,Object value,Long duration){
        Cache cache = getCache();
        cache.put(key,value);
        if(duration==null){
            duration = 7*24*60*60*1000L;
        }
        executorService.schedule(()->{
            cache.remove(key);
        },duration, TimeUnit.MILLISECONDS);
    }

    /**
     * 读取缓存值
     * @param key
     * @param <T>
     * @return
     */
    public <T extends Object> T get(String key){
        Cache cache = getCache();
        return (T)cache.get(key);
    }


    /**
     * 删除元素
     * @param key
     */
    public void del(String key){
        Cache cache = getCache();
        cache.remove(key);
    }



    private Cache getCache(){
        if(cache==null){
            cache = cacheManager.getCache(EhCacheStorage.EHCACHE_NAME,String.class,Object.class);
            cache.getRuntimeConfiguration().registerCacheEventListener(event, EventOrdering.UNORDERED
            , EventFiring.ASYNCHRONOUS
            , EnumSet.of(EventType.EXPIRED));
        }
        return cache;
    }


}
