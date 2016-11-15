
package org.sample;

import com.twitter.bijection.Injection;
import com.twitter.bijection.avro.GenericAvroCodecs;
import org.openjdk.jmh.annotations.*;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

public class MyBenchmark {
	
	public static final String USER_SCHEMA = "{"
            + "\"type\":\"record\","
            + "\"name\":\"myrecord\","
            + "\"fields\":["
            + "  { \"name\":\"str1\", \"type\":\"string\" },"
            + "  { \"name\":\"str2\", \"type\":\"string\" },"
            + "  { \"name\":\"int1\", \"type\":\"int\" }"
            + "]}";

	@State(Scope.Thread)
    public static class MyState {
    	public static byte[] bytes = new byte[10000];
		Injection<GenericRecord, byte[]> recordInjection;
		
		@Setup(Level.Trial)
        public void doSetup() {
            Schema.Parser parser = new Schema.Parser();
	        Schema schema = parser.parse(USER_SCHEMA);
	        recordInjection = GenericAvroCodecs.toBinary(schema);
			GenericData.Record avroRecord = new GenericData.Record(schema);
	        int i = 0;
	        avroRecord.put("str1", "Str 1-" + i);
	        avroRecord.put("str2", "Str 2-" + i);
	        avroRecord.put("int1", i);
	        bytes = recordInjection.apply(avroRecord);
        }
    
    }

    @Benchmark
    public String AvroTest(MyState state) {	
        GenericRecord record = state.recordInjection.invert(state.bytes).get();
        return record.get("str1").toString();
    }

    @Benchmark
    public String SubstrTest(MyState state) {	
        return new String(state.bytes, 0, 5);
    }

}
