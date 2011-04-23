#!/usr/bin/env python

# reducer.py
# Blake Caldwell
# March 4, 2022
# started from example given here: 
#   http://www.michael-noll.com/tutorials/writing-an-hadoop-mapreduce-program-in-python/

import sys

# maps words to their counts
word2count = {}

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    key, count = line.split('\t', 1)
    # convert count (currently a string) to int
    try:
        count = int(count)
        key = key.lower()
        word2count[key] = word2count.get(key, 0) + count
    except ValueError:
        # count was not a number, so silently
        # ignore/discard this line
        pass

# sort the words lexigraphically;
sorted_word2count = word2count.items()

# sort the whole thing
sorted_word2count.sort(lambda x,y : y[1] - x[1])

# write the results to STDOUT (standard output)
for key, count in sorted_word2count:
    print '%s\t%s'% (key, count)
