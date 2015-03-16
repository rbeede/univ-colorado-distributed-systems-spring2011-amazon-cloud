<strong><a href='http://moodle.cs.colorado.edu/mod/assignment/view.php?id=252' title='Programming Assignment - Distributed Systems'>Programming Assignment - Distributed Systems</a></strong>

<p><span><strong>Due Friday March 4 by 11:55 pm</strong></span><span><strong><br /></strong></span></p>

<p>The goal of this programming assignment is to enable you to gain experience programming with:</p>

<p>- Amazon Web Services, specifically the EC2 cloud, and S3</p>

<p>- the Hadoop open source framework</p>

<p>- and breaking down a task into a parallel distributed mapreduce model</p>

<p>You may form groups of two to complete this assignment.</p>

<p><strong>Sign up</strong> for an account on Amazon Web Services.  Use your Amazon key code for $100 credit.  Redeem it at <a href='http://aws.amazon.com/awscredits'><a href='http://aws.amazon.com/awscredits'>http://aws.amazon.com/awscredits</a></a>.  Learn about EC2 at <a href='http://aws.amazon.com/ec2/' title='Amazon EC2'><a href='http://aws.amazon.com/ec2/'>http://aws.amazon.com/ec2/</a></a>.</p>

<p><strong>Find a dataset</strong>.  There are some publicly available datasets on Amazon at <a href='http://aws.amazon.com/datasets' title='Amazon data sets'><a href='http://aws.amazon.com/datasets'>http://aws.amazon.com/datasets</a></a>.  For example, there's a global weather data set at <a href='http://aws.amazon.com/datasets/2759' title='Weather data set'><a href='http://aws.amazon.com/datasets/2759'>http://aws.amazon.com/datasets/2759</a></a> (20 GB), the 2000 Census, transportation, economics, and Wikipedia page statistics (320 GB).  Wikipedia itself can be obtained at <a href='http://dumps.wikimedia.org/' title='Wikipedia data sets'><a href='http://dumps.wikimedia.org/'>http://dumps.wikimedia.org/</a></a>.  You don't have to use a complete data set.  Let's say 10-100 MB should be sufficient, though you're welcome to try GB of data.<span> </span><span></span></p>

<p><strong>Install Hadoop</strong> on your Amazon instance AMI.  Process this data set using the Hadoop framework on EC2.  See <a href='http://hadoop.apache.org/' title='Hadoop Apache'><a href='http://hadoop.apache.org/'>http://hadoop.apache.org/</a></a> for the source code.  You are not allowed to use Amazon's Elastic <code>MapReduce</code> service.  You will have to install hadoop on your own VM instances.  See an example of how to install Hadoop on AWS at <a href='http://www.cloudera.com/blog/2009/05/using-clouderas-hadoop-amis-to-process-ebs-datasets-on-ec2/' title='Hadoop on EC2'><a href='http://www.cloudera.com/blog/2009/05/using-clouderas-hadoop-amis-to-process-ebs-datasets-on-ec2/'>http://www.cloudera.com/blog/2009/05/using-clouderas-hadoop-amis-to-process-ebs-datasets-on-ec2/</a></a>.<span></span></p>

<p><strong>Calculate</strong> the most frequent data item in this data set using Hadoop for a given category, say temperature for the weather data set, or the most popular Wikipedia page.  Sort the data set from most frequent item to least frequent item.  You should use at least 10 instances to perform the mapreduce calculations.  Feel free to vary the # of instances and type of instance to see how the performance varies.  Is it linear across number of instances for a given type?</p>

<p>Here's a mapreduce example <a href='http://hadoop.apache.org/common/docs/r0.20.2/mapred_tutorial.html#Example%3A+WordCount+v2.0' title='Mapreduce example'><a href='http://hadoop.apache.org/common/docs/r0.20.2/mapred_tutorial.html#Example%3A+WordCount+v2.0'>http://hadoop.apache.org/common/docs/r0.20.2/mapred_tutorial.html#Example%3A+WordCount+v2.0</a></a>.   Also see <a href='http://code.google.com/edu/parallel/mapreduce-tutorial.html' title='Google code mapreduce'><a href='http://code.google.com/edu/parallel/mapreduce-tutorial.html'>http://code.google.com/edu/parallel/mapreduce-tutorial.html</a></a>.</p>

<p><strong>Store</strong> the results persistently in Amazon S3 or another persistent data store.  Visualize the sorted results in a suitable way, so it can be observed that the data items were properly sorted.</p>

<p><strong>Grading</strong>: upload a zip file of your source code.  We'll arrange grading meetings in early to mid March to go over the results of your Hadoop implementation on EC2.</p>

<p><strong>Extra Credit:</strong> use mapreduce to compute a histogram of your data frequencies.  I think binning would be helpful here.</p>

<p>Any additional updates will be posted here and/or via updates in the <a href='http://moodle.cs.colorado.edu/mod/forum/view.php?id=11' title='News forum'>news forum</a>.</p><div><table><tr><td>Available from:</td>    <td>Saturday, January 29, 2011, 03:05 PM</td></tr><tr><td>Due date:</td>    <td>Friday, March 4, 2011, 11:55 PM</td></tr></table></div>