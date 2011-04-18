import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;

public class WeatherData {

//Runs two job, one gets the mean temperature count and other reorders
//it from Most to Least. Choose the 2 MapReduce and not the
//"SecondSort" Option
public static void main(String[] args) {
    JobClient client = new JobClient();
    JobConf conf = new JobConf(WeatherData.class);
    conf.setJobName("USAWeathercount");
    // specify output types
    conf.setOutputKeyClass(Text.class);
    conf.setOutputValueClass(LongWritable.class);

    // specify input and output dirs
    FileInputFormat.setInputPaths(conf, new Path("input/input")); //I locked input
    FileOutputFormat.setOutputPath(conf, new Path("output"));

    // specify a mapper
    conf.setMapperClass(WeatherDataMapper.class);

    // specify a reducer
    conf.setCombinerClass(WeatherDataReducer.class);
    conf.setReducerClass(WeatherDataReducer.class);
    conf.setBoolean("mapred.output.compress", false); //So Text Output

    client.setConf(conf);
    try {
      JobClient.runJob(conf);
    } catch (Exception e) {
      e.printStackTrace();
    }

    JobClient client2 = new JobClient();
    JobConf conf2 = new JobConf(WeatherData.class);
    conf2.setJobName("USAWeathercount2");
    // specify output types
    conf2.setBoolean("mapred.output.compress", false); //So Text Output
    conf2.setOutputKeyClass(LongWritable.class);
    conf2.setOutputValueClass(Text.class);

    conf2.setMapOutputKeyClass(LongWritable.class);
    conf2.setMapOutputValueClass(Text.class);


    // specify input and output dirs
    FileInputFormat.setInputPaths(conf2, new Path("output"));
    FileOutputFormat.setOutputPath(conf2, new Path("output2"));

    // specify a mapper
    conf2.setMapperClass(WeatherDataSort.class);
    conf2.setOutputKeyComparatorClass(ReverseCompare.class); //Used to reorder at end

    // specify a reducer
    conf2.setCombinerClass(WeatherDataFlipper.class);
    conf2.setReducerClass(WeatherDataFlipper.class);
    client2.setConf(conf2);
    try {
      JobClient.runJob(conf2);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}