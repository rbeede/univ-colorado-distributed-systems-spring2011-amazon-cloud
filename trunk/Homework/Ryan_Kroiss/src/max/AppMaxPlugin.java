package org.cu.distsys.rkroiss;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.lib.aggregate.ValueAggregatorBaseDescriptor;

public class AppMaxPlugin extends ValueAggregatorBaseDescriptor {
	
	private String inputFile;

	public ArrayList<Entry<Text, Text>> generateKeyValPairs(Object key,
			Object val) {
		
		String countType = LONG_VALUE_MAX;
		ArrayList<Entry<Text, Text>> retv = new ArrayList<Entry<Text, Text>>();
		String line = val.toString();
		String[] tokens = line.split("[,\n]");

		if (tokens == null || tokens.length <= 2)
			return retv;
		if (tokens[0].equals("\"\"") || tokens[0].equals("\"Time\""))
			return retv;

		int index = inputFile.lastIndexOf("/");
		String fileName = inputFile.substring(index + 1);

		Entry<Text, Text> e = generateEntry(countType, fileName, new Text(
				tokens[2].replaceAll("\"", "")));
		if (e != null)
			retv.add(e);
		return retv;
	}

	public void configure(JobConf job) {
		inputFile = job.get("map.input.file");
	};
}