package edu.colorado.cs.csci5673;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class GraphGenerator extends Configured implements Tool {

	// Configuration parameter in JobConf that will specify the threshold for number of flights
	private static final String GRAPHGENERATOR_THRESHOLD_KEY = "graphgenerator.threshold";

	/**
	 * Entry point into the application.
	 * @param args
	 */
	public static void main (String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println ("Usage: "+GraphGenerator.class+" <input> <output> [threshold]");
			System.exit(1);
		}
		int ret = ToolRunner.run(new Configuration(), new GraphGenerator(), args);
		System.exit(ret);
	}

	public GraphGenerator () { }
	public GraphGenerator (Configuration conf) { super(conf); }

	/**
	 * Sets up the map reduce job and initiates execution.
	 * Returns exit status (0 for success, 1 for failure).
	 */
	@SuppressWarnings("deprecation")
	public int run(String[] args) throws Exception {
		// Get the configuration associated with this job
		Configuration conf = getConf();

		// Process command line arguments
		Path input = new Path(args[0]);
		Path output = new Path(args[1]);

		// Third argument is the minimum number of flights needed
		// to consider the orgin,destination pair as a common flight path.
		// Note that the output from the previous job also aggregates
		// by the reporting carrier as well, so we only consider
		// origin,destination pairs valid if the carrier has had more
		// than <threshold> flights that year (assuming input set was for a year).
		// This prevents the (unlikely) case where there are 365 different carriers
		// that had 1 flight from Origin,Dest the whole year.
		String threshold = "365";
		if (args.length > 2) {
			try {
				Integer.parseInt(args[2]);
				threshold = args[2];
			} catch (NumberFormatException nfe) {
				System.out.println ("Invalid threshold specification: "+args[2]);
			}
		}
		conf.set(GRAPHGENERATOR_THRESHOLD_KEY, threshold);

		// Setup the job
		JobConf job = new JobConf(conf, GraphGenerator.class);
		job.setJobName(GraphGenerator.class.getName());
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		// Add paths
		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);

		// Set the input/output formats
		job.setInputFormat(KeyValueTextInputFormat.class);
		job.setOutputFormat(TextOutputFormat.class);

		// Submit the job and wait for completion
		JobClient.runJob(job);
		return 0;
	}

	@SuppressWarnings("deprecation")
	public static class Map extends MapReduceBase implements Mapper<Text, Text, Text, Text>
	{
		private Text origin = new Text();
		private Text dest = new Text();
		private int threshold;

		public void configure (JobConf conf)
		{
			threshold = Integer.parseInt(conf.get(GRAPHGENERATOR_THRESHOLD_KEY));
			System.out.println ("Flight Threshold = "+threshold);
		}

		public void map (Text k, Text v, OutputCollector<Text, Text> collector, Reporter reporter) throws IOException
		{
			// Split the key ("Origin","Dest","RPCarrier") by a comma
			String[] keys = k.toString().split(",");

			try {
    			// Get the value as an Integer
    			int totalFlights = Integer.parseInt(v.toString());

    			// If the # of flights exceeds the threshold, then output
    			// the Origin as the key and the Destination as the value
    			if (totalFlights > threshold) {
        			origin.set(keys[0]);
        			dest.set(keys[1]);
        			collector.collect(origin, dest);
    			}

			} catch (NumberFormatException nfe) {
				System.out.println ("Invalid number of flights: "+v.toString());
			}

		}
	}

	@SuppressWarnings("deprecation")
	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text>
	{

		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> collector, Reporter reporter) throws IOException
		{
			Set<Text> destSet = new HashSet<Text>();
			while (values.hasNext()) {
				// We need to make a copy of the returned object, since the Iterator
				// returns the same object reference every time (only the attributes are changed)
				Text dest = new Text(values.next());
				destSet.add(dest);
			}

			// Create the adjacency list of destinations reachable from this origin
			StringBuffer sb = new StringBuffer();
			for (Text dst : destSet) {
				sb.append(dst+" ");
			}

			// Write the Origin as the key, and all destinations for which there is a
			// directed edge as the value
			collector.collect(key, new Text(sb.toString()));
		}
	}

}
