import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

//This class is used in the second pass to order the data from Most Frequent
//to Least Frequent
public class ReverseCompare extends LongWritable.Comparator  {

	public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
	{
	    long thisValue = readLong(b1, s1);
	    long thatValue = readLong(b2, s2);
	    return (thisValue>thatValue ? -1 : (thisValue==thatValue ? 0 : 1));

		
	}


}
