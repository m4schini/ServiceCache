package com.github.m4schini.servicecache.cache;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UseCache {
    /**
     * Data is saved with this path in cache.
     * @implNote If path is changed, cache has to be manually cleaned
     * @return path
     */
    String path();

    /**
     * When this age was passed, data remains in cache, but is considered expired.
     * Default is 60 seconds
     * @return age in seconds
     */
    int maxAge() default 60;
}
