package com.glinsoft.thetiscache.session;


import com.glinsoft.thetiscache.console.Console;
import com.glinsoft.thetiscache.ehcache.EhCacheStorage;
import com.glinsoft.thetiscache.redis.RedisStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Order(3)
public class SessionStorage {


    @Autowired
    private RedisStorage redisStorage;
    @Autowired
    private EhCacheStorage ehCacheStorage;


    @Value("${spring.application.name:}")
    private String applicationName;

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    /**
     * Session 值存储
     *
     * @param key
     * @param value
     * @param region session的域，共享缓存时需配置
     */
    public void put(String key, Object value, String... region) {
        String[] keys = getSessionKeys(key, region);
        for (String aKey : keys) {
            saveSession(aKey, value);
            Console.println("[Thetis-Cache] Session存值，Key："+aKey);
        }
    }

    /**
     * 值读取
     *
     * @param key
     * @param region session的域，共享缓存时需配置
     * @return
     */
    public Object get(String key, String... region) {
        String[] keys = getSessionKeys(key, region);
        for (String aKey : keys) {
            Object val = getSession(aKey);
            if (val != null) {
                Console.println("[Thetis-Cache] Session取值，Key："+aKey);
                return val;
            }
        }
        return null;
    }


    /**
     * 删除指定Key的session存储
     *
     * @param key
     * @param region session的域，共享缓存时需配置
     */
    public void remove(String key, String... region) {
        String[] keys = getSessionKeys(key, region);
        for (String aKey : keys) {
            deleteSession(aKey);
            Console.println("[Thetis-Cache] Session删值，Key："+aKey);
        }
    }


    private String[] getSessionKeys(String key, String... region) {
        String[] keys;
        if (region == null || region.length == 0) {
            keys = new String[1];
            if(!StringUtils.isEmpty(applicationName)){
                key = "session:" + applicationName + ":" + key;
            }else{
                String userDir = System.getProperty("user.dir");
                userDir = userDir.substring(userDir.lastIndexOf(File.separator)+1);
                key = "session:" + userDir + ":" + key;
            }
            keys[0] = key;
        } else {
            keys = new String[region.length];
            for (int i = 0; i < region.length; i++) {
                String t = "session:" + region[i] + ":" + key;
                keys[i] = t;
            }
        }
        return keys;
    }


    private void saveSession(String key, Object value) {
        ehCacheStorage.set(key, value);
        if (redisStorage.getRedisUsable()) {
            redisStorage.set(key, value);
        }
        executorService.schedule(()->{
            ehCacheStorage.del(key);
            if (redisStorage.getRedisUsable()) {
                redisStorage.del(key);
            }
        },2, TimeUnit.HOURS);
    }

    private Object getSession(String key) {
        Object value = ehCacheStorage.get(key);
        if (value == null && redisStorage.getRedisUsable()) {
            value = redisStorage.get(key);
        }
        return value;
    }

    private void deleteSession(String key) {
        ehCacheStorage.del(key);
        if (redisStorage.getRedisUsable()) {
            redisStorage.del(key);
        }
    }


}
