package com.fedorov.benchmarks;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.analysis.solvers.BaseSecantSolver.Method;
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


@BenchmarkMode({Mode.AverageTime/* , Mode.SampleTime */})
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

    @Param( {  "com.fedorov.util.generic.CHMCacheRefreshable"
             }) 
    public String cacheImplementation;
 

    @Setup(Level.Iteration)
    public void setUp() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException{
        // super.setUp(cacheImplementation);
        Class<String> clazz = (Class<String>) Class.forName(cacheImplementation);
        ((ICache) clazz)::
        // Method medod = clazz.getMethod("getInstance");
    }

    @TearDown(Level.Trial)
    public void tearDown(){
        System.out.println(cacheImplementation + " test is finished.");
        System.out.println("Shutting down");
        cache.shutdown();    
        System.out.println("Shutdown finished");
    }

    @Benchmark
    @Group("cache")
    @GroupThreads(10)
    public Object testMethodReadExisting() {
        String keyString = getRandomKeyExisting();
        return cache.get(keyString);
    }

    @Benchmark
    @Group("cache")
    @GroupThreads(10)
    public Object testMethodReadNonExisting() {
        String keyString = getRandomKeyNonExisting();
        return cache.get(keyString);
    }

    @Benchmark
    @Group("cache")
    @GroupThreads(10)
    public void testMethodWrite() {
        String keyString = getRandomKeyExisting();
        cache.put(keyString,keyString);
    }
 
}
