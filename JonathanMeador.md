**Not finish yet, should be done by 4/15/2011**

# Introduction #
Decided to do a mapreduce on temperature reading from station located in NorthAmerica. Overall I wanted to see the most frequent temperature from 2000-2005.

# Source Code #
Source code can be found:

http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/trunk/Homework/Jon_Meador/ReverseCompare.java

Custom compare that helps with flipping

http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/trunk/Homework/Jon_Meador/WeatherData.java

Main code for MapReduce

http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/trunk/Homework/Jon_Meador/WeatherDataFlipper.java

> Reducer in the flip part

http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/trunk/Homework/Jon_Meador/WeatherDataMapper.java

Main Mapper code

http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/trunk/Homework/Jon_Meador/WeatherDataReducer.java

Main Reducer code

http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/trunk/Homework/Jon_Meador/WeatherDataSort.java

Main Map Code




I decided to write 2 map reduce to first collect the data and then second sort it in terms of frequency. Another way to do this is to implement something called a "secondsort" that runs between MAP and REDUCE to order the data in the format you want.

# Results #
Results of the final output can be found:  http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/trunk/Homework/Jon_Meador/output2

Data is count to temp, Highest to lowest