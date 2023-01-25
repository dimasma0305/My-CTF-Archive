def get_distances(padded_url : str, flag_path : str):
    distances = []
    for i in range(3):
        # calculate hamming distance on 16 byte subgroups
        flag_subgroup = flag_path[i*32:i*32+32]
                
        z = int(padded_url[i*32:i*32+32], 16)^int(flag_subgroup, 16)
        print(z)
        distances.append(bin(z).count('1'))  
        
    return distances

print(get_distances("F"*0x100, "F"*0x100))