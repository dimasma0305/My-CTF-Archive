import zipfile

# file = "5900.zip"
# while True:
#     with zipfile.ZipFile(file) as z:
#         pwd = z.namelist()[0].replace(".zip", "")
#         file = z.namelist()[0]
#         z.extractall(pwd=bytes(pwd, "utf-8"))
    


rockme = "/usr/share/wordlists/rockyou.txt"
with zipfile.ZipFile("6969.zip") as z:
    with open(rockme, "rb") as password:
        for i in password.read().splitlines():
            try:
                pwd = i
                z.extractall(pwd=pwd)
                print("[+] Password:", i.decode())
                break
            except:
                pass
