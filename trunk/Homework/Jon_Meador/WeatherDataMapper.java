import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

//This code counts each time a mean temperature in the US is found.
public class WeatherDataMapper extends MapReduceBase
    implements Mapper<LongWritable, Text, Text, LongWritable> {

  private final LongWritable one = new LongWritable(1);
  private Text word = new Text();

  public void map(LongWritable key, Text value,
      OutputCollector output, Reporter reporter) throws IOException {

    String[] parts = value.toString().split("\\s+");
    if(!parts[0].equals("STN---")) //So it doesn't crash on the first line
    {	
    	int station = Integer.parseInt(parts[0]);
    	//Not perfect but covers most US stations and Canada south stations
    	//Trouble with dynamic station lookup. Downfall of MapReduce?
    	if(720000 <= station && station <= 740204) //Best Range of US stations
    	{
    		word.set(parts[3]);  //Gets the mean temp.
    		output.collect(word, one);
    	}
    }
  }
  
  
}