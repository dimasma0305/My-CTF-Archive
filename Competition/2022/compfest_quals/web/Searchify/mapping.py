import subprocess

escapeList = {}

# for i in range(0,255):
i=0
while True:
    i+=1
    try:
        s = subprocess.check_output(["bash", "-c", f"echo $'\\{i}'"]).strip()
        print(i, end="\r")
        if b"\t" in s:
            print(i,s)
            exit()
        # escapeList[s] = i
    except:
        pass

print(escapeList)    