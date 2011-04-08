#!/usr/bin/python
#
# Brent Smith <brent.m.smith@colorado.edu>
#
# Module that allows mapping from 3-letter airport codes to
# the full airport name, city and state
import sys, csv

# initialize the module
mapper = {}
reader = csv.reader(open('145757522_T_MASTER_CORD.csv', 'rb'), delimiter=',', quotechar='"')
for row in reader:
    mapper[row[0]] = (row[1], row[2], row[3])

def get_airport_info (airport):
    if airport in mapper:
        return mapper[airport]
    else:
        raise Exception ('No airport for code %s' % airport)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print "Usage: %s <3-letter-airport-code>" % (sys.argv[0])
        sys.exit(1)

    print get_airport_info(sys.argv[1])
