import requests
import argparse

from tqdm import tqdm
from os import listdir
from os.path import isfile, join
from time import sleep

parser = argparse.ArgumentParser(description='Simple data generator')
parser.add_argument('-u', '--url', type=str, default=None, help="URL of Cache Controller")
parser.add_argument('-w', '--workload', type=str, default=None, help="Name of folder where workload is present")
parser.add_argument('-n', '--requests', type=int, default=None, help="Number of requests to process")

args = parser.parse_args()

path = './data/' + str(args.workload)
url = args.url

files = [f for f in listdir(path) if isfile(join(path, f))]

with tqdm(total=args.requests) as pbar:
    for filename in files:
        file = open(path + '/' + filename, "r")
        for index, line in zip(range(args.requests), file):
            params = line.split(',')

            key = params[1]
            value = params[2]
            req_type = params[3]
            req_type = req_type.strip()

            sleep(0.05)
            if req_type == 'GET':
                try:
                    requests.get(url + '/api/' + key)
                except requests.exceptions.RequestException as e:
                    print(e)
                    print('Error in GET for key: ' + key)
            else:
                try:
                    requests.put(url + '/api/' + key, data = value)
                except requests.exceptions.RequestException as e:
                    print(e)
                    print('Error in PUT for key: ' + key + ' value: ' + value)
            pbar.update(1)