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
import java.util.regex.Pattern;

public class WordCount extends Configured implements Tool {

   public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
     private final static IntWritable one = new IntWritable(1);
     private Text word = new Text();
     private final static Pattern spcharpattern = Pattern.compile("[^a-z0-9]");
     private final static Pattern spacepattern = Pattern.compile("\\s+");
     
     public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> collector, Reporter reporter)
         throws IOException {
       String temp = value.toString();
       temp = temp.toLowerCase();
       temp = spcharpattern.matcher(temp).replaceAll(" ");
       temp = spacepattern.matcher(temp).replaceAll(" ");
       temp = temp.trim();
       StringTokenizer tokenizer = new StringTokenizer(temp);

	while (tokenizer.hasMoreTokens()) {
         word.set(tokenizer.nextToken());
         collector.collect(word, one);
       }
     }
   }

   public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
     @Override
     public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text,IntWritable> collector, Reporter reporter) throws IOException {

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
     JobConf job = new JobConf(getConf(),WordCount.class);
     job.setJobName("WordCount");

     job.setOutputKeyClass(Text.class);
     job.setOutputValueClass(IntWritable.class);

     job.setMapperClass(Map.class);
     job.setCombinerClass(Reduce.class);
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
     int ret = ToolRunner.run(new WordCount(), args);
     System.exit(ret);
   }
}
