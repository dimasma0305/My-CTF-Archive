
def gen():
    with open("wordlist.txt", "w") as f:
        for i in range(39990000, 39999999):
            f.write(f"{i}.png\n")

gen()