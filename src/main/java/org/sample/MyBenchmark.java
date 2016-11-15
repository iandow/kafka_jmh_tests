
package org.sample;

import com.twitter.bijection.Injection;
import com.twitter.bijection.avro.GenericAvroCodecs;
import org.openjdk.jmh.annotations.*;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import java.io.*;

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
    public static class MyRecord {
    	public static byte[] avro_bytes = new byte[10000];
    	public static byte[] string_bytes = new byte[10000];
    	public static byte[] pojo_bytes = new byte[10000];
		Injection<GenericRecord, byte[]> recordInjection;
		
		@Setup(Level.Trial)
        public void doSetup() {

            // initialize byte array
            String data = "Str 1" + "Str 2" + "0";
            string_bytes = data.getBytes();

            // initialize avro byte array
            Schema.Parser parser = new Schema.Parser();
	        Schema schema = parser.parse(USER_SCHEMA);
	        recordInjection = GenericAvroCodecs.toBinary(schema);
			GenericData.Record avroRecord = new GenericData.Record(schema);
	        avroRecord.put("str1", "Str 1");
	        avroRecord.put("str2", "Str 2");
	        avroRecord.put("int1", 0);
	        avro_bytes = recordInjection.apply(avroRecord);

            // initialize pojo byte array
            RecordType my_record = new RecordType();
            my_record.setStr1("Str 1");
            my_record.setStr2("Str 2");
            my_record.setInt1(0);

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = null;
                out = new ObjectOutputStream(bos);
                out.writeObject(my_record);
                out.flush();
                pojo_bytes = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Benchmark
    public String AvroTest(MyRecord stream_record) {
        // convert byte array to avro, then access the str1 attribute
        GenericRecord record = stream_record.recordInjection.invert(stream_record.avro_bytes).get();
        return record.get("str1").toString();
    }

    @Benchmark
    public String SubstrTest(MyRecord stream_record) {
        // Assuming a known schema, access byte array elements directly.
        return new String(stream_record.string_bytes, 0, 5);
    }

    @Benchmark
    public String PojoTest(MyRecord stream_record) throws Exception {
        // convert byte array to POJO and access str1 attribute
        ByteArrayInputStream bis = new ByteArrayInputStream(stream_record.pojo_bytes);
        ObjectInput in = new ObjectInputStream(bis);
        Object obj = in.readObject();
        RecordType record = (RecordType)obj;
        return record.getStr1();
    }

}
