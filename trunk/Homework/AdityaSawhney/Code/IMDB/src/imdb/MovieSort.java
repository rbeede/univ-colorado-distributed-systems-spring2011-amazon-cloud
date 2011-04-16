package imdb;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.lib.IdentityReducer;

public class MovieSort {
	
	public static class MapClass extends MapReduceBase implements
			Mapper<IntWritable, IntWritable, IntWritable, IntWritable> {

		public void map(IntWritable key, IntWritable value,
				OutputCollector<IntWritable, IntWritable> output,
				Reporter reporter) throws IOException {
			output.collect(value, key);
		}
	}

	public static class DescendingComparator extends WritableComparator {
		IntWritable.Comparator defaultComparator = new IntWritable.Comparator();

		public DescendingComparator() {
			super(IntWritable.class);
		}

		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
			// Reverse the default comparator response to get descending order
			return -defaultComparator.compare(b1, s1, l1, b2, s2, l2);
		}
	}

	public static void execute(Configuration config, boolean purgeInput)
			throws Exception {
		JobConf conf = new JobConf(config, MovieCount.class);
		conf.setJobName("moviesort");

		conf.setOutputKeyClass(IntWritable.class);
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(MapClass.class);
		conf.setReducerClass(IdentityReducer.class);

		conf.setNumReduceTasks(1);
		conf.setOutputKeyComparatorClass(DescendingComparator.class);

		String inputPath = conf.get("moviecount.sort.input");
		conf.setInputFormat(SequenceFileInputFormat.class);
		SequenceFileInputFormat.setInputPaths(conf, inputPath);

		FileOutputFormat.setOutputPath(conf, new Path(conf.get("moviecount.sort.output")));

		JobClient.runJob(conf).waitForCompletion();

		if (purgeInput) {
			conf.getWorkingDirectory().getFileSystem(conf).delete(new Path(inputPath), true);
		}
	}
}
