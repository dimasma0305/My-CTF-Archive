import re

with open("./misc-access.log", "r") as f:
    logs = f.readlines()

def get_time(x):
    return re.findall(r"\d{2}:\d{2}:\d{2}.\d{6}", x)

def get_msg(x):
    return re.findall(r"(?<=GET /).", x)

container = []
for log in logs:
    tmp0 = get_time(log)
    tmp1 = get_msg(log)
    container.append([tmp0, tmp1])

container = sorted(container)

for i in container:
    print(i[1][0], end="")