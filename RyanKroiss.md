# Introduction #

My project analyzed a [data set](http://odysseas.calit2.uci.edu/doku.php/public:online_social_networks#facebook_applications) from the Networking Group at the University of California - Irvine which they kindly allowed me to use.

This data set contained the number of active users and total installations daily for every Facebook application over a certain time span.  I chose to look at the number of active users per day.  Some people use this as an indicator of popularity.  Others suggest that this is a poor metric.  Regardless, the results were interesting.  To respect the wishes of the data providers, I will not post the data and associated results, only my [source code](http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/#svn%2Ftrunk%2FHomework%2FRyan_Kroiss).


# Details #

My [implementation](http://code.google.com/p/univ-colorado-distributed-systems-spring2011-amazon-cloud/source/browse/#svn%2Ftrunk%2FHomework%2FRyan_Kroiss) takes advantage of the [ValueAggregator](http://hadoop.apache.org/mapreduce/docs/current/api/org/apache/hadoop/mapreduce/lib/aggregate/package-summary.html) class.  This greatly simplifies the task of calculating aggregates with MapReduce.


# Citation #

Citation of work done by the researchers that allowed me to use the data:

@inproceedings{mgjoka\_wosn08,
author= {Minas Gjoka and Michael Sirivianos and Athina Markopoulou and
Xiaowei Yang},
title= { {Poking Facebook: Characterization of OSN Applications} },
booktitle = {Proceedings of ACM SIGCOMM Workshop on Online Social
Networks (WOSN) '08},
address = {Seattle, WA},
month = {August},
year = {2008}
}