import hashlib
import os
from flask import Flask, render_template, request
app = Flask(__name__)


@app.route('/', methods=['GET', 'POST'])
def index():
    filename = hashlib.md5(str(request.remote_addr).encode()).hexdigest()
    print("txt:", request.remote_addr)
    print("file:", filename)
    open('./todos/' + filename, 'a+').close()
    success = 0
    if request.method == 'POST':
        try:
            open('./todos/' + filename, 'a').write(request.form.get('todo') + '\n')
            success = 1
        except Exception:
            pass
    return render_template('index.html', data={'title': 'TODO', 'success': success, 'filetodos': filename, 'recently_todos': [i.rstrip() for j, i in enumerate(open('./todos/' + filename, 'r+').readlines()[::-1]) if i and j < 5]})


@app.route('/todos', methods=['GET'])
def todos():
    return render_template('todos.html', data={'title': 'Your Todos', 'todos': [i.rstrip() for i in open('./todos/' + request.args.get('for'), 'r').readlines() if i]})


if __name__ == '__main__':
    app.run('0.0.0.0', port=21201, debug=1)
