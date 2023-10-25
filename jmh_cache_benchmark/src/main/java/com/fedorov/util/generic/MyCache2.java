package com.fedorov.util.generic;

import java.util.HashMap;
import java.util.Map;

import com.fedorov.util.generic.ICacheIterable.Callable;

 
public class MyCache2<T> implements ICacheIterable<T>
{
    private static HashMap  myCacheMap = new HashMap(1000); 
     
    public static  Object getInstance() 
    {
       return new MyCache2<Object>();
    }
 
    public T get(String prop1)
    {
      synchronized(myCacheMap){
        return (T) myCacheMap.get(prop1);
      }
    }

    public void shutdown(){}
 
    public void put( String prop1, T object)
    {
      synchronized(myCacheMap){
        myCacheMap.put(prop1, object);
      }
    }
 
    public void evict(String prop1)
    {
      synchronized (myCacheMap) { myCacheMap.remove(prop1); }
    }
 
    public void evictAll()
    {
      myCacheMap.clear();
    }

    @Override
    public void doForEach(Callable<T> coll) throws Exception {
      synchronized (myCacheMap) {
        for( Object key : myCacheMap.keySet() ){
           put((String)key, coll.call((String)key) );
        }
      }
    }
}