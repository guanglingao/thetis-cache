package com.glinsoft.thetiscache.context;


import com.glinsoft.thetiscache.ehcache.EhCacheStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(3)
public class ContextStorage {

    @Autowired
    private EhCacheStorage ehCacheStorage;


    /**
     * 存储
     * @param key
     * @param value
     * @param duration
     */
    public void put(String key, Object value, Long... duration){
        if(duration==null || duration.length==0){
            ehCacheStorage.set(key,value);
        }else{
            ehCacheStorage.set(key,value,duration[0]);
        }
    }


    /**
     * 取值
     * @param key
     * @return
     */
    public Object get(String key){
        return ehCacheStorage.get(key);
    }

    /**
     * 删除缓存
     * @param key
     */
    public void remove(String key){
        ehCacheStorage.del(key);
    }
}
