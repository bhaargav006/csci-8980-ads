from flask import (
    Blueprint, flash, g, redirect, render_template, request, url_for
)
from werkzeug.exceptions import abort
from joblib import dump, load
from flask import current_app

import torch
import numpy as np
from torch import nn
import torch.optim as optim
import torch.nn.functional as F
from torch.utils.data import Dataset, DataLoader
from models.CNN2 import CNN2

bp = Blueprint('predict', __name__, url_prefix='/predict')

MLP = load('MLP.joblib')

LOGREG = load('LOGREG.joblib')

CNN = CNN2()
CNN.load_state_dict(torch.load('./models/CNN_2_1layer.pth', map_location='cpu'))

@bp.route('/eviction', methods=['POST'])
def index():
    if(request.data):
        inputTrace = request.get_json()

        blockNo = inputTrace['blockTrace']
        recency = inputTrace['recency']
        frequency = inputTrace['frequency']

        blockNo = np.array(blockNo)
        blockNo_ = blockNo / np.linalg.norm(blockNo)
        recency = np.array(recency)
        recency_ = recency / np.linalg.norm(recency)
        frequency = np.array(frequency)
        frequency_ = frequency / np.linalg.norm(frequency)
        stack = np.column_stack((blockNo_, recency_, frequency_)).reshape(1,current_app.config['CACHE_SIZE']*3)
    else:
        return 'Invalid Input'

    if current_app.config['MODEL_NAME'] == 'MLP':
        returnVal = str(MLP.predict(stack)[0])
    else current_app.config['MODEL_NAME'] == 'LOGREG':
        returnVal = str(LOGREG.predict(stack)[0])
    elif current_app.config['MODEL_NAME'] == 'CNN':
        index = CNN(torch.FloatTensor(stack))
        returnVal = str(np.argsort(F.softmax(index.data[0]).numpy())[-1])

    return returnVal