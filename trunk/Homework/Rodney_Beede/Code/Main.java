import java.util.*;

public class Main {
	public static void main(final String[] args) throws Exception {
		final long startTime = Calendar.getInstance().getTimeInMillis();  // master run time for all jobs
		
		final String firstJobOutputDir = "combined_job_Step1of2_" + Long.toString(startTime);
		
		try {
			final String[] adjustedArgs = {args[0], firstJobOutputDir};
			NumberFrequency.main(adjustedArgs);
			System.out.println();
			System.out.println();
		} catch(final Throwable t) {
			System.err.println("Job failed with an error!  " + t.getMessage());
			System.exit(255);
		}
		
		final String secondJobOutputDir = "combined_job_Step2of2_" + Long.toString(startTime);
		
		try {
			final String[] switchedArgs = {firstJobOutputDir, secondJobOutputDir};
		
			SortByMostFrequentItem.main(switchedArgs);
			System.out.println();
			System.out.println();
		} catch(final Throwable t) {
			System.err.println("Job failed with an error!  " + t.getMessage());
			System.exit(255);
		}
		
		final long endTime = Calendar.getInstance().getTimeInMillis();  // master run time for all jobs
		
		System.out.println("Total Run Time of All Jobs:");
		System.out.println("Start Time:\t" + new Date(startTime)); // milliseconds epoch
		System.out.println("Finish time:\t" + new Date(endTime));
		System.out.println("Run Time (milliseconds):\t" + (endTime - startTime));

	}
}