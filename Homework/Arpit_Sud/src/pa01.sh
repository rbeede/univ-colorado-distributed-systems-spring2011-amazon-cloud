#!/bin/bash
if [ -z "$MICROEBS" ]; then
	echo "Please set MICROEBS env variable so that results can be stored there."
else

WORDCOUNT_CLASSES=./wordcount_classes
COUNTHISTOGRAM_CLASSES=./counthistogram_classes
INPUT=input
WORDCOUNT_OUTPUT=countoutput
COUNTHISTOGRAM_OUTPUT=histoutput
#OUTFILENAME=part-r-00000

HADOOP_JAR=/usr/lib/hadoop/hadoop-0.18.3-14.cloudera.CH0_3-core.jar
SORTEDWORDCOUNT=sortedwordcount
SORTEDHIST=sortedhist

echo "Compiling WordCount.."
rm -rvf `echo "$WORDCOUNT_CLASSES"`
mkdir ` echo "$WORDCOUNT_CLASSES"`
javac -classpath $HADOOP_JAR -d `echo "$WORDCOUNT_CLASSES"` WordCount.java
jar -cvf wordcount.jar -C `echo "$WORDCOUNT_CLASSES"` .

echo "Compiling CountHistogram.."
rm -rvf `echo "$COUNTHISTOGRAM_CLASSES"`
mkdir `echo "$COUNTHISTOGRAM_CLASSES"`
javac -classpath `echo "$HADOOP_JAR"` -d `echo "$COUNTHISTOGRAM_CLASSES"` CountHistogram.java
jar -cvf counthistogram.jar -C `echo "$COUNTHISTOGRAM_CLASSES"` .

echo "Running WordCount.."
hadoop fs -rmr $WORDCOUNT_OUTPUT
hadoop jar ./wordcount.jar WordCount $INPUT $WORDCOUNT_OUTPUT
echo "Running CountHistogram.."
hadoop fs -rmr $COUNTHISTOGRAM_OUTPUT
hadoop jar ./counthistogram.jar CountHistogram $WORDCOUNT_OUTPUT $COUNTHISTOGRAM_OUTPUT

# Retrieving and Storing the WordCount output
rm -rvf $WORDCOUNT_OUTPUT
mkdir $WORDCOUNT_OUTPUT
hadoop fs -get `echo "$WORDCOUNT_OUTPUT"/* "$WORDCOUNT_OUTPUT"/`
cat `echo "$WORDCOUNT_OUTPUT"/part*` | cat > `echo "$WORDCOUNT_OUTPUT"/"$WORDCOUNT_OUTPUT"`
cat `echo "$WORDCOUNT_OUTPUT"/"$WORDCOUNT_OUTPUT"` | sort -rnk2 | cat > `echo "$WORDCOUNT_OUTPUT"/"$SORTEDWORDCOUNT"`
hadoop fs -put $WORDCOUNT_OUTPUT/$SORTEDWORDCOUNT $WORDCOUNT_OUTPUT
echo "Sorted Word Count is stored in $WORDCOUNT_OUTPUT/$SORTEDWORDCOUNT"
scp `echo "$WORDCOUNT_OUTPUT"/"$WORDCOUNT_OUTPUT"` ubuntu@`echo "$MICROEBS"`:/home/ubuntu/pa01/
scp `echo "$WORDCOUNT_OUTPUT"/"$SORTEDWORDCOUNT"` ubuntu@`echo "$MICROEBS"`:/home/ubuntu/pa01/

#Retrieving and storing the Histogram output
rm -rvf $COUNTHISTOGRAM_OUTPUT
mkdir $COUNTHISTOGRAM_OUTPUT
hadoop fs -get `echo "$COUNTHISTOGRAM_OUTPUT"/* "$COUNTHISTOGRAM_OUTPUT"/`
cat `echo "$COUNTHISTOGRAM_OUTPUT"/part*` | cat > `echo "$COUNTHISTOGRAM_OUTPUT"/"$COUNTHISTOGRAM_OUTPUT"`
cat `echo "$COUNTHISTOGRAM_OUTPUT"/"$COUNTHISTOGRAM_OUTPUT"` | sort -nk1 | cat > `echo "$COUNTHISTOGRAM_OUTPUT"/"$SORTEDHIST"`
echo "Count Histogram is stored in $COUNTHISTOGRAM_OUTPUT/$COUNTHISTOGRAM_OUTPUT"
scp `echo "$COUNTHISTOGRAM_OUTPUT"/"$COUNTHISTOGRAM_OUTPUT"` ubuntu@`echo "$MICROEBS"`:/home/ubuntu/pa01/
scp `echo "$COUNTHISTOGRAM_OUTPUT"/"$SORTEDHIST"` ubuntu@`echo "$MICROEBS"`:/home/ubuntu/pa01/
fi
