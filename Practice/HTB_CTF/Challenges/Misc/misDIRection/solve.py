import os
from base64 import b64decode


data = [""]*100
list_dir = os.listdir("./.secret/")
for list in list_dir:
    place = os.listdir(f"./.secret/{list}")
    for put in place:
        data[int(put)] = list

print(b64decode(''.join(data)))