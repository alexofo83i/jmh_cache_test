package com.fedorov.benchmarks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import com.fedorov.util.generic.ICache;
import com.fedorov.util.refreshable.ICacheRefreshable;


@BenchmarkMode({Mode.Throughput/*Mode.AverageTime , Mode.SampleTime */})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1
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
    , "-XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true"
    , "-Djmh.shutdownTimeout=300"
                        })
@Warmup(iterations = 0)               
@Measurement(iterations = 1, time = 10, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 2, timeUnit = TimeUnit.MINUTES)
@State(Scope.Benchmark)
public class MyBenchmarkGetPutRefreshable extends MyBenchmark{

    @Param( {  "com.fedorov.util.refreshable.CHMCacheRefreshable"
             }) 
    public String cacheImplementation;
 
    Method medod;

    @Setup(Level.Iteration)
    @SuppressWarnings("unchecked")
    public void setUp() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
        // that is needed for generation of keys sequence for reading
        super.setUp(cacheImplementation);
        Class<String> clazz = (Class<String>) Class.forName(cacheImplementation);
        medod = clazz.getMethod("getInstance");
    }

    @TearDown(Level.Trial)
    public void tearDown(){
        System.out.println(cacheImplementation + " test is finished.");
        System.out.println("Shutting down");
        cache.shutdown();    
        System.out.println("Shutdown finished");
    }

    @SuppressWarnings("unchecked")
    public ICacheRefreshable<String> getCache(){
        ICacheRefreshable<String> cacheFresh = null;
         try {
            cacheFresh = (ICacheRefreshable<String>) medod.invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return cacheFresh;
    }

    @Benchmark
    @Group("cache")
    @GroupThreads(10)
    public Object testMethodReadExisting() {
        String keyString = getRandomKeyExisting();
        ICacheRefreshable<String> cacheFresh = getCache();
        return cacheFresh.get(keyString);
    }

    @Benchmark
    @Group("cache")
    @GroupThreads(10)
    public void testMethodWrite() {
        String keyString = getRandomKeyExisting();
        ICacheRefreshable<String> cacheFresh = getCache();
        cacheFresh.put(keyString,keyString);
    }

    @Benchmark
    @Group("cache")
    @GroupThreads(1)
    public void testMethodRefresh() {
        ICacheRefreshable<String> cacheFresh = getCache();
        cacheFresh.refresh();
    }
 
}
