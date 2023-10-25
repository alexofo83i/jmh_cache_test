package com.fedorov.util.generic;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import com.fedorov.benchmarks.MyBenchmarkGetPut;

/** 
 * Use it for debug purpuses only because of forks(value:0)
 */
public class Main {
    
    public static void main(String... args) throws RunnerException{
        Options opt = new OptionsBuilder()
        .include(MyBenchmarkGetPut.class.getSimpleName())
        .forks(0)
        // .warmupForks(0)
        // .warmupIterations(1)
        // .warmupTime(new TimeValue(10, TimeUnit.SECONDS))
        // .measurementIterations(1)
        // .measurementTime( new TimeValue(1, TimeUnit.MINUTES))
        // .timeout(new TimeValue(1, TimeUnit.MINUTES))
        // .jvmArgs("-Xmx16g"
        //         , "-Xms16g"
        //         , "-XX:+UnlockCommercialFeatures"
        //         , "-XX:+FlightRecorder"
        //         , "-XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true"
        // )
        .resultFormat(ResultFormatType.TEXT)
        .result(args.length > 0 && !"".equalsIgnoreCase(args[0])  ? args[0]: "jmh_cache_benchmark.txt" )
        .build();

        new Runner(opt).run();
    }  
}
