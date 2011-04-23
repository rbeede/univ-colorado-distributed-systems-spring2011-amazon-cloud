#!/bin/bash

$HADOOP_HOME/bin/hadoop fs -rmr npad-bad-output
$HADOOP_HOME/bin/hadoop jar $HADOOP_HOME/contrib/streaming/hadoop-*streaming*.jar \
-file /home/blake/mapper.py \
-mapper /home/blake/mapper.py \
-file /home/blake/reducer.py \
-reducer /home/blake/reducer.py \
-input npad/*.data \
-output npad-bad-output 