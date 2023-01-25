# ''.join([chr((ord(flag[i]) << 8) + ord(flag[i + 1])) for i in range(0, len(flag), 2)])

with open("enc", "r") as f:
    data = f.read()

def decode(txt: str):
    flag = ""
    for i in range(0, len(txt)):
        a = chr(ord(txt[i]) >> 8)
        b = chr(ord(txt[i]) - (ord(a)<<8))
        flag += a+b
    return flag

print(decode(data))