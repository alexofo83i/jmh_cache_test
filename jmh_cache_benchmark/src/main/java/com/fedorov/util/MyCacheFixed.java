package com.fedorov.util;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/*
 *  ehcache.xml 
 * https://www.ehcache.org/generated/2.10.1/html/ehc-all/index.html#page/Ehcache_Documentation_Set/co-cfgbasics_xml_configuration.html
 <ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="ehcache.xsd" updateCheck="true"
    monitoring="autodetect" dynamicConfig="true"> 
  <diskStore path="java.io.tmpdir"/> 
  <cache name="myEhCache"
     maxEntriesLocalHeap="10000"
     eternal="false"
     timeToIdleSeconds="120" 
     timeToLiveSeconds="120"
     maxEntriesLocalDisk="100000"
     diskExpiryThreadIntervalSeconds="120" 
     memoryStoreEvictionPolicy="LRU">
     <persistence strategy="localTempSwap"/> 
  </cache> 
</ehcache>
 */

public class MyCacheFixed<T> implements ICache<T>, Serializable {

    private static final long serialVersionUID = 1L;
    private static CacheManager ehCacheManager;
    private volatile static  MyCacheFixed<Object>  instance;
    private static String cacheName = "myEhCache2";
    private final ConcurrentHashMap<String, SoftReference<T>> localCache = new ConcurrentHashMap<>();
    
    private  MyCacheFixed() {
    }
    
    public static  MyCacheFixed<Object> getInstance() {
        if (instance == null) {
            synchronized (MyCacheFixed.class) {
                if (instance == null) {
                    // issue #4: variable "instance" is volatile variable and should be read first and written last because of 
                    // 1. allocation memory to variable
                    // 2. copy pointer to allocated memory
                    // 3. executing constructor
                    //
                    // All none volatile variables will be visible for reading if staying after reading and before writing into volatile variable 

                    //instance = new MyCacheFixed<Object>();
                    //ehCacheManager = CacheManager.getInstance();
                    //ehCacheManager.addCacheIfAbsent("myEhCache");
                    MyCacheFixed<Object> localInstance = new MyCacheFixed<>();
                    ehCacheManager = CacheManager.newInstance("ehcache.xml");
                    if( ehCacheManager.cacheExists(cacheName)){
                        ehCacheManager.removeCache(cacheName);
                    }
                    ehCacheManager.addCacheIfAbsent(cacheName);
                    instance = localInstance;
                }
            }
        }
        return  instance;
    }
 
    public  T get(String prop1){
        // issue #3: double checking because of containsKey + get
        T res = null;
        SoftReference<T> soft = localCache.get(prop1); 
        // if (!localCache.containsKey(prop1) || localCache.get(prop1).get() == null) {
        if( soft == null || ( res = soft.get()) == null) {
            Cache ehCache = ehCacheManager.getCache("myEhCache2");
            if(ehCache != null && ehCache.isElementInMemory(prop1)) {
                Element cache = ehCache.get(prop1);
                if(cache != null) {
                    SoftReference<T> value1 = (SoftReference<T>) cache.getObjectValue();
                    if(value1 != null &&  (res = value1.get()) != null) {
                        localCache.put(prop1, value1);
                        return localCache.get(prop1).get(); 
                    }
                }
            }
        }
        return  res;
        // issue #1: NullPointerException because of non existing key
        // return localCache.get(prop1).get();
    }
 
    public  void put(String prop1, T object){
        // issue #5: method is not a thread safe because not atomic due to "put" + "refreshLocalCache"
        SoftReference<T> reference = new SoftReference<T>(object);
        localCache.put(prop1, reference);
        Cache globalCache = ehCacheManager.getCache("myEhCache2");
        try{
            globalCache.acquireWriteLockOnKey(prop1);
            globalCache.put(new Element(prop1, reference));
            if(localCache.size()>globalCache.getCacheConfiguration().getMaxEntriesLocalDisk()){
                refreshLocalCache();
            }
        }
        finally{
            // issue #2: absence of releasing lock lead to infinite wait on next attempt to lock
            globalCache.releaseWriteLockOnKey(prop1);
        }
    }
    
    // public void put(T object, String... cacheKeys){
    //     String[] keys = cacheKeys;
    //     SoftReference<T> reference = new SoftReference<T>(object);
    //     localCache.put(keys.toString(), reference);
    //     Cache globalCache = ehCacheManager.getCache("myEhCache");
    //     globalCache.acquireWriteLockOnKey(keys.toString());
    //     globalCache.put(new Element(keys.toString(), reference));
    //     if(localCache.size()>globalCache.getCacheConfiguration().getMaxEntriesLocalDisk()){
    //         refreshLocalCache();
    //     }
    // }
 
    // public void evict(String prop1){
    //     localCache.remove(prop1);
    //     Cache globalCache = ehCacheManager.getCache("myEhCache");
    //     globalCache.acquireWriteLockOnKey(prop1);
    //     globalCache.remove(prop1);
    // }
 
    // public void evictAll(){
    //     localCache.clear();
    //     Cache globalCache = ehCacheManager.getCache("myEhCache");
    //     globalCache.removeAll();
    // }
    
    public  void refreshLocalCache(){
        // issue #5: method is not a thread safe because not atomic due to "clear" + "put"
        localCache.clear();
        Cache globalCache = ehCacheManager.getCache("myEhCache2");
        globalCache.getKeys().forEach(key ->{ 
            String key2put = key.toString(); 
            Element cachedElement = globalCache.get(key);
            if( cachedElement != null && !cachedElement.isExpired()){
                SoftReference<T> softRef = (SoftReference<T>) cachedElement.getObjectValue();
                localCache.put(key2put, softRef);
            }
        });
    }

    @Override
    public void shutdown(){
        ehCacheManager.shutdown();
    }
}
