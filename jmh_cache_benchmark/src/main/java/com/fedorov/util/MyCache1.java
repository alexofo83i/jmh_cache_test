package com.fedorov.util;

import java.util.HashMap;
import java.util.Map;

 
public class MyCache1<T>  implements  ICacheIterable<T>
{
    public static MyCache1<Object>  instance;
    private Map<String, T> cache; 
     
    private MyCache1() 
    {
        cache = new HashMap<>();
    }   

    public void shutdown(){}
    
    public static synchronized <T> MyCache1<T> getInstance() 
    {
      if (instance == null)
      {
        instance = (MyCache1<Object>) new MyCache1<>();
      }
      return (MyCache1<T>) instance;
    }
 
    public T get(String prop1)
    {
      return cache.get(prop1);
    }
 
    public void put( String prop1, T object)
    {
      cache.put(prop1, object);
    }
 
    public void evict(String prop1)
    {
      cache.remove(prop1);
    }
 
    public void evictAll()
    {
      cache.clear();
    }
 
    public void doForEach(Callable<T> coll) throws Exception {
        for( String key : cache.keySet() ){
           put(key, coll.call(key) );
        }
    }
}