REM mvn clean install
REM "%JAVA_HOME%"\bin\java -Xmx16g -Xms16g -XX:+UnlockCommercialFeatures  -XX:+FlightRecorder -XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true  -cp .\target\benchmarks.jar com.fedorov.util.Main
REM "%JAVA_HOME%"\bin\java  -jar .\target\benchmarks.jar

cd jmh_cache_test\jmh_cache_benchmark
del .\target\benchmarks.jar
mvn clean install
"%JAVA_HOME%"\bin\java -Xmx2g -Xms2g -XX:+UnlockCommercialFeatures  -XX:+FlightRecorder -XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true  -jar .\target\benchmarks.jar com.fedorov.benchmarks.MyBenchmarkGetOnly
