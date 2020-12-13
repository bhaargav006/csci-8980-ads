import requests
import argparse

from os.path import isfile, join

parser = argparse.ArgumentParser(description='Simple Metric Calculator')
parser.add_argument('-w', '--workload', type=str, default=None, help="Path to log file")

args = parser.parse_args()

path = str(args.workload)

logfile = open(path, "r")
requests = 0
hit = 0
miss = 0
access_time = 0

# 6 21205000
# 7 true

for line in logfile:
    requests += 1
    values = line.split()
    access_time += int(values[6])
    if values[7] == 'true':
        hit += 1
    else :
        miss += 1

print('Total number of requests: ', requests)
print('Number of hits: ', hit)
print('Number of misses: ', miss)
print('hit ratio: ', hit / (hit + miss))
print('access time in ns: ', access_time)
print('access time in ms: ', access_time/1000000)

logfile.close()
