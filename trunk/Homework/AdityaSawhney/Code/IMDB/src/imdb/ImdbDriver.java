package imdb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ImdbDriver extends Configured implements Tool {
	
	static int printUsage() {
		System.out.println("movieanalysis [-h <histogram>] <input> <output>");
		ToolRunner.printGenericCommandUsage(System.out);
		return -1;
	}

	public int run(String[] args) throws Exception {
		Configuration config = getConf();

		List<String> other_args = new ArrayList<String>();
		boolean histogram = false;
		
		for (int i = 0; i < args.length; ++i) {
			try {
				if ("-h".equals(args[i])) {
					histogram = true;
				} else {
					other_args.add(args[i]);
				}
			} catch (Exception except) {
				System.out.println("ERROR: " + except.getMessage());
				return printUsage();
			}
		}

		// Make sure there are exactly 2 parameters left.
		if (other_args.size() != 2) {
			System.out.println("ERROR: Wrong number of parameters: "
					+ other_args.size() + " instead of 2.");
			return printUsage();
		}
		
		String filterOutput = File.createTempFile("filter", null).getName();
		String countOutput = File.createTempFile("count", null).getName();
		
		config.setBoolean("moviecount.histogram", histogram);
		config.set("moviecount.filter.input", other_args.get(0));
		config.set("moviecount.filter.output", filterOutput);
		config.set("moviecount.count.input", filterOutput);
		config.set("moviecount.count.output", countOutput);
		config.set("moviecount.sort.input", countOutput);
		config.set("moviecount.sort.output", other_args.get(1));
		
		MovieFilter.execute(config, false);
		MovieCount.execute(config, true);
		MovieSort.execute(config, true);
		
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new ImdbDriver(), args);
		System.exit(res);
	}
}
