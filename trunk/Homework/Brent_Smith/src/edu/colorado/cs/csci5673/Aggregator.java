package edu.colorado.cs.csci5673;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Aggregator extends Configured implements Tool {

	// The value 1 as a LongWritable
	private static final LongWritable one = new LongWritable(1);

	// This will be initialized with all the fields and their
	// corresponding index in the CSV file.
	private static final java.util.Map<String,Integer> fieldMap = new HashMap<String,Integer>();

	// Configuration parameter in JobConf that will specify the fields to aggregate on
	private static final String AGGREGATOR_FIELDS_KEY = "aggregator.fields";
	
	// Number of mappers
	private int numMappers = 0;
	private int numReducers = 0;
	private boolean useCombiner = false;


	// Static initializer to initialize the fieldMap.
	static {
		fieldMap.put("ItinID", 0);            //Itinerary ID
		fieldMap.put("MktID", 1);             //Market ID
		fieldMap.put("SeqNum", 2);            //Coupon Sequence Number
		fieldMap.put("Coupons", 3);           //Number of Coupons in the Itinerary
		fieldMap.put("Year", 4);              //Year
		fieldMap.put("Quarter", 5);           //Quarter (1-4)
		fieldMap.put("Origin", 6);            //Origin Airport Code
		fieldMap.put("OriginAptInd", 7);      //Origin Airport, Multiple Airports Indicator. Indicates Number of Airports that Serve a City When the Number is Greater than 1.
		fieldMap.put("OriginCityNum", 8);     //Origin Airport, City Code
		fieldMap.put("OriginCountry", 9);     //Origin Airport, Country Code
		fieldMap.put("OriginStateFips", 10);  //Origin Airport, State FIPS Code
		fieldMap.put("OriginState", 11);      //Origin Airport, State Code
		fieldMap.put("OriginStateName", 12);  //Origin State Name
		fieldMap.put("OriginWac", 13);        //Origin Airport, World Area Code
		fieldMap.put("Dest", 14);             //Destination Airport Code
		fieldMap.put("DestAptInd", 15);       //Destination Airport, Multiple Airports Indicator. Indicates Number of Airports that Serve a City When the Number is Greater than 1.
		fieldMap.put("DestCityNum", 16);      //Destination Airport, City Code
		fieldMap.put("DestCountry", 17);      //Destination Airport, Country Code
		fieldMap.put("DestStateFips", 18);    //Destination Airport, State FIPS Code
		fieldMap.put("DestState", 19);        //Destination Airport, State Code
		fieldMap.put("DestStateName", 20);    //Destination State Name
		fieldMap.put("DestWac", 21);          //Destination Airport, World Area Code
		fieldMap.put("Break", 22);            //Trip Break Code
		fieldMap.put("CouponType", 23);       //Coupon Type Code
		fieldMap.put("TkCarrier", 24);        //Ticketing Carrier Code
		fieldMap.put("OpCarrier", 25);        //Operating Carrier Code
		fieldMap.put("RPCarrier", 26);        //Reporting Carrier Code
		fieldMap.put("Passengers", 27);       //Number of Passengers
		fieldMap.put("FareClass", 28);        //Fare Class Code. Value Is Defined By Carriers And May Not Follow The Same Standard. Not Recommended For Analysis.
		fieldMap.put("Distance", 29);         //Coupon Distance
		fieldMap.put("DistanceGroup", 30);    //Distance Group, in 500 Mile Intervals
		fieldMap.put("Gateway", 31);          //Gateway Indicator (1=Yes)
		fieldMap.put("ItinGeoType", 32);      //Itinerary Geography Type
		fieldMap.put("CouponGeoType", 33);    //Coupon Geography Type
	}


	/**
	 * Entry point into the application.
	 * @param args
	 */
	public static void main (String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println ("Usage: "+Aggregator.class+" <input> <output> <groupByFields>");
			System.exit(1);
		}
		int ret = ToolRunner.run(new Configuration(), new Aggregator(), args);
		System.exit(ret);
	}

	public Aggregator () { }
	public Aggregator (Configuration conf) 
	{ 
		super(conf); 
	}
	public Aggregator (Configuration conf, int numMappers, int numReducers, boolean useCombiner) 
	{ 
		super(conf); 
		this.numMappers = numMappers;
		this.numReducers = numReducers;
		this.useCombiner = useCombiner;
	}

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

		// Parse the third command line argument to determine which fields
		// we will be grouping by.  This is done here as a validation step
		// to prevent the job from starting if invalid fields are specified.
		String[] fields = args[2].split(",");
		for (String field : fields) {
			if (!fieldMap.containsKey(field)) {
				System.out.println ("I'm sorry, I'm not aware of the field \""+field+"\"");
				return 1;
			}
		}
		System.out.println("Aggregation will be done on fields ["+args[2]+"]");

		// Setup the job
		JobConf job = new JobConf(conf, Aggregator.class);
		job.setJobName(Aggregator.class.getName());
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		if (this.numMappers > 0)
		{
			job.setNumMapTasks(this.numMappers);
			System.out.println ("Using #mappers="+this.numMappers);
		}
		if (this.numReducers > 0)
		{
			job.setNumReduceTasks(this.numReducers);
			System.out.println ("Using #reducers="+this.numReducers);
		}
		if (this.useCombiner)
		{
			job.setCombinerClass(Reduce.class);
			System.out.println ("Using combiner");
		}
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		// Set a parameter in JobConf with the set of fields specified on the
		// command line.
		job.set("aggregator.fields", args[2]);

		// Add paths
		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);

		// Set the input/output formats
		job.setInputFormat(TextInputFormat.class);
		job.setOutputFormat(TextOutputFormat.class);

		// Submit the job and wait for completion
		JobClient.runJob(job);
		return 0;
	}

	@SuppressWarnings("deprecation")
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable>
	{
		private Text word = new Text();

    	// The fields we will be grouping by at run-time,
    	// initialized from the command line arguments
    	private int[] groupByFields = null;

		public void configure (JobConf conf)
		{
    		String allfields = conf.get(AGGREGATOR_FIELDS_KEY);
    		String[] fields = allfields.split(",");
    		groupByFields = new int[fields.length];
    		for (int i=0; i < fields.length; i++) {
    			String field = fields[i];
    			groupByFields[i]=fieldMap.get(field);
    		}
		}

		public void map (LongWritable k, Text v, OutputCollector<Text, LongWritable> collector, Reporter reporter) throws IOException
		{
			// Split the line by comma
			String[] values = v.toString().split(",");

			// Emit the desired fields
			StringBuffer sb = new StringBuffer();
			for (int i=0; i < groupByFields.length; i++) {
				String fieldValue = values[groupByFields[i]];
				if (fieldValue.startsWith("\"") && fieldValue.endsWith("\""))
					fieldValue = fieldValue.substring(1, fieldValue.length()-1);
				sb.append (fieldValue);
				if (i < groupByFields.length-1)
					sb.append (",");
			}
			word.set(sb.toString());
			collector.collect(word, one);
		}
	}

	@SuppressWarnings("deprecation")
	public static class Reduce extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable>
	{
		public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> collector, Reporter reporter) throws IOException
		{
			long sum = 0;
			while (values.hasNext()) {
				sum += values.next().get();
			}
			collector.collect(key, new LongWritable(sum));
		}
	}


}
