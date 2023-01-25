from http.server import SimpleHTTPRequestHandler
import socketserver
from pwn import log, context
from http import HTTPStatus

context.log_level = "INFO"

def make_server(text_html):
    class Handler(SimpleHTTPRequestHandler):
        def do_GET(self):
            print(self.client_address)
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


def serve(host, port):
    address = (host, port)
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(address, make_server("dimas")) as httpd:
        log.info(f"serve @ http://localhost:{address[1]}")
        httpd.handle_request()
