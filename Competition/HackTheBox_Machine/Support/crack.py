import base64
 
enc_password = "0Nv32PTwgYjzg9/8j5TbmvPd3e7WhtWWyuPsyO76/Y+U193E"
key = "armando".encode("UTF-8")
 
array = base64.b64decode(enc_password)
array2 = ""
 
for i in range(len(array)):
    array2 += chr(array[i] ^ key[i % len(key)] ^ 223)
 
print(array2)