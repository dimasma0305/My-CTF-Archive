from datetime import datetime
import random

def get_secret():
    SECRET_OFFSET = -67198624
    datetime_str = f"23-01-14 23:03:32 +0000"
    datetime_object = datetime.strptime(datetime_str, '%y-%m-%d %H:%M:%S +0000')
    for i in range(9999999):
        if i % 5000 != 0:
            continue
        timestamp = datetime_object.timestamp()
        timestamp = timestamp + (i/10000000)
        random.seed(round((timestamp + SECRET_OFFSET) * 1000))
        secret_key = "".join([hex(random.randint(0, 15)) for x in range(32)]).replace("0x", "")
        yield secret_key


def secret_unique():
    uniq = set()
    for val in get_secret():
        uniq.add(val)
    return uniq        

if __name__ == "__main__":
    secrets = secret_unique()
    with open("secret_list.txt", "w") as f:
        for i in secrets:
            f.write(f"{i}\n")