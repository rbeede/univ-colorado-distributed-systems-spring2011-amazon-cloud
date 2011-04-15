package org.cu.distsys.rkroiss;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.lib.aggregate.ValueAggregatorBaseDescriptor;

public class AppHistogramPlugin extends ValueAggregatorBaseDescriptor {
	
	private String inputFile;
	
	public ArrayList<Entry<Text, Text>> generateKeyValPairs(Object key,
			Object val) {
		
		ArrayList<Entry<Text, Text>> retv = new ArrayList<Entry<Text, Text>>();
		String line = val.toString();
		String[] tokens = line.split(" |\t");

		Text count = new Text(tokens[2].replaceAll("\"", "")+"\t"+"1");
		Entry<Text, Text> e = generateEntry(VALUE_HISTOGRAM, "APP_HISTOGRAM",
				count);
		if (e != null)
			retv.add(e);
		return retv;
	}

	public void configure(JobConf job) {
		inputFile = job.get("map.input.file");
	};
}