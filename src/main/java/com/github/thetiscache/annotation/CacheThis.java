package com.github.thetiscache.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheThis {


    Class<? extends Object> model() default DefaultModel.class;

    String key() default "";


}
