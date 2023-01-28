import requests

URL = "http://34.76.206.46:10008"

def get_nth_fibonacci_number(n):
    current_numb = 1
    previous_numb = 1
    sequence = []
    for i in range(n+1):
        current_numb = current_numb + previous_numb
        previous_numb = current_numb - previous_numb
        sequence.append(current_numb)
    return sequence

def get(num, url=URL):
    req = requests.get(f"{url}/?page={1}")
    print(req.text)
    for i in get_nth_fibonacci_number(num):
        req = requests.get(f"{url}/?page={i}")
        print(req.text)
        
get(100)