package com.fedorov.util.generic;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
 

public final class CacheFiller {
    
    private CacheFiller(){}
     
    public static void fillTheCache(ICache<String> cache, ArrayList<String> keys,Random rand, int MAX_CACHE_SIZE, int MAX_EHCACHE_SIZE, int MAX_KEY_LENGTH) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
        for ( int i = MAX_CACHE_SIZE - 1; i>= 0; i--){
            String keyString = generateRandomKey(rand,MAX_KEY_LENGTH);
            if( i<MAX_EHCACHE_SIZE){
                cache.put( keyString, keyString);
            }
            keys.add(keyString);
        }
        // System.out.println("keys : " + keys.size());
    }

    private static String generateRandomKey(Random rand, int MAX_KEY_LENGTH){
        byte[] keyArray = new byte[MAX_KEY_LENGTH];
        rand.nextBytes(keyArray);
        return new String(keyArray, StandardCharsets.UTF_8 /*Charset.forName("UTF-8")*/);
    }
}
