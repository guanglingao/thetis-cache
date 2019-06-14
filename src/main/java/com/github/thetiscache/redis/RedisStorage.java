package com.github.thetiscache.redis;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Component
@Order(2)
public class RedisStorage {


    @Autowired
    private RedisTemplate<String, Object> template;

    // RedisServer是否能正常连接
    private static boolean redisUsable = false;
    // 当连接失败时，后续尝试连接次数
    private static int tryCount = 30;


    /**
     * 缓存，保存值
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        template.opsForValue().set(key, value);
    }


    /**
     * 根据key，读取缓存值
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T extends Object> T get(String key) {
        return (T) template.opsForValue().get(key);
    }


    /**
     * 删除元素
     *
     * @param key
     */
    public void del(String key) {
        template.delete(key);
    }

    /**
     * 清理全部缓存数据
     */
    public void clear() {
        template.discard();
    }


    /**
     * Redis 是否可用
     *
     * @return
     */
    public boolean getRedisUsable() {
        if (!redisUsable) {
            if (tryCount > 0) {
                try {
                    template.opsForValue().get("TEST_CONNECTION_KEY");
                    redisUsable = true;

                } catch (Exception e) {
                    System.out.println("[Thetis-Cache]  Redis Server 未能成功连接。未启用二级缓存，Session也将无法共享。");
                    redisUsable = false;
                }
                tryCount--;
            }
        }
        return redisUsable;
    }


}
