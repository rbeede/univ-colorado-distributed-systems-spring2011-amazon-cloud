package net.wsnetwork.ds;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileAsBinaryInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

@SuppressWarnings("deprecation")
public class ColdestDayDriver extends Configured implements Tool {
	@Override public int run(String[] args) throws Exception {
		JobClient client = new JobClient();
		
		JobConf conf = new JobConf(getConf(), this.getClass());
		
		conf.setJar("/root/coldest.jar");
		conf.setJobName("Coldest Day Finder");
		
		conf.setOutputKeyClass(LongWritable.class);
		conf.setOutputValueClass(LongWritable.class);
		
		conf.setMapperClass(ColdestDayMapper.class);
		conf.setReducerClass(ColdestDayReducer.class);
		
		conf.setInputFormat(SequenceFileAsBinaryInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		
		client.setConf(conf);
		JobClient.runJob(conf);
		
		return 0;
	}
	
	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new ColdestDayDriver(), args);
	    System.exit(res);
	}

}
