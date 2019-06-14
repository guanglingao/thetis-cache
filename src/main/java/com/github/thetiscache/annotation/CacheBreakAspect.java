package com.github.thetiscache.annotation;


import com.github.thetiscache.concurrent.DataCoordinate;
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
public class CacheBreakAspect {


    @Autowired
    private DataCoordinate coordinate;



    @Pointcut(value = "execution(@com.github.thetiscache.annotation.CacheBreak * * (..))")
    public void cache() {
    }

    @Around("cache()")
    public Object aroundCacheMethods(ProceedingJoinPoint thisJoinPoint) throws Throwable {

        String key = KeyGenerator.getCacheBreakKey(thisJoinPoint);
        if (key != null && !key.trim().equals("")) {
            coordinate.omitLevel1(key);
            if (coordinate.isLevel2Usable()) {
                coordinate.omitLevel2(key);
            }
        }
        return thisJoinPoint.proceed();
    }




}
