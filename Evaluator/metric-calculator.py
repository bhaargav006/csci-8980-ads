import requests
import argparse

from os.path import isfile, join
from os import listdir

parser = argparse.ArgumentParser(description='Simple Metric Calculator')
parser.add_argument('-p', '--path', type=str, default=None, help="Path to log folder")

args = parser.parse_args()

path = str(args.path)


files = [f for f in listdir(path) if isfile(join(path, f))]
for filename in files:
    logfile = open(path + '/' + filename, "r")
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

    output = open("metric.txt", "a")  # append mode 
    output.write(path + '/' + filename + " \n") 
    output.write('Total number of requests: ' + str(requests) + "\n")
    output.write('Number of hits: ' + str(hit) + "\n")
    output.write('Number of misses: ' + str(miss) + "\n")
    output.write('hit ratio: ' + str(hit / (hit + miss)) + "\n")
    output.write('access time in ns: ' + str(access_time) + "\n")
    output.write('access time in ms: ' + str(access_time/1000000) + "\n")
    output.write('\n\n')
    output.close()
    logfile.close()

