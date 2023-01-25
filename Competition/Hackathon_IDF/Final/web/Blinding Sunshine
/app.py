from flask import Flask, request
import flask
app = Flask(__name__)


@app.route("/")
def index():
    req = request.args.get("text", "True")
    print(req)
    print(eval(req, {"__builtins__": {}}, {}))
    return "200 OK"


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=4444)
