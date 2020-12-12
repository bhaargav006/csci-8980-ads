import argparse
import os
from sklearn.utils import shuffle
from tqdm import tqdm

parser = argparse.ArgumentParser(description='Simple data generator')
parser.add_argument("data", type=str, help="Name of the dataset")
parser.add_argument('-n', '--names', type=int, default=None, help="Number of unique names")
parser.add_argument('-s', '--size', type=int, default=None, help="Average object size")
parser.add_argument('-e', '--seed', type=int, default=None, help="Random generator seed")
parser.add_argument('-r', '--requests', type=int, default=10000000, help="Number of requests to generate")
parser.add_argument('-f', '--rpf', type=int, default=1000000, help="Number of requests per file")
parser.add_argument('-w', '--workload', type=float, default=0.7, help="Percentage read requests in workload")

args = parser.parse_args()

import numpy as np
np.random.seed(args.seed)

requests = args.requests

if args.names is None:
    number_of_names = np.random.randint(requests / 1000, 2 * requests / 1000)
else:
    number_of_names = args.names

if args.size is None:
    size = 20 * 1024
else:
    size = args.size

out_path = os.path.join('data/', args.data)
if not os.path.exists(out_path):
    os.makedirs(out_path)

print('Generating sizes')
names_size_mapping = np.random.poisson(size, size=number_of_names)

access_map = {}

file_counter = 0
num_get = 0
num_put = 0 

stime = 0

for i in range(0, requests, args.rpf):
    reqs_to_generate = min(requests - i, args.rpf)
    print('Generating', reqs_to_generate, 'requests. Done', i, 'out of', requests)

    # p_ids = np.random.poisson(int(number_of_names/2), int(reqs_to_generate/2))
    # l_ids = np.random.logistic(loc=10, scale=1, size=int(reqs_to_generate/2))
    # ids = np.concatenate((p_ids, l_ids), axis=None)
    # ids = shuffle(ids)
    
    ids = np.random.randint(0, number_of_names, size=reqs_to_generate)
    
    timestamps = np.random.exponential(0.5, size=reqs_to_generate).astype(np.int)
    timestamps = stime + np.cumsum(timestamps)

    with open(os.path.join(out_path, str(file_counter) + '.csv'), 'w') as f:
        for j in tqdm(range(reqs_to_generate)):
            if ids[j] in access_map:
                request_type = 'GET' if np.random.rand() <= args.workload else 'PUT'
                access_map[ids[j]] = access_map[ids[j]] + 1
            else:
                request_type = 'PUT'
                access_map[ids[j]] = 1

            if request_type == 'GET':
                num_get += 1
            else:
                num_put += 1
            wstr = str(timestamps[j]) + ',' + str(int(ids[j])) + ',' + str(names_size_mapping[int(ids[j])]) + ',' + str(request_type) + '\n'
            f.write(wstr)

    file_counter += 1
    stime = max(timestamps)

print(num_get, num_put)