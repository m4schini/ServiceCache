package com.github.m4schini.servicecache;

import com.github.m4schini.servicecache.cache.Cache;

/**
 * Use this as a super class to use the ServiceCache
 */
abstract public class ServiceCacheBase {
    protected final Cache cache;

    /**
     * Constructs cache with in in-memory cache
     */
    public ServiceCacheBase() {
        this.cache = new Cache();
    }

    /**
     * Uses redis instead of in-memory for the cache
     * @param host url of redis database
     */
    public ServiceCacheBase(String host) {
        this.cache = new Cache(host);
    }
}
