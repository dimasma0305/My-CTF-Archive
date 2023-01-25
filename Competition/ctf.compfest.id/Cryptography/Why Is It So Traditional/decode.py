import math
from Crypto.Util.number import long_to_bytes


def cc_decrypt(cipher: str):
    '''idk... i just copy from fire's script'''
    plain = ""
    cipher = cipher[::-1]
    for i in range(len(cipher)):
        if i == 0:
            plain += cipher[i]
        else:
            if ord(cipher[i]) - 32 + 64 - ord(plain[-1]) in range(32, 125):
                plain += chr(ord(cipher[i]) - 32 + 64 - ord(plain[-1]))
            elif ord(cipher[i]) - 32 + 64 - ord(plain[-1]) > 125:
                plain += chr(ord(cipher[i]) - 32 + 64 - 94 - ord(plain[-1]))
            else:
                plain += chr(ord(cipher[i]) - 32 + 64 + 94 - ord(plain[-1]))
    return plain[::-1]


def bc_decrypt(cipher: str):
    '''Binary Cipher'''
    plain = ""
    binary = ""
    for i in range(len(cipher)):
        if cipher[i].islower():
            binary += "0"
        else:
            binary += "1"
    for i in range(0, len(binary), 7):
        plain += chr(int(binary[i:i+7], 2) + 32)
    return plain


def ct_decrypt(cipher: str):
    '''columnar-transposition'''
    key = "lorem"
    msg = ""
    k_indx = 0
    msg_indx = 0
    msg_len = float(len(cipher))
    msg_lst = list(cipher)
    col = len(key)
    row = int(math.ceil(msg_len / col))
    key_lst = sorted(list(key))
    dec_cipher = []
    for _ in range(row):
        dec_cipher += [[None] * col]
    for _ in range(col):
        curr_idx = key.index(key_lst[k_indx])
        for j in range(row):
            dec_cipher[j][curr_idx] = msg_lst[msg_indx]
            msg_indx += 1
        k_indx += 1
    try:
        msg = ''.join(sum(dec_cipher, []))
    except TypeError:
        raise TypeError("This program cannot",
                        "handle repeating words.")
    null_count = msg.count('_')
    if null_count > 0:
        return msg[: -null_count]

    return msg


def vc_decrypt(cipher: str):
    '''idk... wtf cipher is this'''
    key = "lorem"
    msg = ""
    for i in range(len(cipher)):
        if ord(cipher[i]) - 32 + 64 - ord(key[i % len(key)]) in range(32, 125):
            msg += chr(ord(cipher[i]) - 32 + 64 - ord(key[i % len(key)]))
        elif ord(cipher[i]) - 32 + 64 - ord(key[i % len(key)]) > 125:
            msg += chr(ord(cipher[i]) - 32 + 64 - 94 - ord(key[i % len(key)]))
        else:
            msg += chr(ord(cipher[i]) - 32 + 64 + 94 - ord(key[i % len(key)]))
    return msg


def decrypt(encrypt: str):
    plain = ""
    print(encrypt[0:20])
    plain += cc_decrypt(encrypt[0:20])
    print(encrypt[0:20])
    plain += bc_decrypt(encrypt[20:160])
    plain += ct_decrypt(encrypt[160:180])
    plain += vc_decrypt(encrypt[180:])
    return plain


def main():
    with open("cipher.txt", 'r') as f:
        cipher = f.read()
    cipher = long_to_bytes(int(cipher)).decode()
    plain = decrypt(cipher)
    print(plain)


if __name__ == "__main__":
    main()
