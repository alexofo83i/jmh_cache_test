package com.fedorov.util.generic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
 
public class RLCache<T> implements ICacheIterable<T> {

    private static volatile RLCache<Object> instance;

    ReentrantReadWriteLock.WriteLock rwlock;
    ReentrantReadWriteLock.ReadLock  rdlock;
    HashMap<String,T> cache;

    private RLCache(){
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        rwlock = lock.writeLock();
        rdlock = lock.readLock();
        cache = new HashMap<>( (int) (MAX_CACHE_SIZE * 1.4) );
    }

    public static RLCache<Object> getInstance(){
        if( instance == null ){
            synchronized(RLCache.class){
                if(instance == null){
                    RLCache<Object> localInstance = new RLCache<>();
                    instance = localInstance;
                }
            }
        }
        return instance;
    }

    @Override
    public T get(String key) {
         T res = null;
         rdlock.lock();
         try{
            res = cache.get(key);
         }
         finally{
            rdlock.unlock();
         } 
         return res;
    }

    @Override
    public void put(String key, T obj) {
        rwlock.lock();
        try{
            cache.put(key, obj);
        }
        finally{
           rwlock.unlock();
        }
    }
    
    @Override
    public void shutdown(){}

    @Override
    public void doForEach(Callable<T> coll) throws Exception {
        Set<String> keySet = null;
        rdlock.lock();
        try{
            keySet = new HashSet<>(cache.keySet());
        }
        finally{
            rdlock.unlock();
        }
        
        for( String key :  keySet){
            T value = coll.call(key);
            put(key,  value);
        }
    }
}