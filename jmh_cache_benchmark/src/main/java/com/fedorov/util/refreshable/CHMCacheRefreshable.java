package com.fedorov.util.refreshable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import com.fedorov.util.generic.CacheFiller;  

public class CHMCacheRefreshable  implements ICacheRefreshable<String>{
    
    private ConcurrentHashMap<String,String> cache;
    protected static volatile boolean isLoaded;

    public static  CHMCacheRefreshable  getInstance(){
        return Holder.instance;
    }

    protected CHMCacheRefreshable(){
        cache = new ConcurrentHashMap<>((int) (MAX_CACHE_SIZE*1.4));
    }

    @Override
    public String get(String key) {
        return cache.get(key);
    }

    @Override
    public void put(String key, String obj) {
        cache.put(key, obj);
    }

    public void shutdown(){}
 
    @Override
    public void refresh() {
        isLoaded = false;
    }

    private static class Holder{
        public static CHMCacheRefreshable  instance;
        private static final int MAX_KEY_LENGTH    = 20;
        private static final int MAX_EHCACHE_SIZE  = 10000;
        private static final int MAX_CACHE_SIZE    = 20000;
        private static final Random  rand = new Random();
    
        static {
            initRefreshThread();
        }

        private static void initRefreshThread(){
            ExecutorService ex  = Executors.newSingleThreadExecutor();  
  
            final Runnable runnable = () -> {  
                while( true ){
                    if( !isLoaded){
                        
                        final ArrayList<String> keys = new ArrayList<>(  MAX_CACHE_SIZE  );
                        final CHMCacheRefreshable newInstance = new CHMCacheRefreshable();
                        try {
                            CacheFiller.fillTheCache(newInstance, keys, rand, MAX_CACHE_SIZE, MAX_EHCACHE_SIZE, MAX_KEY_LENGTH);
                        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                                | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        instance = newInstance;
                        isLoaded = true;
                    }
                    try {
                        Thread.sleep(10);
                    }catch(InterruptedException e){
                        throw new RuntimeException(e);
                    }
                }
            };  
    
            ex.submit(runnable);  
        }
    }
}