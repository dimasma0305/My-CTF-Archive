discord = ""
for i in range(0, len(discord), 2):
    n = (ord(discord[i]) << 8 ) + ord(discord[i+1])
    print(chr(n), end="")
