from flask import (
    Blueprint, flash, g, redirect, render_template, request, url_for
)
from werkzeug.exceptions import abort
from joblib import dump, load
from collections import defaultdict, deque, Counter
from tqdm import tqdm 
from flask import current_app
import os
import csv
import copy
import numpy as np
import pickle as P
import pandas as pd

from matplotlib import pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.neural_network import MLPClassifier
from sklearn.metrics import accuracy_score
from sklearn.metrics import confusion_matrix
from sklearn.neighbors import KNeighborsClassifier

import torch
from torch import nn
from torch.autograd import Variable
from torch.utils.data import Dataset, DataLoader
import torch.nn.functional as F
import torch.optim as optim

bp = Blueprint('train', __name__, url_prefix='/train')

@bp.route('/')
def index():
    filename = "./../Evaluator/data/finaltrain3/0.csv"
    blocktrace = []
    timestamp = []

    current_app.logger.info('Reading Input File')
    with open(filename) as f:
        csv_reader = csv.reader(f, delimiter =",")
        for row in csv_reader:
            blocktrace.append(int(row[1]))
            timestamp.append(int(row[0]))

    current_app.logger.info('Running Belady')
    hr, dataset = belady_opt(blocktrace, current_app.config['CACHE_SIZE'])

    current_app.logger.info('Dataset length: ' + str(len(dataset)))
    current_app.logger.info('Spliting train and test data')
    X_train, X_test, Y_train, Y_test = train_test_split(dataset[:,:-1], dataset[:,-1].astype(int), test_size=0.3, random_state=None, shuffle=True)

    # Fitting Logistic Regression Model
    current_app.logger.info('Building model')

    if (current_app.config['MODEL_NAME'] == 'MLP500'):
        NN = MLPClassifier(hidden_layer_sizes=(500, ), activation='tanh', batch_size= 100, random_state=1, max_iter=300)
    elif (current_app.config['MODEL_NAME'] == 'MLP100'):
        NN = MLPClassifier(hidden_layer_sizes=(100, ), activation='tanh', batch_size= 100, random_state=1, max_iter=300)
    elif (current_app.config['MODEL_NAME'] == 'LOGREG'):
        NN = LogisticRegression()
    NN.fit(X_train, Y_train)

    current_app.logger.info('Running evaluations')

    current_app.logger.info('Accuracy of Classifier on test set: {:.2f}'.format(NN.score(X_test, Y_test)))
    current_app.logger.info('Accuracy of Classifier on train set: {:.2f}'.format(NN.score(X_train, Y_train)))
    current_app.logger.info('Hit Rate from Belady: {:.2f}'.format(hr))
    # current_app.logger.info('Hit Rate from Model: {:.2f}'.format(hitRate(blocktrace, current_app.config['CACHE_SIZE'], NN)))

    dump(NN, current_app.config['MODEL_NAME'] + '.joblib')

    return current_app.config['MODEL_NAME'] + ' model trained!'

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
    
    dataset = np.array([]).reshape(0,2*frame+1)
    #columns represents the number of block in cache and 
    #3 is the number of features such as frequency, recency and block number
    #+1 is for label 0-1
    
    hit, miss = 0, 0
    
    # populate the block_index
    for i, block in enumerate(blocktrace):
        block_index[block].append(i)

    with tqdm(total=len(blocktrace)) as pbar:    
    # sequential block requests start
        for i, block in enumerate(blocktrace):
            
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
            
                    if len(block_index[block]) != 0:
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

                        if (i%10+1==10):
                            blockNo = np.array([i for i in Cache])
                            # blockNo = blockNo / np.linalg.norm(blockNo)
                            recency_ = np.array([recency.index(i) for i in Cache])
                            recency_ = recency_ / np.linalg.norm(recency_)
                            frequency_ = np.array([frequency[i] for i in Cache])
                            frequency_ = frequency_ / np.linalg.norm(frequency_)
                            stack = np.column_stack((recency_, frequency_)).reshape(1,frame*2)
                            stack = np.append(stack, Cache.index(upcoming_index[max_index]))
                            dataset = np.vstack((dataset, stack))

                        # remove the block with max_index from cache
                        Cache[Cache.index(upcoming_index[max_index])] = block
                        
                        # remove the block with max_index from recency dict
                        recency.remove(upcoming_index[max_index])
                        
                        # remove max_index element from upcoming_index
                        del upcoming_index[max_index]
                else:
                    # add block into Cache
                    Cache.append(block)
                    
                # add block into recency
                recency.append(block)
                        
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

            pbar.update(1)
            
    # calculate hitrate
    hitrate = hit / (hit + miss)

    return hitrate, dataset

def hitRate(blocktrace, frame, model):		
    '''		
    INPUT		
    ==========		
    blocktrace = list of block request sequence		
    frame = size of the cache		
            
    OUTPUT		
    ==========		
    hitrate 		
    '''		

    frequency = defaultdict(int)		
    # dictionary of block as key and number		
    # of times it's been requested so far		

    recency = list()		
    # list of block in order of their request		

    Cache = []		
    # Cache with block		

    hit, miss = 0, 0		

    with tqdm(total=len(blocktrace)) as pbar:    	
        # sequential block requests start		
        for i, block in enumerate(blocktrace):		
            # increament the frequency number for the block		
            frequency[block] += 1		

            # if block exist in current cache		
            if block in Cache:		

                # increment hit		
                hit += 1		

                # update the recency		
                recency.remove(block)		
                recency.append(block)		

            # block not in current cache		
            else:		

                # increament miss		
                miss += 1		

                # if cache has no free space		
                if len(Cache) == frame:  		
                    blockNo = np.array([i for i in Cache])		
                    # blockNo = blockNo / np.linalg.norm(blockNo)		
                    recency_ = np.array([recency.index(i) for i in Cache])		
                    # recency_ = recency_ / np.linalg.norm(recency_)		
                    frequency_ = np.array([frequency[i] for i in Cache])		
                    # frequency_ = frequency_ / np.linalg.norm(frequency_)		
                    stack = np.column_stack((recency_, frequency_)).reshape(1,frame*2)		
                    index = model.predict(stack)		
                    pred = index[0]		

                    evict_block = Cache[pred]		

                    # remove the block with max_index from cache		
                    Cache[Cache.index(evict_block)] = block	

                    # remove the block with max_index from recency dict		
                    recency.remove(evict_block)	
                else:	
                    # add block into Cache	
                    Cache.append(block)		

                # add block into recency		
                recency.append(block)	
            pbar.update(1)	

    # calculate hitrate	
    hitrate = hit / (hit + miss)	
    return hitrate 