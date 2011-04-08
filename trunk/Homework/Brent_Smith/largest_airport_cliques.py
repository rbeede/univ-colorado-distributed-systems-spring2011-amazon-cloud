#!/usr/bin/python
#
# Brent Smith <brent.m.smith@colorado.edu>
#
# This script is meant to parse the output of the 
# AllMaximalCliquesGenerate Hadoop MapReduce job, and print
# out only the largest sets of maximal cliques.
import sys,re,airports

if len(sys.argv) < 2:
    print "Usage: %s <MR-output-file>" % (sys.argv[0])
    sys.exit(1)

f = open(sys.argv[1], "r")
l = f.readline()

# remove the first value since it is just the text 
# "all_maximal_cliques"
tokens = re.split("\\s+", l)

# reconstruct the line
l = " ".join(tokens[1:])

# The line should now be in a format recognized by
# python: an array specification.  The only thing we need
# to do, is to put quotes around the 3-letter airport codes
# and then we can use the python eval() function to put this
# array into a variable.  We'll use a simple regular 
# expression to do this.
l = re.sub('(\w+)','"\\1"', l)

# now evaluate this and assign it
all_maximal_cliques = eval(l)

# now, lets figure out the largest set in this array and
# assign it to max_set_size
max_set_size = len(all_maximal_cliques[0])
for i in range(0,len(all_maximal_cliques)):
    if max_set_size < len(all_maximal_cliques[i]):
        max_set_size = len(all_maximal_cliques[i])

# Create an array containing only the largest sets
c = []
for i in range(0,len(all_maximal_cliques)):
    if len(all_maximal_cliques[i]) == max_set_size:
        c.append(map (lambda x: x+' ['+airports.get_airport_info(x)[1]+']', all_maximal_cliques[i]))

    
fmtstr = "%-35s "*len(c)
for i in range(0,max_set_size):
    print fmtstr % tuple([c[j][i] for j in range(0,len(c))])
    #print [c[j][i] for j in range(0,len(c))]
