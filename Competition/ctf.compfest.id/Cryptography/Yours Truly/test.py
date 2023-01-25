from Crypto.Util.number import long_to_bytes
from Crypto.PublicKey import RSA
from Crypto.Util.number import long_to_bytes, bytes_to_long
from Crypto.Cipher import AES, PKCS1_OAEP
from base64 import b64decode
from base64 import b64encode

with open('pubkey.pem', 'r') as f:
    key = RSA.import_key(f.read())

with open('message.enc', 'rb') as f:
    cipher = bytes_to_long(f.read())

factors = [9282105380008121879, 9303850685953812323, 9389357739583927789, 10336650220878499841, 10638241655447339831, 11282698189561966721, 11328768673634243077, 11403460639036243901, 11473665579512371723, 11492065299277279799, 11530534813954192171, 11665347949879312361, 12132158321859677597, 12834461276877415051, 12955403765595949597, 12973972336777979701,
           13099895578757581201, 13572286589428162097, 14100640260554622013, 14178869592193599187, 14278240802299816541, 14523070016044624039, 14963354250199553339, 15364597561881860737, 15669758663523555763, 15824122791679574573, 15998365463074268941, 16656402470578844539, 16898740504023346457, 17138336856793050757, 17174065872156629921, 17281246625998849649]

phi = 1
for factor in factors:
    phi *= (factor-1)

d = pow(key.e, -1, phi)
pt = pow(cipher, d, key.n)
decrypted = long_to_bytes(pt)

print(decrypted)
