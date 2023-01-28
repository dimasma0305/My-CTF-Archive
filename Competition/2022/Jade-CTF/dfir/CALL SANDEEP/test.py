# code nya : 5b2b7f05237305611f3368214d3a601d4325740fa


def brute(text):
    result = ""
    cipher = []
    for i in text:
        cipher.append(ord(i))
    for i in range(len(cipher)):
        if i > 0:
            cipher[i] ^= cipher[i-1]
        cipher[i] ^= cipher[i] >> 4
        cipher[i] ^= cipher[i] >> 3
        cipher[i] ^= cipher[i] >> 2
        cipher[i] ^= cipher[i] >> 1
        result += "%02x" % (cipher[i])
    return result

cipher = "5b2b7f05237305611f3368214d3a601d4325740f"
known = ""
for i in range(0, len(cipher), 2):
    for j in range(33, 126):
        br_res = brute(known+chr(j))
        if cipher[i:i+2] == br_res[i:i+2]:
            known += chr(j)
            break
            
print("text: "+known)

print("cipher: "+cipher)
print("cipher_after: "+brute("ybbx1at_s0e_Fnaqrrc}"))
