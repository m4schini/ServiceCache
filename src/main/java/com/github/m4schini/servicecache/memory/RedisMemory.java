package com.github.m4schini.servicecache.memory;

import com.github.m4schini.servicecache.cache.CacheItem;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

public class RedisMemory implements IMemory<CacheItem>, Closeable {

    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> redisCommands;

    public RedisMemory(String host) {
        this(RedisClient.create(host));
    }

    public RedisMemory(RedisClient client) {
        this.connection = client.connect();
        this.redisCommands = this.connection.sync();
    }

    @Override
    public void set(String key, Serializable data) {
        this.set(key, new CacheItem(CacheItem.serialize(data)));
    }

    @Override
    public void set(String key, CacheItem cacheItem) {
        redisCommands.set(key, CacheItem.serialize(cacheItem));
    }

    @Override
    public CacheItem get(String key) {
        String raw = redisCommands.get(key);
        return raw == null ? new CacheItem() : ((CacheItem) CacheItem.deserialize(raw));
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }

}
