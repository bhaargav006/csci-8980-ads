from flask import (
    Blueprint, flash, g, redirect, render_template, request, url_for
)
from werkzeug.exceptions import abort
from joblib import dump, load
from flask import current_app
import numpy as np

bp = Blueprint('predict', __name__, url_prefix='/predict')
NN = load('cachemodel.joblib')

@bp.route('/eviction', methods=['POST'])
def index():
    if(request.data):
        inputTrace = request.get_json()
        current_app.logger.info(inputTrace)

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

    return str(NN.predict(stack)[0])