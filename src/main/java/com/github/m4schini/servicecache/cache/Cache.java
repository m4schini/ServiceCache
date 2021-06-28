package com.github.m4schini.servicecache.cache;

import com.github.m4schini.servicecache.memory.IMemory;
import com.github.m4schini.servicecache.memory.InMemory;
import com.github.m4schini.servicecache.memory.RedisMemory;
import io.lettuce.core.RedisClient;
import com.github.m4schini.servicecache.exception.MissingAnnotationException;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class Cache {

    private final IMemory<CacheItem> memory;

    public Cache() {
        this.memory = new InMemory();
    }

    public Cache(RedisClient client) {
        this.memory = new RedisMemory(client);
    }

    public Cache(String host) {
        this.memory = new RedisMemory(host);
    }

    /**
     * Pushes new data into cache, overriding existing data
     * @param key key of item
     * @param data data that should be cached
     */
    public void push(String key, Serializable data) {
        memory.set(key, data);
    }

    /**
     * Retrieves an item from the Cache
     * @param key key of item
     * @return null if item not found, CacheItem object if item was found
     */
    public CacheItem retrieve(String key) {
        return memory.get(key);
    }

    /**
     * Checks if the given key exists in the Database.
     * IGNORES EXPIRATION
     * use {@code net.ciwox.facts.cache.Cache.isExpired}
     * @param key of cache item
     * @return true, if key exists in cache
     */
    public boolean has(String key) {
        return memory.get(key) != null;
    }

    /**
     * Cache processor, handles cache retrieval and fallback for simple methods (1 Argument).
     * @param key the key the data is stored under in the cache
     * @param fetcher actual fetcher for the data, if cache misses, data is retrieved from fetcher.
     *                The fetcher needs to return an object that implements {@code Serializable}
     * @param <R> return type
     * @return cached data, or fetched data
     * @throws MissingAnnotationException when the calling method doesn't have a {@code @UseCache} Annotation
     * @implNote The calling method needs to have the {@code @UseCache} Annotation, otherwise this method will fail
     */
    public <R extends Serializable> R process(Object key, Callable<R> fetcher) {
        String cacheKey = getCachePath() + (key == null ? "" : key.toString());
        CacheItem item = this.retrieve(cacheKey);

        assert item != null;

        if (item.isCached()) {
            if (!item.isExpired(getCacheMaxAge())) {
                // Using Cache
                return item.getValue();
            }
        }

        // Using fetcher (external source)
        R value = null;
        try {
            value = fetcher.call();
        } catch (Exception e) {
            // who cares, this isn't production code
        }

        // checking if fetcher call returned data
        if (value != null) {
            this.push(cacheKey, value);
            return value;
        } else {
            // Fallback on expired Cache
            if (item.isCached()) {
                return item.getValue();
            } else {
                // If fallback is not possible, throws com.github.m4schini.servicecache.exception
                throw new RuntimeException("UNAVAILABLE");
            }
        }
    }

    /**
     * Retrieves the method, that called the method, that is calling this method.
     * @param depth how many steps back the stacktrace has to go
     * @return method, if not found: null
     */
    private static Method getCallingMethod(int depth) {
        //Trace stack
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement traceElement = stackTraceElements[depth];
        try {
            //Get method name from stacktrace
            Class<?> clazz = Class.forName(traceElement.getClassName());
            String methodName = traceElement.getMethodName();

            //Find method in class by methodName only (without parameter)
            Method method = null;
            for (Method m : clazz.getMethods()) {
                if (m.getName().equals(methodName)) {
                    method = m;
                    break;
                }
            }

            return method;
        } catch (RuntimeException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the cache path specified in the {@code @UseCache} Annotation
     * @return cache path
     */
    private static String getCachePath() throws MissingAnnotationException {
        Method method = getCallingMethod(4);
        try {
            assert method != null;
            return method.getAnnotation(UseCache.class).path();
        } catch (NullPointerException e) {
            throw new MissingAnnotationException("UseCache", method);
        }
    }

    /**
     * Retrieves the cache max-age specified in the {@code @UseCache} Annotation
     * @return cache max-age
     */
    private static int getCacheMaxAge() throws MissingAnnotationException {
        Method method = getCallingMethod(4);
        try {
            assert method != null;
            return method.getAnnotation(UseCache.class).maxAge();
        } catch (NullPointerException e) {
            throw new MissingAnnotationException("UseCache", method);
        }
    }
}
