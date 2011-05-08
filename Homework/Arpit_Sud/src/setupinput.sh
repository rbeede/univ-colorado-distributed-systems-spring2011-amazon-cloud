#!/bin/bash
rm -rvf ./20news
mkdir ./20news
cd ./20news/
wget http://people.csail.mit.edu/jrennie/20Newsgroups/20news-18828.tar.gz
pax -dz -r -f 20news-18828.tar.gz -s'!\/!_!g'
rm -rvf 20news-18828.tar.gz
rm -rvf 20news-18828
dirlist=(talk.religion.misc comp.sys.mac.hardware sci.electronics talk.politics.mideast alt.atheism comp.os.ms-windows.misc sci.crypt rec.motorcycles soc.religion.christian comp.sys.ibm.pc.hardware rec.autos misc.forsale rec.sport.hockey comp.graphics talk.politics.misc rec.sport.baseball talk.politics.guns sci.space comp.windows.x sci.med)

for dir in "${dirlist[@]}"
do
        rm -rvf `echo 20news-18828_"$dir"`
        cat `echo 20news-18828_"$dir"_*` | cat > `echo 20news-18828_"$dir"`
        rm -rf `echo 20news-18828_"$dir"_*`
done
find -type f | wc -l
cd ..
hadoop fs -rmr input
hadoop fs -mkdir input
hadoop fs -put ./20news/* input
