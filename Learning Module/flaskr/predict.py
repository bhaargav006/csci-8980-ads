from flask import (
    Blueprint, flash, g, redirect, render_template, request, url_for
)
from werkzeug.exceptions import abort

bp = Blueprint('predict', __name__, url_prefix='/predict')

@bp.route('/eviction')
def index():
    return { "data": 'Hello, Prediction World!' }