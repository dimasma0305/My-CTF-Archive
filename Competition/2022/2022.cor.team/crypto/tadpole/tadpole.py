from Crypto.Util.number import bytes_to_long, isPrime
from secrets import randbelow

FLAG = "a"
p = bytes_to_long(FLAG.encode())
# assert isPrime(p)

a = randbelow(p)
b = randbelow(p)

def f(s):
    return (a * s + b) % p

print("p = ", p)
print("a = ", a)
print("b = ", b)
print("f(31337) = ", f(31337))
print("f(f(31337)) = ", f(f(31337)))