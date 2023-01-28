from Crypto.Cipher import AES
from pkcs7 import PKCS7Encoder
import base64

shared_key =  base64.b64decode("OoIsAwwF32cICQoLDA0ODe==")#some random key for a working example
iv = [0, 1, 0, 3, 5, 3, 0, 1, 0, 0, 2, 0, 6, 7, 6, 0]
iv = [chr(i) for i in iv]
iv = b''.join(iv)

cipher_text = open('file.fun', 'rb').read() 

aes_decrypter = AES.new(shared_key, AES.MODE_CBC, iv)
aes_decrypter.block_size = 128
clear_text = PKCS7Encoder().decode(aes_decrypter.decrypt(cipher_text))
print(clear_text)