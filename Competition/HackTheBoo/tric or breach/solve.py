data = open("capture.pcap",'rb').read()

found = []
while (p := data.find(b"pumpkincorp")) != -1:
    found.append(data[p-51:p-1])
    data = data[p+1:]

found = found[:-2] # Last 2 are not from DNS requests

d = b""
i = 0
for x in found:
    i += 1
    if i % 2 == 0:
        continue # Skip response
    try:
        d += (bytes.fromhex(x.decode()))
    except Exception as e:
        pass

open("data",'wb').write(d)