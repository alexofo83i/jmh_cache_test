package com.fedorov.util;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RLCacheLRU<T> implements ICacheIterable<T> {
 
    protected static RLCacheLRU<Object> instance;
    private HashMap<String,T> cache;
    private Deque<String> dq;
    private ReentrantReadWriteLock.WriteLock rwlock;
    private ReentrantReadWriteLock.ReadLock  rdlock;

    private RLCacheLRU(){
        cache = new HashMap<>((int) (MAX_CACHE_SIZE*1.4));
        dq = new LinkedList<>();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        rwlock = lock.writeLock();
        rdlock = lock.readLock();
    }

    public static ICache<Object> getInstance(){
        if( instance == null ){
            synchronized(RLCacheLRU.class){
                if( instance == null){
                    RLCacheLRU<Object> localInstance = new RLCacheLRU<>();
                    instance = localInstance;
                }
            }
        }
        return instance;
    }

    @Override
    public T get(String key) {
        rdlock.lock();
        T res = null;
        try{
            res = cache.get(key);
            if(res != null) {
                dq.remove(key);
                dq.addFirst(key);
            }
        }finally{
            rdlock.unlock();
        }
        return res;
    }

    @Override
    public void put(String key, T obj) {
        rwlock.lock();
        try{
            T res = cache.get(key);
            if( res != null ){
                cache.put(key, obj);
                dq.remove(key);
                dq.addFirst(key);
            }else {
                if(dq.size() == MAX_CACHE_SIZE) {
                    String keyToRemove = dq.removeLast();
                    cache.remove(keyToRemove);
                }
                dq.addFirst(key);
                cache.put(key, obj);
            }
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
