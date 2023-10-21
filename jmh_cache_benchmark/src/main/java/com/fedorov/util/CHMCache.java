package com.fedorov.util;

import java.util.concurrent.ConcurrentHashMap;

public class CHMCache<T> implements ICacheIterable<T> {
 
    protected static CHMCache<Object> instance;
    private ConcurrentHashMap<String,T> cache;

    private CHMCache(){
        cache = new ConcurrentHashMap<>((int) (MAX_CACHE_SIZE*1.4));
    }

    public static ICache<Object> getInstance(){
        if( instance == null ){
            synchronized(CHMCache.class){
                if( instance == null){
                    CHMCache<Object> localInstance = new CHMCache<>();
                    instance = localInstance;
                }
            }
        }
        return instance;
    }

    @Override
    public T get(String key) {
        return cache.get(key);
    }

    @Override
    public void put(String key, T obj) {
        cache.put(key, obj);
    }
    

    public void shutdown(){}

    @Override
    public void doForEach(Callable<T> coll) throws Exception {
        for( String key : cache.keySet() ){
           put(key, coll.call(key) );
        }
    }
}
