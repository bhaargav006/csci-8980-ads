from matplotlib import pyplot as plt
import csv
import numpy as np
from collections import defaultdict, deque, Counter
from tqdm import tqdm_notebook as tqdm 
import pandas as pd

from sklearn.model_selection import train_test_split
from sklearn.neural_network import MLPClassifier
from sklearn.metrics import accuracy_score
from sklearn.metrics import confusion_matrix
import csv
from sklearn.neighbors import KNeighborsClassifier

def belady_opt(blocktrace, frame):
    '''
    INPUT
    ==========
    blocktrace = list of block request sequence
    frame = size of the cache
    
    OUTPUT
    ==========
    hitrate 
    '''
    infinite_index = 10000 * len(blocktrace) # should be a large integer
    
    block_index = defaultdict(deque) 
    # dictionary with block number as key and list
    # of index value in blocktrace
    
    upcoming_index = defaultdict(int)
    # dictionary with index number as key and value as block
    
    frequency = defaultdict(int)
    # dictionary of block as key and number
    # of times it's been requested so far
    
    recency = list()
    # list of block in order of their request
    
    Cache = deque()
    # Cache with block
    
    dataset = np.array([]).reshape(0,3*frame+1)
    #columns represents the number of block in cache and 
    #3 is the number of features such as frequency, recency and block number
    #+1 is for label 0-1
    
    hit, miss = 0, 0

    evicted_list = []
    
    # populate the block_index
    for i, block in enumerate(blocktrace):
        block_index[block].append(i)
        
    # sequential block requests start
    for i, block in enumerate(blocktrace):
        # print(block)

        # increament the frequency number for the block
        frequency[block] += 1
        
        # make sure block has the value in block_index dictionary 
        # as current seq_number
        if len(block_index[block]) != 0 and block_index[block][0] == i:
            
            # if yes, remove the first element of block_index[block]
            block_index[block].popleft()
        
        # if block exist in current cache
        if block in Cache:
            
            # increment hit
            hit += 1
            
            # update the recency
            recency.remove(block)
            recency.append(block)
            
            # update upcoming_index
            if i in upcoming_index:
                
                # delete old index
                del upcoming_index[i]
        
                if len(block_index[block]) is not 0:
                    # add new upcoming index
                    upcoming_index[block_index[block][0]] = block
                    # remove index from block_index
                    block_index[block].popleft()
                else:
                    # add a large integer as index
                    upcoming_index[infinite_index] = block
                    # increament large integer
                    infinite_index+=1
           
        # block not in current cache
        else:
            
            # increament miss
            miss += 1
            
            # if cache has no free space
            if len(Cache) == frame:
                
                
                # evict the farthest block in future request from cache
                if len(upcoming_index) != 0:
                    
                    # find the farthest i.e. max_index in upcoming_index
                    max_index = max(upcoming_index)
                    evicted_list.append(Cache.index(upcoming_index[max_index]))
                    # if (i % 100 +1 == 100):
                    #     blockNo = np.array([i for i in Cache])
                    #     blockNo = blockNo / np.linalg.norm(blockNo)
                    #     recency_ = np.array([recency.index(i) for i in Cache])
                    #     recency_ = recency_ / np.linalg.norm(recency_)
                    #     frequency_ = np.array([frequency[i] for i in Cache])
                    #     frequency_ = frequency_ / np.linalg.norm(frequency_)
                    #     stack = np.column_stack((blockNo, recency_, frequency_)).reshape(1,frame*3)
                        
                    #     # print(upcoming_index[max_index])
                    #     stack = np.append(stack, Cache.index(upcoming_index[max_index]))
                    #     dataset = np.vstack((dataset, stack))
                    # remove the block with max_index from cache
                    Cache.remove(upcoming_index[max_index])
                    
                    # remove the block with max_index from recency dict
                    recency.remove(upcoming_index[max_index])
                    
                    # remove max_index element from upcoming_index
                    del upcoming_index[max_index]
                    
            # add upcoming request of current block in upcoming_index
            if len(block_index[block]) != 0:
                
                # add upcoming index of block
                upcoming_index[block_index[block][0]] = block
               
                # remove the index from block_index 
                block_index[block].popleft()
            
            else:
                
                # add a large integer as index
                upcoming_index[infinite_index] = block
                
                # increament high number
                infinite_index += 1
                
                
            
            # add block into Cache
            Cache.append(block)
            
            # add block into recency
            recency.append(block)
            
            
    # calculate hitrate
    hitrate = hit / (hit + miss)

    bins = np.arange(0, 99, 1)
    Y_train = evicted_list
    plt.xlim([min(Y_train), max(Y_train)])
    plt.hist(Y_train, bins=bins, alpha=0.5)
    plt.title('Histogram of eviction distribution')
    plt.xlabel('Cache id')
    plt.ylabel('count')
    plt.show()
    # calculate hitrate
    hitrate = hit / (hit + miss)
    print(hit)
    print(miss)

    return hitrate

filename = "./data/currenttmp"
blocktrace = []
timestamp = []
trace = []

datadict = {}
id = 0

with open(filename) as f:
    csv_reader = csv.reader(f, delimiter =" ")
    for index, row in zip(range(60000), csv_reader) :        
        if row[2] not in datadict:
            datadict[row[2]] = id
            id += 1
        blocktrace.append(datadict[row[2]])
        timestamp.append(int(row[0]))

for i, block in enumerate(blocktrace):
    trace.append(block)

print(belady_opt(trace, 100))





# print(np.sort(np.unique(blocktrace)))
# bins = np.sort(np.unique(blocktrace))
# Y_train = trace
# plt.xlim([min(Y_train), max(Y_train)])
# plt.hist(Y_train, bins=bins, alpha=0.5)
# plt.title('Histogram of data distribution')
# plt.xlabel('Cache id')
# plt.ylabel('count')
# plt.show()