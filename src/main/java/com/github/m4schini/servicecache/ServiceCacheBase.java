package com.github.m4schini.servicecache;

import com.github.m4schini.servicecache.cache.Cache;
import io.lettuce.core.RedisClient;

abstract public class ServiceCacheBase {
    protected final Cache cache;

    ServiceCacheBase() {
        this.cache = new Cache();
    }

    ServiceCacheBase(String host) {
        this.cache = new Cache(host);
    }
}
