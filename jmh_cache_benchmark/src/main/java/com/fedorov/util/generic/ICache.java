package com.fedorov.util.generic;


public interface ICache<T> {
    public T get(String key);
    public void put( String key, T obj);
 
    public final int MAX_CACHE_SIZE = 10000;

    public void shutdown();
}
