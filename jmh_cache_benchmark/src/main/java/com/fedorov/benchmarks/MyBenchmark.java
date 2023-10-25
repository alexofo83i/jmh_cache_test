package com.fedorov.benchmarks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import com.fedorov.util.generic.CacheFiller;
import com.fedorov.util.generic.ICache;
  
public class MyBenchmark {
 
    public final int MAX_KEY_LENGTH    = 20;
    public final int MAX_EHCACHE_SIZE  = 10000;
    public final int MAX_CACHE_SIZE    = 20000;
    
    
    protected ICache<String> cache;
    protected ArrayList<String> keys;
  
    public final ThreadLocal<Random> threadRand =  ThreadLocal.<Random>withInitial(() ->{
        return new Random();
    });
 
     
    public void setUp(String cacheImplementation) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
        Class<String> clazz = (Class<String>) Class.forName(cacheImplementation);
        Method medod = clazz.getMethod("getInstance");
        
        cache = (ICache<String>) medod.invoke(null);

        System.out.println("");
        System.out.println( "setup is invoked for class " + cache.getClass().getSimpleName() );

        keys = new ArrayList<>(  MAX_CACHE_SIZE  );
        Random rand = new Random();
        //  for ( int i = MAX_CACHE_SIZE - 1; i>= 0; i--){
        //      String keyString = generateRandomKey(rand);
        //      if( i<MAX_EHCACHE_SIZE){
        //          cache.put( keyString, keyString);
        //      }
        //      keys.add(keyString);
        //  }
        CacheFiller.fillTheCache(cache, keys, rand, MAX_CACHE_SIZE, MAX_EHCACHE_SIZE, MAX_KEY_LENGTH);
        System.out.println("keys : " + keys.size());
    }

    public String generateRandomKey(Random rand){
        byte[] keyArray = new byte[MAX_KEY_LENGTH];
        rand.nextBytes(keyArray);
        return new String(keyArray, Charset.forName("UTF-8"));
    }

    public String getRandomKeyExisting(){
        int keyIndex = threadRand.get().nextInt(MAX_EHCACHE_SIZE);
        return keys.get(keyIndex);
    }

    public String getRandomKeyNonExisting(){
        int keyIndex = MAX_EHCACHE_SIZE + threadRand.get().nextInt(MAX_CACHE_SIZE - MAX_EHCACHE_SIZE);
        return keys.get(keyIndex);
    }
        
 }
 