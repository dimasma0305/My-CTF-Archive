#!/bin/python3
from http.server import BaseHTTPRequestHandler, HTTPServer
from pwn import log, context
from argparse import ArgumentParser

context.log_level = "INFO"


def redirect_handler_factory(url):
    class RedirectHandler(BaseHTTPRequestHandler):
        def do_GET(self):
            self.send_response(301)
            self.send_header('Location', url)
            self.end_headers()

    return RedirectHandler


def main():
    args = ArgumentParser(description="HTTP redirect server")
    args.add_argument("--port", '-p', action="store",
                      type=int, default=80, help="listening port")
    args.add_argument("--host", "-o", action="store", type=str,
                      default="localhost", help="host interface to listen")
    args.add_argument("redirect_url", action="store", type=str)
    args = args.parse_args()
    redirect_url = args.redirect_url
    host = args.host
    port = args.port
    redirectHandler = redirect_handler_factory(redirect_url)
    log.info(f"serving at port {port}")
    HTTPServer((host, port), redirectHandler).serve_forever()


if __name__ == "__main__":
    main()
