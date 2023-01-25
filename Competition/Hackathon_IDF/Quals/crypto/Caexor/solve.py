from base64 import b64decode
from string import ascii_uppercase, ascii_lowercase

def caesar(encrypted_message, key):
    decrypted_message = ""

    for c in encrypted_message:
        if c in ascii_lowercase:
            position = ascii_lowercase.find(c)
            new_position = (position - key) % 26
            new_character = ascii_lowercase[new_position]
            decrypted_message += new_character
        else:
            decrypted_message += c
    return decrypted_message

def encrypt_2(msg):
	return ''.join(chr(ord(i)^j) for j,i in enumerate(msg))

flag = b64decode("eXR6Ymp2cWlmbGFlY3ZHek97ZXp1SnN4R0BtenNqcEBKSlJ8fVJHU0NHC1Ym").decode()
flag = encrypt_2(flag)
print(caesar(flag, 22))