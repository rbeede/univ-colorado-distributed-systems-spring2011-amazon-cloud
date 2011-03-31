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
public class SortByMostFrequentItem {
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, ReverseSortLongWritable, Text> {
		private ReverseSortLongWritable number = new ReverseSortLongWritable(); // faster to not reinstantiate each time; holds key
		private Text text = new Text();  // faster to not reinstantiate each time;  holds item which is mapreduce value

		// Note that LongWritable key isn't a key from our input data
		public void map(LongWritable key, Text value, OutputCollector<ReverseSortLongWritable, Text> output, Reporter reporter) throws IOException {
			final String line = value.toString();  // current line with new line characters removed
			final String[] parts = line.split("\t");  // line is expected to have only two parts:  Item (text) and Frequency number (text to convert to number)

			if(null == parts || parts.length < 2 || !isNumeric(parts[1]))  return;  // skip bad data

			number.set(Long.parseLong(parts[1]));  // key is the frequency number
			text.set(parts[0]);
			output.collect(number, text);
		}

		public static boolean isNumeric(final String str) {
			try {
				Double.parseDouble(str);
			} catch(final NumberFormatException nfe) {
				return false;
			}

			return true;
		}
	}

	
	public static class Reduce extends MapReduceBase implements Reducer<ReverseSortLongWritable, Text, ReverseSortLongWritable, Text> {
		public void reduce(ReverseSortLongWritable key, Iterator<Text> values,OutputCollector<ReverseSortLongWritable, Text> output, Reporter reporter) throws IOException {
			// Key is the frequency and values are multiple items that match that
			// We should sort by the values according to however Text sorts
			final List<Text> sortedValuesList = new ArrayList<Text>();
			while(values.hasNext()) {
				final Text nextValue = new Text(values.next());
			
				sortedValuesList.add(nextValue);
			}
			Collections.sort(sortedValuesList);
			
			for(final Text text : sortedValuesList) {
				output.collect(key, text);  // key is the frequency, text is the item (sorted)
			}
		}
	}

	public static void main(String[] args) throws Exception {
		final long startTime = Calendar.getInstance().getTimeInMillis();

		// Setup our job
		JobConf conf = new JobConf(SortByMostFrequentItem.class);
		conf.setJobName(SortByMostFrequentItem.class.getName());

		conf.setOutputKeyClass(ReverseSortLongWritable.class);  // The key type (class determines sorting)
		conf.setOutputValueClass(Text.class);  // The value type

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

		System.out.println("Job to get sort frequency of items:");
		System.out.println("Start Time:\t" + new Date(startTime)); // milliseconds epoch
		System.out.println("Finish time:\t" + new Date(endTime));
		System.out.println("Run Time (milliseconds):\t" + (endTime - startTime));
	}
	
	
	public static class ReverseSortLongWritable extends org.apache.hadoop.io.LongWritable {
		public int compareTo(Object o) {
			final long thisValue = this.get();
			final long thatValue = ((ReverseSortLongWritable)o).get();
			return (thisValue>thatValue ? -1 : (thisValue==thatValue ? 0 : 1));
		}
	}
}
