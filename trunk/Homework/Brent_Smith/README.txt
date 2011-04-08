Brent Smith
CSCI5673
Hadoop EC2 MapReduce Assignment

DISCLAIMER:  This file is longer than it should be.  You can just read the
"Description" section below and I can explain the rest when we meet.

Description:
==================
  I chose to use the TransStats Aviation dataset.  This is provided by the 
Bureau of Transportation Statistics to the public and an older copy of the
data is already available via Amazon's public data sets (http://aws.amazon.com/datasets/2289).

There is a DB1BCoupon table that contains all the flight data for each quarter.
The file contained a row for each flight, with the origin, destination and airline.
The first step I performed was to extract the data for all quarters in 2007,
and concatenate it into a single file for the year.  The resulting file size
was roughly 6GB.  I created an 8GB EBS storage volume, mounted it to an AWS
instance, and copied the uncompressed, concatenated file there for future use.

My goal was to aggregate all the data by the origin, destination and airline.  
This shows the most frequent routes by airline.  The attached image was generated 
with gnuplot, using the top 10 most frequent routes (there were a total of 
~150,000 unique routes+airlines, so a histogram for all combinations is 
not a good idea).  The source code for this particular task is in the 
_Aggregator.java_ file.

The image file containing the histogram is "mostfreqroutes_bycarrier.png".

Extra Credit:
==================
I also wanted to do something a little more challenging with the data to understand
how difficult it is to do something other than simple aggregation.  The idea 
I decided on was to use a graph algorithm to figure out the largest complete 
subgraph of the graph of all origins and destinations.  This is commonly known
as the maximal clique problem in graph theory.  I thought this would be interesting,
since it should tell you the largest set of airports for which every airport in the
set has a direct route to every other airport in the set.

To do this, I first had to write another map-reduce job to generate a graph of 
all the routes. The output of the map-reduce job created a simple adjacency list
representation where each line consisted of a key which is the origin and the value
is the set of all destinations that can be reached from that origin.  The code for
this is in the _GraphGenerator.java_ file.

When I was researching how the maximal clique algorithm works,  I found a library
called X-RIME[1][2], which has implementations for several graph algorithms targeted
for Hadoop.  Luckily, one of these is the maximal clique algorithm.  Since
it seemed quite difficult to reproduce the algorithm, I opted to use the library
instead.  This required a little research into how the library works, as well as
some research on how to perform "chaining" of jobs in Map-Reduce.  But after a little
work, I was able to successfully write a Java class that performs the entire
end-to-end calculation using the previous classes, as well as the custom library.
The code for this class is in the _ChainedMaximalCliqueJob.java_ file.

[1] http://xrime.sourceforge.net/
[2] http://ieeexplore.ieee.org/xpl/freeabs_all.jsp?arnumber=5557271


Performance:
===================
*Default dfs.replication of 3 is used for all tests.

The following data is for instance types of m1.small.
The corresponding graph is "m1small.png".

Instance Type  Clstr Size  Load Time  Exec. Time  #Mappers  #Reducers   Aggregation  Combiner?
m1.small                5    4:43.50    13:35.77        92          1         10:03         No
m1.small               10    4:38.20    10:27.56        92          1          7:29         No
m1.small               19    6:17.32     9:06.56        92          1          6:08         No
m1.small                5         --    12:22.98       100          5          8:51         No
m1.small               10         --     7:39.33       100         10          5:00         No
m1.small               19         --     5:40.99       100         20          2:56         No
m1.small                5         --     9:56.80        92          1          6:40        Yes
m1.small               10         --     6:37.61        92          1          3:49        Yes
m1.small               19         --     4:56.44        92          1          2:22        Yes
m1.small                5         --    11:04.80       100          5          7:35        Yes
m1.small               10         --     7:13.29       100         10          4:20        Yes
m1.small               19         --     5:16.00       100         20          2:33        Yes


The following data is for instance types of c1.medium.
The corresponding graph is "c1medium.png".

Instance Type  Clstr Size  Load Time  Exec. Time  #Mappers  #Reducers   Aggregation  Combiner?
c1.medium               5    3:05.89     5:19.73        92          1          3:04         No
c1.medium              10    3:04.34     4:49.53        92          1          2:40         No
c1.medium              19    3:12.29     4:32.87        92          1          2:23         No
c1.medium               5         --     4:51.35       100          5          2:34         No
c1.medium              10         --     3:42.59       100         10          1:35         No
c1.medium              19         --     3:07.11       100         20          1:02         No
c1.medium               5         --     4:02.78        92          1          1:50        Yes
c1.medium              10         --     3:20.79        92          1          1:08        Yes
c1.medium              19         --     3:45.77        92          1          1:40        Yes
c1.medium               5         --     4:22.14       100          5          2:04        Yes
c1.medium              10         --     3:26.61       100         10          1:18        Yes
c1.medium              19         --     3:00.24       100         20          0:52        Yes

Results:
===================
I found that there were 4 maximal cliques of size 27.  These sets of airports are listed below.

$ ./largest_airport_cliques.py output3/part-00000 
ATL [Atlanta, GA]                   ATL [Atlanta, GA]                   ATL [Atlanta, GA]                   ATL [Atlanta, GA]                   
BNA [Nashville, TN]                 BNA [Nashville, TN]                 BNA [Nashville, TN]                 BNA [Nashville, TN]                 
BOS [Boston, MA]                    BOS [Boston, MA]                    BOS [Boston, MA]                    BOS [Boston, MA]                    
BWI [Baltimore, MD]                 BWI [Baltimore, MD]                 BWI [Baltimore, MD]                 BWI [Baltimore, MD]                 
CLE [Cleveland, OH]                 CLE [Cleveland, OH]                 CLE [Cleveland, OH]                 CLE [Cleveland, OH]                 
CLT [Charlotte, NC]                 CLT [Charlotte, NC]                 CLT [Charlotte, NC]                 CLT [Charlotte, NC]                 
CMH [Columbus, OH]                  CMH [Columbus, OH]                  CMH [Columbus, OH]                  CMH [Columbus, OH]                  
CVG [Cincinnati, OH]                CVG [Cincinnati, OH]                CVG [Cincinnati, OH]                CVG [Cincinnati, OH]                
DCA [Washington, DC]                DCA [Washington, DC]                DCA [Washington, DC]                DCA [Washington, DC]                
DEN [Denver, CO]                    DEN [Denver, CO]                    DEN [Denver, CO]                    DEN [Denver, CO]                    
DFW [Dallas/Ft.Worth, TX]           DFW [Dallas/Ft.Worth, TX]           DFW [Dallas/Ft.Worth, TX]           DFW [Dallas/Ft.Worth, TX]           
DTW [Detroit, MI]                   DTW [Detroit, MI]                   DTW [Detroit, MI]                   DTW [Detroit, MI]                   
EWR [Newark, NJ]                    EWR [Newark, NJ]                    IAD [Washington, DC]                IAD [Washington, DC]                
IAD [Washington, DC]                IAD [Washington, DC]                IAH [Houston, TX]                   IAH [Houston, TX]                   
IAH [Houston, TX]                   IAH [Houston, TX]                   JFK [New York, NY]                  JFK [New York, NY]                  
JFK [New York, NY]                  JFK [New York, NY]                  LAS [Las Vegas, NV]                 LAS [Las Vegas, NV]                 
LAS [Las Vegas, NV]                 LAS [Las Vegas, NV]                 LAX [Los Angeles, CA]               LAX [Los Angeles, CA]               
LAX [Los Angeles, CA]               LAX [Los Angeles, CA]               MCO [Orlando, FL]                   MCO [Orlando, FL]                   
MCO [Orlando, FL]                   MCO [Orlando, FL]                   MIA [Miami, FL]                     MSP [Minneapolis/St. Paul Int, MN]  
MIA [Miami, FL]                     MSP [Minneapolis/St. Paul Int, MN]  MSP [Minneapolis/St. Paul Int, MN]  ORD [Chicago, IL]                   
MSP [Minneapolis/St. Paul Int, MN]  ORD [Chicago, IL]                   ORD [Chicago, IL]                   PHL [Philadelphia, PA]              
ORD [Chicago, IL]                   PHX [Phoenix, AZ]                   PHL [Philadelphia, PA]              PHX [Phoenix, AZ]                   
PHX [Phoenix, AZ]                   PIT [Pittsburgh, PA]                PHX [Phoenix, AZ]                   PIT [Pittsburgh, PA]                
PIT [Pittsburgh, PA]                RDU [Raleigh/Durham, NC]            PIT [Pittsburgh, PA]                RDU [Raleigh/Durham, NC]            
RDU [Raleigh/Durham, NC]            SLC [Salt Lake City, UT]            RDU [Raleigh/Durham, NC]            SLC [Salt Lake City, UT]            
STL [St. Louis, MO]                 STL [St. Louis, MO]                 STL [St. Louis, MO]                 STL [St. Louis, MO]                 
TPA [Tampa, FL]                     TPA [Tampa, FL]                     TPA [Tampa, FL]                     TPA [Tampa, FL]   

Other Notes:
===================
The assignment was certainly challenging in scope.  I spent a significant amount of
time just learning about Amazon's AWS services, including understanding the pricing
model, the command line tools, and the differences between S3 vs. EBS backed instances,
etc...  Then I had several other mis-steps along the way, including using the most
recent version of hadoop (0.21.0), which of course is the unstable version that 
breaks API compatibility with previous releases.  I wrote my first MapReduce job using
this version, tested it locally, only to realize that the Cloudera AMI instances only
had version 0.18.3 installed on them.  So I re-wrote my jobs to be API compatible with 
Hadoop 0.18.3 and ran my first few tests on the Cloudera AMIs.

Later, when I was playing around with the X-RIME library, I found out that it used
some functions that were added after 0.18.3.  To get it to work correctly, I had to
upgrade to 0.20.2, which was fine since the API was not broken between those two
releases, however then I couldn't use the Cloudera instances since they are only 
available with older versions of Hadoop.  To get around this, I ended up using the
contributed scripts included in the official Hadoop package to build my own Amazon 
machine instance with the correct version of Hadoop for my needs (there wasn't an
AMI with hadoop version 0.20.2 available).  After doing this, the scripts had to be
modified a little bit to make sure my custom AMI would work.

Commands:
====================
# vi conf/hadoop-env.sh  (HADOOP_CLASSPATH)
# vi conf/hadoop-site.xml (compression)
% ec2-attach-volume ... -i ami-id -d /dev/sdf
# mount /dev/sdf /data
# bin/hadoop fs -mkdir input
# /usr/bin/time -- bin/hadoop fs -put /data/origin_and_destination_survey_2007.csv input
% % sftp -oIdentifyFile ~/.ec2/brent.smith.pem root@
# cp ~/xrime.jar ~/csci5673-brent-smith.jar .
# /usr/bin/time -- bin/hadoop jar csci5673-brent-smith.jar edu.colorado.cs.csci5673.ChainedMaximalCliqueJob input output
# mkdir /data/small_5_run4 && bin/hadoop fs -copyToLocal output /data/small_5_run4/
# bin/hadoop fs -rmr output
% bin/hadoop-ec2 terminate-cluster c1_medium_cluster_5



