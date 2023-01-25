from flask import Flask, render_template, request
import pycurl
import urllib.parse
import urllib.request
from urllib.parse import parse_qs, urlparse
import ipaddress
import re
import os
from io import BytesIO
import base64

app = Flask(__name__, static_folder='static', static_url_path='')
static_path = os.listdir(os.path.dirname(__file__))

def is_valid_ip(ip):
    m = re.match(r"^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$", ip)
    return bool(m) and all(map(lambda n: 0 <= int(n) <= 255, m.groups()))


def is_local_ip(hostname):
    forbidden_ip = ["0.0.0.0/8", "127.0.0.1/8"]
    if is_valid_ip(hostname):
        for forbidden in forbidden_ip:
            if ipaddress.ip_address(hostname) in ipaddress.ip_network(forbidden):
                return True 
    
    if hostname == "localhost":
        return True
    
    return False


@app.route("/")
def home():
    return render_template("index.html")

@app.route("/fetch-your-way", methods=["POST"])
def fetch():
    if request.form["url"] and request.form["module"]:
        url = request.form["url"]
        module = request.form["module"]
        hostname = urllib.parse.urlparse(url).hostname

        if url[:4].lower() == "file":
            return "Don't even try to disclose our internal file"
        
        if hostname:
            if is_local_ip(hostname):
                return "You cannot access our internal network :*"
        
        if module == "urllib":
            req = urllib.request.URLopener().open(url)
            content = req.read()
            req.close()
            return content
            
        if module == "pycurl":
            b_obj = BytesIO() 
            crl = pycurl.Curl() 
            crl.setopt(pycurl.TIMEOUT, 7)
            crl.setopt(crl.URL, url)
            crl.setopt(crl.WRITEDATA, b_obj)
            crl.perform() 
            crl.close()
            content = b_obj.getvalue()
            return content

        return "Invalid module" 
    else:
        "Please provide 'url' and 'module' POST data"           


@app.route("/<REDACTED>")
def adminonly():
# REDACTED, it exist on the server tho
    pass
