package net.wsnetwork.ds;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class ColdestDayReducer extends MapReduceBase implements
		Reducer<LongWritable, LongWritable, LongWritable, LongWritable> {

	@Override
	public void reduce(LongWritable day, Iterator<LongWritable> stations,
			OutputCollector<LongWritable, LongWritable> out, Reporter reporter)
			throws IOException {
		// Count up number of years for which this index was the coldest day of the year
		long n = 0;
		while (stations.hasNext()) {
			++n;
			stations.next();
		}
		
		// Output the year count
		out.collect(day, new LongWritable(n));
	}
}
