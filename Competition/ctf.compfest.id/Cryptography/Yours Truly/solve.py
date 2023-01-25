from Crypto.PublicKey import RSA
from Crypto.Util.number import long_to_bytes, bytes_to_long
from Crypto.Cipher import AES, PKCS1_OAEP
from base64 import b64decode
from base64 import b64encode

with open('pubkey.pem', 'r') as f:
    key = RSA.import_key(f.read())

with open('message.enc', 'rb') as f:
    cipher = bytes_to_long(f.read())

print(key.n)

assert key.d == pow(key.e, -1, (key.p-1)*(key.q-1))

print(long_to_bytes(pow(cipher, key.d, key.n)))