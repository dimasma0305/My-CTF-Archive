v17 = b"You don't have access to KrackMe 1.0 !"
v18 = b"Since you are here let me ask you something..."
v15 = b"Please enter the flag : "
v16 = b"Oh My God ! What is that ?"
v20 = b"Did you know, Bangladesh has the longest natural beach?..."

v13 = b"mer`]MtGe"
v135 = b"aUG9UeDoU"
v14 = b"(G~Ty_G{("
v145 = b"v}QlOto|s"

for i in range(len(v13)):
    print(chr((v20[14]^v16[8])^v13[i]), end="")

for i in range(len(v135)):
    print(chr((v17[1] ^ v13[1])^v135[i]), end="")

for i in range(len(v14)):
    print(chr((v145[1] ^ v13[1])^v14[i]), end="")

for i in range(len(v145)):
    print(chr((v17[11] ^ v17[1])^v145[i]), end="")

