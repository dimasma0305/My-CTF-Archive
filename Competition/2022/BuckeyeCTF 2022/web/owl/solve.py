import http.server, socketserver
from pyngrok import ngrok
import random

http_port = random.randint(1000, 9999)

class Handler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self) -> None:
        print(self.headers)
        return super().do_GET()

httpd = socketserver.TCPServer(('', http_port), Handler)
ngrok_url = ngrok.connect(http_port).public_url
print(ngrok_url)
httpd.serve_forever()