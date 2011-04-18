import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

//Reducers combines all the counts to get the totalSum Count
public class WeatherDataReducer extends MapReduceBase
    implements Reducer<Text, LongWritable, Text, LongWritable> {

  public void reduce(Text key, Iterator values,
      OutputCollector output, Reporter reporter) throws IOException {

    long sum = 0;
    while (values.hasNext()) {
      LongWritable value = (LongWritable) values.next();
      sum += value.get(); // process value
    }
    output.collect(key, new LongWritable(sum));
  }
}