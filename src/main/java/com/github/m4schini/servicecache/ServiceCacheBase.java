package com.github.m4schini.servicecache;

import com.github.m4schini.servicecache.cache.Cache;
import io.lettuce.core.RedisClient;

public class ServiceCacheBase {
    protected final Cache cache;

    ServiceCacheBase() {
        this.cache = new Cache(RedisClient.create("redis://localhost").connect());
    }
}
