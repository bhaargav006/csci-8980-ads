from flask import (
    Blueprint, flash, g, redirect, render_template, request, url_for
)
from werkzeug.exceptions import abort

bp = Blueprint('train', __name__, url_prefix='/train')

@bp.route('/')
def index():
    return 'Hello, Training World!'
