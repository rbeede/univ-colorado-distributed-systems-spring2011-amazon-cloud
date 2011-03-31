/**
 * Rodney Beede
 * 2011-02
 */

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

// Adapted from Hadoop's WordCount example on the Apache wiki        
public class NumberFrequency {
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private LongWritable number = new LongWritable(); // faster to not reinstantiate each time

		public void map(LongWritable key, Text value, OutputCollector<LongWritable, IntWritable> output, Reporter reporter) throws IOException {
			final String line = value.toString();  // current line with new line characters removed
			final String[] cells = line.split(",");  // cells are csv

			for(final String cell : cells) {
				// Skip non-numbers
				if(!isNumeric(cell))  continue;

				number.set(Long.parseLong(cell));  // key is the number we just saw
				output.collect(number, one);  // mark that we've seen it one more time (reduce combines later)
			}
		}

		public static boolean isNumeric(final String str) {
			try {
				Long.parseLong(str);
			} catch(final NumberFormatException nfe) {
				return false;
			}

			return true;
		}
	}

	
	public static class Reduce extends MapReduceBase implements Reducer<LongWritable, IntWritable, LongWritable, IntWritable> {
		public void reduce(LongWritable key, Iterator<IntWritable> values,OutputCollector<LongWritable, IntWritable> output, Reporter reporter) throws IOException {
			int sum = 0;
			while(values.hasNext()) {
				sum += values.next().get();  // Sum up all the "1's" for the given "key" (number)
			}

			output.collect(key, new IntWritable(sum));  // Save the frequency for this key
		}
	}

	public static void main(String[] args) throws Exception {
		final long startTime = Calendar.getInstance().getTimeInMillis();

		// Setup our job
		JobConf conf = new JobConf(NumberFrequency.class);
		conf.setJobName("numberfrequency");

		conf.setOutputKeyClass(LongWritable.class);  // The key type (class determines sorting)
		conf.setOutputValueClass(IntWritable.class);  // The value type

		// Declare which Map and Reduce code will be used on the data
		conf.setMapperClass(Map.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		// The incoming data is just text at first
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);  // We output in text too

		// Where to get our data and where to store the result
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		JobClient.runJob(conf);  // Blocks until finished

		final long endTime = Calendar.getInstance().getTimeInMillis();

		System.out.println("Job to get frequency of numbers:");
		System.out.println("Start Time:\t" + new Date(startTime)); // milliseconds epoch
		System.out.println("Finish time:\t" + new Date(endTime));
		System.out.println("Run Time (milliseconds):\t" + (endTime - startTime));
	}
}
