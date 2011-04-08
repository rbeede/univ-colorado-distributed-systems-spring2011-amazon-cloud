package edu.colorado.cs.csci5673;


import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.sf.xrime.Transformer;
import org.sf.xrime.algorithms.GraphAlgorithm;
import org.sf.xrime.algorithms.clique.maximal.MaximalStrongCliqueAlgorithm;
import org.sf.xrime.model.Graph;
import org.sf.xrime.preprocessing.textadj.TextAdjTransformer;

public class ChainedMaximalCliqueJob extends Configured implements Tool {
	
	/**
	 * Entry point into the application.
	 * @param args
	 */
	public static void main (String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println ("Usage: "+ChainedMaximalCliqueJob.class+" <input> <output>");
			System.exit(1);
		}
		int ret = ToolRunner.run(new Configuration(), new ChainedMaximalCliqueJob(), args);
		System.exit(ret);
	}

	/**
	 * Sets up the map reduce job and initiates execution.
	 * Returns exit status (0 for success, 1 for failure).
	 */
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		
		int numMappers=0;
		int numReducers=0;
		boolean useCombiner = false;
		
	    List<String> other_args = new ArrayList<String>();
	    // Deal with mapper and reducer numbers.
	    try {
	    	for(int i=0; i < args.length; ++i) {
	    		if ("-m".equals(args[i])) {
	    			numMappers = Integer.parseInt(args[++i]);
	    		} else if ("-r".equals(args[i])) {
	    			numReducers = Integer.parseInt(args[++i]);
	    		} else if ("-c".equals(args[i])) {
	    			useCombiner = true;
	    		} else {
	    			other_args.add(args[i]);
	    		}
	    	}
	    } catch(Exception except) {
	    	throw new Exception(except);
	    }
	    
	    String[] newargs = new String[other_args.size()];
	    for (int i=0; i < other_args.size(); i++) {
	    	newargs[i]=other_args.get(i);
	    }
	    args = newargs;
		
		FileSystem fs = FileSystem.get(conf);
		System.out.println("Filesystem is "+fs.getUri());
		
		// Create a list to keep track of all our temporary directories.
		List<Path> tempDirsList = new ArrayList<Path>();
		
		Path input = new Path(args[0]);
		Path output = new Path(args[1]);
		
		try {
    		// First, we need to run the aggregation job. 
    		// This will perform an aggregation on the Origin, Dest[ination], and
    		// RPCarrier (Reporting Carrier) fields from the TransStats DB1BCoupon
    		// CSV data file.
			Path aggrTempDir = new Path(input.getParent(), "aggregation_temp");
			tempDirsList.add(aggrTempDir);
    		Aggregator aggr = new Aggregator(conf, numMappers, numReducers, useCombiner);
    		aggr.run(new String[]{args[0], "aggregation_temp", "Origin,Dest,RPCarrier"});
    		
    		// The results will be in the form
    		// "Origin","Dest","RPCarrier"\t<count>
    		// The next job we run will be responsible for generating a graph
    		// of this data, where airports are considered nodes and an edge
    		// between airports exists if there are at least 365 flights 
    		// (1 flight/day) from the origin to the destination.
    		Path graphTempDir = new Path(input.getParent(), "graph_temp");
    		tempDirsList.add(graphTempDir);
    		GraphGenerator gg = new GraphGenerator(conf);
    		gg.run(new String[]{"aggregation_temp", "graph_temp", "365"});
    		
    		// Temporary directory to convert from the simple text based representation
    		// of the graph, to a binary representation.
    		Path convertTempDir = new Path(input.getParent(), "convertor_temp");
    		tempDirsList.add(convertTempDir);
        	Transformer transformer = new TextAdjTransformer(graphTempDir, convertTempDir);
        	transformer.execute();
        		
        	// Now we will run the "MaximalStrongCliqueAlgorithm" on the binary version of the graph
        	// Need another temporary directory to store the output
        	Path cliqueTempDir = new Path(convertTempDir.getParent(), "clique_temp");
        	tempDirsList.add(cliqueTempDir);
        	GraphAlgorithm clique = new MaximalStrongCliqueAlgorithm();
            Graph src = new Graph(Graph.defaultGraph());
            src.setPath(convertTempDir);
            Graph dest = new Graph(Graph.defaultGraph());
            //dest.setPath(cliqueTempDir);
            dest.setPath(output);
            
            // Debugging
            System.out.println("Dest Parent="+dest.getPath().getParent());
            
            clique.setSource(src);
            clique.setDestination(dest);
            clique.execute();
            
            // Finally, we convert the binary graph output by the maximal clique algorithm
            // back to a human-readable form.
        	//transformer = new SequenceFileToTextFileTransformer(cliqueTempDir, output);
        	//transformer.execute();
        	
		} finally {
			for (Path path : tempDirsList) {
    			System.out.println("Deleting temporary directory ["+path+"]");
    			fs.delete(path, true);
			}
		}
		
		return 0;
	}
	
}
