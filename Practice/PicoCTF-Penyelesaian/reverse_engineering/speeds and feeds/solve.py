import pwn
import webbrowser

data = pwn.remote("mercury.picoctf.net", 7032).recvall(timeout=3)
filename = "data.gcode"
with open(filename, "wb") as f:
    f.write(data)
    
webbrowser.open('https://ncviewer.com/', new=2)
