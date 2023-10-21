package com.fedorov.util;


import org.junit.Test;
import org.openjdk.jmh.annotations.TearDown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; 
import java.util.concurrent.TimeUnit;

import org.junit.Before;    
    

public abstract class ICacheJUnitTest {

    protected ICache<Object> cache;
    protected List<String> keysExisting = Arrays.<String>asList(new String[]{"first","second"});

    protected abstract ICache<Object> getInstance();

    @Before
    public void setup(){
        cache = getInstance();
        for (String key : keysExisting) {
            cache.put(key,key);
        }
    }

    @TearDown
    public void tearDown(){
        cache.shutdown();
    }
        
    @Test
    public void testGet1 () {
        for (String key : keysExisting) {
            assertEquals(key, cache.get(key));
        }
    }

    @Test
    public void testGet2 () {
        String key = "forth";
        System.out.println(cache.get(key));
        assertNull(cache.get(key));
    }

    @Test
    public void testPut1 () {
        String key = "first";
        cache.put(key,key);
        assertNotNull(key, cache.get(key));
        cache.put(key,key);
        assertNotNull(key, cache.get(key));
    }

    @Test
    public void testPut2 () throws InterruptedException {
        final String key = "first";
        final int MAX_THREADS = 2;
        final CountDownLatch latchGo = new CountDownLatch(1);
        final CountDownLatch latchReady = new CountDownLatch(MAX_THREADS);
        final CountDownLatch latchCompleted = new CountDownLatch(MAX_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        for (int i = 0; i < MAX_THREADS; i++) {
            Runnable worker = ()->{
                latchReady.countDown();
                try{
                    latchGo.await();
                    cache.put(key,key);
                    latchCompleted.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
            executor.execute(worker);
        }
        latchReady.await(); 
        latchGo.countDown(); 
        latchCompleted.await(5, TimeUnit.SECONDS);
        assertEquals( (long) 0, latchCompleted.getCount() );

        executor.shutdownNow();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            assertNull("Test not passed!");
        }
    }

}
    