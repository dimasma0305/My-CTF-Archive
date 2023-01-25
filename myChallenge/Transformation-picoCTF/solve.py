discord = ""
for i in range(0, len(discord)):
    n1 = chr(ord(discord[i]) >> 8)
    n2 = chr(ord(discord[i]) - (ord(n1) << 8))
    print(n1+n2, end="")
print()
