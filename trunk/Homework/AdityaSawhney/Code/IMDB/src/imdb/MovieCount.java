package imdb;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;

public class MovieCount {

	public static class MapClass extends MapReduceBase implements
			Mapper<Text, IntWritable, IntWritable, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private IntWritable year = new IntWritable();
		private boolean isHistogram = false;
		
		public void configure(JobConf job) {
			this.isHistogram = job.getBoolean("moviecount.histogram", false);
		}
		
		public void map(Text key, IntWritable value,
				OutputCollector<IntWritable, IntWritable> output, Reporter reporter)
				throws IOException {
			if (this.isHistogram) {
				// Create bins on decade size
				int y = value.get();
				year.set(y - (y % 10));
				output.collect(year, one);
			}
			else {
				year.set(value.get());
				output.collect(year, one);
			}
		}
	}

	public static class Reduce extends MapReduceBase implements
			Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
		private IntWritable count = new IntWritable();
		
		public void reduce(IntWritable key, Iterator<IntWritable> values,
				OutputCollector<IntWritable, IntWritable> output, Reporter reporter)
				throws IOException {
			int sum = 0;
			
			while (values.hasNext()) {
				sum += values.next().get();
			}
			count.set(sum);
			output.collect(key, count);
		}
	}

	public static void execute(Configuration config, boolean purgeInput) throws Exception {
		JobConf conf = new JobConf(config, MovieCount.class);
		conf.setJobName("moviecount");

		conf.setOutputKeyClass(IntWritable.class);
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(MapClass.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		String inputPath = conf.get("moviecount.count.input");
		conf.setInputFormat(SequenceFileInputFormat.class);
		SequenceFileInputFormat.setInputPaths(conf, inputPath);
		
		conf.setOutputFormat(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setCompressOutput(conf, true);
		SequenceFileOutputFormat.setOutputCompressorClass(conf, DefaultCodec.class);
		SequenceFileOutputFormat.setOutputCompressionType(conf, CompressionType.BLOCK);
		SequenceFileOutputFormat.setOutputPath(conf, new Path(conf.get("moviecount.count.output")));

		JobClient.runJob(conf).waitForCompletion();
		
		if (purgeInput) {
			conf.getWorkingDirectory().getFileSystem(conf).delete(new Path(inputPath), true);
		}
	}
}
