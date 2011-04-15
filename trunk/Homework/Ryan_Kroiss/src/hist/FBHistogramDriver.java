package org.cu.distsys.rkroiss;

import java.io.IOException;

import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.lib.aggregate.ValueAggregatorJob;

public class FBHistogramDriver {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		JobConf conf = ValueAggregatorJob.createValueAggregatorJob(args,
				new Class[] {AppHistogramPlugin.class});
		conf.setJarByClass(FBHistogramDriver.class);
		JobClient.runJob(conf);
	}

}
