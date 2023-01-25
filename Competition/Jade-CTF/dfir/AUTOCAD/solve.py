from struct import pack

file = "poster.png"

def change_size(width, height) -> bytes:
    with open(file, 'rb') as f:
        data = f.read()
        
    ihdr_ofset = data.find(b'IHDR')
    
    height = pack('>I', height)
    width = pack('>I', width)
    new_data = (height+width).join([data[:ihdr_ofset+4], data[ihdr_ofset+4+8:]])
    with open('new_'+file, 'wb') as f:
        f.write(new_data)
        
change_size(0x1c2, 0x320)