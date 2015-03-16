# Introduction #
Word Count program using Hadoop MapReduce on an Amazon EC2 cluster.

# Copyright #
Copyright (C) 2011 by Arpit Sud

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

# Description #
The program does a word frequency count in the 20-newsgroups data set (http://people.csail.mit.edu/jrennie/20Newsgroups/) using MapReduce(WordCount.java). The result is then processed through another MapReduce(CountHistogram.java) in order to find the number of words in each frequency count. After each reduce operation, the result is sorted using the UNIX sort command. The result of the second MapReduce operation is plotted on a graph using a Python script(extra-credit). The whole operation (excluding the graph generation) has been automated by means of a bash script.
The MapReduce operations are performed on a cluster of 10 slave nodes and 1 master node.

The data set used is the 20-newsgroups data-set: http://people.csail.mit.edu/jrennie/20Newsgroups/20news-18828.tar.gz
While performing WordCount the following operations are performed:
  1. All alphabets are converted to lower case.
  1. All special characters are removed.
  1. Any extra spaces(in the beginning/end/between) are removed.
  1. The remaining string is tokeninzed to obtain words.

Two different versions of graph generation program are attached. One version (generategraph\_line.py) plots the points connected by a line. The other (generategraph\_point.py) plots the points without connecting them.

The source files are in the src/ directory.
The result (including the graphs) are stored in the result/ directory.

# Lessons Learned #
  1. Hadoop/HDFS works best with small number of large files. While the map operation on 18828 small files took close to 52 minutes on a cluster of 10 machines, when the same files were joined together into 20(bigger) files, the map operation finished in less than a minute.
  1. Writing the result of reduce operation directly to S3 is possible, but it slows down reduce by a large factor(20 minutes vs 30 seconds). It is best to write the result of reduce to HDFS and later copy it to S3/other location.

# Software Components Used #
  1. Cloudera Distribution for Hadoop(CDH)
  1. CDH Cloud Scripts
  1. Java(for writing MapReduce programs)
  1. Python(and matplotlib for graph generation)
  1. EC2 Command line tools
  1. Bash scripting

# Instructions #
  1. Install CDH Cloud Scripts on your local computer. Configure it with you AWS access keys.
  1. Run the following commands in order to start the cluster:
> > `hadoop-ec2 launch-cluster csci5673-pa01 10`
  1. Login to the master node of the cluster:
> > `hadoop-ec2 login csci5673-pa01`
  1. Copy the archive containing the Java code and scripts into the master node. Unzip/untar it.
  1. In the master node run the script setupinput.sh. This downloads the data set from the MIT website and stores it in HDFS.
  1. Set the variable MICROEBS in the environment to a running micro instance on the EC2, where the results will be copied after computation. (e.g. export MICROEBS=ec2-184-73-0-76.compute-1.amazonaws.com). This was done to make sure that the result is stored automatically in a persistent state. Initially I had tried to store the result in S3 bucket directly from the Reduce operation. But it slowed the execution of my program by a significant factor. In case you don't have a valid host to SSH into store an invalid ip address/hostname which will not resolve (e.g. export MICROEBS=foobar)
  1. Run the script pa01.sh. This compiles the Java code, submits it to hadoop, obtain and sort the results. It also stores the result in a remote machine using SSH, if MICROEBS was configured properly.
  1. The wordcount result is stored in countoutput/countoutput. The sorted word count result is stored in countoutput/sortedwordcount(both on HDFS & the local filesystem and if a valid host was entered in step 2 then also on that machine).
  1. The histogram result is stored in histoutput/histoutput. The sorted histogram result is stored in histoutput/sortedhist(both on HDFS & the local filesystem and if a valid host was entered in step 2 then also on that machine).
  1. After you are satisfied that the results have been stored safely on a persistent storage, you can logout from the master node and run the following command from your local machine.
> > `hadoop-ec2 terminate-cluster csci5673-pa01`
  1. While the MapReduce job is running, you can monitor the progress of the tasks on the following URL in your browser
> > <Master Node DNS Name>:50030