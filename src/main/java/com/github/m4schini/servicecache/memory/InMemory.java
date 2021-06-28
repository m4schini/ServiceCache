package com.github.m4schini.servicecache.memory;

import com.github.m4schini.servicecache.cache.CacheItem;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class InMemory implements IMemory<CacheItem> {
    private final HashMap<String, String> map;

    public InMemory() {
        map = new HashMap<>();
    }

    @Override
    public void set(String key, Serializable data) {
        this.set(key, new CacheItem(CacheItem.serialize(data)));
    }

    @Override
    public void set(String key, CacheItem cacheItem) {
        map.put(key, CacheItem.serialize(cacheItem));
    }

    @Override
    public CacheItem get(String key) {
        String raw = map.get(key);
        return raw == null ? new CacheItem() : ((CacheItem) CacheItem.deserialize(raw));
    }

    @Override
    public void close() throws IOException {}
}
