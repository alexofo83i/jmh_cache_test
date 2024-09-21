package com.fedorov.benchmarks;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
 
 import org.openjdk.jmh.annotations.Benchmark;
 import org.openjdk.jmh.annotations.BenchmarkMode;
 import org.openjdk.jmh.annotations.Fork;
 import org.openjdk.jmh.annotations.Group;
 import org.openjdk.jmh.annotations.GroupThreads;
 import org.openjdk.jmh.annotations.Level;
 import org.openjdk.jmh.annotations.Measurement;
 import org.openjdk.jmh.annotations.Mode;
 import org.openjdk.jmh.annotations.OutputTimeUnit;
 import org.openjdk.jmh.annotations.Param;
 import org.openjdk.jmh.annotations.Scope;
 import org.openjdk.jmh.annotations.Setup;
 import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Timeout;
 import org.openjdk.jmh.annotations.Warmup; 

import java.util.function.LongFunction;
import java.util.stream.LongStream;
 
 @BenchmarkMode({Mode.AverageTime/* , Mode.SampleTime */})
 @OutputTimeUnit(TimeUnit.MICROSECONDS)
 @Fork( value = 1
      , jvmArgsAppend = { "-Xmx16g"
                        , "-Xms16g"
                        //, "-XX:+UseG1GC"
                        , "-XX:+UseParallelGC"
                        , "-verbose:gc"
                        , "-Xloggc:gc_%p_%t.log"
                        , "-XX:+UseGCLogFileRotation"
                        , "-XX:NumberOfGCLogFiles=10"
                        , "-XX:GCLogFileSize=10M"
                        , "-XX:+PrintGCTimeStamps"
                        , "-XX:+PrintGCDateStamps"
                        , "-XX:+PrintGCDetails"
                        , "-XX:+HeapDumpOnOutOfMemoryError"
                        , "-XX:ReservedCodeCacheSize=256M"
                        , "-XX:+UnlockCommercialFeatures"
                        , "-XX:+FlightRecorder"
                        // , "-XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true"
                        , "-XX:StartFlightRecording=dumponexit=true"
                        , "-Djmh.shutdownTimeout=30"
                    })
 @Warmup(iterations = 0)
 //@Measurement(iterations = 1, time = 10, timeUnit = TimeUnit.SECONDS)
 @Measurement(iterations = 0, time = 10, timeUnit = TimeUnit.SECONDS)
 @State(Scope.Benchmark)
 public class MyBenchmarkPutNonExisting  extends MyBenchmark{

    private static final int MAX_NON_EXIST_KEYS = 50000000;
    
    @Param( { "com.fedorov.util.generic.RLCache"
            , "com.fedorov.util.generic.RLCacheLRU"
            , "com.fedorov.util.generic.CHMCache"
            , "com.fedorov.util.generic.MyCacheFixed"
            // , "com.fedorov.util.MyCache1"
            // , "com.fedorov.util.MyCache2" 
        }) 
    public String cacheImplementation;

    ArrayBlockingQueue<String> queue;
     
    @Setup(Level.Iteration)
    public void setUp() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
        super.setUp(cacheImplementation);
        System.out.println("Reinitializing keys using queue with size " + MAX_NON_EXIST_KEYS);
        
        
        final  ArrayBlockingQueue<String> queueLocal = new ArrayBlockingQueue<>(MAX_NON_EXIST_KEYS);
        final ThreadLocal<Random> rndTD = ThreadLocal.withInitial( Random::new );
        LongStream.rangeClosed(1, MAX_NON_EXIST_KEYS)
        .parallel()
        .forEach(x->{
            Random rand = rndTD.get(); 
            String keyString = generateRandomKey(rand);
            queueLocal.add(keyString);
        });
        queue = queueLocal;
        System.out.println("Reinitializing finished!");
    }

    @TearDown(Level.Trial)
    public void tearDown(){
        System.out.println(cacheImplementation + " test is finished.");
        System.out.println("Shutting down");
        cache.shutdown();    
        System.out.println("Shutdown finished");
    }

    @Override
    public String getRandomKeyNonExisting(){
        return queue.poll();
    }

    @Benchmark
    @Group("cache")
    @GroupThreads(10)
    public void testMethodWriteNonExisting() {
        String keyString = getRandomKeyNonExisting();
        cache.put(keyString,keyString);
    }
 }
 