import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

//Reducer writes values out, Key is the overloaded Compare.
public class WeatherDataFlipper extends MapReduceBase
    implements Reducer<LongWritable, Text, LongWritable, Text> {

	private Text word = new Text();
	private Text count = new Text();
  public void reduce(LongWritable key, Iterator values,
      OutputCollector output, Reporter reporter) throws IOException {

	
    while (values.hasNext()) {
      Text value = (Text) values.next();
     // sum += value.get(); // process value
      output.collect(key, value);
    }

   // output.collect(key, new IntWritable(sum));
  }
}