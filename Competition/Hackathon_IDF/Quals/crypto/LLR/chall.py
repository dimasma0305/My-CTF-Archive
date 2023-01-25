from Crypto.Util.number import bytes_to_long, getPrime
from flag import flag

def generate():
    p = getPrime(2048)
    q = getPrime(2048)
    n = p * q
    return n

def encrypt(m,e,n):
    c = pow(m,e,n)
    c = pow(c,e,n)
    c = pow(c,e,n)
    return c

with open("output.txt","w") as f:
    for i in range(3):
        m = bytes_to_long(flag)
        n = generate()
        c = encrypt(m,3,n)
        f.write(str(n) + "♡" + str(c) + "\n")