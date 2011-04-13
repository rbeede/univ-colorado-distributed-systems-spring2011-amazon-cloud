package net.wsnetwork.ds;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 * Processes weather data files and returns a key/value pair mapping the index of the coldest day
 * of the year to the station ID.
 * @author cairns@colorado.edu (Brian Cairns)
 */
public class ColdestDayMapper extends MapReduceBase implements 
Mapper<BytesWritable, BytesWritable, LongWritable, LongWritable> {
	/**
	 * Finds the coldest day of the year for a particular year and station, producing an output
	 * pair that maps the coldest day (Integer - day of year) to the station ID (value).
	 */
	@Override public void map(BytesWritable fileName, BytesWritable data,
			OutputCollector<LongWritable, LongWritable> out, Reporter reporter)
			throws IOException {
		Long stationId = null;
		
		// Read weather data
		Map<Date, Double> temperatures = new HashMap<Date, Double>();
		String weatherData = new String(data.getBytes());
		String[] dataEntries = weatherData.split("\n");
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		for (int i = 1; i < dataEntries.length; ++i) {
			String entry = dataEntries[i];
			
			// Split fields on whitespace characters
			String[] fields = entry.split("[\t ]+");
			
			// Check entry length
			if (fields.length < 4) {
				reporter.incrCounter(Counters.NotEnoughFields, 1);
				continue;
			}
			
			// Read station ID
			long entryStationId;
			try {
				entryStationId = Integer.parseInt(fields[0]);
			} catch (NumberFormatException e) {
				reporter.incrCounter(Counters.InvalidStationFormat, 1);
				continue;
			}
			if (stationId == null) {
				stationId = entryStationId;
			} else {
				// Station ID should be the same for all entries in the file
				if (!stationId.equals(entryStationId)) {
					reporter.incrCounter(Counters.MultipleStationsInSingleFile, 1);
					continue;
				}
			}
			
			// Read entry date
			Date entryDate;
			try {
				entryDate = format.parse(fields[2]);
			} catch (ParseException e) {
				reporter.incrCounter(Counters.InvalidDateFormat, 1);
				continue;
			}
			
			// Read temperature
			double entryTemperature;
			try { 
				entryTemperature = Double.parseDouble(fields[3]);
			} catch (NumberFormatException e) {
				reporter.incrCounter(Counters.InvalidTemperatureFormat, 1);
				continue;
			}
			
			temperatures.put(entryDate, entryTemperature);
		}
		
		// Find coldest day of the year
		long coldestDay = 0;
		Double coldestTemperature = null;
		for (Entry<Date, Double> entry : temperatures.entrySet()) {
			double temperature = entry.getValue();
			if (coldestTemperature == null || temperature < coldestTemperature) {
				coldestTemperature = temperature;
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(entry.getKey());
				int dayNumber = calendar.get(Calendar.DAY_OF_YEAR);
				coldestDay = dayNumber;
			}
		}
		
		// Output coldest day (key) and station ID (value)
		out.collect(new LongWritable(coldestDay), new LongWritable(stationId));
	}
}
