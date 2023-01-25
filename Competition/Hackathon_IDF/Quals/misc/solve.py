from PIL import Image, ImageStat

bin_flag = ""
for img in range(0, 295):
    n = Image.open(f"./soal/{img}.jpg")
    if n.getcolors()[0][1] == (12, 16, 15):
        bin_flag += "1"
    else:
        bin_flag += "0"
print(bin_flag)