#!/usr/bin/env python
# mapper.py
# Blake Caldwell
# CSCI 5673 Programming Assignment
# geo_ip_lookup function is from 
# http://rickyrosario.com/blog/quick-and-dirty-geoip-lookup-function-in-python/

import sys
import re
import urllib
import threading
from xml.dom import minidom

GML_NS = 'http://www.opengis.net/gml'
domainname_re = re.compile("Target: .*\.(.*\..*) \((.*)\)")
pass_re = re.compile("pathPass")
leave=0


def geo_ip_lookup(ip_address):
    
    """
    Look up the geo information based on the IP address passed in
    """
    GEO_IP_LOOKUP_URL = 'http://api.hostip.info/?ip=%s'

    dom = minidom.parse(urllib.urlopen(GEO_IP_LOOKUP_URL % ip_address))
    elem = dom.getElementsByTagName('Hostip')[0]
    location = elem.getElementsByTagNameNS(GML_NS, 'name')[0].firstChild.data.partition(',')

    try:
        latlong = elem.getElementsByTagNameNS(GML_NS, 'coordinates')[0].firstChild.data.partition(',')
    except:
        # lat/long isnt always returned
        latlong = None

    return {
            'country_code': elem.getElementsByTagName('countryAbbrev')[0].firstChild.data,
            'country_name': elem.getElementsByTagName('countryName')[0].firstChild.data,
            'locality': location[0].strip(),
            'region': location[2].strip(),
            'longitude': latlong[0].strip() if latlong else '',
            'latitude': latlong[2].strip() if latlong else ''
    }

class myLookupThread (threading.Thread):
    def __init__(self,  ip, domain):
        self.ip = ip
        self.domain = domain
        threading.Thread.__init__(self)
    def run(self):
        lookup_results=geo_ip_lookup(self.ip)
        try:
            #print '%s,%s,%s,%s,%s\t%s' % (lookup_results['locality'],lookup_results['region'],lookup_results['country_code'],1)
            print '%s,%s,%s,%s\t%s' % (self.domain,lookup_results['country_code'],lookup_results['region'],lookup_results['locality'],lookup_results['latitude'],lookup_results['longitude'],1)
        except:
            print '%s,%s,%s,%s,%s,%s\t%s' % (self.domain,lookup_results['country_code'].encode('ascii','ignore'),lookup_results['region'],lookup_results['locality'].encode('ascii','ignore'),lookup_results['latitude'],lookup_results['longitude'],1)
            #print '(%s),%s,%s\t%s' % (lookup_results['locality'].encode('ascii','ignore'),lookup_results['region'].encode('ascii','ignore'),lookup_results['country_code'].encode('ascii','ignore'),1)

domain=''
ip=''
new = []
thread_count = 0
for line in sys.stdin:
    line = line.strip()
    d=domainname_re.search(line)
    p=pass_re.search(line)
    if d:
        domain=d.group(1).strip()
        ip=d.group(2).strip()
    if not p and d:
        if thread_count and new[thread_count-1].isAlive():
            #print 'waiting for it to finish'
            new[thread_count-1].join()
            #print 'finished'
        new.append(myLookupThread(ip,domain))
        thread_count += 1
        new[thread_count-1].start()
        d=''
        p=''
        domain=''
        ip=''


