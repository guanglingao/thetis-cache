package com.github.thetiscache.concurrent;

import com.github.thetiscache.annotation.NullValObject;
import com.github.thetiscache.ehcache.EhCacheStorage;
import com.github.thetiscache.redis.RedisStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
@Order(3)
public class DataCoordinate {

    @Autowired
    private RedisStorage redisStorage;

    @Autowired
    private EhCacheStorage ehCacheStorage;

    // 执行线程池
    private static ExecutorService executorService = Executors.newCachedThreadPool();



    /**
     * 删除二级缓存
     * @param key
     */
    public void omitLevel2(String key){
        executorService.execute(()-> redisStorage.del(key));
    }

    /**
     * 填充二级缓存
     * @param key
     * @param value
     */
    public void fulfillLevel2(String key, Object value){
        executorService.execute(()->redisStorage.set(key,value));
    }


    /**
     * 删除一级缓存
     * @param key
     */
    public void omitLevel1(String key){
        executorService.execute(()->ehCacheStorage.del(key));
    }

    /**
     * 填充一级缓存
     * @param key
     * @param value
     */
    public void fulfillLevel1(String key, Object value){
        executorService.execute(()->ehCacheStorage.set(key,value));
    }

    /**
     * 一级缓存填充空值（21s）防击穿
     * @param key
     */
    public void fulfillLevel1NullValue(String key){
        executorService.execute(()->ehCacheStorage.set(key,new NullValObject(),21*1000L));
    }

    /**
     * 读取一级缓存值
     * @param key
     * @return
     */
    public Object getLevel1Val(String key){
        return ehCacheStorage.get(key);
    }


    /**
     * 读取二级缓存值
     * @param key
     * @return
     */
    public Object getLevel2Val(String key){
        return redisStorage.get(key);
    }


    /**
     * 二级缓存是否可用
     * @return
     */
    public boolean isLevel2Usable(){
       return  redisStorage.getRedisUsable();
    }



}
