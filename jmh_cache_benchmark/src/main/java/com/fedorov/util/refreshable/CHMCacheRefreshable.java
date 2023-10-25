package com.fedorov.util.refreshable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import com.fedorov.util.generic.CacheFiller;  
import java.util.concurrent.TimeUnit;

public class CHMCacheRefreshable  implements ICacheRefreshable<String>{
    
    private ConcurrentHashMap<String,String> cache;
    protected static volatile boolean isLoaded = false;

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

    @Override
    public void shutdown(){
        Holder.shutdown();
    }
 
    @Override
    public void refresh() {
        isLoaded = false;
    }

    private static class Holder{
        public static CHMCacheRefreshable  instance;
        private static ScheduledExecutorService scheduledExecutorService;

        private static final int MAX_KEY_LENGTH    = 20;
        private static final int MAX_EHCACHE_SIZE  = 10000;
        private static final int MAX_CACHE_SIZE    = 20000;
        private static final Random  rand = new Random();
    
        static {
            // System.out.println("before initRefreshThread");
            initRefreshThread();
            // System.out.println("after initRefreshThread");
        }

        public static void shutdown(){
            scheduledExecutorService.shutdownNow();
            try {
                scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private static void initRefreshThread(){
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            final Runnable runnable = () -> {  
                if( !isLoaded && !Thread.currentThread().interrupted()){
                        // System.out.println("begin refreshing");
                        final ArrayList<String> keys = new ArrayList<>(  MAX_CACHE_SIZE  );
                        final CHMCacheRefreshable newInstance = new CHMCacheRefreshable();
                        try {
                            // System.out.println("before fill cache");
                            CacheFiller.fillTheCache(newInstance, keys, rand, MAX_CACHE_SIZE, MAX_EHCACHE_SIZE, MAX_KEY_LENGTH);
                            // System.out.println("after fill cache");
                        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                                | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        // System.out.println("before instance = newInstance");
                        instance = newInstance;
                        // System.out.println("after instance = newInstance");
                        isLoaded = true;
                    }
            };  
            isLoaded = false;    
            runnable.run();
            
            scheduledExecutorService.scheduleWithFixedDelay(runnable, 0, 100, TimeUnit.MILLISECONDS);
        }
    }
}