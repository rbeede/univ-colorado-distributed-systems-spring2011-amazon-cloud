#!/bin/bash

rm NumberFrequency_hadoop.jar

rm -Rf target
mkdir -p target/classes

javac -classpath hadoop-0.20.2/hadoop-0.20.2-core.jar -d target/classes *.java

jar -cvf NumberFrequency_hadoop.jar -C target/classes .

jar -tf NumberFrequency_hadoop.jar
