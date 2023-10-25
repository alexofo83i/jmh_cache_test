package com.fedorov.util.generic;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/*
 *  ehcache.xml 
 * <ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="ehcache.xsd" updateCheck="true"
    monitoring="autodetect" dynamicConfig="true">

        <diskStore path="d:\\ehcache" />
    
        <!-- Sample cache named cache1 -->
        This cache contains a maximum in memory of 1000 elements.
    
        <cache name="myEhCache" 
            maxEntriesOnHeap="10000"
              maxBytesLocalOffHeap="8g"
              <persistence strategy="distributed"/>
        </cache>

 * </ehcache>
 */

public class MyCache<T> implements ICache<T>, Serializable {

    private static final long serialVersionUID = 1L;
    private static CacheManager ehCacheManager;
    private volatile static MyCache<Object> instance;
    private final ConcurrentHashMap<String, SoftReference<T>> localCache = new ConcurrentHashMap<>();
    
    private  MyCache() {
    }
    
    public static MyCache<Object> getInstance() {
        if (instance == null) {
            synchronized (MyCache.class) {
                if (instance == null)
                    instance = new MyCache<Object>();
                    ehCacheManager = CacheManager.newInstance("ehcache.xml");
                    ehCacheManager.addCacheIfAbsent("myEhCache");
            }
        }
        return instance;
    }
 
    public T get(String prop1){
        if (!localCache.containsKey(prop1) || localCache.get(prop1).get() == null) {
            Cache ehCache = ehCacheManager.getCache("myEhCache");
            if(ehCache != null && ehCache.isElementInMemory(prop1)) {
                Element cache = ehCache.get(prop1);
                if(cache != null) {
                    SoftReference<T> value1 = (SoftReference<T>) cache.getObjectValue();
                    if(value1 != null) {
                        localCache.put(prop1, value1);
                    }
                }
            }
        }
        return localCache.get(prop1).get();
    }
 
    public void put(String prop1, T object){
        SoftReference<T> reference = new SoftReference<T>(object);
        localCache.put(prop1, reference);
        Cache globalCache = ehCacheManager.getCache("myEhCache");
        // synchronized(globalCache){
            globalCache.acquireWriteLockOnKey(prop1);
            globalCache.put(new Element(prop1, reference));
            if(localCache.size()>globalCache.getCacheConfiguration().getMaxEntriesLocalDisk()){
                refreshLocalCache();
            }
        // }
    }
    
    public void put(T object, String... cacheKeys){
        String[] keys = cacheKeys;
        SoftReference<T> reference = new SoftReference<T>(object);
        localCache.put(keys.toString(), reference);
        Cache globalCache = ehCacheManager.getCache("myEhCache");
        globalCache.acquireWriteLockOnKey(keys.toString());
        globalCache.put(new Element(keys.toString(), reference));
        if(localCache.size()>globalCache.getCacheConfiguration().getMaxEntriesLocalDisk()){
            refreshLocalCache();
        }
    }
 
    public void evict(String prop1){
        localCache.remove(prop1);
        Cache globalCache = ehCacheManager.getCache("myEhCache");
        globalCache.acquireWriteLockOnKey(prop1);
        globalCache.remove(prop1);
    }
 
    public void evictAll(){
        localCache.clear();
        Cache globalCache = ehCacheManager.getCache("myEhCache");
        globalCache.removeAll();
    }
    
    public void refreshLocalCache(){
        localCache.clear();
        Cache globalCache = ehCacheManager.getCache("myEhCache");
        globalCache.getKeys().forEach(key -> 
        localCache.put(key.toString(), (SoftReference<T>)((globalCache.get(key)).getObjectValue())));
    }




















    @Override
    public void shutdown(){}
}