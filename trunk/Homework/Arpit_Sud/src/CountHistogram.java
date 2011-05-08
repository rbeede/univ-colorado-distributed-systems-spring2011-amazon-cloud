/*
* Copyright (C) 2011 by Arpit Sud
* See the LICENSE file for Copyright information
*/
import java.io.IOException;
import java.util.*;
	
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class CountHistogram extends Configured implements Tool {

   public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, IntWritable> {
     private final static IntWritable one = new IntWritable(1);
     private Text word = new Text();
     
    
     public void map(LongWritable key, Text value, OutputCollector<IntWritable, IntWritable> collector, Reporter reporter)
         throws IOException {
        String[] tempArray = value.toString().split("\\t");
        IntWritable count = new IntWritable(Integer.parseInt(tempArray[1]));
 	collector.collect(count, one);
       }
     }

   public static class Reduce extends MapReduceBase implements Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
     @Override
     public void reduce(IntWritable key, Iterator<IntWritable> values, OutputCollector<IntWritable,IntWritable> collector, Reporter reporter) throws IOException {

       int sum = 0;
       while(values.hasNext()) {
	 IntWritable value = values.next();
         sum += value.get();
       }
       collector.collect(key, new IntWritable(sum));
     }
   }

   @Override
   public int run(String [] args) throws Exception {  
	try {
     JobClient client = new JobClient();
     JobConf job = new JobConf(getConf(),CountHistogram.class);
     job.setJobName("CountHistogram");

     job.setOutputKeyClass(IntWritable.class);
     job.setOutputValueClass(IntWritable.class);

     job.setMapperClass(Map.class);
     job.setReducerClass(Reduce.class);

     job.setInputFormat(TextInputFormat.class);
     job.setOutputFormat(TextOutputFormat.class);

     FileInputFormat.setInputPaths(job, new Path(args[0]));
     FileOutputFormat.setOutputPath(job, new Path(args[1]));

     client.setConf(job);
     JobClient.runJob(job);
   } catch(Exception e) {
	e.printStackTrace();
	throw e;
   }
   return 0;
  }

   public static void main(String[] args) throws Exception {
     int ret = ToolRunner.run(new CountHistogram(), args);
     System.exit(ret);
   }
}
