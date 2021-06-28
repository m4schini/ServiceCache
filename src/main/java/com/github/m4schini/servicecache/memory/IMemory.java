package com.github.m4schini.servicecache.memory;

import java.io.Closeable;
import java.io.Serializable;


public interface IMemory<T> extends Closeable {
    void set(String key, Serializable data);

    void set(String key, T data);

    T get(String key);
}
