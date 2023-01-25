from flask import Flask
from flask_session import Session
from blueprints import *

app = Flask(__name__)
app.template_folder = './templates'
app.config.from_pyfile('config.py')

Session(app)
app.register_blueprint(index_page)

if __name__ == '__main__':
    app.run(host='0.0.0.0',port=20202,debug=False)