package com.github.thetiscache.annotation;


import com.github.thetiscache.concurrent.DataCoordinate;
import com.github.thetiscache.console.Console;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Aspect
@Order(4)
public class CacheThisAspect {


    @Autowired
    private DataCoordinate coordinate;





    @Pointcut(value = "execution(@com.github.thetiscache.annotation.CacheThis * * (..))")
    public void cache() {
    }

    @Around("cache()")
    public Object aroundCacheMethods(ProceedingJoinPoint thisJoinPoint) throws Throwable {

        long start;
        long end;
        start = System.currentTimeMillis();
        String key = KeyGenerator.getCacheThisKey(thisJoinPoint);
        if(key==null || key.trim().equals("")){
            return thisJoinPoint.proceed();
        }
        Object value = coordinate.getLevel1Val(key);
        if (value == null) {
            if (coordinate.isLevel2Usable()) {
                value = coordinate.getLevel2Val(key);
                if (value == null) {
                    value = thisJoinPoint.proceed();
                    if (value == null) {
                        coordinate.fulfillLevel1NullValue(key);
                    } else {
                        coordinate.fulfillLevel1(key, value);
                        coordinate.fulfillLevel2(key, value);
                    }
                } else {
                    end = System.currentTimeMillis();
                    Console.println("[Thetis-Cache] 二级缓存命中，Key："+key+", 耗时："+(end-start)+"ms 。");
                    coordinate.fulfillLevel1(key, value);
                }
            }else{
                value = thisJoinPoint.proceed();
                if (value == null) {
                    coordinate.fulfillLevel1NullValue(key);
                } else {
                    coordinate.fulfillLevel1(key, value);
                }
            }

        } else {
            end = System.currentTimeMillis();
            Console.println("[Thetis-Cache] 一级缓存命中，Key："+key+", 耗时："+(end-start)+"ms 。");
            if (value instanceof NullValObject) {
                return null;
            }
        }
        return value;
    }



}
