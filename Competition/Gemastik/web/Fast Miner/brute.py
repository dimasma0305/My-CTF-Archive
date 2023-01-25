from binascii import hexlify, unhexlify
from hashlib import sha256
import hashlib
from Crypto.Util.number import long_to_bytes

def test():
    for i in range(4228250625):
        i_bytes = long_to_bytes(i)
        hash = sha256(sha256(i_bytes).digest())
        idx = hash.hexdigest()[-8:]
        if idx == "00000000":
            print(hash.hexdigest())
            print(idx)

header_hex = "1873f45c2dca3b0016918d559bf1f36ada242059ae87b9cff6f81b87b0aadc7e"
header_bin = unhexlify(header_hex)
hash = hashlib.sha256(hashlib.sha256(header_bin).digest()).digest()

print(hexlify(hash).decode("utf-8"))