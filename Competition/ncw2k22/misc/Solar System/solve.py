from Crypto.Util.number import long_to_bytes


for i in range(50):
    a = 0x81273829301826362844392+50+84539+133+i
    a = long_to_bytes(a)
    print(a)