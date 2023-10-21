# jmh_cache_test
jmh microbenchmark tests for choosing between different implementations of a thread safe key-value cache

# generated using JMH project
# mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=org.openjdk.jmh -DarchetypeArtifactId=jmh-java-benchmark-archetype -DgroupId=com.fedorov -DartifactId=jmh_cache_benchmark -Dversion=1.0

# <dependencies>
#        <dependency>
#            <groupId>org.openjdk.jmh</groupId>
#            <artifactId>jmh-core</artifactId>
#            <version>${jmh.version}</version>
#        </dependency>
#        <dependency>
#            <groupId>org.openjdk.jmh</groupId>
#            <artifactId>jmh-generator-annprocess</artifactId>
#            <version>${jmh.version}</version>
#            <scope>provided</scope>
#        </dependency>
#        <!-- https://mvnrepository.com/artifact/org.ehcache/ehcache -->
#        <dependency>
#            <groupId>net.sf.ehcache</groupId>
#            <artifactId>ehcache</artifactId>
#            <version>2.10.9.2</version>
#        </dependency>
#        <!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-api -->
#        <dependency>
#            <groupId>org.slf4j</groupId>
#            <artifactId>slf4j-api</artifactId>
#            <version>2.0.7</version>
#        </dependency>
#    </dependencies>

# compile using windows cmd
cd jmh_cache_test\jmh_cache_benchmark
del .\target\benchmarks.jar
mvn clean install

# execute test using windows cmd
"%JAVA_HOME%"\bin\java -Xmx2g -Xms2g -XX:+UnlockCommercialFeatures  -XX:+FlightRecorder -XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true  -jar .\target\benchmarks.jar com.fedorov.benchmarks.MyBenchmarkGetOnly
