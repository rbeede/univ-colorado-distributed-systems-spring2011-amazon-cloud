# Analysis of NPAD Web100 data set using GeoIP #

## Introduction ##

For this project, I started with the NPAD project data set available on Amazon EC2:
http://aws.amazon.com/datasets/3189?_encoding=UTF8&queryArg=searchQuery&x=0&fromSearch=1&y=0&searchPath=datasets&searchQuery=npad

NPAD project details:
http://www.psc.edu/networking/projects/pathdiag/

Using some shell commands, I broke out parts of this data set that contained the result of the test (pass/fail), the IP address, and other details of the test.  Then I broke combined files of those records into files for each data a test results.  This created a reasonable number of input files (in the 100's) to use as input for Hadoop map tasks.  Using the python wrapper for Hadoop, I ran the code linked below to produce a a single result file of of key, value pairs.  The key was associated with an identifier based on the domain name and the value was the count of passed tests.

From these results, I used Google Fusion Tables to produce the intensity maps below.  Latitude and longitude were obtained from the www.hostip.info Geotarget database.


## Code ##

http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/Blake_Caldwell

## Results ##
### Good paths ###
![http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/Blake_Caldwell/good%20paths.png](http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/Blake_Caldwell/good%20paths.png)

### Bad paths ###
![http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/Blake_Caldwell/bad_paths.png](http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/Blake_Caldwell/bad_paths.png)
![http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/Blake_Caldwell/bad_paths2.png](http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/Blake_Caldwell/bad_paths2.png)

## Google Earth Map (.kml) ##
http://univ-colorado-distributed-systems-spring2011-amazon-cloud.googlecode.com/svn/trunk/Homework/Blake_Caldwell/ip.geo.kml