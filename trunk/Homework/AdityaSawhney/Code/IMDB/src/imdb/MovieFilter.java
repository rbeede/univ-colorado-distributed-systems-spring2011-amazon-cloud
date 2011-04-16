package imdb;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.TextInputFormat;

public class MovieFilter {

	public static class MapClass extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, IntWritable> {
		
		enum Movie {
			INVALID_RECORD
		}
		
		ImdbRecordParser imdbRecordParser = new ImdbRecordParser();
		Text title = new Text();
		IntWritable year = new IntWritable();
		
		public void map(LongWritable key, Text value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			String line = value.toString();
			imdbRecordParser.parse(line);

			if (imdbRecordParser.isValidRecord()) {
				title.set(imdbRecordParser.getTitle());
				year.set(imdbRecordParser.getYear());
				output.collect(title, year);
			}
			else {
				System.err.println("INVALID IMDB movie record - " + line);
				reporter.setStatus("Invalid movie record detected. See logs.");
				reporter.incrCounter(Movie.INVALID_RECORD, 1);
			}
		}
	}

	public static class Reduce extends MapReduceBase implements
			Reducer<Text, IntWritable, Text, IntWritable> {
		IntWritable year = new IntWritable();
		
		public void reduce(Text key, Iterator<IntWritable> values,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			Set<Integer> uniqueYears = new HashSet<Integer>();
			
			while (values.hasNext()) {
				uniqueYears.add(values.next().get());
			}
			for (Integer uniqueYear : uniqueYears) {
				year.set(uniqueYear);
				output.collect(key, year);
			}
		}
	}

	public static void execute(Configuration config, boolean purgeInput) throws Exception {
		JobConf conf = new JobConf(config, MovieFilter.class);
		conf.setJobName("moviefilter");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(MapClass.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		String inputPath = conf.get("moviecount.filter.input");
		conf.setInputFormat(TextInputFormat.class);
		FileInputFormat.setInputPaths(conf, inputPath);

		conf.setOutputFormat(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setCompressOutput(conf, true);
		SequenceFileOutputFormat.setOutputCompressorClass(conf, DefaultCodec.class);
		SequenceFileOutputFormat.setOutputCompressionType(conf, CompressionType.BLOCK);
		SequenceFileOutputFormat.setOutputPath(conf, new Path(conf.get("moviecount.filter.output")));

		JobClient.runJob(conf).waitForCompletion();
		
		if (purgeInput) {
			conf.getWorkingDirectory().getFileSystem(conf).delete(new Path(inputPath), true);
		}
	}
}
