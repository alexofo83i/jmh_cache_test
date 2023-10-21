package com.fedorov.benchmarks;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.fedorov.volatiles.IVolatile;
import com.fedorov.volatiles.Volatile1;

@Warmup(iterations = 0)
// @Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.MINUTES)
@Measurement(iterations = 0, time = 1, timeUnit = TimeUnit.MINUTES)
@Fork(value = 0
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
                        // , "-XX:+UnlockCommercialFeatures"
                        // , "-XX:+FlightRecorder"
                        // , "-XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true"
                        // , "-XX:StartFlightRecording=dumponexit=true"
                        , "-Djmh.shutdownTimeout=30"
})
@BenchmarkMode(Mode.Throughput)
@State(Scope.Thread)
public class VolatileBenchmark {
    


    public class Vector{
        final int days;
        final int months;
        final int years;
        public /* final */ int total;

        public Vector(int days, int months, int years){
            this.days = days;
            this.months = months;
            this.years = years;
            this.total = IVolatile.getTotal( days,  months, years);
        }
    }
 
   
    public LinkedList<Vector> list; 
    public static Volatile1 vol = new Volatile1();
    
    @Setup(Level.Iteration)
    public void setup(){
        System.out.println("");
        System.out.println("begin setup");

        list = new LinkedList<>();

        IntStream.range(1, 10000)
        .parallel()
        .mapToObj(year->{
            LinkedList<Vector> listLocal = new LinkedList<>();
            for( int months  = 1; months <= 12; months++ ){
                for( int days  = 1; days <= IVolatile.MAX_DAYS_IN_MONTH; days++ ){
                    listLocal.push(new Vector(days, months, year));
                }
            }
            return listLocal;
        })
        .collect(Collectors.toList())
        .forEach(listLocal->{
            list.addAll(listLocal);
        });

        // for( int years = 1; years <= 10000; years++ ){
        //     for( int months  = 1; months <= 12; months++ ){
        //         for( int days  = 1; days <= IVolatile.MAX_DAYS_IN_MONTH; days++ ){
        //             list.push(new Vector(days, months, years));
        //         }
        //     }
        // }
        System.out.println("generated " + list.size() + " values for " + list.hashCode());
        System.out.println("end setup");
    }

    @Benchmark
    @GroupThreads(20)
    @Group("volatile")
    public void testUpdate(){
        Vector vect = list.pop();
        int totalExpected = vect.total;
        // synchronized(Volatile1.class){
            vol.update(vect.years, vect.months, vect.days);
            int totalActual = vol.totalDays();
            if( totalActual != totalExpected ){
                throw new RuntimeException("totalActual = " + totalActual + ", totalExpected = " + totalExpected);
            }
        // }
        list.push(vect);
    }
    
}
