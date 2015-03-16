# Introduction #

I decided to do a mapreduce job that parses all the numbers in the data files for the State of Oklahoma from the 2000 US Census.

Using this I found that the number 0 was the most frequent which is interesting because it shows how the compression of the data from about 3GB to about 700MB was effective.

# Source Code #

http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/#svn%2Ftrunk%2FHomework%2FRodney_Beede%2FCode

I wrote two separate classes that are run as independent mapreduce jobs.  Although I could have implemented the sorting by frequency as a secondary key in the first job I choose a separate run because it gave me a more general tool.  It also only took about 40 seconds.

# Result Data on Amazon Cloud #

http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/trunk/Homework/Rodney_Beede/Report.txt

Running on 10 instances versus 5 instances was not a 2x speed up.  This is due to the overhead of the disk I/O and communication I believe.

You can view the raw data at http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/trunk/Homework/Rodney_Beede/2011-03-04__Rodney_Beede__hadoop_ec2___RESULTS.tar.bz2