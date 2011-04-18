import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

//Mapper moves the count and temp keys around so the NewSort will list
//count of temperature from Most to Least
public class WeatherDataSort extends MapReduceBase
    implements Mapper<LongWritable, Text, LongWritable, Text> {

  private Text word = new Text();
  private LongWritable count = new LongWritable();

  public void map(LongWritable key, Text value,
      OutputCollector output, Reporter reporter) throws IOException {

    String[] parts = value.toString().split("\\s+");

    word.set(parts[0]);
    count.set(Long.parseLong(parts[1]));
    output.collect(count, word);

  }
  
  
}