package com.fedorov.util.generic;


import org.junit.Test;

import com.fedorov.util.generic.ICache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.stream.LongStream;


public abstract class ILRUCacheJUnitTest extends ICacheJUnitTest {

    @Test
    public void testPut3(){
        String keyThird = "third";
        assertNull("ehCache should be reinitialized before checking keys for existing", cache.get(keyThird));
        cache.put(keyThird,keyThird);
        assertEquals(keyThird, cache.get(keyThird));
        LongStream.rangeClosed(1, ICache.MAX_CACHE_SIZE /* 10000L */)
        // myCache implementation will be locked on attempt to put elements in concurrent threads due to acquireWriteLock
        //.parallel()  
        .forEach((x)->{
            String keyString = Long.toString(x);
            cache.put(keyString, keyString);
        });
        String keyLast = "last";
        cache.put(keyLast,keyLast);
        System.out.println("put3: " + cache.get(keyThird));
        assertNull("key should be evicted after putting last key that breached the limit by size",cache.get(keyThird));
        assertEquals("key should be accessed because added right now", keyLast, cache.get(keyLast));
    }

}
    