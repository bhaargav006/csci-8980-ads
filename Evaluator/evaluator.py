import requests
import argparse

from os import listdir
from os.path import isfile, join

parser = argparse.ArgumentParser(description='Simple data generator')
parser.add_argument('-u', '--url', type=str, default=None, help="URL of Cache Controller")
parser.add_argument('-w', '--workload', type=str, default=None, help="Name of folder where workload is present")

args = parser.parse_args()

path = './data/' + str(args.workload)
url = args.url

files = [f for f in listdir(path) if isfile(join(path, f))]

for filename in files:
    file = open(path + '/' + filename, "r")
    for line in file:
        params = line.split(',')

        key = params[1]
        value = params[2]
        req_type = params[3]

        if req_type == 'GET':
            requests.get(url + '/api/' + key)
        else:
            requests.put(url + '/api/' + key, data = value)