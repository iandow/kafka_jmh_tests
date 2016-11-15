JMH is a Java micro benchmarking framework. In this project we use JMH to compare how quickly we can access attributes in an byte array encoded Avro object, versus accessing those attributes directly with pre-defined offsets into the byte array. Obviously the later is faster, but how much faster?  This benchmark seems to suggest a 10x improvement.

# How to compile

`mvn clean install`

# How to run
The simplest way is to just run,

`java -jar target/benchmarks.jar`

The quickest way to get a result is like this:

`java -jar target/benchmarks.jar -i 2 -f 1 -wi 1`

JMH provides a lot of options. To see them, run `java -jar target/benchmarks.jar -h`.

# Expected output

	Benchmark                Mode  Cnt         Score       Error  Units
	MyBenchmark.AvroTest    thrpt  200   3360845.667 ±  51103.886  ops/s
	MyBenchmark.PojoTest    thrpt  200    241485.913 ±   3748.181  ops/s
	MyBenchmark.SubstrTest  thrpt  200  20081971.452 ± 114315.726  ops/s